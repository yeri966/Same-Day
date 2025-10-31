package co.edu.uniquindio.sameday.models;

public class Person {
    private String id;
    private String nombre;
    private String correo;
    private String telefono;
    private User user;


    public Person(String id, String nombre, String correo, String telefono,User user) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.user=user;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
