package pe.edu.upeu.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pe.edu.upeu.config.AppContext;

import java.io.IOException;

public class StageManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void cambiarEscena(Stage stage, String fxmlRuta, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(fxmlRuta));

            // Integración con AppContext para la inyección de dependencias en controladores
            try {
                loader.setControllerFactory(AppContext.getInstance()::getBean);
            } catch (Exception ignored) {}

            Parent root = loader.load();

            if (stage == null) {
                stage = primaryStage != null ? primaryStage : new Stage();
            }
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void abrirVentanaModal(String fxmlRuta, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(fxmlRuta));

            try {
                loader.setControllerFactory(AppContext.getInstance()::getBean);
            } catch (Exception ignored) {}

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            if (primaryStage != null) {
                stage.initOwner(primaryStage);
            }

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}