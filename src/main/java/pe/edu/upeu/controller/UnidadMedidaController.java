package pe.edu.upeu.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pe.edu.upeu.components.Toast;
import pe.edu.upeu.model.UnidadMedida;
import pe.edu.upeu.service.IUnidadMedidaService;

import java.util.Optional;

public class UnidadMedidaController {
    @FXML private TextField txtNombre, txtAbreviatura, txtFiltro;
    @FXML private Label lblMsg;
    @FXML private TableView<UnidadMedida> tableView;
    @FXML private TableColumn<UnidadMedida, Long> colId;
    @FXML private TableColumn<UnidadMedida, String> colNombre, colAbrev;

    private final IUnidadMedidaService service;
    private final ObservableList<UnidadMedida> lista = FXCollections.observableArrayList();
    private Long idSel = null;

    public UnidadMedidaController(IUnidadMedidaService service) { this.service = service; }

    @FXML public void initialize() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idUnidMedida"));
        if (colNombre != null) colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        if (colAbrev != null) colAbrev.setCellValueFactory(new PropertyValueFactory<>("abreviatura"));
        if (tableView != null) {
            tableView.setItems(lista);
            tableView.setRowFactory(tv -> { TableRow<UnidadMedida> r = new TableRow<>(); r.setOnMouseClicked(e -> { if (e.getClickCount()==2 && !r.isEmpty()) editar(r.getItem()); }); return r; });
        }
        if (txtFiltro != null) txtFiltro.textProperty().addListener((o,a,b)-> filtrar(b));
        listar();
    }

    @FXML public void guardar() {
        String nombre = txtNombre != null ? txtNombre.getText().trim() : "";
        String abrev = txtAbreviatura != null ? txtAbreviatura.getText().trim() : "";
        if (nombre.isEmpty()) { msg("El nombre es obligatorio.", true); marcar(txtNombre,true); return; }
        marcar(txtNombre,false);
        try {
            UnidadMedida c = UnidadMedida.builder().nombre(nombre).abreviatura(abrev).build();
            if (idSel == null) { service.guardar(c); msg("Unidad registrada.", false); }
            else { c.setIdUnidMedida(idSel); service.actualizar(idSel, c); msg("Unidad actualizada.", false); }
            limpiar(); listar();
        } catch (Exception e) { msg("Error: " + e.getMessage(), true); }
    }

    @FXML public void eliminar() {
        if (idSel == null) { msg("Seleccione una unidad para eliminar.", true); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar la unidad seleccionada?", ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> r = a.showAndWait();
        if (r.isEmpty() || r.get()!=ButtonType.OK) return;
        try { service.eliminar(idSel); msg("Unidad eliminada.", false); limpiar(); listar(); }
        catch (Exception e) { msg("Error al eliminar: " + e.getMessage(), true); }
    }

    @FXML public void listar() {
        try { lista.setAll(service.listarTodo()); filtrar(txtFiltro != null ? txtFiltro.getText() : ""); }
        catch (Exception e) { msg("Error al listar: " + e.getMessage(), true); }
    }

    @FXML public void limpiar() { idSel = null; if (txtNombre!=null) txtNombre.clear(); if (txtAbreviatura!=null) txtAbreviatura.clear(); marcar(txtNombre,false); msg("Listo.", false); }

    private void editar(UnidadMedida c) { idSel = c.getIdUnidMedida(); if (txtNombre!=null) txtNombre.setText(c.getNombre()); if (txtAbreviatura!=null) txtAbreviatura.setText(c.getAbreviatura()); msg("Editando ID " + idSel, false); }

    private void filtrar(String q) {
        if (tableView == null) return;
        if (q == null || q.isBlank()) { tableView.setItems(lista); return; }
        String s = q.toLowerCase();
        tableView.setItems(lista.filtered(c -> (c.getNombre()!=null && c.getNombre().toLowerCase().contains(s)) || (c.getAbreviatura()!=null && c.getAbreviatura().toLowerCase().contains(s))));
    }

    private void msg(String m, boolean err) {
        if (lblMsg != null) { lblMsg.setText(m); lblMsg.setStyle(err?"-fx-text-fill:#b91c1c;":"-fx-text-fill:#166534;"); }
        try { if (txtNombre!=null && txtNombre.getScene()!=null) Toast.showToast((Stage)txtNombre.getScene().getWindow(), m, err?"#FCA5A5":"#86EFAC", 2000, 360, 50); } catch (Exception ignored) {}
    }
    private void marcar(TextField f, boolean err) { if (f!=null) f.setStyle(err?"-fx-border-color:#dc2626;-fx-border-width:1.5;":""); }
}
