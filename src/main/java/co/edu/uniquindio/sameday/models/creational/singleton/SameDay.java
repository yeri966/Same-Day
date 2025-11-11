package co.edu.uniquindio.sameday.models.creational.singleton;

import co.edu.uniquindio.sameday.models.*;
import co.edu.uniquindio.sameday.models.Admin;
import co.edu.uniquindio.sameday.models.Client;
import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.Person;

import java.util.ArrayList;

public class SameDay {

    private static SameDay instance;
    private Person userActive;
    private ArrayList<Person> listPersons;
    private ArrayList<Address> listAddresses;
    private ArrayList<Envio> listEnvios;

    private SameDay() {
        listPersons = new ArrayList<>();
        listAddresses = new ArrayList<>();
        listEnvios = new ArrayList<>();
        cargarDatos();
    }

    public static SameDay getInstance() {
        if (instance == null) {
            instance = new SameDay();
        }
        return instance;
    }

    public void setUserActive(Person userActive) {
        this.userActive = userActive;
    }

    public void cargarDatos() {
        // Crear Cliente de prueba
        UserAccount user1 = new UserAccount("veronica", "1010", null, TypeUser.CLIENT);
        Client client1 = new Client("0001","251000210","Veronica Mendoza", "verom@mail.com",
                "320141011", "Centro",null);
        client1.setUserAccount(user1);
        user1.setPerson(client1);
        agregarPersona(client1);

        // Crear Repartidor de prueba
        UserAccount user2 = new UserAccount("yerilin", "2020", null, TypeUser.DEALER);
        Dealer dealer1=new Dealer("0001" ,"12410001","Yerilin Ul", "yeriul@mail.com2",
                "310121001",null,false,City.CALARCA);
        dealer1.setUserAccount(user2);
        user2.setPerson(client1);
        agregarPersona(dealer1);

        // Crear Administrador de prueba
        UserAccount user3 = new UserAccount("cristian","3030", null,TypeUser.ADMINISTRATOR);
        Admin admin1 = new Admin("0001", "10001411","Cristian M", "cristianm@mail.com",
                "3217412369",null,"Supervisor");
        admin1.setUserAccount(user3);
        user3.setPerson(admin1);
        agregarPersona(admin1);

        // Cargar direcciones de prueba
        Address dir1 = new Address("DIR001", "Mi Casa", "Calle 15 # 20-45",
                City.ARMENIA, AddressType.REMITENTE, "Casa", "Torre 3, Apto 501");
        Address dir2 = new Address("DIR002", "Oficina Principal", "Carrera 14 # 10-20",
                City.ARMENIA, AddressType.DESTINATARIO, "Oficina", "Piso 2");
        addAddress(dir1);
        addAddress(dir2);
    }

    public void agregarPersona(Person person) {
        listPersons.add(person);
    }

    public Person validarUsuario(String usuario, String contrasenia) {
        for (Person person : listPersons) {
            UserAccount persActive = person.getUserAccount();
            if (persActive != null && persActive.getUser().equals(usuario) && persActive.getContrasenia().equals(contrasenia)) {
                userActive = person;
                return person;
            }
        }
        return null;
    }

    public ArrayList<Person> getListPersons() {
        return listPersons;
    }

    public void setListPersons(ArrayList<Person> listPersons) {
        this.listPersons = listPersons;
    }

    // MÉTODOS PARA DIRECCIONES
    public void addAddress(Address address) {
        listAddresses.add(address);
    }

    public void updateAddress(Address address) {
        for (int i = 0; i < listAddresses.size(); i++) {
            if (listAddresses.get(i).getId().equals(address.getId())) {
                listAddresses.set(i, address);
                break;
            }
        }
    }

    public boolean deleteAddress(String addressId) {
        return listAddresses.removeIf(address -> address.getId().equals(addressId));
    }

    public ArrayList<Address> getListAddresses() {
        return listAddresses;
    }

    // MÉTODOS PARA ENVÍOS (NUEVO)
    public void addEnvio(Envio envio) {
        listEnvios.add(envio);
    }

    public void updateEnvio(Envio envio) {
        for (int i = 0; i < listEnvios.size(); i++) {
            if (listEnvios.get(i).getId().equals(envio.getId())) {
                listEnvios.set(i, envio);
                break;
            }
        }
    }

    public boolean deleteEnvio(String envioId) {
        return listEnvios.removeIf(envio -> envio.getId().equals(envioId));
    }

    public ArrayList<Envio> getListEnvios() {
        return listEnvios;
    }

    public Person getUserActive() {
        return userActive;
    }
}