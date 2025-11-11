package co.edu.uniquindio.sameday.models;

public class Client extends Person {
    private String direccion;

    public Client(String id, String documento, String nombre, String correo, String telefono, String direccion, UserAccount userAccount) {
        super(id, documento,nombre,correo,telefono,userAccount);
        this.direccion = direccion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
