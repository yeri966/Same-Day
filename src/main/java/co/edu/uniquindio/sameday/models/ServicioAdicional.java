package co.edu.uniquindio.sameday.models;

public enum ServicioAdicional {
    SEGURO,
    FRAGIL,
    FIRMA_REQUERIDA,
    PRIORIDAD;

    @Override
    public String toString() {
        switch (this) {
            case SEGURO: return "Seguro";
            case FRAGIL: return "Frágil";
            case FIRMA_REQUERIDA: return "Firma Requerida";
            case PRIORIDAD: return "Prioridad";
            default: return name();
        }
    }
}