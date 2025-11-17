package co.edu.uniquindio.sameday.models.behavioral.state;

public class EstadoEsperandoCredenciales implements EstadoLogin {

    @Override
    public void manejar(ContextoLogin contexto) {
        // Estado inicial, esperando que el usuario ingrese datos
    }

    @Override
    public String getMensaje() {
        return "Ingrese sus credenciales";
    }

    @Override
    public boolean puedeIntentarLogin() {
        return true;
    }
}