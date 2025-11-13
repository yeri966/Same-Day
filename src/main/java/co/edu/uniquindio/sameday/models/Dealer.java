package co.edu.uniquindio.sameday.models;

public class Dealer extends Person {
    private boolean disponible;
    private City city;

    public Dealer(String id, String documento, String nombre, String correo, String telefono, UserAccount userAccount, boolean disponible,
                  City city){
        super(id,documento,nombre, correo,telefono, userAccount);
        this.disponible=disponible;
        this.city=city;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}