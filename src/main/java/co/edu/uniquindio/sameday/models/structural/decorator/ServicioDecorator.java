package co.edu.uniquindio.sameday.models.structural.decorator;

/**
 * PATRÓN ESTRUCTURAL: DECORATOR
 * Decorador abstracto que envuelve un componente de envío y añade funcionalidad adicional
 */
public abstract class ServicioDecorator implements EnvioComponent {
    protected EnvioComponent envio;

    public ServicioDecorator(EnvioComponent envio) {
        this.envio = envio;
    }

    @Override
    public double calcularCosto() {
        return envio.calcularCosto();
    }

    @Override
    public String getDescripcion() {
        return envio.getDescripcion();
    }
}