package pe.edu.upeu.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import pe.edu.upeu.config.AppContext;
import pe.edu.upeu.dto.MenuMenuItenTO;
import pe.edu.upeu.dto.SessionManager;
import pe.edu.upeu.service.IMenuMenuItemDao;
import pe.edu.upeu.utils.UiAnimations;
import pe.edu.upeu.utils.UtilsX;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.Preferences;

/**
 * POO - CONTROLADOR PRINCIPAL (MVC):
 * - ENCAPSULAMIENTO: atributos privados/@FXML; lógica interna oculta.
 * - ABSTRACCIÓN: delega en servicios/DAO (IMenuMenuItemDao) y utilidades.
 * - POLIMORFISMO: usa interfaces/abstracciones y maneja eventos.
 * - SINGLETON (uso): SessionManager.getInstance() para sesión; AppContext.getInstance() para beans.
 * - COMPOSICIÓN: coordina MenuBar, TabPane, menús dinámicos y apertura de vistas FXML.
 */
public class MainGuiController {

    @FXML BorderPane bp;
    @FXML MenuBar menuBar;
    @FXML TabPane tabPane;

    // Enlaces directos a los elementos del menú estático de tu FXML (pueden quedar nulos si reconstruimos)
    @FXML private MenuItem menuItemProducto;
    @FXML private MenuItem menuItemVenta;
    @FXML private MenuItem menuItemComprobante;
    @FXML private MenuItem menuItemClose;

    private final IMenuMenuItemDao mmiDao;
    private Parent parent;
    Stage stage;
    Preferences userPrefs = Preferences.userRoot();
    UtilsX util = new UtilsX();
    Properties myresources = new Properties();
    List<MenuMenuItenTO> lista;

    private final Menu menuEstilo = new Menu("Cambiar Estilo");
    private final ComboBox<String> comboBoxEstilo = new ComboBox<>(
            FXCollections.observableArrayList(
                    "Estilo por Defecto", "Estilo Oscuro",
                    "Estilo Azul", "Estilo Verde", "Estilo Rosado"));
    private final CustomMenuItem customItem = new CustomMenuItem(comboBoxEstilo);

    private final Menu menuIdioma = new Menu("Idioma");
    private final ComboBox<String> comboBoxIdioma = new ComboBox<>(
            FXCollections.observableArrayList("Español", "Ingles", "Frances"));
    private final CustomMenuItem customItemIdioma = new CustomMenuItem(comboBoxIdioma);

    public MainGuiController(IMenuMenuItemDao mmiDao) {
        this.mmiDao = mmiDao;
    }

    // Inicializa la GUI principal: configura listeners, idioma, estilo y construye menús según perfil
    @FXML
    public void initialize(){
        PauseTransition pause = new PauseTransition(Duration.millis(300));
        pause.setOnFinished(e -> {
            if (tabPane.getScene() != null && tabPane.getScene().getWindow() != null) {
                stage = (Stage) tabPane.getScene().getWindow();
            }
        });
        pause.play();

        if (tabPane != null) tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

        // Seleccionar idioma guardado en combo
        String lang = userPrefs.get("IDIOMAX", "es");
        if ("en".equals(lang)) comboBoxIdioma.getSelectionModel().select("Ingles");
        else if ("fr".equals(lang)) comboBoxIdioma.getSelectionModel().select("Frances");
        else comboBoxIdioma.getSelectionModel().select("Español");

        // Asegurar listeners base (se vuelven a usar al reconstruir)
        comboBoxEstilo.setOnAction(e -> cambiarEstilo());
        customItem.setHideOnClick(false);
        comboBoxIdioma.setOnAction(e -> cambiarIdioma());
        customItemIdioma.setHideOnClick(false);

        graficarMenus();
        cargarDashboardInicial();
        if (bp != null) bp.setCenter(tabPane);
    }

    // Obtiene accesos de menú desde BD/DAO según perfil de sesión (SessionManager -> IMenuMenuItemDao)
    private List<MenuMenuItenTO> listaAccesos() {
        myresources = util.detectLanguage(userPrefs.get("IDIOMAX", "es"));
        return mmiDao.listaAccesos(SessionManager.getInstance().getUserPerfil(), myresources);
    }

    // Cambia hoja de estilos CSS de la escena (UI)
    private void cambiarEstilo() {
        String estiloSeleccionado = comboBoxEstilo.getSelectionModel().getSelectedItem();
        if (estiloSeleccionado == null || bp == null || bp.getScene() == null) return;
        Scene escena = bp.getScene();

        String cssPath = switch (estiloSeleccionado) {
            case "Estilo Oscuro" -> "/css/estilo-oscuro.css";
            case "Estilo Azul" -> "/css/estilo-azul.css";
            case "Estilo Verde" -> "/css/estilo-verde.css";
            case "Estilo Rosado" -> "/css/estilo-rosado.css";
            default -> "/css/style.css";
        };

        escena.getStylesheets().clear();
        URL baseUrl = getClass().getResource("/css/style.css");
        if (baseUrl != null) escena.getStylesheets().add(baseUrl.toExternalForm());
        if (!"/css/style.css".equals(cssPath)) {
            URL cssUrl = getClass().getResource(cssPath);
            if (cssUrl != null) escena.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    // Cambia idioma guardado en preferencias y reconstruye menús
    private void cambiarIdioma() {
        String idiomaSeleccionado = comboBoxIdioma.getSelectionModel().getSelectedItem();
        if (idiomaSeleccionado == null) return;
        switch (idiomaSeleccionado) {
            case "Español" -> userPrefs.put("IDIOMAX", "es");
            case "Ingles" -> userPrefs.put("IDIOMAX", "en");
            case "Frances" -> userPrefs.put("IDIOMAX", "fr");
            default -> userPrefs.put("IDIOMAX", "es");
        }
        graficarMenus();
    }

    private String tr(String key, String def) {
        if (myresources == null) return def;
        String v = myresources.getProperty(key);
        return (v == null || v.isBlank()) ? def : v;
    }

    // Construye MenuBar dinámico según accesos del perfil y agrega menús de estilo/idioma
    private void graficarMenus() {
        lista = listaAccesos();
        if (menuBar == null) return;
        menuBar.getMenus().clear();

        // Menú base (File) con textos traducibles
        Menu fileMenu = new Menu(tr("menu.archivo", "Archivo"));
        MenuItem miPanelAdmin = new MenuItem("Panel Administrador");
        miPanelAdmin.setOnAction(e -> abrirTabPaneFXML("/view/main_admin.fxml", "Panel Administrador"));
        MenuItem miPanel = new MenuItem("Panel Vendedor");
        miPanel.setOnAction(e -> abrirTabPaneFXML("/view/main_vendedor.fxml", "Panel Vendedor"));
        MenuItem miPanelAlmacen = new MenuItem("Panel Almacén");
        miPanelAlmacen.setOnAction(e -> abrirTabPaneFXML("/view/main_almacen.fxml", "Panel Almacén"));
        MenuItem miProd = new MenuItem(tr("menu.inventario", "Productos"));
        miProd.setOnAction(e -> abrirTabPaneFXML("/view/main_producto.fxml", tr("menu.inventario", "Productos")));
        MenuItem miVenta = new MenuItem(tr("menu.ventas", "Ventas"));
        miVenta.setOnAction(e -> abrirTabPaneFXML("/view/main_venta.fxml", tr("menu.ventas", "Ventas")));
        MenuItem miComp = new MenuItem(tr("menu.comprobantes", "Comprobantes"));
        miComp.setOnAction(e -> abrirTabPaneFXML("/view/main_comprobante.fxml", tr("menu.comprobantes", "Comprobantes")));
        MenuItem miClose = new MenuItem(tr("menu.salir", "Salir"));
        miClose.setOnAction(e -> {
            if (bp != null && bp.getScene() != null && bp.getScene().getWindow() != null) {
                ((Stage) bp.getScene().getWindow()).close();
            }
        });
        if (tieneAcceso("/view/main_admin.fxml")) fileMenu.getItems().add(miPanelAdmin);
        if (tieneAcceso("/view/main_vendedor.fxml")) fileMenu.getItems().add(miPanel);
        if (tieneAcceso("/view/main_almacen.fxml")) fileMenu.getItems().add(miPanelAlmacen);
        if (tieneAcceso("/view/main_producto.fxml")) fileMenu.getItems().add(miProd);
        if (tieneAcceso("/view/main_venta.fxml")) fileMenu.getItems().add(miVenta);
        if (tieneAcceso("/view/main_comprobante.fxml")) fileMenu.getItems().add(miComp);
        if (!fileMenu.getItems().isEmpty()) fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(miClose);
        menuBar.getMenus().add(fileMenu);

        // Menús dinámicos desde BD (traducidos por nombre conocido)
        MenuItemListener d = new MenuItemListener();
        String menuN = "";
        Menu currentMenu = null;
        for (MenuMenuItenTO mmix : lista) {
            String nombreMenu = traducirNombre(mmix.getMenunombre());
            String nombreItem = traducirNombre(mmix.getMenuitemnombre());

            if (currentMenu == null || !mmix.getMenunombre().equals(menuN)) {
                currentMenu = new Menu(nombreMenu);
                currentMenu.setId("m" + mmix.getIdNombreObj());
                menuBar.getMenus().add(currentMenu);
                menuN = mmix.getMenunombre();
            }
            if (mmix.getMenuitemnombre() != null && !mmix.getMenuitemnombre().isBlank()) {
                MenuItem mi = new MenuItem(nombreItem);
                mi.setId("mi" + mmix.getIdNombreObj());
                mi.setOnAction(d::handle);
                currentMenu.getItems().add(mi);
            }
        }

        // Menú Acceso rápido
        Menu quickMenu = new Menu("Acceso Rápido");
        MenuItem miHome = new MenuItem("Dashboard");
        miHome.setOnAction(e -> {
            if (tabPane != null && !tabPane.getTabs().isEmpty()) {
                tabPane.getSelectionModel().select(0);
            }
        });
        quickMenu.getItems().add(miHome);
        quickMenu.getItems().add(new SeparatorMenuItem());

        MenuItem qVenta = new MenuItem("Ventas / POS");
        qVenta.setOnAction(e -> abrirTabPaneFXML("/view/main_venta.fxml", "Ventas / POS"));
        MenuItem qProductos = new MenuItem("Productos");
        qProductos.setOnAction(e -> abrirTabPaneFXML("/view/main_producto.fxml", "Productos"));
        MenuItem qClientes = new MenuItem("Clientes");
        qClientes.setOnAction(e -> abrirTabPaneFXML("/view/main_cliente.fxml", "Clientes"));
        MenuItem qComprobantes = new MenuItem("Comprobantes");
        qComprobantes.setOnAction(e -> abrirTabPaneFXML("/view/main_comprobante.fxml", "Comprobantes"));
        MenuItem qUsuarios = new MenuItem("Usuarios");
        qUsuarios.setOnAction(e -> abrirTabPaneFXML("/view/main_usuario.fxml", "Usuarios"));
        MenuItem qCategorias = new MenuItem("Categorías");
        qCategorias.setOnAction(e -> abrirTabPaneFXML("/view/main_categoria.fxml", "Categorías"));
        MenuItem qMarcas = new MenuItem("Marcas");
        qMarcas.setOnAction(e -> abrirTabPaneFXML("/view/main_marca.fxml", "Marcas"));
        MenuItem qUnidades = new MenuItem("Unidad de Medida");
        qUnidades.setOnAction(e -> abrirTabPaneFXML("/view/main_unidadmedida.fxml", "Unidad de Medida"));

        if (tieneAcceso("/view/main_venta.fxml")) quickMenu.getItems().add(qVenta);
        if (tieneAcceso("/view/main_producto.fxml")) quickMenu.getItems().add(qProductos);
        if (tieneAcceso("/view/main_cliente.fxml")) quickMenu.getItems().add(qClientes);
        if (tieneAcceso("/view/main_comprobante.fxml")) quickMenu.getItems().add(qComprobantes);
        if (tieneAcceso("/view/main_usuario.fxml")) quickMenu.getItems().add(qUsuarios);
        if (tieneAcceso("/view/main_categoria.fxml")) quickMenu.getItems().add(qCategorias);
        if (tieneAcceso("/view/main_marca.fxml")) quickMenu.getItems().add(qMarcas);
        if (tieneAcceso("/view/main_unidadmedida.fxml")) quickMenu.getItems().add(qUnidades);
        quickMenu.getItems().add(new SeparatorMenuItem());
        MenuItem qCerrarActual = new MenuItem("Cerrar pestaña actual");
        qCerrarActual.setOnAction(e -> {
            if (tabPane != null && !tabPane.getTabs().isEmpty()) {
                Tab sel = tabPane.getSelectionModel().getSelectedItem();
                if (sel != null && sel.isClosable()) tabPane.getTabs().remove(sel);
            }
        });
        MenuItem qCerrarOtras = new MenuItem("Cerrar otras pestañas");
        qCerrarOtras.setOnAction(e -> {
            if (tabPane == null) return;
            Tab sel = tabPane.getSelectionModel().getSelectedItem();
            tabPane.getTabs().removeIf(t -> t != sel && t.isClosable());
        });
        quickMenu.getItems().addAll(qCerrarActual, qCerrarOtras);
        menuBar.getMenus().add(quickMenu);

        // Menú Estilo e Idioma
        menuEstilo.setText(tr("menu.estilo", "Cambiar Estilo"));
        menuEstilo.getItems().setAll(customItem);
        menuBar.getMenus().add(menuEstilo);

        menuIdioma.setText(tr("menu.idioma", "Idioma"));
        menuIdioma.getItems().setAll(customItemIdioma);
        menuBar.getMenus().add(menuIdioma);

        // Actualizar título de ventana y pestaña principal si existen
        if (bp != null && bp.getScene() != null && bp.getScene().getWindow() instanceof Stage st) {
            st.setTitle(tr("app.titulo", "Sistema de Ventas"));
        }
        if (tabPane != null && !tabPane.getTabs().isEmpty()) {
            Tab t0 = tabPane.getTabs().get(0);
            if (t0 != null) t0.setText("Dashboard");
        }
        // Abrir panel por defecto según perfil
        String perfil = SessionManager.getInstance().getUserPerfil();
        String p = perfil == null ? "" : perfil.trim().toUpperCase();
        boolean esVendedor = p.equals("2") || p.equals("VENDEDOR");
        boolean esAdmin = p.equals("1") || p.equals("ADMIN") || p.equals("ADMINISTRADOR");
        boolean esAlmacen = p.equals("3") || p.equals("ALMACEN") || p.equals("ALMACENERO");
        if (tabPane != null && tabPane.getTabs().size() <= 1) {
            if (esVendedor) abrirTabPaneFXML("/view/main_vendedor.fxml", "Panel Vendedor");
            else if (esAdmin) abrirTabPaneFXML("/view/main_admin.fxml", "Panel Administrador");
            else if (esAlmacen) abrirTabPaneFXML("/view/main_almacen.fxml", "Panel Almacén");
        }
    }

    private String traducirNombre(String original) {
        if (original == null) return "";
        String o = original.trim();
        if (o.equalsIgnoreCase("Almacén") || o.equalsIgnoreCase("Almacen")) return tr("menu.inventario", o);
        if (o.equalsIgnoreCase("Producto") || o.equalsIgnoreCase("Productos")) return tr("menu.inventario", o);
        if (o.equalsIgnoreCase("Ventas")) return tr("menu.ventas", o);
        if (o.equalsIgnoreCase("Registrar Venta / POS") || o.equalsIgnoreCase("Venta")) return tr("menu.ventas", o);
        if (o.equalsIgnoreCase("Reportes")) return tr("menu.reportes", "Reportes");
        if (o.equalsIgnoreCase("Comprobantes")) return tr("menu.comprobantes", "Comprobantes");
        if (o.equalsIgnoreCase("Sesión") || o.equalsIgnoreCase("Sesion")) return tr("menu.sesion", "Sesión");
        if (o.equalsIgnoreCase("Cerrar Sesión") || o.equalsIgnoreCase("Cerrar Sesion")) return tr("menu.cerrar_sesion", "Cerrar Sesión");
        return o;
    }

    private void cargarDashboardInicial() {
        try {
            if (tabPane == null || tabPane.getTabs().isEmpty()) return;
            Tab t0 = tabPane.getTabs().get(0);
            if (t0 == null || t0.getContent() != null) return;
            URL url = getClass().getResource("/view/main_dashboard.fxml");
            if (url == null) return;
            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(AppContext.getInstance()::getBean);
            Parent root = loader.load();
            ScrollPane sp = new ScrollPane(root);
            sp.setFitToWidth(true);
            sp.setFitToHeight(true);
            animarEntrada(root);
            t0.setContent(sp);
            t0.setText("Dashboard");
        } catch (Exception e) {
            System.err.println("No se pudo cargar dashboard inicial: " + e.getMessage());
        }
    }

    private void animarEntrada(Parent node) {
        if (node == null) return;
        node.setOpacity(0);
        node.setTranslateY(18);
        FadeTransition fade = new FadeTransition(Duration.millis(320), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(320), node);
        slide.setFromY(18);
        slide.setToY(0);
        ParallelTransition pt = new ParallelTransition(fade, slide);
        pt.setOnFinished(e -> UiAnimations.decorateTree(node));
        pt.play();
    }

    // Método seguro para abrir cada vista en su pestaña respectiva sin duplicados
    // Carga un FXML en un Tab nuevo (o selecciona existente); usa AppContext como controllerFactory para inyectar dependencias
    private void abrirTabPaneFXML(String fxmlPath, String tittle) {
        try {
            if (!tieneAcceso(fxmlPath)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Acceso denegado");
                alert.setContentText("Su rol/perfil no tiene permiso para acceder a este módulo.");
                alert.showAndWait();
                return;
            }
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("No se encontró el archivo FXML en la ruta: " + fxmlPath);
                return;
            }

            if (tabPane != null) {
                for (Tab tab : tabPane.getTabs()) {
                    if (tab.getText().equals(tittle)) {
                        tabPane.getSelectionModel().select(tab);
                        return;
                    }
                }
            }

            AppContext context = AppContext.getInstance();
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            fxmlLoader.setControllerFactory(context::getBean);
            Parent root = fxmlLoader.load();
            animarEntrada(root);

            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            Tab newTab = new Tab(tittle, scrollPane);
            tabPane.getTabs().add(newTab);
            tabPane.getSelectionModel().select(newTab);
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private boolean tieneAcceso(String fxmlPath) {
        if (fxmlPath == null) return false;
        String perfil = SessionManager.getInstance().getUserPerfil();
        String p = perfil == null ? "1" : perfil.trim().toUpperCase();
        boolean esAdmin = p.equals("1") || p.equals("ADMIN") || p.equals("ADMINISTRADOR");
        boolean esVendedor = p.equals("2") || p.equals("VENDEDOR");
        boolean esAlmacen = p.equals("3") || p.equals("ALMACEN") || p.equals("ALMACENERO");
        if (esAdmin) return true;
        if (esVendedor) {
            return fxmlPath.contains("main_vendedor.fxml")
                    || fxmlPath.contains("main_venta.fxml")
                    || fxmlPath.contains("main_comprobante.fxml")
                    || fxmlPath.contains("login.fxml");
        }
        if (esAlmacen) {
            return fxmlPath.contains("main_almacen.fxml")
                    || fxmlPath.contains("main_producto.fxml")
                    || fxmlPath.contains("main_categoria.fxml")
                    || fxmlPath.contains("main_marca.fxml")
                    || fxmlPath.contains("main_unidadmedida.fxml")
                    || fxmlPath.contains("login.fxml");
        }
        // Por defecto, solo vistas no administrativas básicas
        return fxmlPath.contains("login.fxml");
    }

    class MenuItemListener {

        Map<String, String[]> menuConfig;
        MenuItemListener() {
            menuConfig = mmiDao.accesosAutorizados(lista);
        }

        public void handle(ActionEvent e){
            String id = ((MenuItem)e.getSource()).getId();
            if(menuConfig.containsKey(id)){
                String[] items = menuConfig.get(id);
                String title = traducirNombre(items[1]);
                if(items.length > 2 && "C".equals(items[2])){
                    redireccionar(items[0]);
                }else{
                    abrirTabPaneFXML(items[0], title);
                }
            }
        }

        private void redireccionar(String fxmlPath) {
            if (tabPane != null) tabPane.getTabs().clear();
            try {
                URL fxmlUrl = getClass().getResource(fxmlPath);
                if (fxmlUrl == null) {
                    System.err.println("Error al redireccionar: FXML no encontrado en " + fxmlPath);
                    return;
                }
                AppContext ctx = AppContext.getInstance();
                FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
                fxmlLoader.setControllerFactory(clazz -> ctx.getBean(clazz));
                parent = fxmlLoader.load();
                Scene scene = new Scene(parent);

                Platform.runLater(() -> {
                    if (stage == null && bp != null && bp.getScene() != null) stage = (Stage) bp.getScene().getWindow();
                    if (stage != null) {
                        stage.sizeToScene();
                        stage.setScene(scene);
                        stage.centerOnScreen();
                        stage.setTitle(tr("app.titulo", "SysVentas - Sistema de Ventas"));
                        stage.setResizable(false);
                        stage.show();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
