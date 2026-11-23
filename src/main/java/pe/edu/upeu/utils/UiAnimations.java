package pe.edu.upeu.utils;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class UiAnimations {

    private UiAnimations() {}

    public static void fadeIn(Node node, int millis) {
        if (node == null) return;
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(millis), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setInterpolator(Interpolator.EASE_OUT);
        ft.play();
    }

    public static void applyHoverLift(Node node) {
        if (node == null) return;
        node.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), node);
            st.setToX(1.03);
            st.setToY(1.03);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        node.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), node);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
    }

    public static void applyButtonPress(Button btn) {
        if (btn == null) return;
        btn.setOnMousePressed(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
            st.setToX(0.97);
            st.setToY(0.97);
            st.play();
        });
        btn.setOnMouseReleased(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    public static void decorateTree(Parent root) {
        if (root == null) return;
        for (Node n : root.lookupAll(".stat-card, .quick-action, .card")) {
            applyHoverLift(n);
        }
        for (Node n : root.lookupAll(".button")) {
            if (n instanceof Button b) applyButtonPress(b);
        }
    }
}