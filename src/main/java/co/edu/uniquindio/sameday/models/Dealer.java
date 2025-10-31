package co.edu.uniquindio.sameday.models;

public class Dealer extends Person {
    private String zonaCobertura;

    public Dealer(String id, String nombre, String correo, String telefono,User user) {
        super(id, nombre, correo, telefono,user);
        this.zonaCobertura = zonaCobertura;
    }

    public String getZonaCobertura() {
        return zonaCobertura;
    }

    public void setZonaCobertura(String zonaCobertura) {
        this.zonaCobertura = zonaCobertura;
    }
}
