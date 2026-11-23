package pe.edu.upeu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * POO - HERENCIA Y POLIMORFISMO:
 * - HERENCIA: App extiende Application (clase base JavaFX).
 * - POLIMORFISMO/SOBRESCRITURA: @Override start(Stage) redefine el comportamiento de inicio.
 * - ABSTRACCIÓN: JavaFX gestiona el ciclo de vida; nosotros solo definimos la UI inicial.
 * - OBJETOS: crea objetos FXMLLoader, Scene, Stage para construir la ventana.
 */
public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException { // POLIMORFISMO: sobrescritura del método de Application
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/view/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300); // OBJETO Scene
        stage.setTitle("Login - Sistema de Ventas de Ropa");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(); // Método estático de Application que inicia JavaFX
    }
}