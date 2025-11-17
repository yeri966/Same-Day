package co.edu.uniquindio.sameday.models.behavioral.state;

public interface EstadoLogin {
    void manejar(ContextoLogin contexto);
    String getMensaje();
    boolean puedeIntentarLogin();
}