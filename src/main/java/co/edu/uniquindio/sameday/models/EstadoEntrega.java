package co.edu.uniquindio.sameday.models;

public enum EstadoEntrega {
    ASIGNADO("Asignado", "📋"),
    RECOGIDO("Recogido", "📦"),
    EN_RUTA("En Ruta", "🚚"),
    ENTREGADO("Entregado", "✅"),
    CON_INCIDENCIA("Con Incidencia", "⚠️");

    private final String displayName;
    private final String emoji;

    EstadoEntrega(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmoji() {
        return emoji;
    }

    @Override
    public String toString() {
        return emoji + " " + displayName;
    }
}