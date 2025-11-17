package co.edu.uniquindio.sameday.models.behavioral.state;

public class ContextoLogin {
    private EstadoLogin estadoActual;
    private int intentosFallidos;
    private long tiempoBloqueado;
    private static final int MAX_INTENTOS = 3;
    private static final long TIEMPO_BLOQUEO_MS = 30000; // 30 segundos

    public ContextoLogin() {
        this.estadoActual = new EstadoEsperandoCredenciales();
        this.intentosFallidos = 0;
        this.tiempoBloqueado = 0;
    }

    public void setEstado(EstadoLogin nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }

    public EstadoLogin getEstado() {
        return estadoActual;
    }

    public void incrementarIntentosFallidos() {
        intentosFallidos++;
    }

    public void resetearIntentos() {
        intentosFallidos = 0;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public int getMaxIntentos() {
        return MAX_INTENTOS;
    }

    public void iniciarBloqueo() {
        tiempoBloqueado = System.currentTimeMillis();
    }

    public long getTiempoRestanteBloqueo() {
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoBloqueado;
        long restante = TIEMPO_BLOQUEO_MS - tiempoTranscurrido;
        return restante > 0 ? restante : 0;
    }

    public boolean estaBloqueado() {
        return getTiempoRestanteBloqueo() > 0;
    }

    public void manejarEstado() {
        estadoActual.manejar(this);
    }

    public String getMensaje() {
        return estadoActual.getMensaje();
    }

    public boolean puedeIntentarLogin() {
        return estadoActual.puedeIntentarLogin();
    }
}