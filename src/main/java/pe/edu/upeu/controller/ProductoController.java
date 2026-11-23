package pe.edu.upeu.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import pe.edu.upeu.components.Toast;
import pe.edu.upeu.dto.ComboBoxOption;
import pe.edu.upeu.model.Categoria;
import pe.edu.upeu.model.Marca;
import pe.edu.upeu.model.Producto;
import pe.edu.upeu.model.UnidadMedida;
import pe.edu.upeu.service.ICategoriaService;
import pe.edu.upeu.service.IMarcaService;
import pe.edu.upeu.service.IProductoService;
import pe.edu.upeu.service.IUnidadMedidaService;

import java.util.List;
import java.util.Optional;

public class ProductoController {

    @FXML private TextField txtNombreProducto;
    @FXML private TextField txtPUnit;
    @FXML private TextField txtPUnitOld;
    @FXML private TextField txtUtilidad;
    @FXML private TextField txtStock;
    @FXML private TextField txtStockOld;
    @FXML private TextField txtFiltroDato;

    @FXML private ComboBox<String> cbxTipoProducto;
    @FXML private ComboBox<ComboBoxOption> cbxCategoria;
    @FXML private ComboBox<ComboBoxOption> cbxUnidadMedida;
    @FXML private ComboBox<ComboBoxOption> cbxMarca;

    @FXML private Label lbnMsg;

    @FXML private TableView<Producto> tableView;
    @FXML private TableColumn<Producto, Long> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Double> colStock;
    @FXML private TableColumn<Producto, Long> colCategoria;

    private final IProductoService productoService;
    private final ICategoriaService categoriaService;
    private final IMarcaService marcaService;
    private final IUnidadMedidaService unidadMedidaService;
    private final ObservableList<Producto> productosList = FXCollections.observableArrayList();
    private Long idProductoSeleccionado = null;

    public ProductoController(IProductoService productoService,
                              ICategoriaService categoriaService,
                              IMarcaService marcaService,
                              IUnidadMedidaService unidadMedidaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.marcaService = marcaService;
        this.unidadMedidaService = unidadMedidaService;
    }

    // Compatibilidad con constructor anterior
    public ProductoController(IProductoService productoService) {
        this(productoService, null, null, null);
    }

    @FXML
    public void initialize() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        if (colNombre != null) colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        if (colPrecio != null) colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        if (colStock != null) colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        if (colCategoria != null) colCategoria.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));

        cargarCombos();
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
        aplicarValidacionesCampos();
    }

    // Carga combos de categoría/marca/unidad desde servicios (BD) para el formulario producto
    private void cargarCombos() {
        try {
            if (cbxCategoria != null) {
                ObservableList<ComboBoxOption> cats = FXCollections.observableArrayList();
                if (categoriaService != null) {
                    for (Categoria c : categoriaService.listarTodo()) {
                        Long id = c.getIdCategoria();
                        if (id != null) cats.add(new ComboBoxOption(id.intValue(), nvl(c.getNombre(), "Categoría " + id)));
                    }
                }
                if (cats.isEmpty()) cats.add(new ComboBoxOption(1, "General"));
                cbxCategoria.setItems(cats);
                cbxCategoria.getSelectionModel().selectFirst();
            }
            if (cbxMarca != null) {
                ObservableList<ComboBoxOption> marcas = FXCollections.observableArrayList();
                if (marcaService != null) {
                    for (Marca m : marcaService.listarTodo()) {
                        Long id = m.getIdMarca();
                        if (id != null) marcas.add(new ComboBoxOption(id.intValue(), nvl(m.getNombre(), "Marca " + id)));
                    }
                }
                if (marcas.isEmpty()) marcas.add(new ComboBoxOption(1, "Genérica"));
                cbxMarca.setItems(marcas);
                cbxMarca.getSelectionModel().selectFirst();
            }
            if (cbxUnidadMedida != null) {
                ObservableList<ComboBoxOption> unds = FXCollections.observableArrayList();
                if (unidadMedidaService != null) {
                    for (UnidadMedida u : unidadMedidaService.listarTodo()) {
                        Long id = u.getIdUnidMedida();
                        if (id != null) unds.add(new ComboBoxOption(id.intValue(), nvl(u.getNombre(), "Unidad " + id)));
                    }
                }
                if (unds.isEmpty()) unds.add(new ComboBoxOption(1, "Unidad"));
                cbxUnidadMedida.setItems(unds);
                cbxUnidadMedida.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            setMsg("No se pudieron cargar combos: " + e.getMessage(), true);
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

    // Valida formulario y guarda/actualiza producto: UI -> ProductoController -> IProductoService -> ProductoRepository -> BD
    @FXML
    public void validarFormulario(ActionEvent event) {
        try {
            limpiarEstilos();

            String nombre = txtNombreProducto != null ? txtNombreProducto.getText().trim() : "";
            String precioTxt = txtPUnit != null ? txtPUnit.getText().trim() : "";
            String stockTxt = txtStock != null ? txtStock.getText().trim() : "";

            if (nombre.isEmpty()) {
                marcarError(txtNombreProducto);
                setMsg("El nombre del producto es obligatorio.", true);
                return;
            }
            if (nombre.length() < 3) {
                marcarError(txtNombreProducto);
                setMsg("El nombre debe tener al menos 3 caracteres.", true);
                return;
            }
            if (precioTxt.isEmpty()) {
                marcarError(txtPUnit);
                setMsg("El precio es obligatorio.", true);
                return;
            }
            if (stockTxt.isEmpty()) {
                marcarError(txtStock);
                setMsg("El stock es obligatorio.", true);
                return;
            }

            double precio;
            double stock;
            try {
                precio = Double.parseDouble(precioTxt.replace(',', '.'));
            } catch (NumberFormatException e) {
                marcarError(txtPUnit);
                setMsg("El precio debe ser un número válido (ej: 45.50).", true);
                return;
            }
            try {
                stock = Double.parseDouble(stockTxt.replace(',', '.'));
            } catch (NumberFormatException e) {
                marcarError(txtStock);
                setMsg("El stock debe ser un número válido.", true);
                return;
            }
            if (precio <= 0) {
                marcarError(txtPUnit);
                setMsg("El precio debe ser mayor a 0.", true);
                return;
            }
            if (stock < 0) {
                marcarError(txtStock);
                setMsg("El stock no puede ser negativo.", true);
                return;
            }

            Long idCat = getComboId(cbxCategoria, 1L);
            Long idMar = getComboId(cbxMarca, 1L);
            Long idMed = getComboId(cbxUnidadMedida, 1L);

            Producto producto = Producto.builder()
                    .nombre(nombre)
                    .precio(precio)
                    .stock(stock)
                    .idCategoria(idCat)
                    .idMarca(idMar)
                    .idUnidMedida(idMed)
                    .build();

            if (idProductoSeleccionado == null) {
                Producto guardado = productoService.guardar(producto);
                if (guardado != null) {
                    setMsg("Producto registrado correctamente.", false);
                    limpiarCampos();
                    listarProductos();
                } else {
                    setMsg("No se pudo registrar el producto.", true);
                }
            } else {
                Producto actualizado = productoService.actualizar(idProductoSeleccionado, producto);
                if (actualizado != null) {
                    setMsg("Producto actualizado correctamente.", false);
                    limpiarCampos();
                    listarProductos();
                } else {
                    setMsg("No se pudo actualizar el producto.", true);
                }
            }
        } catch (Exception e) {
            setMsg("Error al guardar: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    // Elimina producto seleccionado tras confirmación: UI -> IProductoService.eliminar -> BD
    @FXML
    public void eliminarProducto(ActionEvent event) {
        if (idProductoSeleccionado == null) {
            setMsg("Seleccione un producto (doble clic) para eliminar.", true);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("Eliminar producto");
        confirm.setContentText("¿Desea eliminar el producto seleccionado? Esta acción no se puede deshacer.");
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) return;

        try {
            boolean ok = productoService.eliminar(idProductoSeleccionado);
            if (ok) {
                setMsg("Producto eliminado correctamente.", false);
                limpiarCampos();
                listarProductos();
            } else {
                setMsg("No se pudo eliminar el producto.", true);
            }
        } catch (Exception e) {
            setMsg("Error al eliminar: " + e.getMessage(), true);
        }
    }

    // Lista productos desde servicio y refresca tabla (aplica filtro si existe)
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

    // Doble clic en tabla: carga datos del producto al formulario para edición
    private void cargarDatosEnFormulario(Producto producto) {
        idProductoSeleccionado = producto.getIdProducto();
        if (txtNombreProducto != null) txtNombreProducto.setText(nvl(producto.getNombre(), ""));
        if (txtPUnit != null) txtPUnit.setText(producto.getPrecio() != null ? String.valueOf(producto.getPrecio()) : "");
        if (txtStock != null) txtStock.setText(producto.getStock() != null ? String.valueOf(producto.getStock()) : "");
        seleccionarComboPorId(cbxCategoria, producto.getIdCategoria());
        seleccionarComboPorId(cbxMarca, producto.getIdMarca());
        seleccionarComboPorId(cbxUnidadMedida, producto.getIdUnidMedida());
        setMsg("Editando producto ID " + producto.getIdProducto() + ". Modifique y presione Guardar.", false);
    }

    // Limpia formulario y vuelve a modo "nuevo producto"
    @FXML
    public void limpiarCampos() {
        idProductoSeleccionado = null;
        if (txtNombreProducto != null) txtNombreProducto.clear();
        if (txtPUnit != null) txtPUnit.clear();
        if (txtPUnitOld != null) txtPUnitOld.clear();
        if (txtUtilidad != null) txtUtilidad.clear();
        if (txtStock != null) txtStock.clear();
        if (txtStockOld != null) txtStockOld.clear();
        if (txtFiltroDato != null) { /* no limpiar filtro */ }
        if (cbxCategoria != null) cbxCategoria.getSelectionModel().selectFirst();
        if (cbxMarca != null) cbxMarca.getSelectionModel().selectFirst();
        if (cbxUnidadMedida != null) cbxUnidadMedida.getSelectionModel().selectFirst();
        limpiarEstilos();
        if (lbnMsg != null) lbnMsg.setText("Estado: Listo para registrar un nuevo producto.");
    }

    private void setMsg(String msg, boolean error) {
        if (lbnMsg != null) {
            lbnMsg.setText((error ? "Error: " : "Estado: ") + msg);
            lbnMsg.setStyle(error ? "-fx-text-fill: #b91c1c;" : "-fx-text-fill: #166534;");
        }
        try {
            if (txtNombreProducto != null && txtNombreProducto.getScene() != null) {
                Stage stage = (Stage) txtNombreProducto.getScene().getWindow();
                Toast.showToast(stage, msg, error ? "#FCA5A5" : "#86EFAC", 2200, 420, 55);
            }
        } catch (Exception ignored) { }
    }

    private void marcarError(TextField field) {
        if (field != null) field.setStyle("-fx-border-color: #dc2626; -fx-border-width: 1.5; -fx-background-color: #fff5f5;");
    }

    private void limpiarEstilos() {
        String ok = "";
        if (txtNombreProducto != null) txtNombreProducto.setStyle(ok);
        if (txtPUnit != null) txtPUnit.setStyle(ok);
        if (txtStock != null) txtStock.setStyle(ok);
    }

    private Long getComboId(ComboBox<ComboBoxOption> combo, Long def) {
        if (combo == null || combo.getValue() == null) return def;
        return (long) combo.getValue().getId();
    }

    private void seleccionarComboPorId(ComboBox<ComboBoxOption> combo, Long id) {
        if (combo == null || id == null) return;
        for (ComboBoxOption opt : combo.getItems()) {
            if (opt.getId() == id.intValue()) {
                combo.getSelectionModel().select(opt);
                return;
            }
        }
    }

    private void aplicarValidacionesCampos() {
        limitarDecimal(txtPUnit);
        limitarDecimal(txtStock);
        if (txtNombreProducto != null) {
            txtNombreProducto.textProperty().addListener((obs, old, nv) -> {
                if (nv != null && nv.length() > 100) txtNombreProducto.setText(old);
            });
        }
    }

    private void limitarDecimal(TextField field) {
        if (field == null) return;
        field.setTextFormatter(new TextFormatter<>(change -> {
            String n = change.getControlNewText();
            if (n.isEmpty()) return change;
            return n.matches("\\d{0,10}([\\.,]\\d{0,2})?") ? change : null;
        }));
    }

    private String nvl(String s, String def) { return s == null || s.isBlank() ? def : s; }
}
