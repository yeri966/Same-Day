package co.edu.uniquindio.sameday.models.structural.adapter;

import co.edu.uniquindio.sameday.models.Envio;
import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.EstadoEntrega;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PATRÓN ESTRUCTURAL: ADAPTER
 * Adapta la estructura de Envio a un formato compatible con el generador de PDF
 */
public class EnvioPdfAdapter implements PdfGenerator {

    private ITextPdfService pdfService;

    public EnvioPdfAdapter() {
        this.pdfService = new ITextPdfService();
    }

    @Override
    public File generateEnvioPdf(Envio envio, String outputPath) {
        // Crear contenido estructurado del PDF
        ITextPdfService.PdfContent content = new ITextPdfService.PdfContent(
                "COMPROBANTE DE ENVIO - SAMEDAY"
        );

        // Sección: Información General
        ITextPdfService.PdfSection seccionGeneral = new ITextPdfService.PdfSection("INFORMACION GENERAL");
        seccionGeneral.addField("Numero de Rastreo", envio.getId());
        seccionGeneral.addField("Fecha de Creacion", formatDate(envio.getFechaCreacion()));
        seccionGeneral.addField("Estado de Pago", envio.getEstado());
        content.addSection(seccionGeneral);

        // Sección: Origen
        ITextPdfService.PdfSection seccionOrigen = new ITextPdfService.PdfSection("ORIGEN");
        seccionOrigen.addField("Direccion",
                envio.getOrigen() != null ? envio.getOrigen().getFullAddress() : "No especificado");
        content.addSection(seccionOrigen);

        // Sección: Destino
        ITextPdfService.PdfSection seccionDestino = new ITextPdfService.PdfSection("DESTINO");
        seccionDestino.addField("Direccion",
                envio.getDestino() != null ? envio.getDestino().getFullAddress() : "No especificado");
        content.addSection(seccionDestino);

        // Sección: Destinatario
        ITextPdfService.PdfSection seccionDestinatario = new ITextPdfService.PdfSection("DESTINATARIO");
        seccionDestinatario.addField("Nombre", envio.getNombreDestinatario());
        seccionDestinatario.addField("Cedula", envio.getCedulaDestinatario());
        seccionDestinatario.addField("Telefono", envio.getTelefonoDestinatario());
        content.addSection(seccionDestinatario);

        // Sección: Repartidor Asignado
        ITextPdfService.PdfSection seccionRepartidor = new ITextPdfService.PdfSection("REPARTIDOR ASIGNADO");
        if (envio.getRepartidorAsignado() != null) {
            Dealer repartidor = envio.getRepartidorAsignado();
            seccionRepartidor.addField("Nombre", repartidor.getNombre());
            seccionRepartidor.addField("ID", repartidor.getId());
            seccionRepartidor.addField("Ciudad", repartidor.getCity().toString());
        } else {
            seccionRepartidor.addField("Estado", "Sin repartidor asignado");
        }
        content.addSection(seccionRepartidor);

        // Sección: Estado de Entrega
        ITextPdfService.PdfSection seccionEstado = new ITextPdfService.PdfSection("ESTADO DE ENTREGA");
        if (envio.getRepartidorAsignado() != null && envio.getEstadoEntrega() != null) {
            seccionEstado.addField("Estado", envio.getEstadoEntregaString());
            seccionEstado.addField("Ultima actualizacion", formatDate(envio.getFechaActualizacionEstado()));

            if (envio.getObservaciones() != null && !envio.getObservaciones().trim().isEmpty()) {
                seccionEstado.addField("Observaciones", envio.getObservaciones());
            }
        } else if (envio.getRepartidorAsignado() != null) {
            seccionEstado.addField("Estado", "Asignado (pendiente de actualizacion)");
        } else {
            seccionEstado.addField("Estado", "Sin repartidor asignado");
        }
        content.addSection(seccionEstado);

        // Sección: Información del Paquete
        ITextPdfService.PdfSection seccionPaquete = new ITextPdfService.PdfSection("INFORMACION DEL PAQUETE");
        seccionPaquete.addField("Contenido", envio.getContenido());
        seccionPaquete.addField("Peso", String.format("%.2f kg", envio.getPeso()));
        seccionPaquete.addField("Dimensiones", envio.getDimensiones());
        seccionPaquete.addField("Volumen", String.format("%.2f cm3", envio.getVolumen()));
        seccionPaquete.addField("Servicios Adicionales", envio.getServiciosAdicionalesString());
        content.addSection(seccionPaquete);

        // Sección: Información Financiera
        ITextPdfService.PdfSection seccionFinanciera = new ITextPdfService.PdfSection("INFORMACION FINANCIERA");
        seccionFinanciera.addField("Costo Total", String.format("$%,.0f COP", envio.getCostoTotal()));
        seccionFinanciera.addField("Estado de Pago", envio.getEstado());
        content.addSection(seccionFinanciera);

        // Generar el PDF
        return pdfService.createPdfDocument(outputPath, content);
    }

    @Override
    public File generateEnviosReport(List<Envio> envios, String outputPath) {
        ITextPdfService.PdfContent content = new ITextPdfService.PdfContent(
                "REPORTE DE ENVIOS - SAMEDAY"
        );

        // Sección: Resumen
        ITextPdfService.PdfSection seccionResumen = new ITextPdfService.PdfSection("RESUMEN");
        seccionResumen.addField("Total de Envios", String.valueOf(envios.size()));
        seccionResumen.addField("Fecha de Generacion", formatDate(java.time.LocalDateTime.now()));
        content.addSection(seccionResumen);

        // Estadísticas
        long entregados = envios.stream()
                .filter(e -> e.getEstadoEntrega() == EstadoEntrega.ENTREGADO)
                .count();

        long pendientes = envios.size() - entregados;

        ITextPdfService.PdfSection seccionEstadisticas = new ITextPdfService.PdfSection("ESTADISTICAS");
        seccionEstadisticas.addField("Envios Entregados", String.valueOf(entregados));
        seccionEstadisticas.addField("Envios Pendientes", String.valueOf(pendientes));
        content.addSection(seccionEstadisticas);

        // Listar envíos (primeros 10)
        int maxEnvios = Math.min(envios.size(), 10);
        for (int i = 0; i < maxEnvios; i++) {
            Envio envio = envios.get(i);
            ITextPdfService.PdfSection seccionEnvio = new ITextPdfService.PdfSection(
                    "ENVIO #" + (i + 1) + " - " + envio.getId()
            );
            seccionEnvio.addField("Destinatario", envio.getNombreDestinatario());
            seccionEnvio.addField("Destino",
                    envio.getDestino() != null ? envio.getDestino().getCity().toString() : "N/A");
            seccionEnvio.addField("Estado", envio.getEstadoEntregaString());
            seccionEnvio.addField("Costo", String.format("$%,.0f", envio.getCostoTotal()));
            content.addSection(seccionEnvio);
        }

        return pdfService.createPdfDocument(outputPath, content);
    }

    private String formatDate(java.time.LocalDateTime date) {
        if (date == null) return "No especificada";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return date.format(formatter);
    }
}