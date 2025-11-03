package co.edu.uniquindio.sameday.models;

import co.edu.uniquindio.sameday.models.entities.Address;
import java.util.ArrayList;

/**
 * Clase principal del sistema que gestiona todos los usuarios (Singleton)
 * Mantiene una lista de todas las personas registradas en la plataforma
 */
public class SameDay {

    private static SameDay instance;
    private ArrayList<Person> listPersons;
    private ArrayList<Address> listAddresses;  // NUEVO

    /**
     * Constructor privado para implementar el patrón Singleton
     * Inicializa la lista de personas y carga datos de prueba
     */
    private SameDay() {
        listPersons = new ArrayList<>();
        listAddresses = new ArrayList<>();  // NUEVO
        cargarDatos();
    }

    /**
     * Método para obtener la única instancia de SameDay (Singleton)
     * @return La instancia única de SameDay
     */
    public static SameDay getInstance() {
        if (instance == null) {
            instance = new SameDay();
        }
        return instance;
    }

    /**
     * Método que carga datos iniciales de prueba en el sistema
     * Crea usuarios de tipo Cliente, Repartidor y Administrador
     */
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

    /**
     * Agrega una nueva persona al sistema
     * @param person La persona a agregar (puede ser Client, Dealer o Admin)
     */
    public void agregarPersona(Person person) {
        listPersons.add(person);
    }

    /**
     * Valida las credenciales de un usuario
     * @param usuario Nombre de usuario
     * @param contrasenia Contraseña del usuario
     * @return La persona si las credenciales son correctas, null en caso contrario
     */
    public Person validarUsuario(String usuario, String contrasenia) {
        for (Person person : listPersons) {
            if (person.getUser().getUsuario().equals(usuario) &&
                    person.getUser().getContrasenia().equals(contrasenia)) {
                return person;
            }
        }
        return null;
    }

    /**
     * Obtiene la lista de todas las personas registradas
     * @return ArrayList con todas las personas del sistema
     */
    public ArrayList<Person> getListPersons() {
        return listPersons;
    }

    /**
     * Establece la lista de personas (útil para cargar datos desde archivo)
     * @param listPersons Nueva lista de personas
     */
    public void setListPersons(ArrayList<Person> listPersons) {
        this.listPersons = listPersons;
    }

    // ========== MÉTODOS PARA GESTIONAR DIRECCIONES ==========

    /**
     * Agrega una nueva dirección al sistema
     */
    public void addAddress(Address address) {
        listAddresses.add(address);
    }

    /**
     * Actualiza una dirección existente
     */
    public void updateAddress(Address address) {
        for (int i = 0; i < listAddresses.size(); i++) {
            if (listAddresses.get(i).getId().equals(address.getId())) {
                listAddresses.set(i, address);
                break;
            }
        }
    }

    /**
     * Elimina una dirección del sistema
     */
    public boolean deleteAddress(String addressId) {
        return listAddresses.removeIf(address -> address.getId().equals(addressId));
    }

    /**
     * Obtiene todas las direcciones del sistema
     */
    public ArrayList<Address> getListAddresses() {
        return listAddresses;
    }
}