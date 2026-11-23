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
    public void login(ActionEvent e) {
        try {
            if (u == null) {
                this.u = AppContext.getInstance().getBean(IUsuarioService.class);
            }

            String userText = txtUsuario != null ? txtUsuario.getText().trim() : "";
            String passText = txtClave != null ? txtClave.getText().trim() : "";

            if (userText.isEmpty()) {
                marcarCampo(txtUsuario, true);
                mensajeError(e, "Ingrese su usuario.");
                return;
            }
            marcarCampo(txtUsuario, false);
            if (passText.isEmpty()) {
                marcarCampo(txtClave, true);
                mensajeError(e, "Ingrese su contraseña.");
                return;
            }
            if (passText.length() < 3) {
                marcarCampo(txtClave, true);
                mensajeError(e, "La contraseña debe tener al menos 3 caracteres.");
                return;
            }
            marcarCampo(txtClave, false);

            u.loginUsuario(userText, passText)
                    .ifPresentOrElse(
                            usu -> {
                                mensajeOk(e, "Bienvenido, " + usu.getUsuario() + ".");
                                abrirMain(e, usu);
                            },
                            () -> mensajeError(e, "Credenciales incorrectas. Verifique usuario y contraseña.")
                    );

        } catch (Exception ex) {
            mensajeError(e, "Error al iniciar sesión: " + ex.getMessage());
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

    public void mensajeError(ActionEvent e, String msg) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        double w = Math.max(280, stage.getWidth() - 40);
        Toast.showToast(stage, msg, "#FCA5A5", 2200, w, 55);
    }

    public void mensajeOk(ActionEvent e, String msg) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        double w = Math.max(280, stage.getWidth() - 40);
        Toast.showToast(stage, msg, "#86EFAC", 1500, w, 55);
    }

    private void marcarCampo(TextField campo, boolean error) {
        if (campo == null) return;
        campo.setStyle(error
                ? "-fx-border-color: #dc2626; -fx-border-width: 1.5; -fx-background-color: #fff5f5;"
                : "");
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
    public void logout(ActionEvent e) {
        try {
            DatabaseConfig.shutdown();
        } catch (Exception ignored) {
        }
        Platform.exit();
    }
}