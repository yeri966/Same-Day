package co.edu.uniquindio.sameday.models.decorator;

/**
 * Decorador concreto que añade el servicio de SEGURO al envío
 */
public class SeguroDecorator extends ServicioDecorator {
    private static final double COSTO_SEGURO = 5000.0;

    public SeguroDecorator(EnvioComponent envio) {
        super(envio);
    }

    @Override
    public double calcularCosto() {
        return envio.calcularCosto() + COSTO_SEGURO;
    }

    @Override
    public String getDescripcion() {
        return envio.getDescripcion() + "\n  + Seguro ($5,000)";
    }
}