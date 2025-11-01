package co.edu.uniquindio.sameday.models;

public class User {
    private String usuario;
    private String contrasenia;
    private Person person;
    private TipoUsuario tipoUsuario;

    public User(String usuario,String contrasenia,Person person, TipoUsuario tipoUsuario){
        this.usuario=usuario;
        this.contrasenia=contrasenia;
        this.person=person;
        this.tipoUsuario=tipoUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
