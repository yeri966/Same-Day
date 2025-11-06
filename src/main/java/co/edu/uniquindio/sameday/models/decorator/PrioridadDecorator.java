package co.edu.uniquindio.sameday.models.decorator;

/**
 * Decorador concreto que añade el servicio de PRIORIDAD al envío
 */
class PrioridadDecorator extends ServicioDecorator {
    private static final double COSTO_PRIORIDAD = 8000.0;

    public PrioridadDecorator(EnvioComponent envio) {
        super(envio);
    }

    @Override
    public double calcularCosto() {
        return envio.calcularCosto() + COSTO_PRIORIDAD;
    }

    @Override
    public String getDescripcion() {
        return envio.getDescripcion() + "\n  + Prioridad ($8,000)";
    }
}