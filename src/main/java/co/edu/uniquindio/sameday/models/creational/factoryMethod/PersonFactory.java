package co.edu.uniquindio.sameday.models.creational.factoryMethod;


import co.edu.uniquindio.sameday.models.Person;
import co.edu.uniquindio.sameday.models.UserAccount;

public interface PersonFactory {
    Person crearPerson(String id, String documento, String nombre, String correo, String telefono, UserAccount userAccount);

}