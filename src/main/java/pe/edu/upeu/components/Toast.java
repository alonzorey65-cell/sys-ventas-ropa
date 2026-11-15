package pe.edu.upeu.components;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public final class Toast {

    // Método original que ya estabas usando en otros controladores
    public static void makeToast(Stage ownerStage, String mensaje, int duracionMilisegundos) {
        Stage toastStage = new Stage();
        toastStage.initOwner(ownerStage);
        toastStage.initStyle(StageStyle.TRANSPARENT);
        toastStage.setResizable(false);

        Label text = new Label(mensaje);
        text.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold;");

        Rectangle background = new Rectangle(300, 45);
        background.setArcWidth(20);
        background.setArcHeight(20);
        background.setFill(Color.rgb(30, 30, 30, 0.85));

        StackPane root = new StackPane(background, text);
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);

        toastStage.setX(ownerStage.getX() + (ownerStage.getWidth() / 2) - 150);
        toastStage.setY(ownerStage.getY() + ownerStage.getHeight() - 100);

        toastStage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(duracionMilisegundos), ae -> toastStage.close()));
        timeline.play();
    }

    // Nuevo método agregado para satisfacer la llamada showToast que requiere LoginController
    public static void showToast(Stage ownerStage, String message, String hexColor, int durationMillis, double x, double y) {
        Stage toastStage = new Stage();
        toastStage.initOwner(ownerStage);
        toastStage.initStyle(StageStyle.TRANSPARENT);
        toastStage.setResizable(false);

        Label text = new Label(message);
        text.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px;");

        Rectangle background = new Rectangle(320, 50);
        background.setArcWidth(20);
        background.setArcHeight(20);

        // Aplica el color hexadecimal personalizado que le mandes (ej: #FEA1B6 para errores)
        try {
            background.setFill(Color.web(hexColor, 0.90));
        } catch (Exception e) {
            background.setFill(Color.rgb(30, 30, 30, 0.90));
        }

        StackPane root = new StackPane(background, text);
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);

        toastStage.setX(ownerStage.getX() + x);
        toastStage.setY(ownerStage.getY() + y);

        toastStage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(durationMillis), ae -> toastStage.close()));
        timeline.play();
    }
}