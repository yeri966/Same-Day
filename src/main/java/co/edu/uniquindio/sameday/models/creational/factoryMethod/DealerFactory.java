package co.edu.uniquindio.sameday.models.creational.factoryMethod;

import co.edu.uniquindio.sameday.models.City;
import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.Person;
import co.edu.uniquindio.sameday.models.UserAccount;

public class DealerFactory implements PersonFactory{

    private boolean disponible;
    private City city;

    public DealerFactory(boolean disponible, City city){
        this.disponible=disponible;
        this.city=city;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public Person crearPerson(String id, String documento, String nombre, String correo, String telefono, UserAccount userAccount) {
        Dealer dealer=new Dealer(id,documento,nombre,correo,telefono,userAccount,disponible,city);
        if(userAccount!=null){
            userAccount.setPerson(dealer);
        }
        return dealer;
    }
}
