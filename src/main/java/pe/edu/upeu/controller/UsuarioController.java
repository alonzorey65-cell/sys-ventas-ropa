package pe.edu.upeu.controller; // Capa controlador

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pe.edu.upeu.components.Toast;
import pe.edu.upeu.dto.ComboBoxOption; // Opción para ComboBox perfil
import pe.edu.upeu.model.Usuario; // Modelo Usuario
import pe.edu.upeu.service.IUsuarioService; // Servicio inyectado

import java.util.Optional;

// CRUD Usuarios: main_usuario.fxml -> UsuarioController -> IUsuarioService -> UsuarioRepository -> BD
public class UsuarioController {
    @FXML private TextField txtUsuario, txtClave, txtConfirmar, txtFiltro; // Campos usuario, clave, confirmar clave, filtro
    @FXML private ComboBox<ComboBoxOption> cbxPerfil; // Selector de perfil (ADMIN/VENDEDOR)
    @FXML private Label lblMsg; // Mensaje UI
    @FXML private TableView<Usuario> tableView; // Tabla usuarios
    @FXML private TableColumn<Usuario, Long> colId, colPerfil; // Columnas id y perfil
    @FXML private TableColumn<Usuario, String> colUsuario; // Columna username

    private final IUsuarioService service; // Servicio inyectado
    private final ObservableList<Usuario> lista = FXCollections.observableArrayList(); // Datos tabla
    private Long idSel = null; // ID seleccionado

    public UsuarioController(IUsuarioService service) { this.service = service; } // DI por AppContext

    @FXML public void initialize() { // Configura tabla, combo perfil y carga datos
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario")); // mapea idUsuario
        if (colUsuario != null) colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario")); // mapea username
        if (colPerfil != null) colPerfil.setCellValueFactory(new PropertyValueFactory<>("idPerfil")); // mapea idPerfil
        if (cbxPerfil != null) {
            cbxPerfil.setItems(FXCollections.observableArrayList(
                    new ComboBoxOption(1, "ADMINISTRADOR"),
                    new ComboBoxOption(2, "VENDEDOR"),
                    new ComboBoxOption(3, "ALMACENERO"))); // opciones perfil
            cbxPerfil.getSelectionModel().selectFirst(); // default ADMIN
        }
        if (tableView != null) { tableView.setItems(lista); tableView.setRowFactory(tv -> { TableRow<Usuario> r = new TableRow<>(); r.setOnMouseClicked(e -> { if (e.getClickCount()==2 && !r.isEmpty()) editar(r.getItem()); }); return r; }); } // doble clic edita
        if (txtFiltro != null) txtFiltro.textProperty().addListener((o,a,b)-> filtrar(b)); // filtro en vivo
        listar(); // carga inicial desde BD
    }

    @FXML public void guardar() { // Crea o actualiza usuario
        String u = val(txtUsuario), c = val(txtClave), cc = val(txtConfirmar); // lee inputs: usuario, clave, confirmar clave
        limpiarMarcas(); // limpia marcas de error previas
        if (u.isEmpty()) { marcar(txtUsuario,true); msg("El usuario es obligatorio.", true); return; } // valida usuario
        if (c.isEmpty() || c.length() < 3) { marcar(txtClave,true); msg("La clave debe tener al menos 3 caracteres.", true); return; } // valida clave
        if (cc.isEmpty()) { marcar(txtConfirmar,true); msg("Debe confirmar la clave.", true); return; } // valida confirmación presente
        if (!c.equals(cc)) { marcar(txtClave,true); marcar(txtConfirmar,true); msg("Las claves no coinciden.", true); return; } // valida coincidencia
        Long perfil = (cbxPerfil != null && cbxPerfil.getValue() != null) ? (long) cbxPerfil.getValue().getId() : 1L; // obtiene perfil seleccionado
        try {
            Usuario ent = Usuario.builder().usuario(u).clave(c).idPerfil(perfil).build(); // crea objeto Usuario
            if (idSel == null) { service.guardar(ent); msg("Usuario registrado.", false); } // INSERT
            else { ent.setIdUsuario(idSel); service.actualizar(idSel, ent); msg("Usuario actualizado.", false); } // UPDATE
            limpiar(); listar(); // refresca
        } catch (Exception e) { msg("Error: " + e.getMessage(), true); }
    }

    @FXML public void eliminar() { // Elimina usuario seleccionado
        if (idSel == null) { msg("Seleccione un usuario.", true); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar usuario seleccionado?", ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> r = a.showAndWait();
        if (r.isEmpty() || r.get()!=ButtonType.OK) return;
        try { service.eliminar(idSel); msg("Usuario eliminado.", false); limpiar(); listar(); }
        catch (Exception e) { msg("Error: " + e.getMessage(), true); }

    }

    @FXML public void confirmar() { // Confirma que clave y confirmación coinciden
        String c = val(txtClave), cc = val(txtConfirmar);
        if (c.isEmpty() || cc.isEmpty()) { msg("Ingrese clave y confirmación.", true); return; }
        if (!c.equals(cc)) { marcar(txtClave,true); marcar(txtConfirmar,true); msg("Las claves no coinciden.", true); return; }
        msg("Claves coinciden.", false);



    }


    @FXML public void listar() { try { lista.setAll(service.listarTodo()); filtrar(val(txtFiltro)); } catch (Exception e) { msg("Error: "+e.getMessage(), true); } } // BD -> tabla
    @FXML public void limpiar() { idSel=null; if(txtUsuario!=null)txtUsuario.clear(); if(txtClave!=null)txtClave.clear(); if(txtConfirmar!=null)txtConfirmar.clear(); if(cbxPerfil!=null)cbxPerfil.getSelectionModel().selectFirst(); limpiarMarcas(); msg("Listo.", false); } // reset (incluye confirmar clave)

    private void editar(Usuario u) { // carga usuario en formulario para edición
        idSel=u.getIdUsuario(); // guarda ID seleccionado para actualizar
        if(txtUsuario!=null)txtUsuario.setText(u.getUsuario()); // carga usuario
        if(txtClave!=null)txtClave.setText(u.getClave()); // carga clave
        if(txtConfirmar!=null)txtConfirmar.setText(u.getClave()); // precarga confirmación para evitar error al editar
        if (cbxPerfil != null && u.getIdPerfil() != null) for (ComboBoxOption o : cbxPerfil.getItems()) if (o.getId() == u.getIdPerfil().intValue()) cbxPerfil.getSelectionModel().select(o); // selecciona perfil
        msg("Editando ID "+idSel,false);
    }
    private void filtrar(String q) { if(tableView==null) return; if(q==null||q.isBlank()){tableView.setItems(lista);return;} String s=q.toLowerCase(); tableView.setItems(lista.filtered(u->u.getUsuario()!=null&&u.getUsuario().toLowerCase().contains(s))); } // filtro username
    private String val(TextField t){ return t!=null&&t.getText()!=null?t.getText().trim():""; } // lee texto seguro
    private void msg(String m, boolean err){ if(lblMsg!=null){lblMsg.setText(m); lblMsg.setStyle(err?"-fx-text-fill:#b91c1c;":"-fx-text-fill:#166534;");} try{ if(txtUsuario!=null&&txtUsuario.getScene()!=null) Toast.showToast((Stage)txtUsuario.getScene().getWindow(),m,err?"#FCA5A5":"#86EFAC",2000,360,50);}catch(Exception ignored){} } // msg
    private void marcar(TextField f, boolean err){ if(f!=null) f.setStyle(err?"-fx-border-color:#dc2626;-fx-border-width:1.5;":""); } // marca error
    private void limpiarMarcas(){ marcar(txtUsuario,false); marcar(txtClave,false); marcar(txtConfirmar,false); } // limpia marcas de validación
    public void limpiarUsuario() {
        limpiar(); // llama al método de limpieza
    }// método público para limpiar desde otro controlador
}