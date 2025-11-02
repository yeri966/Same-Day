package co.edu.uniquindio.sameday.models;

import java.util.ArrayList;

public class SameDay {

    private static SameDay instance;
    private ArrayList<Person> listPersons;

    private SameDay() {
        listPersons = new ArrayList<>();
        cargarDatos();

    }

    public static SameDay getInstance() {
        if (instance == null) {
            instance = new SameDay();
        }
        return instance;
    }

    public void cargarDatos() {
        User user1 = new User("cristian", "1010", null, TipoUsuario.CLIENTE);
        Client client = new Client("0001", "Mario Restrepo", "mariorestre@mail.com", "32010100", "Calle 50", null);
        client.setUser(user1);
        user1.setPerson(client);
        agregarPersona(client);

        User user2 = new User("veronica", "2020", null, TipoUsuario.REPARTIDOR);
        Dealer dealer1 = new Dealer("0002","Stiven Garcia","stiveng@mail.com","3251000145",null);
        dealer1.setUser(user2);
        user2.setPerson(dealer1);
        agregarPersona(dealer1);

        User user3 = new User("yeri","3030",null,TipoUsuario.ADMIN);
        Admin admin1= new Admin("0003","Jaime Maestre","jaimaes@mail.com","387101400",null);
        admin1.setUser(user3);
        user3.setPerson(admin1);
        agregarPersona(admin1);

    }

    public void agregarPersona(Person person) {
        listPersons.add(person);
    }

    public Person validarUsuario(String usuario, String contrasenia) {
        for (Person person : listPersons) {
            if (person.getUser().getUsuario().equals(usuario) && person.getUser().getContrasenia().equals(contrasenia)) {
                return person;
            }
        }
        return null;
        }
    }