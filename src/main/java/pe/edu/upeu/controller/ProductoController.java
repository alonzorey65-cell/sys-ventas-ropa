package pe.edu.upeu.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pe.edu.upeu.components.Toast;
import pe.edu.upeu.model.Producto;
import pe.edu.upeu.service.IProductoService;

import java.util.List;

/*
 * ========================= POO aplicada en la capa Controlador (UI) =========================
 * CLASE: ProductoController define el comportamiento del panel de productos.
 * OBJETOS: durante la ejecución se crean/usan objetos como Producto (modelo), listas, servicios,
 *        componentes JavaFX, etc. Ej.: Producto producto = Producto.builder()...build();
 * MÉTODOS: initialize(), listarProductos(), validarFormulario(), eliminarProducto() son métodos
 *        que ejecutan acciones (comportamientos) ante eventos de la interfaz.
 * ENCAPSULAMIENTO: campos @FXML y atributos private ocultan detalles internos del controlador.
 * ABSTRACCIÓN/POLIMORFISMO: depende de la interfaz IProductoService (no de una clase concreta fija),
 *        permitiendo cambiar la implementación del servicio sin modificar la lógica de UI.
 * CONEXIÓN (capas): Vista FXML -> ProductoController -> IProductoService/ProductoServiceImp
 *        -> ProductoRepository/AbstractJpaRepository -> Conexion/DatabaseConfig -> Neon DB.
 */
public class ProductoController { // CLASE controladora (capa presentación)

    @FXML private TextField txtNombreProducto;
    @FXML private TextField txtPUnit;
    @FXML private TextField txtPUnitOld;
    @FXML private TextField txtUtilidad;
    @FXML private TextField txtStock;
    @FXML private TextField txtStockOld;
    @FXML private TextField txtFiltroDato;

    @FXML private ComboBox<String> cbxTipoProducto;
    @FXML private ComboBox<Long> cbxCategoria;
    @FXML private ComboBox<Long> cbxUnidadMedida;
    @FXML private ComboBox<Long> cbxMarca;

    @FXML private Label lbnMsg;

    @FXML private TableView<Producto> tableView;
    @FXML private TableColumn<Producto, Long> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Double> colStock;
    @FXML private TableColumn<Producto, Long> colCategoria;

    private final IProductoService productoService;
    private final ObservableList<Producto> productosList = FXCollections.observableArrayList();
    private Long idProductoSeleccionado = null;

    public ProductoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    @FXML
    public void initialize() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        if (colNombre != null) colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        if (colPrecio != null) colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        if (colStock != null) colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        if (colCategoria != null) colCategoria.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));

        if (cbxCategoria != null) cbxCategoria.setItems(FXCollections.observableArrayList(1L));
        if (cbxMarca != null) cbxMarca.setItems(FXCollections.observableArrayList(1L));
        if (cbxUnidadMedida != null) cbxUnidadMedida.setItems(FXCollections.observableArrayList(1L));
        if (cbxCategoria != null) cbxCategoria.getSelectionModel().selectFirst();
        if (cbxMarca != null) cbxMarca.getSelectionModel().selectFirst();
        if (cbxUnidadMedida != null) cbxUnidadMedida.getSelectionModel().selectFirst();

        listarProductos();

        if (tableView != null) {
            tableView.setRowFactory(tv -> {
                TableRow<Producto> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        cargarDatosEnFormulario(row.getItem());
                    }
                });
                return row;
            });
        }

        if (txtFiltroDato != null) {
            txtFiltroDato.textProperty().addListener((obs, oldV, newV) -> filtrar(newV));
        }
    }

    private void filtrar(String texto) {
        if (tableView == null) return;
        if (texto == null || texto.isBlank()) {
            tableView.setItems(productosList);
            return;
        }
        String q = texto.toLowerCase().trim();
        FilteredList<Producto> filtered = new FilteredList<>(productosList, p ->
                p.getNombre() != null && p.getNombre().toLowerCase().contains(q));
        tableView.setItems(filtered);
    }

    @FXML
    public void validarFormulario(ActionEvent event) {
        try {
            String nombre = txtNombreProducto != null ? txtNombreProducto.getText() : "";
            if (nombre == null || nombre.trim().isEmpty() || txtPUnit.getText().isEmpty() || txtStock.getText().isEmpty()) {
                setMsg("Complete nombre, precio y stock", true);
                return;
            }

            double precio = Double.parseDouble(txtPUnit.getText().trim().replace(',', '.'));
            double stock = Double.parseDouble(txtStock.getText().trim().replace(',', '.'));
            if (precio < 0 || stock < 0) {
                setMsg("Precio y stock no pueden ser negativos", true);
                return;
            }

            Long idCat = cbxCategoria != null && cbxCategoria.getValue() != null ? cbxCategoria.getValue() : 1L;
            Long idMar = cbxMarca != null && cbxMarca.getValue() != null ? cbxMarca.getValue() : 1L;
            Long idMed = cbxUnidadMedida != null && cbxUnidadMedida.getValue() != null ? cbxUnidadMedida.getValue() : 1L;

            Producto producto = Producto.builder()
                    .nombre(nombre.trim())
                    .precio(precio)
                    .stock(stock)
                    .idCategoria(idCat)
                    .idMarca(idMar)
                    .idUnidMedida(idMed)
                    .build();

            if (idProductoSeleccionado == null) {
                Producto guardado = productoService.guardar(producto);
                if (guardado != null) {
                    setMsg("Producto registrado con éxito", false);
                    limpiarCampos();
                    listarProductos();
                } else {
                    setMsg("Error al registrar el producto", true);
                }
            } else {
                Producto actualizado = productoService.actualizar(idProductoSeleccionado, producto);
                if (actualizado != null) {
                    setMsg("Producto actualizado con éxito", false);
                    limpiarCampos();
                    listarProductos();
                } else {
                    setMsg("Error al actualizar el producto", true);
                }
            }
        } catch (NumberFormatException e) {
            setMsg("Precio y stock deben ser números válidos", true);
        } catch (Exception e) {
            setMsg("Error: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    @FXML
    public void eliminarProducto(ActionEvent event) {
        if (idProductoSeleccionado == null) {
            setMsg("Seleccione un producto (doble clic) para eliminar", true);
            return;
        }
        try {
            boolean ok = productoService.eliminar(idProductoSeleccionado);
            if (ok) {
                setMsg("Producto eliminado", false);
                limpiarCampos();
                listarProductos();
            } else {
                setMsg("No se pudo eliminar el producto", true);
            }
        } catch (Exception e) {
            setMsg("Error al eliminar: " + e.getMessage(), true);
        }
    }

    @FXML
    public void listarProductos() {
        try {
            List<Producto> lista = productoService.listarTodo();
            productosList.setAll(lista);
            if (tableView != null) {
                String q = txtFiltroDato != null ? txtFiltroDato.getText() : null;
                if (q == null || q.isBlank()) tableView.setItems(productosList);
                else filtrar(q);
            }
        } catch (Exception e) {
            setMsg("Error al listar productos: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void cargarDatosEnFormulario(Producto producto) {
        idProductoSeleccionado = producto.getIdProducto();
        if (txtNombreProducto != null) txtNombreProducto.setText(producto.getNombre());
        if (txtPUnit != null) txtPUnit.setText(String.valueOf(producto.getPrecio()));
        if (txtStock != null) txtStock.setText(String.valueOf(producto.getStock()));
        if (cbxCategoria != null && producto.getIdCategoria() != null) cbxCategoria.getSelectionModel().select(producto.getIdCategoria());
        if (cbxMarca != null && producto.getIdMarca() != null) cbxMarca.getSelectionModel().select(producto.getIdMarca());
        if (cbxUnidadMedida != null && producto.getIdUnidMedida() != null) cbxUnidadMedida.getSelectionModel().select(producto.getIdUnidMedida());
        setMsg("Editando producto ID " + producto.getIdProducto(), false);
    }

    @FXML
    public void limpiarCampos() {
        idProductoSeleccionado = null;
        if (txtNombreProducto != null) txtNombreProducto.clear();
        if (txtPUnit != null) txtPUnit.clear();
        if (txtPUnitOld != null) txtPUnitOld.clear();
        if (txtUtilidad != null) txtUtilidad.clear();
        if (txtStock != null) txtStock.clear();
        if (txtStockOld != null) txtStockOld.clear();
        if (lbnMsg != null) lbnMsg.setText("Estado: Listo");
    }

    private void setMsg(String msg, boolean error) {
        if (lbnMsg != null) lbnMsg.setText((error ? "Error: " : "Estado: ") + msg);
        try {
            if (txtNombreProducto != null && txtNombreProducto.getScene() != null) {
                Stage stage = (Stage) txtNombreProducto.getScene().getWindow();
                Toast.showToast(stage, msg, error ? "#FEA1B6" : "#B6FEC3", 2000, 350, 60);
            }
        } catch (Exception ignored) { }
    }
}
