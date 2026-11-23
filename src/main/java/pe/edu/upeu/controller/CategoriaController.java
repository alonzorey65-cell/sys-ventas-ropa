package pe.edu.upeu.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pe.edu.upeu.components.Toast;
import pe.edu.upeu.model.Categoria;
import pe.edu.upeu.service.ICategoriaService;

import java.util.Optional;

public class CategoriaController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtFiltro;
    @FXML private Label lblMsg;
    @FXML private TableView<Categoria> tableView;
    @FXML private TableColumn<Categoria, Long> colId;
    @FXML private TableColumn<Categoria, String> colNombre;

    private final ICategoriaService service;
    private final ObservableList<Categoria> lista = FXCollections.observableArrayList();
    private Long idSel = null;

    public CategoriaController(ICategoriaService service) { this.service = service; }

    @FXML public void initialize() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        if (colNombre != null) colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        if (tableView != null) {
            tableView.setItems(lista);
            tableView.setRowFactory(tv -> { TableRow<Categoria> r = new TableRow<>(); r.setOnMouseClicked(e -> { if (e.getClickCount()==2 && !r.isEmpty()) editar(r.getItem()); }); return r; });
        }
        if (txtFiltro != null) txtFiltro.textProperty().addListener((o,a,b)-> filtrar(b));
        listar();
    }

    @FXML public void guardar() {
        String nombre = txtNombre != null ? txtNombre.getText().trim() : "";
        if (nombre.isEmpty()) { msg("El nombre es obligatorio.", true); marcar(txtNombre,true); return; }
        marcar(txtNombre,false);
        try {
            Categoria c = Categoria.builder().nombre(nombre).build();
            if (idSel == null) { service.guardar(c); msg("Categoría registrada.", false); }
            else { c.setIdCategoria(idSel); service.actualizar(idSel, c); msg("Categoría actualizada.", false); }
            limpiar(); listar();
        } catch (Exception e) { msg("Error: " + e.getMessage(), true); }
    }

    @FXML public void eliminar() {
        if (idSel == null) { msg("Seleccione una categoría para eliminar.", true); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar la categoría seleccionada?", ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> r = a.showAndWait();
        if (r.isEmpty() || r.get()!=ButtonType.OK) return;
        try { service.eliminar(idSel); msg("Categoría eliminada.", false); limpiar(); listar(); }
        catch (Exception e) { msg("Error al eliminar: " + e.getMessage(), true); }
    }

    @FXML public void listar() {
        try { lista.setAll(service.listarTodo()); filtrar(txtFiltro != null ? txtFiltro.getText() : ""); }
        catch (Exception e) { msg("Error al listar: " + e.getMessage(), true); }
    }

    @FXML public void limpiar() { idSel = null; if (txtNombre!=null) txtNombre.clear(); marcar(txtNombre,false); msg("Listo.", false); }

    private void editar(Categoria c) { idSel = c.getIdCategoria(); if (txtNombre!=null) txtNombre.setText(c.getNombre()); msg("Editando ID " + idSel, false); }

    private void filtrar(String q) {
        if (tableView == null) return;
        if (q == null || q.isBlank()) { tableView.setItems(lista); return; }
        String s = q.toLowerCase();
        tableView.setItems(lista.filtered(c -> c.getNombre()!=null && c.getNombre().toLowerCase().contains(s)));
    }

    private void msg(String m, boolean err) {
        if (lblMsg != null) { lblMsg.setText(m); lblMsg.setStyle(err?"-fx-text-fill:#b91c1c;":"-fx-text-fill:#166534;"); }
        try { if (txtNombre!=null && txtNombre.getScene()!=null) Toast.showToast((Stage)txtNombre.getScene().getWindow(), m, err?"#FCA5A5":"#86EFAC", 2000, 360, 50); } catch (Exception ignored) {}
    }
    private void marcar(TextField f, boolean err) { if (f!=null) f.setStyle(err?"-fx-border-color:#dc2626;-fx-border-width:1.5;":""); }
}
