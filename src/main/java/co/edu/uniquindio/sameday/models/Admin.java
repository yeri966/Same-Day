package co.edu.uniquindio.sameday.models;

public class Admin extends Person {
    private String cargo;

    public Admin (String id, String documento, String nombre, String correo, String telefono, UserAccount userAccount,String cargo){
        super(id,documento,nombre,correo,telefono,userAccount);
        this.cargo=cargo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
