package co.edu.uniquindio.sameday.models;

public class Client extends Person{
    private String direccion;

    public Client(String id, String nombre, String correo, String telefono,String direccion,User user){
        super(id,nombre,correo,telefono,user);
        this.direccion=direccion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

}
