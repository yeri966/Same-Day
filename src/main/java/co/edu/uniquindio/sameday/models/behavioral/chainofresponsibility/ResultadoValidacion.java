package co.edu.uniquindio.sameday.models.behavioral.chainofresponsibility;

/**
 * PATRÓN COMPORTAMENTAL: CHAIN OF RESPONSIBILITY
 *
 * Clase que encapsula el resultado de una validación en la cadena.
 * Contiene información sobre si la validación fue exitosa, el mensaje
 * descriptivo y el validador que generó el resultado.
 */
public class ResultadoValidacion {

    private boolean valido;
    private String mensaje;
    private String validadorOrigen;

    /**
     * Constructor completo
     * @param valido Si la validación fue exitosa
     * @param mensaje Mensaje descriptivo del resultado
     * @param validadorOrigen Nombre del validador que generó el resultado
     */
    public ResultadoValidacion(boolean valido, String mensaje, String validadorOrigen) {
        this.valido = valido;
        this.mensaje = mensaje;
        this.validadorOrigen = validadorOrigen;
    }

    /**
     * @return true si la validación fue exitosa
     */
    public boolean isValido() {
        return valido;
    }

    /**
     * @return Mensaje descriptivo del resultado
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @return Nombre del validador que generó este resultado
     */
    public String getValidadorOrigen() {
        return validadorOrigen;
    }

    /**
     * Crea un resultado exitoso
     * @param mensaje Mensaje de éxito
     * @return ResultadoValidacion exitoso
     */
    public static ResultadoValidacion exito(String mensaje) {
        return new ResultadoValidacion(true, mensaje, "Final");
    }

    /**
     * Crea un resultado fallido
     * @param mensaje Mensaje de error
     * @param origen Nombre del validador que falló
     * @return ResultadoValidacion fallido
     */
    public static ResultadoValidacion fallo(String mensaje, String origen) {
        return new ResultadoValidacion(false, mensaje, origen);
    }

    @Override
    public String toString() {
        return "ResultadoValidacion{" +
                "valido=" + valido +
                ", mensaje='" + mensaje + '\'' +
                ", validadorOrigen='" + validadorOrigen + '\'' +
                '}';
    }
}