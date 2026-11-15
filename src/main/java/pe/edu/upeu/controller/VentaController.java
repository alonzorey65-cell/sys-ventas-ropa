package pe.edu.upeu.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.components.Toast;
import pe.edu.upeu.config.AppContext;
import pe.edu.upeu.dto.SessionManager;
import pe.edu.upeu.dto.comprobante.Comprobante;
import pe.edu.upeu.model.Cliente;
import pe.edu.upeu.model.Producto;
import pe.edu.upeu.model.Venta;
import pe.edu.upeu.model.VentaDetalle;
import pe.edu.upeu.service.IClienteService;
import pe.edu.upeu.service.IProductoService;
import pe.edu.upeu.service.IVentaDetalleService;
import pe.edu.upeu.service.IVentaService;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
 * ========================= POO y CONEXIÓN (Panel Ventas/POS) =========================
 * CLASE: VentaController define la lógica de la interfaz de ventas.
 * OBJETOS: crea y manipula objetos Venta, VentaDetalle, Cliente, Producto, Comprobante, etc.
 * MÉTODOS: buscarClienteCdni(), guardarCliente(), registarPCarrito(), registrarVenta(), generarComprobante()
 *        representan comportamientos del proceso de venta.
 * ENCAPSULAMIENTO: atributos y estado interno (carrito, selecciones) son privados.
 * ABSTRACCIÓN/POLIMORFISMO: trabaja con interfaces de servicio (IProductoService, IClienteService, etc.)
 *        recibidas por constructor (inyección), desacoplándose de implementaciones concretas.
 * CONEXIÓN: Vista FXML -> VentaController -> Servicios -> Repositorios -> Conexion/DatabaseConfig -> Neon;
 *        también se conecta con ConsultaDNI (API apisperu) para autocompletar datos de cliente.
 */
public class VentaController { // CLASE controladora de ventas (POO + integración de capas)

    @FXML private AnchorPane miContenedor;

    // Cliente
    @FXML private TextField autocompCliente;
    @FXML private Button btnFormCliente;
    @FXML private TextField dniRuc;
    @FXML private TextField razonSocial;
    @FXML private TextField txtDireccion;
    @FXML private Button btnRegCliente;

    // Producto y carrito
    @FXML private TextField autocompProducto;
    @FXML private TextField nombreProducto;
    @FXML private TextField codigoPro;
    @FXML private TextField stockPro;
    @FXML private TextField cantidadPro;
    @FXML private TextField punitPro;
    @FXML private TextField preTPro;
    @FXML private Button btnRegCarrito;

    @FXML private TableView<CarritoItem> tableView;

    // Totales
    @FXML private TextField txtBaseImp;
    @FXML private TextField txtIgv;
    @FXML private TextField txtDescuento;
    @FXML private TextField txtImporteT;
    @FXML private Button btnRegVenta;
    @FXML private Button btnGenerarComprobante;

    private final IProductoService productoService;
    private final IClienteService clienteService;
    private final IVentaService ventaService;
    private final IVentaDetalleService ventaDetalleService;
    private final pe.edu.upeu.utils.ConsultaDNI consultaDNI = new pe.edu.upeu.utils.ConsultaDNI();

    private final ObservableList<CarritoItem> carrito = FXCollections.observableArrayList();
    private final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private Producto productoSeleccionado;
    private Cliente clienteSeleccionado;
    private Comprobante ultimoComprobante;

    public VentaController(IProductoService productoService,
                           IClienteService clienteService,
                           IVentaService ventaService,
                           IVentaDetalleService ventaDetalleService) {
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.ventaService = ventaService;
        this.ventaDetalleService = ventaDetalleService;
    }

    public VentaController() {
        this(null, null, null, null);
    }

    @FXML
    public void initialize() {
        try {
            configurarTabla();
            cargarProductos();
            cargarClientePorDefecto();
            if (txtDescuento != null) {
                txtDescuento.setText("0");
                txtDescuento.textProperty().addListener((o, a, b) -> actualizarTotales());
            }
            if (cantidadPro != null) cantidadPro.setText("1");

            // Búsqueda automática simple de cliente por DNI/RUC
            if (dniRuc != null) {
                dniRuc.focusedProperty().addListener((obs, oldV, newV) -> {
                    if (!newV) autoBuscarCliente(dniRuc.getText());
                });
                dniRuc.textProperty().addListener((obs, oldV, newV) -> {
                    if (newV != null) {
                        String t = newV.trim();
                        if (t.length() == 8 || t.length() == 11) autoBuscarCliente(t);
                    }
                });
            }
            if (autocompCliente != null) {
                autocompCliente.focusedProperty().addListener((obs, oldV, newV) -> {
                    if (!newV) autoBuscarCliente(autocompCliente.getText());
                });
            }
            // Búsqueda simple de producto al salir del campo
            if (autocompProducto != null) {
                autocompProducto.focusedProperty().addListener((obs, oldV, newV) -> {
                    if (!newV) buscarProductoPorTexto(autocompProducto.getText());
                });
                autocompProducto.setOnAction(e -> buscarProductoPorTexto(autocompProducto.getText()));
            }

            actualizarTotales();
        } catch (Exception e) {
            e.printStackTrace();
            toast("Error al inicializar ventas: " + e.getMessage(), true);
        }
    }

    private void autoBuscarCliente(String doc) {
        try {
            if (clienteService == null || doc == null) return;
            String t = doc.trim();
            if (t.isEmpty()) return;
            Cliente c = clienteService.buscarPorDniRuc(t);
            if (c != null) {
                seleccionarCliente(c);
            }
        } catch (Exception ignored) { }
    }

    private void configurarTabla() {
        if (tableView == null) return;
        tableView.getColumns().clear();

        TableColumn<CarritoItem, Long> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        cId.setPrefWidth(60);
        TableColumn<CarritoItem, String> cNom = new TableColumn<>("Producto");
        cNom.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        cNom.setPrefWidth(220);
        TableColumn<CarritoItem, Double> cCant = new TableColumn<>("Cant.");
        cCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        cCant.setPrefWidth(80);
        TableColumn<CarritoItem, Double> cPu = new TableColumn<>("P.Unit");
        cPu.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        cPu.setPrefWidth(90);
        TableColumn<CarritoItem, Double> cSub = new TableColumn<>("Subtotal");
        cSub.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        cSub.setPrefWidth(100);
        TableColumn<CarritoItem, Void> cAcc = new TableColumn<>("Acción");
        cAcc.setPrefWidth(90);
        cAcc.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Quitar");
            { btn.setOnAction(e -> { carrito.remove(getTableView().getItems().get(getIndex())); actualizarTotales(); }); }
            @Override protected void updateItem(Void item, boolean empty) { super.updateItem(item, empty); setGraphic(empty ? null : btn); }
        });

        tableView.getColumns().addAll(cId, cNom, cCant, cPu, cSub, cAcc);
        tableView.setItems(carrito);
    }

    private void cargarProductos() {
        if (productoService == null) return;
        productos.setAll(productoService.listarTodo());
    }

    private void cargarClientePorDefecto() {
        if (clienteService == null) return;
        Cliente c = clienteService.buscarPorDniRuc("00000000");
        if (c == null) {
            List<Cliente> all = clienteService.listarTodo();
            if (!all.isEmpty()) c = all.get(0);
        }
        if (c != null) seleccionarCliente(c);
    }

    private void seleccionarCliente(Cliente c) {
        clienteSeleccionado = c;
        if (dniRuc != null) dniRuc.setText(nvl(c.getDniRuc()));
        if (razonSocial != null) razonSocial.setText(nvl(c.getNombre()));
        if (txtDireccion != null) txtDireccion.setText(nvl(c.getDireccion()));
        if (autocompCliente != null) autocompCliente.setText(nvl(c.getDniRuc()));
    }

    @FXML
    private void buscarClienteCdni() {
        try {
            String criterio = autocompCliente != null ? autocompCliente.getText() : null;
            if ((criterio == null || criterio.isBlank()) && dniRuc != null) criterio = dniRuc.getText();
            if (criterio == null || criterio.isBlank()) {
                toast("Ingrese DNI/RUC para buscar", true);
                return;
            }
            String doc = criterio.trim();
            Cliente c = clienteService.buscarPorDniRuc(doc);
            if (c != null) {
                seleccionarCliente(c);
                toast("Cliente encontrado (sistema): " + c.getNombre(), false);
                return;
            }

            // No está en BD: buscar en API externa (apisperu)
            clienteSeleccionado = null;
            if (dniRuc != null) dniRuc.setText(doc);
            if (autocompCliente != null) autocompCliente.setText(doc);
            pe.edu.upeu.dto.PersonaDto p = consultaDNI != null ? consultaDNI.consultarDNI(doc) : null;
            String nombreApi = null;
            String dirApi = null;
            if (p != null) {
                nombreApi = (p.getRazonSocial() != null && !p.getRazonSocial().isBlank()) ? p.getRazonSocial() : p.getNombre();
                if ((nombreApi == null || nombreApi.isBlank()) && p.getApellidoPaterno() != null) {
                    nombreApi = String.join(" ",
                            nvl(p.getNombre()), nvl(p.getApellidoPaterno()), nvl(p.getApellidoMaterno())).trim();
                }
                dirApi = p.getDireccion();
            }
            if (nombreApi != null && !nombreApi.isBlank()) {
                if (razonSocial != null) razonSocial.setText(nombreApi.trim());
                if (txtDireccion != null && dirApi != null) txtDireccion.setText(dirApi);
                toast("Datos obtenidos desde API. Verifique y presione Add para guardar", false);
            } else {
                if (razonSocial != null) razonSocial.clear();
                if (txtDireccion != null) txtDireccion.clear();
                toast("No encontrado en sistema ni en API. Complete nombre y presione Add", true);
            }
        } catch (Exception e) {
            toast("Error al buscar cliente: " + e.getMessage(), true);
        }
    }

    @FXML
    private void guardarCliente() {
        try {
            String doc = dniRuc != null ? dniRuc.getText() : null;
            if ((doc == null || doc.isBlank()) && autocompCliente != null) doc = autocompCliente.getText();
            String nombre = razonSocial != null ? razonSocial.getText() : null;
            String dir = txtDireccion != null ? txtDireccion.getText() : null;
            if (doc == null || doc.isBlank() || nombre == null || nombre.isBlank()) {
                toast("Ingrese DNI/RUC y nombre del cliente", true);
                return;
            }
            Cliente existente = clienteService.buscarPorDniRuc(doc.trim());
            Cliente c = existente != null ? existente : new Cliente();
            c.setDniRuc(doc.trim());
            c.setNombre(nombre.trim());
            c.setDireccion(dir != null ? dir.trim() : "");
            if (c.getTelefono() == null) c.setTelefono("");
            Cliente guardado = existente == null ? clienteService.guardar(c) : clienteService.actualizar(existente.getIdCliente(), c);
            if (guardado != null) {
                seleccionarCliente(guardado);
                toast(existente == null ? "Cliente registrado" : "Cliente actualizado", false);
            } else {
                toast("No se pudo guardar el cliente", true);
            }
        } catch (Exception e) {
            toast("Error al guardar cliente: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void buscarProductoPorTexto(String texto) {
        if (texto == null || texto.isBlank() || productoService == null) return;
        String q = texto.trim().toLowerCase();
        if (productos.isEmpty()) cargarProductos();
        Producto p = productos.stream()
                .filter(x -> String.valueOf(x.getIdProducto()).equals(q)
                        || (x.getNombre() != null && x.getNombre().toLowerCase().contains(q)))
                .findFirst().orElse(null);
        if (p != null) seleccionarProducto(p);
    }

    private void seleccionarProducto(Producto p) {
        productoSeleccionado = p;
        if (autocompProducto != null) autocompProducto.setText(nvl(p.getNombre()));
        if (nombreProducto != null) nombreProducto.setText(nvl(p.getNombre()));
        if (codigoPro != null) codigoPro.setText(p.getIdProducto() != null ? String.valueOf(p.getIdProducto()) : "");
        if (stockPro != null) stockPro.setText(String.valueOf(p.getStock() != null ? p.getStock() : 0));
        if (punitPro != null) punitPro.setText(String.valueOf(p.getPrecio() != null ? p.getPrecio() : 0));
        calcularPT(null);
    }

    @FXML
    private void calcularPT(KeyEvent event) {
        try {
            if (cantidadPro == null || punitPro == null || preTPro == null) return;
            double cantidad = parseDouble(cantidadPro.getText(), 0);
            double punit = parseDouble(punitPro.getText(), 0);
            if (punit == 0 && productoSeleccionado != null && productoSeleccionado.getPrecio() != null) {
                punit = productoSeleccionado.getPrecio();
                punitPro.setText(String.valueOf(punit));
            }
            preTPro.setText(String.format(java.util.Locale.US, "%.2f", cantidad * punit));
        } catch (Exception ignored) { }
    }

    @FXML
    private void registarPCarrito() {
        try {
            if (productoSeleccionado == null && autocompProducto != null) {
                buscarProductoPorTexto(autocompProducto.getText());
            }
            if (productoSeleccionado == null) {
                toast("Seleccione/busque un producto válido", true);
                return;
            }
            double cantidad = parseDouble(cantidadPro != null ? cantidadPro.getText() : "0", 0);
            if (cantidad <= 0) { toast("Ingrese una cantidad válida", true); return; }
            double stock = productoSeleccionado.getStock() != null ? productoSeleccionado.getStock() : 0;
            // considerar lo ya agregado
            double ya = carrito.stream().filter(i -> i.getIdProducto().equals(productoSeleccionado.getIdProducto()))
                    .mapToDouble(CarritoItem::getCantidad).sum();
            if (cantidad + ya > stock) { toast("Stock insuficiente. Disponible: " + (stock - ya), true); return; }
            double pu = parseDouble(punitPro != null ? punitPro.getText() : "0", productoSeleccionado.getPrecio() != null ? productoSeleccionado.getPrecio() : 0);
            double subtotal = cantidad * pu;

            for (CarritoItem it : carrito) {
                if (it.getIdProducto().equals(productoSeleccionado.getIdProducto())) {
                    double nuevaCant = it.getCantidad() + cantidad;
                    it.setCantidad(nuevaCant);
                    it.setSubtotal(nuevaCant * it.getPrecioUnitario());
                    tableView.refresh();
                    actualizarTotales();
                    limpiarProducto();
                    return;
                }
            }
            carrito.add(new CarritoItem(productoSeleccionado.getIdProducto(), productoSeleccionado.getNombre(), cantidad, pu, subtotal));
            actualizarTotales();
            limpiarProducto();
        } catch (Exception e) {
            toast("Error al agregar al carrito: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void limpiarProducto() {
        productoSeleccionado = null;
        if (autocompProducto != null) autocompProducto.clear();
        if (nombreProducto != null) nombreProducto.clear();
        if (codigoPro != null) codigoPro.clear();
        if (stockPro != null) stockPro.clear();
        if (cantidadPro != null) cantidadPro.setText("1");
        if (punitPro != null) punitPro.clear();
        if (preTPro != null) preTPro.clear();
    }

    private void actualizarTotales() {
        double total = carrito.stream().mapToDouble(CarritoItem::getSubtotal).sum();
        double descuento = parseDouble(txtDescuento != null ? txtDescuento.getText() : "0", 0);
        if (descuento < 0) descuento = 0;
        if (descuento > total) descuento = total;
        double neto = total - descuento;
        double base = neto / 1.18;
        double igv = neto - base;
        if (txtBaseImp != null) txtBaseImp.setText(fmt(base));
        if (txtIgv != null) txtIgv.setText(fmt(igv));
        if (txtImporteT != null) txtImporteT.setText(fmt(neto));
    }

    @FXML
    private void registrarVenta() {
        try {
            if (carrito.isEmpty()) { toast("Agregue al menos un producto al carrito", true); return; }
            resolverCliente();
            if (clienteSeleccionado == null) { toast("Debe seleccionar o registrar un cliente", true); return; }

            Long userId = SessionManager.getInstance().getUserId();
            double total = parseDouble(txtImporteT != null ? txtImporteT.getText() : "0", 0);
            LocalDateTime fecha = LocalDateTime.now();
            String serie = "B001";
            String nro = String.valueOf(System.currentTimeMillis() % 100000000L);

            Venta venta = Venta.builder()
                    .fecha(fecha)
                    .idCliente(clienteSeleccionado.getIdCliente())
                    .idUsuario(userId)
                    .total(total)
                    .tipoComprobante("BOLETA")
                    .serie(serie)
                    .nroComprobante(nro)
                    .build();
            Venta guardada = ventaService.guardar(venta);
            if (guardada == null || guardada.getIdVenta() == null) { toast("No se pudo registrar la venta", true); return; }

            List<Comprobante.DetalleItem> itemsComp = new ArrayList<>();
            for (CarritoItem item : carrito) {
                VentaDetalle det = VentaDetalle.builder()
                        .idVenta(guardada.getIdVenta())
                        .idProducto(item.getIdProducto())
                        .cantidad(item.getCantidad())
                        .precio(item.getPrecioUnitario())
                        .subtotal(item.getSubtotal())
                        .build();
                ventaDetalleService.guardar(det);
                itemsComp.add(new Comprobante.DetalleItem(
                        item.getNombre(),
                        item.getCantidad() == null ? 0 : item.getCantidad().intValue(),
                        item.getPrecioUnitario() == null ? 0 : item.getPrecioUnitario(),
                        item.getSubtotal() == null ? 0 : item.getSubtotal()));

                Producto prod = productoService.buscarPorId(item.getIdProducto());
                if (prod != null) {
                    double nuevoStock = (prod.getStock() != null ? prod.getStock() : 0) - item.getCantidad();
                    if (nuevoStock < 0) nuevoStock = 0;
                    prod.setStock(nuevoStock);
                    productoService.actualizar(prod.getIdProducto(), prod);
                }
            }

            String fechaStr = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            ultimoComprobante = new Comprobante(serie, nro, fechaStr,
                    nvl(clienteSeleccionado.getNombre()), nvl(clienteSeleccionado.getDniRuc()), total, itemsComp);

            carrito.clear();
            cargarProductos();
            actualizarTotales();

            clienteSeleccionado = null;

            if (autocompCliente != null) autocompCliente.setText("");
            if (dniRuc != null) dniRuc.setText("");
            if (razonSocial != null) razonSocial.setText("");
            if (txtDireccion != null) txtDireccion.setText("");


            toast("Venta registrada: " + serie + "-" + nro, false);
            generarComprobante();
        } catch (Exception e) {
            toast("Error al registrar venta: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void resolverCliente() {
        if (clienteSeleccionado != null) return;
        String doc = dniRuc != null ? dniRuc.getText() : null;
        if ((doc == null || doc.isBlank()) && autocompCliente != null) doc = autocompCliente.getText();
        if (doc != null && !doc.isBlank()) {
            Cliente c = clienteService.buscarPorDniRuc(doc.trim());
            if (c != null) { seleccionarCliente(c); return; }
            // si hay nombre, registrar rápido
            String nombre = razonSocial != null ? razonSocial.getText() : null;
            if (nombre != null && !nombre.isBlank()) {
                guardarCliente();
                return;
            }
        }
        cargarClientePorDefecto();
    }

    @FXML
    private void generarComprobante() {
        try {
            if (ultimoComprobante == null) {
                if (carrito.isEmpty()) {
                    toast("No hay venta/comprobante para mostrar. Registre una venta primero.", true);
                    return;
                }
                // vista previa del carrito actual
                resolverCliente();
                List<Comprobante.DetalleItem> itemsComp = new ArrayList<>();
                for (CarritoItem item : carrito) {
                    itemsComp.add(new Comprobante.DetalleItem(
                            item.getNombre(),
                            item.getCantidad() == null ? 0 : item.getCantidad().intValue(),
                            item.getPrecioUnitario() == null ? 0 : item.getPrecioUnitario(),
                            item.getSubtotal() == null ? 0 : item.getSubtotal()));
                }
                double total = parseDouble(txtImporteT != null ? txtImporteT.getText() : "0", 0);
                String fechaStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                ultimoComprobante = new Comprobante("B001", "PREV", fechaStr,
                        clienteSeleccionado != null ? nvl(clienteSeleccionado.getNombre()) : "Público General",
                        clienteSeleccionado != null ? nvl(clienteSeleccionado.getDniRuc()) : "", total, itemsComp);
            }

            URL fxmlUrl = getClass().getResource("/view/main_comprobante.fxml");
            if (fxmlUrl == null) { toast("No se encontró la vista de comprobante", true); return; }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setControllerFactory(AppContext.getInstance()::getBean);
            var root = loader.load();
            Object ctrl = loader.getController();
            if (ctrl instanceof MainComprobanteController mcc) {
                mcc.pasarDatos(ultimoComprobante);
            }
            Stage stage = new Stage();
            stage.setTitle("Comprobante de Pago");
            stage.initModality(Modality.NONE);
            Scene scene = new Scene((javafx.scene.Parent) root, 700, 600);
            try { var css = getClass().getResource("/css/style.css"); if (css != null) scene.getStylesheets().add(css.toExternalForm()); } catch (Exception ignored) {}
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            toast("Error al generar comprobante: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private double parseDouble(String s, double def) {
        try {
            if (s == null || s.isBlank()) return def;
            return Double.parseDouble(s.replace(',', '.').trim());
        } catch (Exception e) { return def; }
    }
    private String fmt(double v) { return String.format(java.util.Locale.US, "%.2f", v); }
    private String nvl(String s) { return s == null ? "" : s; }

    private void toast(String msg, boolean error) {
        try {
            if (miContenedor != null && miContenedor.getScene() != null) {
                Stage stage = (Stage) miContenedor.getScene().getWindow();
                Toast.showToast(stage, msg, error ? "#FCA5A5" : "#86EFAC", 2200, 460, 60);
            }
        } catch (Exception ignored) { }
        System.out.println((error ? "[ERR] " : "[OK] ") + msg);
    }

    public static class CarritoItem {
        private Long idProducto;
        private String nombre;
        private Double cantidad;
        private Double precioUnitario;
        private Double subtotal;

        public CarritoItem(Long idProducto, String nombre, Double cantidad, Double precioUnitario, Double subtotal) {
            this.idProducto = idProducto;
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = subtotal;
        }
        public Long getIdProducto() { return idProducto; }
        public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public Double getCantidad() { return cantidad; }
        public void setCantidad(Double cantidad) { this.cantidad = cantidad; }
        public Double getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
        public Double getSubtotal() { return subtotal; }
        public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    }
}
