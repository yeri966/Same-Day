package co.edu.uniquindio.sameday.models.creational.singleton;

import co.edu.uniquindio.sameday.models.*;
import co.edu.uniquindio.sameday.models.Admin;
import co.edu.uniquindio.sameday.models.Client;
import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.Person;
import co.edu.uniquindio.sameday.models.creational.factoryMethod.AdminFactory;
import co.edu.uniquindio.sameday.models.creational.factoryMethod.ClienteFactory;
import co.edu.uniquindio.sameday.models.creational.factoryMethod.DealerFactory;

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
        ClienteFactory clienteFactory = new ClienteFactory("Centro");
        Client client1 = (Client) clienteFactory.crearPerson("0001","251000210","Veronica Mendoza", "verom@mail.com",
                "3201410110", user1);
        agregarPersona(client1);

        // Crear Repartidor de prueba
        UserAccount user2 = new UserAccount("yerilin", "2020", null, TypeUser.DEALER);
        DealerFactory dealerFactory = new DealerFactory(false,City.ARMENIA);
        Dealer dealer1= (Dealer) dealerFactory.crearPerson("0001" ,"12410001","Yerilin Ul", "yeriul@mail.com",
                "310121001",user2);
        agregarPersona(dealer1);

        // Crear Administrador de prueba
        UserAccount user3 = new UserAccount("cristian","3030", null,TypeUser.ADMINISTRATOR);
        AdminFactory adminFactory= new AdminFactory("Supervisor");
        Admin admin1 = (Admin) adminFactory.crearPerson ("0001", "10001411","Cristian M", "cristianm@mail.com",
                "3217412369",user3);
        agregarPersona(admin1);

        // ==================== DIRECCIONES DE PRUEBA ====================

        // Direcciones de REMITENTE (Origen)
        Address dir1 = new Address("DIR001", "Mi Casa", "Calle 15 # 20-45",
                City.ARMENIA, AddressType.REMITENTE, "Casa", "Torre 3, Apto 501");
        Address dir3 = new Address("DIR003", "Oficina Armenia", "Carrera 14 # 18-30",
                City.ARMENIA, AddressType.REMITENTE, "Oficina", "Edificio Central, Piso 5");
        Address dir5 = new Address("DIR005", "Casa Circasia", "Calle 10 # 8-15",
                City.CIRCASIA, AddressType.REMITENTE, "Casa", "Barrio El Prado");
        Address dir7 = new Address("DIR007", "Negocio Calarcá", "Carrera 25 # 30-10",
                City.CALARCA, AddressType.REMITENTE, "Local Comercial", "Centro Comercial");
        Address dir9 = new Address("DIR009", "Apartamento Montenegro", "Calle 12 # 15-20",
                City.MONTENEGRO, AddressType.REMITENTE, "Apartamento", "Conjunto Residencial Los Pinos");

        // Direcciones de DESTINATARIO (Destino)
        Address dir2 = new Address("DIR002", "Oficina Principal", "Carrera 14 # 10-20",
                City.ARMENIA, AddressType.DESTINATARIO, "Oficina", "Piso 2");
        Address dir4 = new Address("DIR004", "Casa Salento", "Calle 6 # 4-12",
                City.SALENTO, AddressType.DESTINATARIO, "Casa", "Barrio La Floresta");
        Address dir6 = new Address("DIR006", "Tienda La Tebaida", "Carrera 5 # 8-25",
                City.LA_TEBAIDA, AddressType.DESTINATARIO, "Tienda", "Local 102");
        Address dir8 = new Address("DIR008", "Bodega Quimbaya", "Calle 20 # 12-50",
                City.QUIMBAYA, AddressType.DESTINATARIO, "Bodega", "Zona Industrial");
        Address dir10 = new Address("DIR010", "Restaurante Filandia", "Carrera 7 # 7-30",
                City.FILANDIA, AddressType.DESTINATARIO, "Restaurante", "Plaza Principal");
        Address dir11 = new Address("DIR011", "Farmacia Córdoba", "Calle 8 # 9-15",
                City.CORDOBA, AddressType.DESTINATARIO, "Farmacia", "Centro");
        Address dir12 = new Address("DIR012", "Librería Pijao", "Carrera 4 # 5-20",
                City.PIJAO, AddressType.DESTINATARIO, "Librería", "");

        addAddress(dir1);
        addAddress(dir2);
        addAddress(dir3);
        addAddress(dir4);
        addAddress(dir5);
        addAddress(dir6);
        addAddress(dir7);
        addAddress(dir8);
        addAddress(dir9);
        addAddress(dir10);
        addAddress(dir11);
        addAddress(dir12);

        // ==================== ENVÍOS DE PRUEBA ====================

        // ENVÍO 1: Documentos urgentes - PAGADO
        Envio envio1 = new Envio("ENV0001", dir1, dir2, 0.5,
                "30x20x5 cm", 3000.0, "Documentos Legales");
        envio1.setNombreDestinatario("Carlos Rodríguez");
        envio1.setCedulaDestinatario("1094123456");
        envio1.setTelefonoDestinatario("3151234567");
        envio1.getServiciosAdicionales().add(ServicioAdicional.PRIORIDAD);
        envio1.getServiciosAdicionales().add(ServicioAdicional.FIRMA_REQUERIDA);
        envio1.setCostoTotal(19000.0);
        envio1.setEstado("PAGADO");
        envio1.setFechaCreacion(java.time.LocalDateTime.now().minusDays(5));
        addEnvio(envio1);

        // ENVÍO 2: Electrónicos frágiles - PAGADO
        Envio envio2 = new Envio("ENV0002", dir5, dir6, 4.0,
                "35x35x25 cm", 30625.0, "Computadora Portátil");
        envio2.setNombreDestinatario("Pedro Martínez");
        envio2.setCedulaDestinatario("1094345678");
        envio2.setTelefonoDestinatario("3173456789");
        envio2.getServiciosAdicionales().add(ServicioAdicional.SEGURO);
        envio2.getServiciosAdicionales().add(ServicioAdicional.FRAGIL);
        envio2.setCostoTotal(19000.0);
        envio2.setEstado("PAGADO");
        envio2.setFechaCreacion(java.time.LocalDateTime.now().minusDays(3));
        addEnvio(envio2);

        // ENVÍO 3: Medicamentos con firma - PAGADO
        Envio envio3 = new Envio("ENV0003", dir9, dir10, 1.2,
                "25x20x15 cm", 7500.0, "Medicamentos");
        envio3.setNombreDestinatario("Luis Ramírez");
        envio3.setCedulaDestinatario("1094567890");
        envio3.setTelefonoDestinatario("3195678901");
        envio3.getServiciosAdicionales().add(ServicioAdicional.FIRMA_REQUERIDA);
        envio3.getServiciosAdicionales().add(ServicioAdicional.PRIORIDAD);
        envio3.setCostoTotal(19000.0);
        envio3.setEstado("PAGADO");
        envio3.setFechaCreacion(java.time.LocalDateTime.now().minusDays(2));
        addEnvio(envio3);

        // ENVÍO 4: Paquete grande con todos los servicios - PAGADO
        Envio envio4 = new Envio("ENV0004", dir3, dir12, 15.0,
                "60x50x40 cm", 120000.0, "Equipos de Oficina");
        envio4.setNombreDestinatario("Roberto Silva");
        envio4.setCedulaDestinatario("1094789012");
        envio4.setTelefonoDestinatario("3217890123");
        envio4.getServiciosAdicionales().add(ServicioAdicional.SEGURO);
        envio4.getServiciosAdicionales().add(ServicioAdicional.FRAGIL);
        envio4.getServiciosAdicionales().add(ServicioAdicional.FIRMA_REQUERIDA);
        envio4.getServiciosAdicionales().add(ServicioAdicional.PRIORIDAD);
        envio4.setCostoTotal(32000.0);
        envio4.setEstado("PAGADO");
        envio4.setFechaCreacion(java.time.LocalDateTime.now().minusDays(1));
        addEnvio(envio4);

        // ENVÍO 5: Alimentos - PAGADO
        Envio envio5 = new Envio("ENV0005", dir7, dir4, 10.0,
                "40x40x30 cm", 48000.0, "Productos Alimenticios");
        envio5.setNombreDestinatario("Jorge Mendoza");
        envio5.setCedulaDestinatario("1094901234");
        envio5.setTelefonoDestinatario("3239012345");
        envio5.getServiciosAdicionales().add(ServicioAdicional.PRIORIDAD);
        envio5.setCostoTotal(24000.0);
        envio5.setEstado("PAGADO");
        envio5.setFechaCreacion(java.time.LocalDateTime.now().minusDays(7));
        addEnvio(envio5);

        System.out.println("=== DATOS CARGADOS CORRECTAMENTE ===");
        System.out.println("Total Personas: " + listPersons.size());
        System.out.println("Total Direcciones: " + listAddresses.size());
        System.out.println("Total Envíos: " + listEnvios.size());
    }

    public void agregarPersona(Person person) {
        listPersons.add(person);
    }

    public void eliminarPersona(Person persona){
        listPersons.remove(persona);
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

    // MÉTODOS PARA ENVÍOS
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