package co.edu.uniquindio.sameday.models.structural.facade;

/**
 * PATRÓN ESTRUCTURAL: FACADE
 * Clase auxiliar para retornar resultados de operaciones del Facade
 */
public class ResultadoOperacion {
    private boolean exitoso;
    private String mensaje;
    private Object dato;

    public ResultadoOperacion(boolean exitoso, String mensaje) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
    }

    public ResultadoOperacion(boolean exitoso, String mensaje, Object dato) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.dato = dato;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Object getDato() {
        return dato;
    }

    public static ResultadoOperacion exito(String mensaje) {
        return new ResultadoOperacion(true, mensaje);
    }

    public static ResultadoOperacion exitoConDato(String mensaje, Object dato) {
        return new ResultadoOperacion(true, mensaje, dato);
    }

    public static ResultadoOperacion error(String mensaje) {
        return new ResultadoOperacion(false, mensaje);
    }
}