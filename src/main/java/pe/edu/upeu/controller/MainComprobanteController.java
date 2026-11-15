package pe.edu.upeu.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import pe.edu.upeu.components.comprob.ComprobantePdfService;
import pe.edu.upeu.components.comprob.ComprobanteView;
import pe.edu.upeu.dto.comprobante.Comprobante;
import pe.edu.upeu.model.Cliente;
import pe.edu.upeu.model.Venta;
import pe.edu.upeu.model.VentaDetalle;
import pe.edu.upeu.service.IClienteService;
import pe.edu.upeu.service.IVentaDetalleService;
import pe.edu.upeu.service.IVentaService;

import java.awt.Desktop;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainComprobanteController {

    @FXML private ScrollPane scrollVisor;
    @FXML private Button btnExportarPdf;
    @FXML private Button btnImprimir;
    @FXML private Button btnRefrescar;
    @FXML private Button btnVer;
    @FXML private Button btnEliminarVenta;

    @FXML private TableView<Venta> tableVentas;
    @FXML private TableColumn<Venta, Long> colId;
    @FXML private TableColumn<Venta, String> colFecha;
    @FXML private TableColumn<Venta, String> colSerie;
    @FXML private TableColumn<Venta, String> colNumero;
    @FXML private TableColumn<Venta, String> colCliente;
    @FXML private TableColumn<Venta, Double> colTotal;

    private final ComprobantePdfService pdfService = new ComprobantePdfService();
    private final IVentaService ventaService;
    private final IVentaDetalleService ventaDetalleService;
    private final IClienteService clienteService;

    private final ObservableList<Venta> ventas = FXCollections.observableArrayList();
    private VBox vistaActual;
    private Comprobante boletaActual;

    public MainComprobanteController(IVentaService ventaService,
                                     IVentaDetalleService ventaDetalleService,
                                     IClienteService clienteService) {
        this.ventaService = ventaService;
        this.ventaDetalleService = ventaDetalleService;
        this.clienteService = clienteService;
    }

    // Fallback
    public MainComprobanteController() {
        this(null, null, null);
    }

    @FXML
    public void initialize() {
        if (btnExportarPdf != null) btnExportarPdf.setDisable(true);
        if (btnImprimir != null) btnImprimir.setDisable(true);
        configurarTabla();
        listarVentas();
        if (boletaActual != null) mostrarBoleta();
    }

    private void configurarTabla() {
        if (tableVentas == null) return;
        if (colId != null) colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getIdVenta()));
        if (colFecha != null) colFecha.setCellValueFactory(c -> {
            var f = c.getValue().getFecha();
            String s = f == null ? "" : f.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return new SimpleStringProperty(s);
        });
        if (colSerie != null) colSerie.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getSerie())));
        if (colNumero != null) colNumero.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getNroComprobante())));
        if (colTotal != null) colTotal.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getTotal()));
        if (colCliente != null) colCliente.setCellValueFactory(c -> new SimpleStringProperty(nombreCliente(c.getValue().getIdCliente())));
        tableVentas.setItems(ventas);
        tableVentas.setOnMouseClicked(e -> { if (e.getClickCount() == 2) verComprobanteSeleccionado(); });
    }

    private String nombreCliente(Long idCliente) {
        try {
            if (idCliente == null || clienteService == null) return "";
            Cliente c = clienteService.buscarPorId(idCliente);
            return c != null ? nvl(c.getNombre()) : "";
        } catch (Exception e) { return ""; }
    }

    @FXML
    public void listarVentas() {
        try {
            if (ventaService == null || tableVentas == null) return;
            List<Venta> lista = ventaService.listarTodo();
            lista.sort((a,b) -> Long.compare(b.getIdVenta() == null ? 0 : b.getIdVenta(), a.getIdVenta() == null ? 0 : a.getIdVenta()));
            ventas.setAll(lista);
            // refrescar nombres cliente
            tableVentas.refresh();
        } catch (Exception e) {
            mostrarError("Error", "No se pudo listar ventas", e.getMessage());
        }
    }

    @FXML
    public void verComprobanteSeleccionado() {
        if (tableVentas == null) return;
        Venta v = tableVentas.getSelectionModel().getSelectedItem();
        if (v == null) { mostrarInfo("Seleccione una venta de la tabla."); return; }
        try {
            Comprobante comp = construirComprobanteDesdeVenta(v);
            pasarDatos(comp);
        } catch (Exception e) {
            mostrarError("Error", "No se pudo construir el comprobante", e.getMessage());
        }
    }

    private Comprobante construirComprobanteDesdeVenta(Venta v) {
        String clienteNombre = nombreCliente(v.getIdCliente());
        String clienteDoc = "";
        try { if (v.getIdCliente() != null && clienteService != null) { Cliente c = clienteService.buscarPorId(v.getIdCliente()); if (c != null) clienteDoc = nvl(c.getDniRuc()); } } catch (Exception ignored) {}
        String fecha = v.getFecha() == null ? "" : v.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        List<Comprobante.DetalleItem> items = new ArrayList<>();
        try {
            if (ventaDetalleService != null && v.getIdVenta() != null) {
                List<VentaDetalle> dets = ventaDetalleService.listarPorVenta(v.getIdVenta());
                for (VentaDetalle d : dets) {
                    String desc = "Producto " + (d.getIdProducto() == null ? "" : d.getIdProducto());
                    int cant = d.getCantidad() == null ? 0 : d.getCantidad().intValue();
                    double pu = d.getPrecio() == null ? 0 : d.getPrecio();
                    double sub = d.getSubtotal() == null ? 0 : d.getSubtotal();
                    items.add(new Comprobante.DetalleItem(desc, cant, pu, sub));
                }
            }
        } catch (Exception ignored) {}
        return new Comprobante(nvl(v.getSerie()), nvl(v.getNroComprobante()), fecha, clienteNombre, clienteDoc,
                v.getTotal() == null ? 0 : v.getTotal(), items);
    }

    @FXML
    public void eliminarVentaSeleccionada() {
        if (tableVentas == null) return;
        Venta v = tableVentas.getSelectionModel().getSelectedItem();
        if (v == null) { mostrarInfo("Seleccione una venta para eliminar."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar la venta seleccionada y sus detalles?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText("Confirmar eliminación");
        var res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) return;
        try {
            boolean ok = ventaService.eliminar(v.getIdVenta());
            if (ok) {
                boletaActual = null;
                vistaActual = null;
                if (scrollVisor != null) scrollVisor.setContent(new javafx.scene.layout.AnchorPane());
                if (btnExportarPdf != null) btnExportarPdf.setDisable(true);
                if (btnImprimir != null) btnImprimir.setDisable(true);
                listarVentas();
                mostrarInfo("Venta eliminada.");
            } else {
                mostrarError("Error", "No se pudo eliminar la venta", "Operación no completada");
            }
        } catch (Exception e) {
            mostrarError("Error", "No se pudo eliminar la venta", e.getMessage());
        }
    }

    public void pasarDatos(Comprobante boleta) {
        this.boletaActual = boleta;
        if (boletaActual != null) {
            mostrarBoleta();
        }
    }

    public void mostrarBoleta() {
        if (boletaActual == null || scrollVisor == null) return;

        if (btnExportarPdf != null) btnExportarPdf.setDisable(true);
        if (btnImprimir != null) btnImprimir.setDisable(true);

        Task<VBox> task = new Task<>() {
            @Override
            protected VBox call() {
                return new ComprobanteView(boletaActual).construir();
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            vistaActual = task.getValue();
            VBox wrapper = new VBox(vistaActual);
            wrapper.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 16;");
            wrapper.setAlignment(javafx.geometry.Pos.TOP_CENTER);
            scrollVisor.setContent(wrapper);
            if (btnExportarPdf != null) btnExportarPdf.setDisable(false);
            if (btnImprimir != null) btnImprimir.setDisable(false);
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            mostrarError("Error", "No se pudo generar la boleta",
                    task.getException() == null ? "" : task.getException().getMessage());
        }));

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onExportarPdf() {
        if (boletaActual == null) { mostrarInfo("Seleccione o genere un comprobante primero."); return; }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar Comprobante como PDF");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf"));
        chooser.setInitialFileName("boleta_" + safeFile(boletaActual.getSerie()) + "_" + safeFile(boletaActual.getNumero()) + ".pdf");

        File archivo = chooser.showSaveDialog(btnExportarPdf.getScene().getWindow());
        if (archivo == null) return;
        if (btnExportarPdf != null) btnExportarPdf.setDisable(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                pdfService.exportar(boletaActual, archivo.toPath());
                return null;
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            if (btnExportarPdf != null) btnExportarPdf.setDisable(false);
            try { if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(archivo); } catch (Exception ignored) {}
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            if (btnExportarPdf != null) btnExportarPdf.setDisable(false);
            mostrarError("Error al exportar PDF", "No se pudo generar el PDF",
                    task.getException() == null ? "" : task.getException().getMessage());
        }));

        Thread th = new Thread(task); th.setDaemon(true); th.start();
    }

    @FXML
    private void onImprimir() {
        if (vistaActual == null) { mostrarInfo("No hay comprobante para imprimir."); return; }

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            mostrarError("Sin impresora", "No se encontró ninguna impresora instalada", "Instala una impresora o usa 'Exportar PDF'.");
            return;
        }

        boolean imprimir = job.showPrintDialog(btnImprimir.getScene().getWindow());
        if (imprimir) {
            job.printPage(vistaActual);
            job.endJob();
        }
    }

    private void mostrarInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarError(String titulo, String header, String detalle) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(detalle);
        alert.showAndWait();
    }

    private String nvl(String s) { return s == null ? "" : s; }
    private String safeFile(String s) { return s == null || s.isBlank() ? "x" : s.replaceAll("[^a-zA-Z0-9_-]", "_"); }
}
