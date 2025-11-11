package co.edu.uniquindio.sameday.models;

public class Person {
    private String id;
    private String documento;
    private String nombre;
    private String correo;
    private String telefono;
    private UserAccount userAccount;


    public Person(String id, String documento,String nombre, String correo, String telefono, UserAccount userAccount) {
        this.id = id;
        this.documento=documento;
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

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
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

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
}
