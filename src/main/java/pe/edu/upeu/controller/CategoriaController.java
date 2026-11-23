package pe.edu.upeu.controller; // Paquete del controlador (capa UI/MVC)

import javafx.collections.FXCollections; // Crea listas observables para JavaFX
import javafx.collections.ObservableList; // Lista observable enlazada a TableView
import javafx.fxml.FXML; // Marca campos/métodos conectados al FXML
import javafx.scene.control.*; // Controles JavaFX
import javafx.scene.control.cell.PropertyValueFactory; // Mapea columnas a propiedades del modelo
import javafx.stage.Stage; // Ventana para Toast
import pe.edu.upeu.components.Toast; // Notificación visual
import pe.edu.upeu.model.Categoria; // Modelo de dominio
import pe.edu.upeu.service.ICategoriaService; // Abstracción del servicio (inyectada)

import java.util.Optional; // Manejo de respuesta de Alert

// Controlador CRUD de Categorías: main_categoria.fxml -> CategoriaController -> ICategoriaService -> BD
public class CategoriaController {
    @FXML private TextField txtNombre; // Input nombre categoría (desde FXML)
    @FXML private TextField txtFiltro; // Input filtro de búsqueda
    @FXML private Label lblMsg; // Label de mensajes
    @FXML private TableView<Categoria> tableView; // Tabla de categorías
    @FXML private TableColumn<Categoria, Long> colId; // Columna ID
    @FXML private TableColumn<Categoria, String> colNombre; // Columna nombre

    private final ICategoriaService service; // Dependencia inyectada: lógica de negocio de categoría
    private final ObservableList<Categoria> lista = FXCollections.observableArrayList(); // Lista base en memoria para tabla/filtro
    private Long idSel = null; // ID seleccionado; null=nuevo, valor=edición

    // Constructor con inyección de dependencias (AppContext lo usa)
    public CategoriaController(ICategoriaService service) { this.service = service; }

    @FXML public void initialize() { // Se ejecuta al cargar el FXML
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idCategoria")); // Columna -> getIdCategoria()
        if (colNombre != null) colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre")); // Columna -> getNombre()
        if (tableView != null) {
            tableView.setItems(lista); // Enlaza tabla a lista observable
            tableView.setRowFactory(tv -> { // Doble clic en fila -> cargar para editar
                TableRow<Categoria> r = new TableRow<>();
                r.setOnMouseClicked(e -> { if (e.getClickCount()==2 && !r.isEmpty()) editar(r.getItem()); });
                return r;
            });
        }
        if (txtFiltro != null) txtFiltro.textProperty().addListener((o,a,b)-> filtrar(b)); // Filtra mientras escribe
        listar(); // Carga inicial desde servicio/BD
    }

    @FXML public void guardar() { // Botón Guardar: crea o actualiza
        String nombre = txtNombre != null ? txtNombre.getText().trim() : ""; // Lee nombre desde UI
        if (nombre.isEmpty()) { msg("El nombre es obligatorio.", true); marcar(txtNombre,true); return; } // Validación
        marcar(txtNombre,false); // Limpia marca de error
        try {
            Categoria c = Categoria.builder().nombre(nombre).build(); // Crea objeto modelo
            if (idSel == null) { service.guardar(c); msg("Categoría registrada.", false); } // Nuevo -> service.guardar -> BD
            else { c.setIdCategoria(idSel); service.actualizar(idSel, c); msg("Categoría actualizada.", false); } // Editar -> service.actualizar
            limpiar(); listar(); // Limpia formulario y recarga tabla
        } catch (Exception e) { msg("Error: " + e.getMessage(), true); } // Muestra error
    }

    @FXML public void eliminar() { // Botón Eliminar
        if (idSel == null) { msg("Seleccione una categoría para eliminar.", true); return; } // Requiere selección
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar la categoría seleccionada?", ButtonType.OK, ButtonType.CANCEL); // Confirmación
        Optional<ButtonType> r = a.showAndWait(); // Espera respuesta usuario
        if (r.isEmpty() || r.get()!=ButtonType.OK) return; // Cancela si no confirma
        try { service.eliminar(idSel); msg("Categoría eliminada.", false); limpiar(); listar(); } // Elimina en BD y refresca
        catch (Exception e) { msg("Error al eliminar: " + e.getMessage(), true); }
    }

    @FXML public void listar() { // Carga tabla desde servicio
        try { lista.setAll(service.listarTodo()); filtrar(txtFiltro != null ? txtFiltro.getText() : ""); } // BD -> lista -> tabla
        catch (Exception e) { msg("Error al listar: " + e.getMessage(), true); }
    }

    @FXML public void limpiar() { // Limpia formulario/estado
        idSel = null; // Vuelve a modo "nuevo"
        if (txtNombre!=null) txtNombre.clear(); // Limpia input
        marcar(txtNombre,false); // Quita borde error
        msg("Listo.", false); // Mensaje estado
    }

    private void editar(Categoria c) { // Carga fila seleccionada al formulario
        idSel = c.getIdCategoria(); // Guarda ID para actualizar
        if (txtNombre!=null) txtNombre.setText(c.getNombre()); // Pone nombre en input
        msg("Editando ID " + idSel, false); // Informa modo edición
    }

    private void filtrar(String q) { // Filtra lista en memoria por nombre
        if (tableView == null) return;
        if (q == null || q.isBlank()) { tableView.setItems(lista); return; } // Sin filtro: muestra todo
        String s = q.toLowerCase();
        tableView.setItems(lista.filtered(c -> c.getNombre()!=null && c.getNombre().toLowerCase().contains(s))); // Filtro por nombre
    }

    private void msg(String m, boolean err) { // Muestra mensaje en label y Toast
        if (lblMsg != null) { lblMsg.setText(m); lblMsg.setStyle(err?"-fx-text-fill:#b91c1c;":"-fx-text-fill:#166534;"); }
        try { if (txtNombre!=null && txtNombre.getScene()!=null) Toast.showToast((Stage)txtNombre.getScene().getWindow(), m, err?"#FCA5A5":"#86EFAC", 2000, 360, 50); } catch (Exception ignored) {}
    }
    private void marcar(TextField f, boolean err) { // Marca/desmarca borde rojo de validación
        if (f!=null) f.setStyle(err?"-fx-border-color:#dc2626;-fx-border-width:1.5;":"");
    }
}