package co.edu.uniquindio.sameday.models;

import java.util.ArrayList;

public class SameDay {

    private static SameDay instance;
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

    public void cargarDatos() {
        // Crear Cliente de prueba
        User user1 = new User("cristian", "1010", null, TipoUsuario.CLIENTE);
        Client client = new Client("0001", "Mario Restrepo", "mariorestre@mail.com", "32010100", "Calle 50", null);
        client.setUser(user1);
        user1.setPerson(client);
        agregarPersona(client);

        // Crear Repartidor de prueba
        User user2 = new User("veronica", "2020", null, TipoUsuario.REPARTIDOR);
        Dealer dealer1 = new Dealer("0002","Stiven Garcia","stiveng@mail.com","3251000145",null);
        dealer1.setUser(user2);
        user2.setPerson(dealer1);
        agregarPersona(dealer1);

        // Crear Administrador de prueba
        User user3 = new User("yeri","3030",null,TipoUsuario.ADMIN);
        Admin admin1= new Admin("0003","Jaime Maestre","jaimaes@mail.com","387101400",null);
        admin1.setUser(user3);
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
            if (person.getUser().getUsuario().equals(usuario) &&
                    person.getUser().getContrasenia().equals(contrasenia)) {
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
}