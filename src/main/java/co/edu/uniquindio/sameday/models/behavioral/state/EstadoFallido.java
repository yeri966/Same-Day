package co.edu.uniquindio.sameday.models.behavioral.state;

public class EstadoFallido implements EstadoLogin {

    @Override
    public void manejar(ContextoLogin contexto) {
        contexto.incrementarIntentosFallidos();

        if (contexto.getIntentosFallidos() >= contexto.getMaxIntentos()) {
            contexto.iniciarBloqueo();
            contexto.setEstado(new EstadoBloqueado());
        } else {
            contexto.setEstado(new EstadoEsperandoCredenciales());
        }
    }

    @Override
    public String getMensaje() {
        return "Credenciales incorrectas";
    }

    @Override
    public boolean puedeIntentarLogin() {
        return false;
    }
}