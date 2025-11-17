package co.edu.uniquindio.sameday.models.behavioral.state;

public class EstadoBloqueado implements EstadoLogin {

    @Override
    public void manejar(ContextoLogin contexto) {
        if (!contexto.estaBloqueado()) {
            contexto.resetearIntentos();
            contexto.setEstado(new EstadoEsperandoCredenciales());
        }
    }

    @Override
    public String getMensaje() {
        return "Cuenta bloqueada temporalmente por múltiples intentos fallidos";
    }

    @Override
    public boolean puedeIntentarLogin() {
        return false;
    }
}