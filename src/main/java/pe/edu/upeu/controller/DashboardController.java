package pe.edu.upeu.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pe.edu.upeu.config.AppContext;
import pe.edu.upeu.dto.SessionManager;
import pe.edu.upeu.model.Venta;
import pe.edu.upeu.service.IClienteService;
import pe.edu.upeu.service.IProductoService;
import pe.edu.upeu.service.IUsuarioService;
import pe.edu.upeu.service.IVentaService;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private Label lblSaludo;
    @FXML private Label lblFecha;
    @FXML private Label lblProductos;
    @FXML private Label lblClientes;
    @FXML private Label lblVentasHoy;
    @FXML private Label lblTotalHoy;
    @FXML private Label lblUsuarios;
    @FXML private Label lblStockBajo;
    @FXML private Label lblMsg;

    @FXML private TableView<Venta> tableVentas;
    @FXML private TableColumn<Venta, Long> colId;
    @FXML private TableColumn<Venta, String> colFecha;
    @FXML private TableColumn<Venta, String> colSerie;
    @FXML private TableColumn<Venta, String> colNumero;
    @FXML private TableColumn<Venta, Double> colTotal;
    @FXML private TableColumn<Venta, String> colEstado;

    private final IVentaService ventaService;
    private final IProductoService productoService;
    private final IClienteService clienteService;
    private final IUsuarioService usuarioService;
    private final ObservableList<Venta> ventas = FXCollections.observableArrayList();

    public DashboardController(IVentaService ventaService, IProductoService productoService,
                               IClienteService clienteService, IUsuarioService usuarioService) {
        this.ventaService = ventaService;
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }

    @FXML
    public void initialize() {
        String user = SessionManager.getInstance().getUserName();
        if (lblSaludo != null) lblSaludo.setText("Bienvenido, " + (user != null && !user.isBlank() ? user : "Usuario"));
        if (lblFecha != null) lblFecha.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        if (colFecha != null) colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getFecha() != null ? c.getValue().getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : ""));
        if (colSerie != null) colSerie.setCellValueFactory(new PropertyValueFactory<>("serie"));
        if (colNumero != null) colNumero.setCellValueFactory(new PropertyValueFactory<>("nroComprobante"));
        if (colTotal != null) colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        if (colEstado != null) colEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEstado() != null ? c.getValue().getEstado() : "ACTIVO"));
        if (tableVentas != null) tableVentas.setItems(ventas);

        refrescar();
    }

    @FXML
    public void refrescar() {
        try {
            List<Venta> all = ventaService.listarTodo();
            LocalDate hoy = LocalDate.now();
            List<Venta> hoyVentas = all.stream()
                    .filter(v -> v.getFecha() != null && v.getFecha().toLocalDate().equals(hoy)
                            && !"ANULADO".equalsIgnoreCase(v.getEstado()))
                    .collect(Collectors.toList());
            double totalHoy = hoyVentas.stream().mapToDouble(v -> v.getTotal() != null ? v.getTotal() : 0).sum();
            long stockBajo = productoService.listarTodo().stream()
                    .filter(p -> p.getStock() != null && p.getStock() <= 5).count();

            if (lblProductos != null) lblProductos.setText(String.valueOf(productoService.listarTodo().size()));
            if (lblClientes != null) lblClientes.setText(String.valueOf(clienteService.listarTodo().size()));
            if (lblVentasHoy != null) lblVentasHoy.setText(String.valueOf(hoyVentas.size()));
            if (lblTotalHoy != null) lblTotalHoy.setText(String.format(Locale.US, "S/ %.2f", totalHoy));
            if (lblUsuarios != null) lblUsuarios.setText(String.valueOf(usuarioService.listarTodo().size()));
            if (lblStockBajo != null) lblStockBajo.setText(String.valueOf(stockBajo));

            ventas.setAll(all.stream()
                    .sorted(Comparator.comparing((Venta v) -> v.getIdVenta() != null ? v.getIdVenta() : 0L).reversed())
                    .limit(12)
                    .collect(Collectors.toList()));

            if (lblMsg != null) {
                lblMsg.setText("Dashboard actualizado correctamente.");
                lblMsg.setStyle("-fx-text-fill:#166534; -fx-font-weight:bold;");
            }
        } catch (Exception e) {
            if (lblMsg != null) {
                lblMsg.setText("Error al cargar dashboard: " + e.getMessage());
                lblMsg.setStyle("-fx-text-fill:#b91c1c; -fx-font-weight:bold;");
            }
        }
    }

    @FXML public void abrirVentas() { abrirTab("/view/main_venta.fxml", "Ventas / POS"); }
    @FXML public void abrirProductos() { abrirTab("/view/main_producto.fxml", "Productos"); }
    @FXML public void abrirClientes() { abrirTab("/view/main_cliente.fxml", "Clientes"); }
    @FXML public void abrirComprobantes() { abrirTab("/view/main_comprobante.fxml", "Comprobantes"); }
    @FXML public void abrirUsuarios() { abrirTab("/view/main_usuario.fxml", "Usuarios"); }
    @FXML public void abrirCategorias() { abrirTab("/view/main_categoria.fxml", "Categorías"); }
    @FXML public void abrirMarcas() { abrirTab("/view/main_marca.fxml", "Marcas"); }
    @FXML public void abrirUnidades() { abrirTab("/view/main_unidadmedida.fxml", "Unidad de Medida"); }

    private void abrirTab(String fxml, String titulo) {
        try {
            if (tableVentas == null || tableVentas.getScene() == null) return;
            TabPane tabPane = (TabPane) tableVentas.getScene().lookup("#tabPane");
            if (tabPane == null) return;
            for (Tab t : tabPane.getTabs()) {
                if (titulo.equals(t.getText())) { tabPane.getSelectionModel().select(t); return; }
            }
            URL url = getClass().getResource(fxml);
            if (url == null) return;
            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(AppContext.getInstance()::getBean);
            Parent root = loader.load();
            ScrollPane sp = new ScrollPane(root);
            sp.setFitToWidth(true);
            sp.setFitToHeight(true);
            Tab tab = new Tab(titulo, sp);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
        } catch (Exception e) {
            if (lblMsg != null) lblMsg.setText("No se pudo abrir: " + e.getMessage());
        }
    }
}