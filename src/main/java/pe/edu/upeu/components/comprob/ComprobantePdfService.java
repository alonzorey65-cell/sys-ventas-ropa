package pe.edu.upeu.components.comprob;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import pe.edu.upeu.dto.comprobante.Comprobante;

import java.awt.Color;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Locale;

public class ComprobantePdfService {

    public void exportar(Comprobante comprobante, Path rutaArchivo) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo.toFile()));
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(15, 118, 110));
        Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(90, 90, 90));
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(70, 70, 70));
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(6, 95, 70));

        Paragraph title = new Paragraph("SYSVENTAS", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph sub = new Paragraph("COMPROBANTE DE PAGO", subFont);
        sub.setAlignment(Element.ALIGN_CENTER);
        sub.setSpacingAfter(8);
        document.add(sub);

        String serie = safe(comprobante != null ? comprobante.getSerie() : "B001");
        String numero = safe(comprobante != null ? comprobante.getNumero() : "000001");
        String fecha = safe(comprobante != null ? comprobante.getFecha() : "");
        String cliente = safe(comprobante != null ? comprobante.getClienteNombre() : "Público General");
        String dni = safe(comprobante != null ? comprobante.getClienteDniRuc() : "-");
        String direccion = safe(comprobante != null ? comprobante.getClienteDireccion() : "-");
        double total = comprobante != null ? comprobante.getTotal() : 0.0;
        double base = total / 1.18;
        double igv = total - base;

        Paragraph num = new Paragraph("N° " + serie + "-" + numero, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.DARK_GRAY));
        num.setAlignment(Element.ALIGN_CENTER);
        num.setSpacingAfter(12);
        document.add(num);

        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(100);
        info.setWidths(new float[]{1.2f, 3.8f});
        info.setSpacingAfter(12);
        addInfoRow(info, "Fecha:", fecha, labelFont, valueFont);
        addInfoRow(info, "Cliente:", cliente.isBlank() ? "Público General" : cliente, labelFont, valueFont);
        addInfoRow(info, etiquetaDocumento(dni) + ":", dni.isBlank() ? "-" : dni, labelFont, valueFont);
        addInfoRow(info, "Dirección:", direccion.isBlank() ? "-" : direccion, labelFont, valueFont);
        String estado = safe(comprobante != null ? comprobante.getEstado() : "ACTIVO");
        if (estado.isBlank()) estado = "ACTIVO";
        addInfoRow(info, "Estado:", "ANULADO".equalsIgnoreCase(estado) ? "ANULADO" : "PAGADO", labelFont, valueFont);
        document.add(info);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4.5f, 1f, 1.5f, 1.5f});
        table.setSpacingBefore(5);
        addHeaderCell(table, "Descripción", headFont);
        addHeaderCell(table, "Cant.", headFont);
        addHeaderCell(table, "P. Unit", headFont);
        addHeaderCell(table, "Subtotal", headFont);

        if (comprobante != null && comprobante.getItems() != null && !comprobante.getItems().isEmpty()) {
            boolean alt = false;
            for (Comprobante.DetalleItem item : comprobante.getItems()) {
                Color bg = alt ? new Color(250, 250, 250) : Color.WHITE;
                addBodyCell(table, safe(item.getDescripcion()), Element.ALIGN_LEFT, bg);
                addBodyCell(table, String.valueOf(item.getCantidad()), Element.ALIGN_CENTER, bg);
                addBodyCell(table, money(item.getPrecioUnitario()), Element.ALIGN_RIGHT, bg);
                addBodyCell(table, money(item.getSubtotal()), Element.ALIGN_RIGHT, bg);
                alt = !alt;
            }
        } else {
            PdfPCell empty = new PdfPCell(new Phrase("Sin detalle disponible", valueFont));
            empty.setColspan(4);
            empty.setPadding(8);
            table.addCell(empty);
        }
        document.add(table);

        PdfPTable totals = new PdfPTable(2);
        totals.setWidthPercentage(45);
        totals.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totals.setSpacingBefore(12);
        totals.setWidths(new float[]{2f, 1.5f});
        addTotalRow(totals, "Op. Gravada / Base", money(base), labelFont, valueFont, false);
        addTotalRow(totals, "IGV (18%)", money(igv), labelFont, valueFont, false);
        addTotalRow(totals, "TOTAL PAGADO", "S/ " + String.format(Locale.US, "%.2f", total), totalFont, totalFont, true);
        document.add(totals);

        document.add(new Paragraph(" "));
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(new Color(220, 220, 220));
        document.add(new Chunk(ls));

        Paragraph thanks = new Paragraph("¡Gracias por su compra!", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(60, 60, 60)));
        thanks.setAlignment(Element.ALIGN_CENTER);
        thanks.setSpacingBefore(10);
        document.add(thanks);
        Paragraph note = new Paragraph("Conserve este comprobante como constancia de pago.", subFont);
        note.setAlignment(Element.ALIGN_CENTER);
        document.add(note);

        document.close();
    }

    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell l = new PdfPCell(new Phrase(label, labelFont));
        l.setBorder(Rectangle.NO_BORDER);
        l.setPadding(4);
        PdfPCell v = new PdfPCell(new Phrase(value, valueFont));
        v.setBorder(Rectangle.NO_BORDER);
        v.setPadding(4);
        table.addCell(l);
        table.addCell(v);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(31, 41, 55));
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, int align, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY)));
        cell.setPadding(7);
        cell.setHorizontalAlignment(align);
        cell.setBackgroundColor(bg);
        cell.setBorderColor(new Color(230, 230, 230));
        table.addCell(cell);
    }

    private void addTotalRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont, boolean highlight) {
        PdfPCell l = new PdfPCell(new Phrase(label, labelFont));
        l.setPadding(6);
        l.setBorder(Rectangle.NO_BORDER);
        if (highlight) l.setBackgroundColor(new Color(236, 253, 245));
        PdfPCell v = new PdfPCell(new Phrase(value, valueFont));
        v.setPadding(6);
        v.setHorizontalAlignment(Element.ALIGN_RIGHT);
        v.setBorder(Rectangle.NO_BORDER);
        if (highlight) v.setBackgroundColor(new Color(236, 253, 245));
        table.addCell(l);
        table.addCell(v);
    }

    private String etiquetaDocumento(String doc) {
        String d = safe(doc).trim();
        if (d.matches("\\d{11}")) return "RUC";
        if (d.matches("\\d{8}")) return "DNI";
        return "DNI/RUC";
    }

    private String money(double v) { return String.format(Locale.US, "S/ %.2f", v); }
    private String safe(String s) { return s == null ? "" : s; }
}
