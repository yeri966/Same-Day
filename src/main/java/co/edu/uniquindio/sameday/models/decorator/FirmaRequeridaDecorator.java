package co.edu.uniquindio.sameday.models.decorator;

/**
 * Decorador concreto que añade el servicio de FIRMA REQUERIDA al envío
 * Este servicio no tiene costo adicional
 */
public class FirmaRequeridaDecorator extends ServicioDecorator {
    public FirmaRequeridaDecorator(EnvioComponent envio) {
        super(envio);
    }

    @Override
    public double calcularCosto() {
        return envio.calcularCosto(); // No tiene costo adicional
    }

    @Override
    public String getDescripcion() {
        return envio.getDescripcion() + "\n  + Firma Requerida (Gratis)";
    }
}