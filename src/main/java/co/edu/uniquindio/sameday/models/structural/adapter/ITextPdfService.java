package co.edu.uniquindio.sameday.models.structural.adapter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;

/**
 * PATRÓN ESTRUCTURAL: ADAPTER
 * Servicio que maneja la generación de PDFs usando Apache PDFBox
 */
public class ITextPdfService {

    private static final float MARGIN = 50;
    private static final float FONT_SIZE_TITLE = 18;
    private static final float FONT_SIZE_SECTION = 14;
    private static final float FONT_SIZE_NORMAL = 11;
    private static final float LINE_HEIGHT = 15;

    /**
     * Crea un documento PDF con formato profesional
     */
    public File createPdfDocument(String outputPath, PdfContent content) {
        PDDocument document = new PDDocument();

        try {
            // Crear el directorio si no existe
            File pdfFile = new File(outputPath);
            File parentDir = pdfFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Crear página
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float yPosition = page.getMediaBox().getHeight() - MARGIN;

            // Título principal
            yPosition = addTitle(contentStream, content.getTitle(), yPosition);
            yPosition -= 20;

            // Línea separadora
            addSeparatorLine(contentStream, yPosition);
            yPosition -= 20;

            // Contenido por secciones
            for (PdfSection section : content.getSections()) {
                // Verificar si necesitamos una nueva página
                if (yPosition < 100) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yPosition = page.getMediaBox().getHeight() - MARGIN;
                }

                // Título de sección
                yPosition = addSectionTitle(contentStream, section.getTitle(), yPosition);
                yPosition -= 10;

                // Campos de la sección
                for (PdfField field : section.getFields()) {
                    yPosition = addField(contentStream, field.getLabel(), field.getValue(), yPosition);
                    yPosition -= LINE_HEIGHT;
                }

                yPosition -= 10; // Espacio entre secciones
            }

            // Footer
            addFooter(contentStream, page);

            contentStream.close();

            // Guardar documento
            document.save(pdfFile);
            document.close();

            System.out.println("✅ PDF generado exitosamente: " + pdfFile.getAbsolutePath());
            return pdfFile;

        } catch (IOException e) {
            System.err.println("❌ Error generando PDF: " + e.getMessage());
            e.printStackTrace();

            try {
                document.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return null;
        }
    }

    private float addTitle(PDPageContentStream contentStream, String title, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, FONT_SIZE_TITLE);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(title);
        contentStream.endText();
        return yPosition - 25;
    }

    private float addSectionTitle(PDPageContentStream contentStream, String title, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, FONT_SIZE_SECTION);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(title);
        contentStream.endText();
        return yPosition - 18;
    }

    private float addField(PDPageContentStream contentStream, String label, String value, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, FONT_SIZE_NORMAL);
        contentStream.newLineAtOffset(MARGIN + 10, yPosition);
        contentStream.showText(label + ": ");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE_NORMAL);
        contentStream.newLineAtOffset(MARGIN + 150, yPosition);

        // Manejar texto largo (wrap)
        String displayValue = value != null ? value : "N/A";
        if (displayValue.length() > 50) {
            displayValue = displayValue.substring(0, 47) + "...";
        }

        contentStream.showText(displayValue);
        contentStream.endText();

        return yPosition;
    }

    private void addSeparatorLine(PDPageContentStream contentStream, float yPosition) throws IOException {
        contentStream.setLineWidth(1f);
        contentStream.moveTo(MARGIN, yPosition);
        contentStream.lineTo(PDRectangle.A4.getWidth() - MARGIN, yPosition);
        contentStream.stroke();
    }

    private void addFooter(PDPageContentStream contentStream, PDPage page) throws IOException {
        float footerY = 30;

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 9);
        contentStream.newLineAtOffset(MARGIN, footerY);
        contentStream.showText("SAMEDAY - Sistema de Logistica");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 8);
        contentStream.newLineAtOffset(PDRectangle.A4.getWidth() - MARGIN - 150, footerY);
        contentStream.showText("Generado: " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        contentStream.endText();
    }

    /**
     * Clases auxiliares para estructurar el contenido del PDF
     */
    public static class PdfContent {
        private String title;
        private java.util.List<PdfSection> sections = new java.util.ArrayList<>();

        public PdfContent(String title) {
            this.title = title;
        }

        public void addSection(PdfSection section) {
            sections.add(section);
        }

        public String getTitle() { return title; }
        public java.util.List<PdfSection> getSections() { return sections; }
    }

    public static class PdfSection {
        private String title;
        private java.util.List<PdfField> fields = new java.util.ArrayList<>();

        public PdfSection(String title) {
            this.title = title;
        }

        public void addField(String label, String value) {
            fields.add(new PdfField(label, value));
        }

        public String getTitle() { return title; }
        public java.util.List<PdfField> getFields() { return fields; }
    }

    public static class PdfField {
        private String label;
        private String value;

        public PdfField(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() { return label; }
        public String getValue() { return value; }
    }
}