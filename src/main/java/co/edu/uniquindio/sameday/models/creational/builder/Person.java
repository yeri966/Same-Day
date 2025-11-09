package co.edu.uniquindio.sameday.models.creational.builder;

import co.edu.uniquindio.sameday.models.UserAccount;

public class Person {
    private String id;
    private String nombre;
    private String correo;
    private String telefono;
    private UserAccount userAccount;


    public Person(String id, String nombre, String correo, String telefono, UserAccount userAccount) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.userAccount = userAccount;

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

    public UserAccount getUser() {
        return userAccount;
    }

    public void setUser(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
}
