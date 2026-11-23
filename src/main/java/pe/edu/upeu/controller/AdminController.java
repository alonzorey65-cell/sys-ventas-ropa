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
import pe.edu.upeu.service.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * POO - CONTROLADOR (capa presentación MVC): gestiona el panel principal del Administrador.
 * ENCAPSULAMIENTO: campos @FXML y dependencias privadas.
 * ABSTRACCIÓN/POLIMORFISMO: depende de interfaces de servicio (IProductoService, etc.), no de implementaciones concretas.
 * INYECCIÓN DE DEPENDENCIAS: servicios inyectados por constructor desde AppContext (IoC/DI simple).
 * SINGLETON (uso): SessionManager.getInstance() y AppContext.getInstance().
 * CONEXIÓN: Vista FXML -> AdminController -> Services -> Repositories -> BD.
 */
public class AdminController {
    // ENCAPSULAMIENTO: componentes de UI inyectados por JavaFX (@FXML), no accesibles desde fuera.
    @FXML private Label lblBienvenida, lblVentasHoy, lblTotalHoy, lblProductos, lblClientes, lblUsuarios, lblStockBajo, lblAnuladasHoy, lblMsg;
    @FXML private TableView<Venta> tableVentas;
    @FXML private TableColumn<Venta, Long> colId;
    @FXML private TableColumn<Venta, String> colFecha, colSerie, colNumero, colEstado;
    @FXML private TableColumn<Venta, Double> colTotal;

    private final IVentaService ventaService;
    private final IProductoService productoService;
    private final IClienteService clienteService;
    private final IUsuarioService usuarioService;
    private final ObservableList<Venta> ventas = FXCollections.observableArrayList();

    public AdminController(IVentaService ventaService, IProductoService productoService, IClienteService clienteService, IUsuarioService usuarioService) {
        this.ventaService = ventaService;
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }

    @FXML public void initialize() {
        String user = SessionManager.getInstance().getUserName();
        if (lblBienvenida != null) lblBienvenida.setText("Panel Administrador - " + (user != null ? user : "admin"));
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        if (colFecha != null) colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFecha()!=null?c.getValue().getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")):""));
        if (colSerie != null) colSerie.setCellValueFactory(new PropertyValueFactory<>("serie"));
        if (colNumero != null) colNumero.setCellValueFactory(new PropertyValueFactory<>("nroComprobante"));
        if (colTotal != null) colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        if (colEstado != null) colEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEstado()!=null?c.getValue().getEstado():"ACTIVO"));
        if (tableVentas != null) tableVentas.setItems(ventas);
        refrescar();
    }

    @FXML public void refrescar() {
        try {
            List<Venta> all = ventaService.listarTodo(); // ABSTRACCIÓN: el controller usa el servicio, no SQL directo
            LocalDate hoy = LocalDate.now();
            List<Venta> hoyVentas = all.stream()
                    .filter(v -> v.getFecha()!=null && v.getFecha().toLocalDate().equals(hoy) && !"ANULADO".equalsIgnoreCase(v.getEstado()))
                    .collect(Collectors.toList());
            long anuladasHoy = all.stream()
                    .filter(v -> v.getFecha()!=null && v.getFecha().toLocalDate().equals(hoy) && "ANULADO".equalsIgnoreCase(v.getEstado()))
                    .count();
            double totalHoy = hoyVentas.stream().mapToDouble(v -> v.getTotal()!=null?v.getTotal():0).sum();
            long stockBajo = productoService.listarTodo().stream().filter(p -> p.getStock()!=null && p.getStock() <= 5).count();
            if (lblVentasHoy != null) lblVentasHoy.setText(String.valueOf(hoyVentas.size()));
            if (lblTotalHoy != null) lblTotalHoy.setText(String.format(java.util.Locale.US, "S/ %.2f", totalHoy));
            if (lblProductos != null) lblProductos.setText(String.valueOf(productoService.listarTodo().size()));
            if (lblClientes != null) lblClientes.setText(String.valueOf(clienteService.listarTodo().size()));
            if (lblUsuarios != null) lblUsuarios.setText(String.valueOf(usuarioService.listarTodo().size()));
            if (lblStockBajo != null) lblStockBajo.setText(String.valueOf(stockBajo));
            if (lblAnuladasHoy != null) lblAnuladasHoy.setText(String.valueOf(anuladasHoy));
            ventas.setAll(all.stream().sorted((a,b)->Long.compare(b.getIdVenta()!=null?b.getIdVenta():0,a.getIdVenta()!=null?a.getIdVenta():0)).limit(15).collect(Collectors.toList()));
            if (lblMsg != null) { lblMsg.setText("Panel actualizado."); lblMsg.setStyle("-fx-text-fill:#166534;"); }
        } catch (Exception e) {
            if (lblMsg != null) { lblMsg.setText("Error: " + e.getMessage()); lblMsg.setStyle("-fx-text-fill:#b91c1c;"); }
        }
    }

    @FXML public void abrirProductos() { abrirTab("/view/main_producto.fxml", "Productos"); }
    @FXML public void abrirClientes() { abrirTab("/view/main_cliente.fxml", "Clientes"); }
    @FXML public void abrirVentas() { abrirTab("/view/main_venta.fxml", "Ventas / POS"); }
    @FXML public void abrirComprobantes() { abrirTab("/view/main_comprobante.fxml", "Comprobantes"); }
    @FXML public void abrirUsuarios() { abrirTab("/view/main_usuario.fxml", "Usuarios"); }

    private void abrirTab(String fxml, String titulo) {
        try {
            if (tableVentas == null || tableVentas.getScene() == null) return;
            TabPane tabPane = (TabPane) tableVentas.getScene().lookup("#tabPane");
            if (tabPane == null) return;
            for (Tab t : tabPane.getTabs()) if (titulo.equals(t.getText())) { tabPane.getSelectionModel().select(t); return; }
            URL url = getClass().getResource(fxml);
            if (url == null) return;
            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(AppContext.getInstance()::getBean); // SINGLETON AppContext
            Parent root = loader.load();
            ScrollPane sp = new ScrollPane(root); sp.setFitToWidth(true); sp.setFitToHeight(true);
            Tab tab = new Tab(titulo, sp);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
        } catch (Exception e) { if (lblMsg != null) lblMsg.setText("No se pudo abrir: " + e.getMessage()); }
    }
}
