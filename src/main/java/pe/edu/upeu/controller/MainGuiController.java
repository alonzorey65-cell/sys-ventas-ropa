package pe.edu.upeu.controller;

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
import pe.edu.upeu.utils.UtilsX;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.Preferences;

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
        if (bp != null) bp.setCenter(tabPane);
    }

    private List<MenuMenuItenTO> listaAccesos() {
        myresources = util.detectLanguage(userPrefs.get("IDIOMAX", "es"));
        return mmiDao.listaAccesos(SessionManager.getInstance().getUserPerfil(), myresources);
    }

    private void cambiarEstilo() {
        String estiloSeleccionado = comboBoxEstilo.getSelectionModel().getSelectedItem();
        if (estiloSeleccionado == null || bp == null || bp.getScene() == null) return;
        Scene escena = bp.getScene();

        String cssPath = switch (estiloSeleccionado) {
            case "Estilo Oscuro" -> "/css/estilo-oscuro.css";
            case "Estilo Azul" -> "/css/estilo-azul.css";
            case "Estilo Verde" -> "/css/estilo-verde.css";
            case "Estilo Rosado" -> "/css/estilo-rosado.css";
            default -> "/css/style.css"; // por defecto, tema claro corregido
        };

        escena.getStylesheets().clear();
        URL cssUrl = getClass().getResource(cssPath);
        if (cssUrl != null) escena.getStylesheets().add(cssUrl.toExternalForm());
    }

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

    private void graficarMenus() {
        lista = listaAccesos();
        if (menuBar == null) return;
        menuBar.getMenus().clear();

        // Menú base (File) con textos traducibles
        Menu fileMenu = new Menu(tr("menu.archivo", "Archivo"));
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
        fileMenu.getItems().addAll(miProd, miVenta, miComp, new SeparatorMenuItem(), miClose);
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
            if (t0 != null) t0.setText(tr("label.bienvenido", "Pantalla Principal"));
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

    // Método seguro para abrir cada vista en su pestaña respectiva sin duplicados
    private void abrirTabPaneFXML(String fxmlPath, String tittle) {
        try {
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
