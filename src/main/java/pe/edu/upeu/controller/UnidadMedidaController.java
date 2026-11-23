package pe.edu.upeu.controller; // Capa controlador

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pe.edu.upeu.components.Toast;
import pe.edu.upeu.model.UnidadMedida; // Modelo
import pe.edu.upeu.service.IUnidadMedidaService; // Servicio

import java.util.Optional;

// CRUD Unidades de Medida: main_unidadmedida.fxml -> UnidadMedidaController -> IUnidadMedidaService -> BD
public class UnidadMedidaController {
    @FXML private TextField txtNombre, txtAbreviatura, txtFiltro; // Inputs formulario/filtro
    @FXML private Label lblMsg; // Mensaje UI
    @FXML private TableView<UnidadMedida> tableView; // Tabla
    @FXML private TableColumn<UnidadMedida, Long> colId; // idUnidMedida
    @FXML private TableColumn<UnidadMedida, String> colNombre, colAbrev; // nombre y abreviatura

    private final IUnidadMedidaService service; // Servicio inyectado
    private final ObservableList<UnidadMedida> lista = FXCollections.observableArrayList(); // Fuente tabla
    private Long idSel = null; // ID seleccionado

    public UnidadMedidaController(IUnidadMedidaService service) { this.service = service; } // DI

    @FXML public void initialize() { // Configura tabla y listeners
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idUnidMedida"));
        if (colNombre != null) colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        if (colAbrev != null) colAbrev.setCellValueFactory(new PropertyValueFactory<>("abreviatura"));
        if (tableView != null) {
            tableView.setItems(lista);
            tableView.setRowFactory(tv -> { TableRow<UnidadMedida> r = new TableRow<>(); r.setOnMouseClicked(e -> { if (e.getClickCount()==2 && !r.isEmpty()) editar(r.getItem()); }); return r; }); // doble clic -> editar
        }
        if (txtFiltro != null) txtFiltro.textProperty().addListener((o,a,b)-> filtrar(b));
        listar(); // carga inicial
    }

    @FXML public void guardar() { // Crea o actualiza unidad
        String nombre = txtNombre != null ? txtNombre.getText().trim() : ""; // lee nombre
        String abrev = txtAbreviatura != null ? txtAbreviatura.getText().trim() : ""; // lee abreviatura
        if (nombre.isEmpty()) { msg("El nombre es obligatorio.", true); marcar(txtNombre,true); return; } // valida
        marcar(txtNombre,false);
        try {
            UnidadMedida c = UnidadMedida.builder().nombre(nombre).abreviatura(abrev).build(); // crea modelo
            if (idSel == null) { service.guardar(c); msg("Unidad registrada.", false); } // INSERT
            else { c.setIdUnidMedida(idSel); service.actualizar(idSel, c); msg("Unidad actualizada.", false); } // UPDATE
            limpiar(); listar(); // refresca
        } catch (Exception e) { msg("Error: " + e.getMessage(), true); }
    }

    @FXML public void eliminar() { // Elimina unidad seleccionada
        if (idSel == null) { msg("Seleccione una unidad para eliminar.", true); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar la unidad seleccionada?", ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> r = a.showAndWait();
        if (r.isEmpty() || r.get()!=ButtonType.OK) return;
        try { service.eliminar(idSel); msg("Unidad eliminada.", false); limpiar(); listar(); }
        catch (Exception e) { msg("Error al eliminar: " + e.getMessage(), true); }
    }

    @FXML public void listar() { // Carga desde BD
        try { lista.setAll(service.listarTodo()); filtrar(txtFiltro != null ? txtFiltro.getText() : ""); }
        catch (Exception e) { msg("Error al listar: " + e.getMessage(), true); }
    }

    @FXML public void limpiar() { // Limpia formulario
        idSel = null; if (txtNombre!=null) txtNombre.clear(); if (txtAbreviatura!=null) txtAbreviatura.clear(); marcar(txtNombre,false); msg("Listo.", false);
    }

    private void editar(UnidadMedida c) { // Carga registro en formulario
        idSel = c.getIdUnidMedida(); if (txtNombre!=null) txtNombre.setText(c.getNombre()); if (txtAbreviatura!=null) txtAbreviatura.setText(c.getAbreviatura()); msg("Editando ID " + idSel, false);
    }

    private void filtrar(String q) { // Filtra por nombre o abreviatura
        if (tableView == null) return;
        if (q == null || q.isBlank()) { tableView.setItems(lista); return; }
        String s = q.toLowerCase();
        tableView.setItems(lista.filtered(c -> (c.getNombre()!=null && c.getNombre().toLowerCase().contains(s)) || (c.getAbreviatura()!=null && c.getAbreviatura().toLowerCase().contains(s))));
    }

    private void msg(String m, boolean err) { // Mensaje visual
        if (lblMsg != null) { lblMsg.setText(m); lblMsg.setStyle(err?"-fx-text-fill:#b91c1c;":"-fx-text-fill:#166534;"); }
        try { if (txtNombre!=null && txtNombre.getScene()!=null) Toast.showToast((Stage)txtNombre.getScene().getWindow(), m, err?"#FCA5A5":"#86EFAC", 2000, 360, 50); } catch (Exception ignored) {}
    }
    private void marcar(TextField f, boolean err) { // Resalta campo inválido
        if (f!=null) f.setStyle(err?"-fx-border-color:#dc2626;-fx-border-width:1.5;":"");
    }
}