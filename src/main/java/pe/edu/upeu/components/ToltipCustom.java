package pe.edu.upeu.components;

import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;

public class ToltipCustom {

    public static Tooltip crearTooltip(String mensaje) {
        Tooltip tooltip = new Tooltip(mensaje);
        tooltip.setFont(new Font("System Bold", 12));
        tooltip.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-background-radius: 5px;");
        return tooltip;
    }
}