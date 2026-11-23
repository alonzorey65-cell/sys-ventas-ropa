package pe.edu.upeu.controller; // Capa controlador

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pe.edu.upeu.components.Toast;
import pe.edu.upeu.model.Cliente; // Modelo Cliente
import pe.edu.upeu.service.IClienteService; // Servicio Cliente

import java.util.Optional;

// CRUD Clientes: main_cliente.fxml -> ClienteController -> IClienteService -> ClienteRepository -> BD tabla cliente
public class ClienteController {
    @FXML private TextField txtNombre, txtDniRuc, txtDireccion, txtTelefono, txtFiltro; // Campos UI
    @FXML private Label lblMsg; // Mensaje estado
    @FXML private TableView<Cliente> tableView; // Tabla clientes
    @FXML private TableColumn<Cliente, Long> colId; // idCliente
    @FXML private TableColumn<Cliente, String> colNombre, colDniRuc, colDireccion, colTelefono; // columnas cliente

    private final IClienteService service; // Servicio inyectado por AppContext
    private final ObservableList<Cliente> lista = FXCollections.observableArrayList(); // lista observable
    private Long idSel = null; // id seleccionado para editar/eliminar

    public ClienteController(IClienteService service) { this.service = service; } // DI

    @FXML public void initialize() { // Configura columnas, eventos y carga inicial
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idCliente")); // mapea idCliente
        if (colNombre != null) colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre")); // mapea nombre
        if (colDniRuc != null) colDniRuc.setCellValueFactory(new PropertyValueFactory<>("dniRuc")); // mapea DNI/RUC
        if (colDireccion != null) colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion")); // mapea dirección
        if (colTelefono != null) colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono")); // mapea teléfono
        if (tableView != null) { tableView.setItems(lista); tableView.setRowFactory(tv -> { TableRow<Cliente> r = new TableRow<>(); r.setOnMouseClicked(e -> { if (e.getClickCount()==2 && !r.isEmpty()) editar(r.getItem()); }); return r; }); } // doble clic edita
        if (txtFiltro != null) txtFiltro.textProperty().addListener((o,a,b)-> filtrar(b)); // filtro en vivo
        listar(); // carga datos desde BD
    }

    @FXML public void guardar() { // Crear/actualizar cliente
        String nombre = val(txtNombre), dni = val(txtDniRuc), dir = val(txtDireccion), tel = val(txtTelefono); // lee inputs
        limpiarMarcas(); // limpia validaciones previas
        if (dni.isEmpty() || !dni.matches("\\d{8}|\\d{11}")) { marcar(txtDniRuc,true); msg("DNI debe tener 8 dígitos o RUC 11.", true); return; } // valida documento
        if (nombre.isEmpty()) { marcar(txtNombre,true); msg("El nombre es obligatorio.", true); return; } // valida nombre
        try {
            Cliente c = Cliente.builder().nombre(nombre).dniRuc(dni).direccion(dir).telefono(tel).build(); // construye modelo
            if (idSel == null) { service.guardar(c); msg("Cliente registrado.", false); } // INSERT -> service -> repo -> BD
            else { c.setIdCliente(idSel); service.actualizar(idSel, c); msg("Cliente actualizado.", false); } // UPDATE
            limpiar(); listar(); // refresca UI
        } catch (Exception e) { msg("Error: " + e.getMessage(), true); }
    }

    @FXML public void eliminar() { // Elimina cliente
        if (idSel == null) { msg("Seleccione un cliente.", true); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar cliente seleccionado?", ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> r = a.showAndWait();
        if (r.isEmpty() || r.get()!=ButtonType.OK) return;
        try { service.eliminar(idSel); msg("Cliente eliminado.", false); limpiar(); listar(); } // DELETE
        catch (Exception e) { msg("Error: " + e.getMessage(), true); }
    }

    @FXML public void listar() { try { lista.setAll(service.listarTodo()); filtrar(val(txtFiltro)); } catch (Exception e) { msg("Error: "+e.getMessage(), true); } } // SELECT all
    @FXML public void limpiar() { idSel=null; if(txtNombre!=null)txtNombre.clear(); if(txtDniRuc!=null)txtDniRuc.clear(); if(txtDireccion!=null)txtDireccion.clear(); if(txtTelefono!=null)txtTelefono.clear(); limpiarMarcas(); msg("Listo.", false); } // reset form

    private void editar(Cliente c) { idSel=c.getIdCliente(); if(txtNombre!=null)txtNombre.setText(c.getNombre()); if(txtDniRuc!=null)txtDniRuc.setText(c.getDniRuc()); if(txtDireccion!=null)txtDireccion.setText(c.getDireccion()); if(txtTelefono!=null)txtTelefono.setText(c.getTelefono()); msg("Editando ID "+idSel,false); } // carga fila a form
    private void filtrar(String q) { if(tableView==null) return; if(q==null||q.isBlank()){tableView.setItems(lista);return;} String s=q.toLowerCase(); tableView.setItems(lista.filtered(c->(c.getNombre()!=null&&c.getNombre().toLowerCase().contains(s))||(c.getDniRuc()!=null&&c.getDniRuc().contains(s)))); } // filtra por nombre o doc
    private String val(TextField t){ return t!=null&&t.getText()!=null?t.getText().trim():""; } // helper lectura segura
    private void msg(String m, boolean err){ if(lblMsg!=null){lblMsg.setText(m); lblMsg.setStyle(err?"-fx-text-fill:#b91c1c;":"-fx-text-fill:#166534;");} try{ if(txtNombre!=null&&txtNombre.getScene()!=null) Toast.showToast((Stage)txtNombre.getScene().getWindow(),m,err?"#FCA5A5":"#86EFAC",2000,360,50);}catch(Exception ignored){} } // label+toast
    private void marcar(TextField f, boolean err){ if(f!=null) f.setStyle(err?"-fx-border-color:#dc2626;-fx-border-width:1.5;":""); } // estilo error
    private void limpiarMarcas(){ marcar(txtNombre,false); marcar(txtDniRuc,false); } // limpia validaciones
}