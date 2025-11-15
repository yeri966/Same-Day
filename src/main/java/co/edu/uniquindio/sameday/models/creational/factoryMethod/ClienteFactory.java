package co.edu.uniquindio.sameday.models.creational.factoryMethod;

import co.edu.uniquindio.sameday.models.Client;
import co.edu.uniquindio.sameday.models.Person;
import co.edu.uniquindio.sameday.models.UserAccount;

public class ClienteFactory implements PersonFactory {

    private String direccion;

    public ClienteFactory(String direccion){
        this.direccion=direccion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public Person crearPerson(String id, String documento, String nombre, String correo, String telefono, UserAccount userAccount) {
        Client client = new Client(id,documento,nombre,correo,telefono,direccion,userAccount);
        if(userAccount!=null){
            userAccount.setPerson(client);
        }
        return client;
    }
}
