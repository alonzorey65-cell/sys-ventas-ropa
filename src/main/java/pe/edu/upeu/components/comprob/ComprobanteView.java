package pe.edu.upeu.components.comprob;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pe.edu.upeu.dto.comprobante.Comprobante;

import java.util.Locale;

public class ComprobanteView {
    private final Comprobante comprobante;

    public ComprobanteView(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public VBox construir() {
        String serie = comprobante != null ? safe(comprobante.getSerie()) : "B001";
        String numero = comprobante != null ? safe(comprobante.getNumero()) : "000001";
        String fecha = comprobante != null ? safe(comprobante.getFecha()) : "";
        String cliente = comprobante != null ? safe(comprobante.getClienteNombre()) : "Público General";
        String dni = comprobante != null ? safe(comprobante.getClienteDniRuc()) : "";
        double total = comprobante != null ? comprobante.getTotal() : 0.0;
        double base = total / 1.18;
        double igv = total - base;

        VBox card = new VBox();
        card.setPrefWidth(460);
        card.setMaxWidth(460);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-radius: 14; " +
                "-fx-border-color: #e5e7eb; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 18, 0.12, 0, 4);");

        // Header
        VBox header = new VBox(4);
        header.setPadding(new Insets(22, 22, 18, 22));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: linear-gradient(to right, #0f766e, #1d4ed8); -fx-background-radius: 14 14 0 0;");

        Label brand = new Label("SYSVENTAS");
        brand.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        Label subBrand = new Label("Sistema de Ventas · Comprobante de Pago");
        subBrand.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 12px;");

        HBox badgeRow = new HBox(10);
        badgeRow.setAlignment(Pos.CENTER_LEFT);
        Label badge = new Label("BOLETA / COMPROBANTE");
        badge.setStyle("-fx-background-color: rgba(255,255,255,0.18); -fx-text-fill: white; -fx-padding: 5 10; " +
                "-fx-background-radius: 999; -fx-font-size: 11px; -fx-font-weight: bold;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label num = new Label(serie + "-" + numero);
        num.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        badgeRow.getChildren().addAll(badge, sp, num);

        header.getChildren().addAll(brand, subBrand, badgeRow);

        // Info
        GridPane info = new GridPane();
        info.setHgap(12);
        info.setVgap(8);
        info.setPadding(new Insets(18, 22, 14, 22));
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setMinWidth(90);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        info.getColumnConstraints().addAll(c1, c2);

        addInfo(info, 0, "Fecha", fecha);
        addInfo(info, 1, "Cliente", cliente.isBlank() ? "Público General" : cliente);
        addInfo(info, 2, "DNI/RUC", dni.isBlank() ? "-" : dni);
        addInfo(info, 3, "Estado", "PAGADO");

        // Details
        VBox detailBox = new VBox(0);
        detailBox.setPadding(new Insets(0, 22, 0, 22));

        HBox tableHeader = new HBox(8);
        tableHeader.setPadding(new Insets(10, 10, 10, 10));
        tableHeader.setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 8 8 0 0;");
        Label hDesc = headerLabel("DESCRIPCIÓN", 220);
        Label hCant = headerLabel("CANT.", 55);
        hCant.setAlignment(Pos.CENTER_RIGHT);
        Label hPu = headerLabel("P. UNIT", 70);
        hPu.setAlignment(Pos.CENTER_RIGHT);
        Label hSub = headerLabel("SUBTOTAL", 75);
        hSub.setAlignment(Pos.CENTER_RIGHT);
        tableHeader.getChildren().addAll(hDesc, hCant, hPu, hSub);
        detailBox.getChildren().add(tableHeader);

        if (comprobante != null && comprobante.getItems() != null && !comprobante.getItems().isEmpty()) {
            int i = 0;
            for (Comprobante.DetalleItem item : comprobante.getItems()) {
                HBox row = new HBox(8);
                row.setPadding(new Insets(10));
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle(i % 2 == 0 ? "-fx-background-color: white; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;"
                        : "-fx-background-color: #fafafa; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");

                Label d = bodyLabel(safe(item.getDescripcion()), 220);
                d.setWrapText(true);
                Label c = bodyLabel(String.valueOf(item.getCantidad()), 55);
                c.setAlignment(Pos.CENTER_RIGHT);
                c.setMaxWidth(Double.MAX_VALUE);
                Label pu = bodyLabel(money(item.getPrecioUnitario()), 70);
                pu.setAlignment(Pos.CENTER_RIGHT);
                pu.setMaxWidth(Double.MAX_VALUE);
                Label sub = bodyLabel(money(item.getSubtotal()), 75);
                sub.setAlignment(Pos.CENTER_RIGHT);
                sub.setMaxWidth(Double.MAX_VALUE);
                sub.setStyle("-fx-text-fill: #111827; -fx-font-weight: bold; -fx-font-size: 12px;");
                row.getChildren().addAll(d, c, pu, sub);
                detailBox.getChildren().add(row);
                i++;
            }
        } else {
            Label empty = new Label("Sin detalle disponible");
            empty.setPadding(new Insets(14));
            empty.setStyle("-fx-text-fill: #6b7280;");
            detailBox.getChildren().add(empty);
        }

        // Totals
        VBox totals = new VBox(8);
        totals.setPadding(new Insets(16, 22, 18, 22));
        totals.getChildren().add(totalRow("Op. Gravada / Base", money(base), false));
        totals.getChildren().add(totalRow("IGV (18%)", money(igv), false));

        Separator sep = new Separator();
        sep.setStyle("-fx-opacity: 0.7;");

        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        totalRow.setPadding(new Insets(10, 12, 10, 12));
        totalRow.setStyle("-fx-background-color: #ecfdf5; -fx-background-radius: 10; -fx-border-color: #a7f3d0; -fx-border-radius: 10;");
        Label totalLbl = new Label("TOTAL PAGADO");
        totalLbl.setStyle("-fx-text-fill: #065f46; -fx-font-weight: bold; -fx-font-size: 13px;");
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);
        Label totalVal = new Label("S/ " + String.format(Locale.US, "%.2f", total));
        totalVal.setStyle("-fx-text-fill: #065f46; -fx-font-weight: bold; -fx-font-size: 20px;");
        totalRow.getChildren().addAll(totalLbl, sp2, totalVal);

        totals.getChildren().addAll(sep, totalRow);

        // Footer
        VBox footer = new VBox(3);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(14, 22, 20, 22));
        footer.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 0 0 14 14; -fx-border-color: #f3f4f6; -fx-border-width: 1 0 0 0;");
        Label thanks = new Label("¡Gracias por su compra!");
        thanks.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-font-size: 13px;");
        Label note = new Label("Conserve este comprobante como constancia de pago.");
        note.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");
        footer.getChildren().addAll(thanks, note);

        card.getChildren().addAll(header, info, detailBox, totals, footer);
        return new VBox(card) {{ setAlignment(Pos.TOP_CENTER); setPadding(new Insets(8)); }};
    }

    private void addInfo(GridPane grid, int row, String key, String value) {
        Label k = new Label(key);
        k.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px; -fx-font-weight: bold;");
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: #111827; -fx-font-size: 12px;");
        v.setWrapText(true);
        grid.add(k, 0, row);
        grid.add(v, 1, row);
    }

    private Label headerLabel(String text, double width) {
        Label l = new Label(text);
        l.setMinWidth(width);
        l.setPrefWidth(width);
        l.setMaxWidth(width);
        l.setStyle("-fx-text-fill: #374151; -fx-font-size: 11px; -fx-font-weight: bold;");
        return l;
    }

    private Label bodyLabel(String text, double width) {
        Label l = new Label(text);
        l.setMinWidth(width);
        l.setPrefWidth(width);
        l.setMaxWidth(width);
        l.setStyle("-fx-text-fill: #374151; -fx-font-size: 12px;");
        return l;
    }

    private HBox totalRow(String label, String value, boolean bold) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 12px;" + (bold ? "-fx-font-weight: bold;" : ""));
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: #111827; -fx-font-size: 12px;" + (bold ? "-fx-font-weight: bold;" : ""));
        row.getChildren().addAll(l, sp, v);
        return row;
    }

    private String money(double v) { return "S/ " + String.format(Locale.US, "%.2f", v); }
    private String safe(String s) { return s == null ? "" : s; }
}
