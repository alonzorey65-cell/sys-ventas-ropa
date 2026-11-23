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
import pe.edu.upeu.service.IVentaService;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * POO - CONTROLADOR (MVC): Panel del rol Vendedor.
 * - ENCAPSULAMIENTO: atributos privados + @FXML; estado interno no expuesto.
 * - ABSTRACCIÓN/POLIMORFISMO: depende de IVentaService (interfaz), no de implementación concreta.
 * - INYECCIÓN DE DEPENDENCIAS: el servicio se recibe por constructor (inyectado por AppContext).
 * - SINGLETON (uso): obtiene sesión y contexto con SessionManager.getInstance()/AppContext.getInstance().
 */
public class VendedorController {
    @FXML private Label lblBienvenida, lblVentasHoy, lblTotalHoy, lblMisVentas, lblAnuladasHoy, lblTicketProm, lblMsg;
    @FXML private TableView<Venta> tableVentas;
    @FXML private TableColumn<Venta, Long> colId;
    @FXML private TableColumn<Venta, String> colFecha, colSerie, colNumero, colEstado;
    @FXML private TableColumn<Venta, Double> colTotal;

    private final IVentaService ventaService; // dependencia a abstracción
    private final ObservableList<Venta> ventas = FXCollections.observableArrayList();

    // INYECCIÓN POR CONSTRUCTOR: AppContext crea el objeto y le pasa la dependencia
    public VendedorController(IVentaService ventaService) { this.ventaService = ventaService; }

    @FXML public void initialize() {
        String user = SessionManager.getInstance().getUserName();
        if (lblBienvenida != null) lblBienvenida.setText("Bienvenido, " + (user != null ? user : "Vendedor"));
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
            // SINGLETON: SessionManager.getInstance() da la única sesión activa
            Long userId = SessionManager.getInstance().getUserId();
            List<Venta> all = ventaService.listarTodo();
            List<Venta> mias = all.stream().filter(v -> userId != null && userId.equals(v.getIdUsuario())).collect(Collectors.toList());
            LocalDate hoy = LocalDate.now();
            List<Venta> hoyVentas = mias.stream().filter(v -> v.getFecha()!=null && v.getFecha().toLocalDate().equals(hoy) && !"ANULADO".equalsIgnoreCase(v.getEstado())).collect(Collectors.toList());
            long anuladasHoy = mias.stream().filter(v -> v.getFecha()!=null && v.getFecha().toLocalDate().equals(hoy) && "ANULADO".equalsIgnoreCase(v.getEstado())).count();
            double totalHoy = hoyVentas.stream().mapToDouble(v -> v.getTotal()!=null?v.getTotal():0).sum();
            double ticket = hoyVentas.isEmpty() ? 0 : totalHoy / hoyVentas.size();
            if (lblVentasHoy != null) lblVentasHoy.setText(String.valueOf(hoyVentas.size()));
            if (lblTotalHoy != null) lblTotalHoy.setText(String.format(java.util.Locale.US, "S/ %.2f", totalHoy));
            if (lblMisVentas != null) lblMisVentas.setText(String.valueOf(mias.size()));
            if (lblAnuladasHoy != null) lblAnuladasHoy.setText(String.valueOf(anuladasHoy));
            if (lblTicketProm != null) lblTicketProm.setText(String.format(java.util.Locale.US, "S/ %.2f", ticket));
            ventas.setAll(mias.stream().sorted((a,b)->Long.compare(b.getIdVenta()!=null?b.getIdVenta():0,a.getIdVenta()!=null?a.getIdVenta():0)).limit(20).collect(Collectors.toList()));
            if (lblMsg != null) { lblMsg.setText("Datos actualizados."); lblMsg.setStyle("-fx-text-fill:#166534;"); }
        } catch (Exception e) {
            if (lblMsg != null) { lblMsg.setText("Error: " + e.getMessage()); lblMsg.setStyle("-fx-text-fill:#b91c1c;"); }
        }
    }

    @FXML public void abrirVentas() { abrirTab("/view/main_venta.fxml", "Ventas / POS"); }
    @FXML public void abrirComprobantes() { abrirTab("/view/main_comprobante.fxml", "Comprobantes"); }

    private void abrirTab(String fxml, String titulo) {
        try {
            if (tableVentas == null || tableVentas.getScene() == null) return;
            TabPane tabPane = (TabPane) tableVentas.getScene().lookup("#tabPane");
            if (tabPane == null) return;
            for (Tab t : tabPane.getTabs()) if (titulo.equals(t.getText())) { tabPane.getSelectionModel().select(t); return; }
            URL url = getClass().getResource(fxml);
            if (url == null) return;
            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(AppContext.getInstance()::getBean);
            Parent root = loader.load();
            ScrollPane sp = new ScrollPane(root); sp.setFitToWidth(true); sp.setFitToHeight(true);
            Tab tab = new Tab(titulo, sp);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
        } catch (Exception e) { if (lblMsg != null) lblMsg.setText("No se pudo abrir: " + e.getMessage()); }
    }
}
