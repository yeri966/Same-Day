package co.edu.uniquindio.sameday.models;

public class Admin extends Person {
    private String cargo;

    public Admin(String id, String nombre, String correo, String telefono,User user){
        super(id,nombre,correo,telefono,user);
        this.cargo=cargo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
