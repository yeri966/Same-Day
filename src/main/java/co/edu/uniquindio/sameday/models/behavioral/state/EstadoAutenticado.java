package co.edu.uniquindio.sameday.models.behavioral.state;

public class EstadoAutenticado implements EstadoLogin {

    @Override
    public void manejar(ContextoLogin contexto) {
        contexto.resetearIntentos();
    }

    @Override
    public String getMensaje() {
        return "¡Autenticación exitosa! Redirigiendo...";
    }

    @Override
    public boolean puedeIntentarLogin() {
        return false;
    }
}