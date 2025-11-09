package co.edu.uniquindio.sameday.models.structural.decorator;

/**
 * Decorador concreto que añade el servicio de FRÁGIL al envío
 */
public class FragilDecorator extends ServicioDecorator {
    private static final double COSTO_FRAGIL = 3000.0;

    public FragilDecorator(EnvioComponent envio) {
        super(envio);
    }

    @Override
    public double calcularCosto() {
        return envio.calcularCosto() + COSTO_FRAGIL;
    }

    @Override
    public String getDescripcion() {
        return envio.getDescripcion() + "\n  + Frágil ($3,000)";
    }
}