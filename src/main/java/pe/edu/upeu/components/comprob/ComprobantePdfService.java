package pe.edu.upeu.components.comprob;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import pe.edu.upeu.dto.comprobante.Comprobante;

import java.io.FileOutputStream;
import java.nio.file.Path;

public class ComprobantePdfService {

    public void exportar(Comprobante comprobante, Path rutaArchivo) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo.toFile()));

        document.open();
        document.add(new Paragraph("========================================"));
        document.add(new Paragraph("        SYSVENTAS - COMPROBANTE         "));
        document.add(new Paragraph("========================================"));
        if (comprobante != null) {
            document.add(new Paragraph("Serie-Número: " + safe(comprobante.getSerie()) + "-" + safe(comprobante.getNumero())));
            document.add(new Paragraph("Fecha: " + safe(comprobante.getFecha())));
            document.add(new Paragraph("Cliente: " + safe(comprobante.getClienteNombre())));
            document.add(new Paragraph("DNI/RUC: " + safe(comprobante.getClienteDniRuc())));
            document.add(new Paragraph("----------------------------------------"));
            if (comprobante.getItems() != null) {
                for (Comprobante.DetalleItem item : comprobante.getItems()) {
                    String line = String.format(java.util.Locale.US, "%d x %s  P.U. %.2f  Subt. %.2f",
                            item.getCantidad(), safe(item.getDescripcion()), item.getPrecioUnitario(), item.getSubtotal());
                    document.add(new Paragraph(line));
                }
            }
            document.add(new Paragraph("----------------------------------------"));
            document.add(new Paragraph(String.format(java.util.Locale.US, "TOTAL: S/ %.2f", comprobante.getTotal())));
        }
        document.add(new Paragraph("========================================"));

        document.close();
    }

    private String safe(String s) { return s == null ? "" : s; }
}