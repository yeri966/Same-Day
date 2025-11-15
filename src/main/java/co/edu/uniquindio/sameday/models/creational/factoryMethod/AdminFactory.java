package co.edu.uniquindio.sameday.models.creational.factoryMethod;


import co.edu.uniquindio.sameday.models.Admin;
import co.edu.uniquindio.sameday.models.Person;
import co.edu.uniquindio.sameday.models.UserAccount;

public class AdminFactory implements PersonFactory {

    private String cargo;

    public AdminFactory(String cargo){
        this.cargo=cargo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    @Override
    public Person crearPerson(String id, String documento, String nombre, String correo, String telefono, UserAccount userAccount) {
        Admin admin = new Admin(id,documento,nombre,correo,telefono,userAccount,cargo);
        if(userAccount!=null){
            userAccount.setPerson(admin);
        }
        return admin;
    }
}
