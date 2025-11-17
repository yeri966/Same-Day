package co.edu.uniquindio.sameday.models.behavioral.state;

public class EstadoValidando implements EstadoLogin {

    @Override
    public void manejar(ContextoLogin contexto) {
        // El controlador validará las credenciales y cambiará al siguiente estado
    }

    @Override
    public String getMensaje() {
        return "Validando credenciales...";
    }

    @Override
    public boolean puedeIntentarLogin() {
        return false;
    }
}