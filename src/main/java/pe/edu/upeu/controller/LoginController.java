package pe.edu.upeu.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pe.edu.upeu.components.StageManager;
import pe.edu.upeu.components.Toast;
import pe.edu.upeu.config.AppContext;
import pe.edu.upeu.config.DatabaseConfig;
import pe.edu.upeu.dto.SessionManager;
import pe.edu.upeu.model.Usuario;
import pe.edu.upeu.service.IUsuarioService;

/*
 * POO - CLASE, OBJETO, MÉTODOS, ENCAPSULAMIENTO, ABSTRACCIÓN, POLIMORFISMO y CONEXIÓN:
 * - CLASE: LoginController controla la vista de inicio de sesión.
 * - OBJETOS: usa objetos Usuario, servicios, Stage/Scene, SessionManager, etc.
 * - MÉTODOS: login(), iniciarSesion(), abrirMain(), logout() definen comportamientos/eventos.
 * - ENCAPSULAMIENTO: campos @FXML y dependencias se mantienen como atributos privados.
 * - ABSTRACCIÓN/POLIMORFISMO: depende de IUsuarioService (interfaz) e interactúa con implementaciones concretas vía AppContext.
 * CONEXIÓN: Vista login.fxml -> LoginController -> IUsuarioService/UsuarioServiceImp -> UsuarioRepository -> Neon DB;
 *          al autenticar conecta con MainGuiController/maingui.fxml.
 */
public class LoginController { // CLASE controladora de autenticación

    private IUsuarioService u;

    // 1. Constructor vacío obligatorio para que JavaFX / FXMLLoader nunca lance NoSuchMethodException
    public LoginController() {
        try {
            this.u = AppContext.getInstance().getBean(IUsuarioService.class);
        } catch (Exception e) {
            // Fallback de seguridad si el contexto se inicializa después
            this.u = null;
        }
    }

    // 2. Constructor con parámetros para tu AppContext (Inyección de Dependencias)
    public LoginController(IUsuarioService u) {
        this.u = u;
    }

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtClave;

    @FXML
    public void initialize(){
        // Inicialización opcional
    }

    @FXML
    public void login(ActionEvent e){
        try {
            if (u == null) {
                this.u = AppContext.getInstance().getBean(IUsuarioService.class);
            }

            String userText = txtUsuario != null ? txtUsuario.getText() : "";
            String passText = txtClave != null ? txtClave.getText() : "";

            if (userText.isEmpty() || passText.isEmpty()) {
                mensajeError(e, "Por favor ingrese usuario y contraseña");
                return;
            }

            u.loginUsuario(userText, passText)
                    .ifPresentOrElse(
                            usu -> abrirMain(e, usu),
                            () -> mensajeError(e, "Error en las credenciales... intente nuevamente!!")
                    );

        } catch (Exception ex) {
            mensajeError(e, "Error al intentar iniciar sesión: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    public void iniciarSesion(ActionEvent e) {
        login(e);
    }

    @FXML
    public void cerrarVentana(ActionEvent e){
        logout(e);
    }

    public void mensajeError(ActionEvent e, String msg){
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
        Toast.showToast(stage, msg, "#FEA1B6", 2000, w, h);
    }

    private void abrirMain(ActionEvent event, Usuario usu){
        try {
            SessionManager.getInstance().setUserId(usu.getIdUsuario());
            SessionManager.getInstance().setUserName(usu.getUsuario());

            if (usu.getIdPerfil() != null) {
                SessionManager.getInstance().setUserPerfil(String.valueOf(usu.getIdPerfil()));
            } else {
                SessionManager.getInstance().setUserPerfil("ADMIN");
            }

            AppContext context = AppContext.getInstance();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/maingui.fxml"));
            fxmlLoader.setControllerFactory(context::getBean);

            Screen screen = Screen.getPrimary();
            Rectangle2D r2d = screen.getVisualBounds();
            Scene scene = new Scene(fxmlLoader.load(), r2d.getWidth(), r2d.getHeight() - 50);

            if (getClass().getResource("/css/style.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            }

            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();

            stage.setTitle("Sistema de ventas - SysVentas");
            stage.setResizable(true);
            stage.setScene(scene);
            stage.setX(r2d.getMinX());
            stage.setY(r2d.getMinY());
            StageManager.setPrimaryStage(stage);
            stage.setWidth(r2d.getWidth());
            stage.setHeight(r2d.getHeight());

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al abrir la ventana principal: " + e.getMessage(), e);
        }
    }

    @FXML
    public void logout(ActionEvent e){
        Platform.exit();
        System.exit(0);
        DatabaseConfig.shutdown();
    }
}