package pe.edu.upeu.components.comprob;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import pe.edu.upeu.dto.comprobante.Comprobante;

public class ComprobanteView {
    private final Comprobante comprobante;

    public ComprobanteView(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public VBox construir() {
        VBox vbox = new VBox(8);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: white; -fx-border-color: #d1d5db; -fx-border-radius: 6; -fx-background-radius: 6;");
        vbox.setPrefWidth(420);

        Label lblTitulo = new Label("SYSVENTAS - COMPROBANTE DE PAGO");
        lblTitulo.setFont(new Font("System Bold", 16));
        Label lblSub = new Label("Boleta / Comprobante");
        lblSub.setFont(new Font("System", 12));

        String serie = comprobante != null ? safe(comprobante.getSerie()) : "B001";
        String numero = comprobante != null ? safe(comprobante.getNumero()) : "000001";
        String fecha = comprobante != null ? safe(comprobante.getFecha()) : "";
        String cliente = comprobante != null ? safe(comprobante.getClienteNombre()) : "Público General";
        String dni = comprobante != null ? safe(comprobante.getClienteDniRuc()) : "";
        double total = comprobante != null ? comprobante.getTotal() : 0.0;

        vbox.getChildren().addAll(lblTitulo, lblSub, new Separator(),
                new Label("Serie-Número: " + serie + "-" + numero),
                new Label("Fecha: " + fecha),
                new Label("Cliente: " + cliente),
                new Label("DNI/RUC: " + dni),
                new Separator(),
                new Label("Detalle:"));

        if (comprobante != null && comprobante.getItems() != null && !comprobante.getItems().isEmpty()) {
            for (Comprobante.DetalleItem item : comprobante.getItems()) {
                String desc = safe(item.getDescripcion());
                int cant = item.getCantidad();
                double pu = item.getPrecioUnitario();
                double sub = item.getSubtotal();
                HBox row = new HBox(8);
                Label lDesc = new Label(cant + " x " + desc);
                lDesc.setWrapText(true);
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                Label lSub = new Label(String.format(java.util.Locale.US, "S/ %.2f", sub));
                row.getChildren().addAll(lDesc, spacer, lSub);
                Label lPu = new Label(String.format(java.util.Locale.US, "P.U. S/ %.2f", pu));
                lPu.setStyle("-fx-text-fill:#4b5563; -fx-font-size:11px;");
                VBox itemBox = new VBox(2, row, lPu);
                vbox.getChildren().add(itemBox);
            }
        } else {
            vbox.getChildren().add(new Label("(Sin detalle disponible)"));
        }

        Label lblTotal = new Label(String.format(java.util.Locale.US, "TOTAL A PAGAR: S/ %.2f", total));
        lblTotal.setFont(new Font("System Bold", 14));
        vbox.getChildren().addAll(new Separator(), lblTotal);
        return vbox;
    }

    private String safe(String s) { return s == null ? "" : s; }
}