package co.edu.uniquindio.sameday.models;

public class UserAccount {
    private String user;
    private String contrasenia;
    private Person person;
    private TypeUser typeUser;


    public UserAccount(String user, String contrasenia, Person person, TypeUser typeUser){
        this.user=user;
        this.contrasenia=contrasenia;
        this.person=person;
        this.typeUser=typeUser;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public TypeUser getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(TypeUser typeUser) {
        this.typeUser = typeUser;
    }
}

