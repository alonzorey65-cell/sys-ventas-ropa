package pe.edu.upeu.controller; // Paquete controlador

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pe.edu.upeu.components.Toast;
import pe.edu.upeu.model.Marca; // Modelo Marca
import pe.edu.upeu.service.IMarcaService; // Servicio inyectado

import java.util.Optional;

// CRUD de Marcas: main_marca.fxml -> MarcaController -> IMarcaService -> MarcaRepository -> BD
public class MarcaController {
    @FXML private TextField txtNombre; // Nombre marca
    @FXML private TextField txtFiltro; // Filtro
    @FXML private Label lblMsg; // Mensaje
    @FXML private TableView<Marca> tableView; // Tabla
    @FXML private TableColumn<Marca, Long> colId; // Columna idMarca
    @FXML private TableColumn<Marca, String> colNombre; // Columna nombre

    private final IMarcaService service; // Dependencia de negocio
    private final ObservableList<Marca> lista = FXCollections.observableArrayList(); // Datos tabla
    private Long idSel = null; // ID seleccionado para editar/eliminar

    public MarcaController(IMarcaService service) { this.service = service; } // DI desde AppContext

    @FXML public void initialize() { // Inicializa tabla y eventos
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idMarca")); // getIdMarca()
        if (colNombre != null) colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre")); // getNombre()
        if (tableView != null) {
            tableView.setItems(lista); // enlaza datos
            tableView.setRowFactory(tv -> { TableRow<Marca> r = new TableRow<>(); r.setOnMouseClicked(e -> { if (e.getClickCount()==2 && !r.isEmpty()) editar(r.getItem()); }); return r; }); // doble clic edita
        }
        if (txtFiltro != null) txtFiltro.textProperty().addListener((o,a,b)-> filtrar(b)); // filtro en vivo
        listar(); // carga inicial
    }

    @FXML public void guardar() { // Guardar/actualizar marca
        String nombre = txtNombre != null ? txtNombre.getText().trim() : "";
        if (nombre.isEmpty()) { msg("El nombre es obligatorio.", true); marcar(txtNombre,true); return; }
        marcar(txtNombre,false);
        try {
            Marca c = Marca.builder().nombre(nombre).build(); // crea objeto Marca
            if (idSel == null) { service.guardar(c); msg("Marca registrada.", false); } // INSERT vía servicio
            else { c.setIdMarca(idSel); service.actualizar(idSel, c); msg("Marca actualizada.", false); } // UPDATE vía servicio
            limpiar(); listar(); // refresca UI
        } catch (Exception e) { msg("Error: " + e.getMessage(), true); }
    }

    @FXML public void eliminar() { // Elimina marca seleccionada
        if (idSel == null) { msg("Seleccione una marca para eliminar.", true); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar la marca seleccionada?", ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> r = a.showAndWait();
        if (r.isEmpty() || r.get()!=ButtonType.OK) return;
        try { service.eliminar(idSel); msg("Marca eliminada.", false); limpiar(); listar(); } // DELETE vía servicio
        catch (Exception e) { msg("Error al eliminar: " + e.getMessage(), true); }
    }

    @FXML public void listar() { // Lee marcas desde BD
        try { lista.setAll(service.listarTodo()); filtrar(txtFiltro != null ? txtFiltro.getText() : ""); }
        catch (Exception e) { msg("Error al listar: " + e.getMessage(), true); }
    }

    @FXML public void limpiar() { // Resetea formulario
        idSel = null; if (txtNombre!=null) txtNombre.clear(); marcar(txtNombre,false); msg("Listo.", false);
    }

    private void editar(Marca c) { // Carga marca al formulario
        idSel = c.getIdMarca(); if (txtNombre!=null) txtNombre.setText(c.getNombre()); msg("Editando ID " + idSel, false);
    }

    private void filtrar(String q) { // Filtro por nombre en memoria
        if (tableView == null) return;
        if (q == null || q.isBlank()) { tableView.setItems(lista); return; }
        String s = q.toLowerCase();
        tableView.setItems(lista.filtered(c -> c.getNombre()!=null && c.getNombre().toLowerCase().contains(s)));
    }

    private void msg(String m, boolean err) { // Mensaje label + Toast
        if (lblMsg != null) { lblMsg.setText(m); lblMsg.setStyle(err?"-fx-text-fill:#b91c1c;":"-fx-text-fill:#166534;"); }
        try { if (txtNombre!=null && txtNombre.getScene()!=null) Toast.showToast((Stage)txtNombre.getScene().getWindow(), m, err?"#FCA5A5":"#86EFAC", 2000, 360, 50); } catch (Exception ignored) {}
    }
    private void marcar(TextField f, boolean err) { // Borde rojo de error
        if (f!=null) f.setStyle(err?"-fx-border-color:#dc2626;-fx-border-width:1.5;":"");
    }
}