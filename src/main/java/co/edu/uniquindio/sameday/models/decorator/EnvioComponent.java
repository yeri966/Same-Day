package co.edu.uniquindio.sameday.models.decorator;

/**
 * PATRÓN ESTRUCTURAL: DECORATOR
 * Interfaz base que representa un componente de envío que puede tener servicios adicionales
 */
public interface EnvioComponent {

    double calcularCosto();
    String getDescripcion();
}