package pe.edu.upeu.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pe.edu.upeu.components.Toast;
import pe.edu.upeu.dto.ComboBoxOption;
import pe.edu.upeu.model.Usuario;
import pe.edu.upeu.service.IUsuarioService;

import java.util.Optional;

public class UsuarioController {
    @FXML private TextField txtUsuario, txtClave, txtFiltro;
    @FXML private ComboBox<ComboBoxOption> cbxPerfil;
    @FXML private Label lblMsg;
    @FXML private TableView<Usuario> tableView;
    @FXML private TableColumn<Usuario, Long> colId, colPerfil;
    @FXML private TableColumn<Usuario, String> colUsuario;

    private final IUsuarioService service;
    private final ObservableList<Usuario> lista = FXCollections.observableArrayList();
    private Long idSel = null;

    public UsuarioController(IUsuarioService service) { this.service = service; }

    @FXML public void initialize() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        if (colUsuario != null) colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        if (colPerfil != null) colPerfil.setCellValueFactory(new PropertyValueFactory<>("idPerfil"));
        if (cbxPerfil != null) {
            cbxPerfil.setItems(FXCollections.observableArrayList(new ComboBoxOption(1, "ADMINISTRADOR"), new ComboBoxOption(2, "VENDEDOR")));
            cbxPerfil.getSelectionModel().selectFirst();
        }
        if (tableView != null) { tableView.setItems(lista); tableView.setRowFactory(tv -> { TableRow<Usuario> r = new TableRow<>(); r.setOnMouseClicked(e -> { if (e.getClickCount()==2 && !r.isEmpty()) editar(r.getItem()); }); return r; }); }
        if (txtFiltro != null) txtFiltro.textProperty().addListener((o,a,b)-> filtrar(b));
        listar();
    }

    @FXML public void guardar() {
        String u = val(txtUsuario), c = val(txtClave);
        limpiarMarcas();
        if (u.isEmpty()) { marcar(txtUsuario,true); msg("El usuario es obligatorio.", true); return; }
        if (c.isEmpty() || c.length() < 3) { marcar(txtClave,true); msg("La clave debe tener al menos 3 caracteres.", true); return; }
        Long perfil = (cbxPerfil != null && cbxPerfil.getValue() != null) ? (long) cbxPerfil.getValue().getId() : 1L;
        try {
            Usuario ent = Usuario.builder().usuario(u).clave(c).idPerfil(perfil).build();
            if (idSel == null) { service.guardar(ent); msg("Usuario registrado.", false); }
            else { ent.setIdUsuario(idSel); service.actualizar(idSel, ent); msg("Usuario actualizado.", false); }
            limpiar(); listar();
        } catch (Exception e) { msg("Error: " + e.getMessage(), true); }
    }

    @FXML public void eliminar() {
        if (idSel == null) { msg("Seleccione un usuario.", true); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar usuario seleccionado?", ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> r = a.showAndWait();
        if (r.isEmpty() || r.get()!=ButtonType.OK) return;
        try { service.eliminar(idSel); msg("Usuario eliminado.", false); limpiar(); listar(); }
        catch (Exception e) { msg("Error: " + e.getMessage(), true); }
    }

    @FXML public void listar() { try { lista.setAll(service.listarTodo()); filtrar(val(txtFiltro)); } catch (Exception e) { msg("Error: "+e.getMessage(), true); } }
    @FXML public void limpiar() { idSel=null; if(txtUsuario!=null)txtUsuario.clear(); if(txtClave!=null)txtClave.clear(); if(cbxPerfil!=null)cbxPerfil.getSelectionModel().selectFirst(); limpiarMarcas(); msg("Listo.", false); }

    private void editar(Usuario u) {
        idSel=u.getIdUsuario(); if(txtUsuario!=null)txtUsuario.setText(u.getUsuario()); if(txtClave!=null)txtClave.setText(u.getClave());
        if (cbxPerfil != null && u.getIdPerfil() != null) for (ComboBoxOption o : cbxPerfil.getItems()) if (o.getId() == u.getIdPerfil().intValue()) cbxPerfil.getSelectionModel().select(o);
        msg("Editando ID "+idSel,false);
    }
    private void filtrar(String q) { if(tableView==null) return; if(q==null||q.isBlank()){tableView.setItems(lista);return;} String s=q.toLowerCase(); tableView.setItems(lista.filtered(u->u.getUsuario()!=null&&u.getUsuario().toLowerCase().contains(s))); }
    private String val(TextField t){ return t!=null&&t.getText()!=null?t.getText().trim():""; }
    private void msg(String m, boolean err){ if(lblMsg!=null){lblMsg.setText(m); lblMsg.setStyle(err?"-fx-text-fill:#b91c1c;":"-fx-text-fill:#166534;");} try{ if(txtUsuario!=null&&txtUsuario.getScene()!=null) Toast.showToast((Stage)txtUsuario.getScene().getWindow(),m,err?"#FCA5A5":"#86EFAC",2000,360,50);}catch(Exception ignored){} }
    private void marcar(TextField f, boolean err){ if(f!=null) f.setStyle(err?"-fx-border-color:#dc2626;-fx-border-width:1.5;":""); }
    private void limpiarMarcas(){ marcar(txtUsuario,false); marcar(txtClave,false); }
}
