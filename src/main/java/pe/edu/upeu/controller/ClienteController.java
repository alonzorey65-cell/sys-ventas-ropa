package pe.edu.upeu.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pe.edu.upeu.components.Toast;
import pe.edu.upeu.model.Cliente;
import pe.edu.upeu.service.IClienteService;

import java.util.Optional;

public class ClienteController {
    @FXML private TextField txtNombre, txtDniRuc, txtDireccion, txtTelefono, txtFiltro;
    @FXML private Label lblMsg;
    @FXML private TableView<Cliente> tableView;
    @FXML private TableColumn<Cliente, Long> colId;
    @FXML private TableColumn<Cliente, String> colNombre, colDniRuc, colDireccion, colTelefono;

    private final IClienteService service;
    private final ObservableList<Cliente> lista = FXCollections.observableArrayList();
    private Long idSel = null;

    public ClienteController(IClienteService service) { this.service = service; }

    @FXML public void initialize() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        if (colNombre != null) colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        if (colDniRuc != null) colDniRuc.setCellValueFactory(new PropertyValueFactory<>("dniRuc"));
        if (colDireccion != null) colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        if (colTelefono != null) colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        if (tableView != null) { tableView.setItems(lista); tableView.setRowFactory(tv -> { TableRow<Cliente> r = new TableRow<>(); r.setOnMouseClicked(e -> { if (e.getClickCount()==2 && !r.isEmpty()) editar(r.getItem()); }); return r; }); }
        if (txtFiltro != null) txtFiltro.textProperty().addListener((o,a,b)-> filtrar(b));
        listar();
    }

    @FXML public void guardar() {
        String nombre = val(txtNombre), dni = val(txtDniRuc), dir = val(txtDireccion), tel = val(txtTelefono);
        limpiarMarcas();
        if (dni.isEmpty() || !dni.matches("\\d{8}|\\d{11}")) { marcar(txtDniRuc,true); msg("DNI debe tener 8 dígitos o RUC 11.", true); return; }
        if (nombre.isEmpty()) { marcar(txtNombre,true); msg("El nombre es obligatorio.", true); return; }
        try {
            Cliente c = Cliente.builder().nombre(nombre).dniRuc(dni).direccion(dir).telefono(tel).build();
            if (idSel == null) { service.guardar(c); msg("Cliente registrado.", false); }
            else { c.setIdCliente(idSel); service.actualizar(idSel, c); msg("Cliente actualizado.", false); }
            limpiar(); listar();
        } catch (Exception e) { msg("Error: " + e.getMessage(), true); }
    }

    @FXML public void eliminar() {
        if (idSel == null) { msg("Seleccione un cliente.", true); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar cliente seleccionado?", ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> r = a.showAndWait();
        if (r.isEmpty() || r.get()!=ButtonType.OK) return;
        try { service.eliminar(idSel); msg("Cliente eliminado.", false); limpiar(); listar(); }
        catch (Exception e) { msg("Error: " + e.getMessage(), true); }
    }

    @FXML public void listar() { try { lista.setAll(service.listarTodo()); filtrar(val(txtFiltro)); } catch (Exception e) { msg("Error: "+e.getMessage(), true); } }
    @FXML public void limpiar() { idSel=null; if(txtNombre!=null)txtNombre.clear(); if(txtDniRuc!=null)txtDniRuc.clear(); if(txtDireccion!=null)txtDireccion.clear(); if(txtTelefono!=null)txtTelefono.clear(); limpiarMarcas(); msg("Listo.", false); }

    private void editar(Cliente c) { idSel=c.getIdCliente(); if(txtNombre!=null)txtNombre.setText(c.getNombre()); if(txtDniRuc!=null)txtDniRuc.setText(c.getDniRuc()); if(txtDireccion!=null)txtDireccion.setText(c.getDireccion()); if(txtTelefono!=null)txtTelefono.setText(c.getTelefono()); msg("Editando ID "+idSel,false); }
    private void filtrar(String q) { if(tableView==null) return; if(q==null||q.isBlank()){tableView.setItems(lista);return;} String s=q.toLowerCase(); tableView.setItems(lista.filtered(c->(c.getNombre()!=null&&c.getNombre().toLowerCase().contains(s))||(c.getDniRuc()!=null&&c.getDniRuc().contains(s)))); }
    private String val(TextField t){ return t!=null&&t.getText()!=null?t.getText().trim():""; }
    private void msg(String m, boolean err){ if(lblMsg!=null){lblMsg.setText(m); lblMsg.setStyle(err?"-fx-text-fill:#b91c1c;":"-fx-text-fill:#166534;");} try{ if(txtNombre!=null&&txtNombre.getScene()!=null) Toast.showToast((Stage)txtNombre.getScene().getWindow(),m,err?"#FCA5A5":"#86EFAC",2000,360,50);}catch(Exception ignored){} }
    private void marcar(TextField f, boolean err){ if(f!=null) f.setStyle(err?"-fx-border-color:#dc2626;-fx-border-width:1.5;":""); }
    private void limpiarMarcas(){ marcar(txtNombre,false); marcar(txtDniRuc,false); }
}
