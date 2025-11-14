package co.edu.uniquindio.sameday.models.structural.adapter;

import co.edu.uniquindio.sameday.models.Envio;
import java.io.File;

/**
 * PATRÓN ESTRUCTURAL: ADAPTER
 * Interface que define el contrato para generar PDFs
 */
public interface PdfGenerator {
    /**
     * Genera un PDF con los detalles del envío
     * @param envio El envío a documentar
     * @param outputPath Ruta donde se guardará el PDF
     * @return El archivo PDF generado
     */
    File generateEnvioPdf(Envio envio, String outputPath);

    /**
     * Genera un PDF con múltiples envíos (reporte)
     * @param envios Lista de envíos
     * @param outputPath Ruta donde se guardará el PDF
     * @return El archivo PDF generado
     */
    File generateEnviosReport(java.util.List<Envio> envios, String outputPath);
}