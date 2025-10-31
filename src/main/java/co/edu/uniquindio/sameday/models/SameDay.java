package co.edu.uniquindio.sameday.models;

import java.util.ArrayList;

public class SameDay {

    private static SameDay instance;
    private ArrayList<Person>listPersons;

    private SameDay(){
        listPersons=new ArrayList<>();
        cargarDatos();

    }
    public static SameDay getInstance(){
        if(instance==null){
            instance=new SameDay();
        }
        return instance;
    }

    public void cargarDatos(){
        User user=new User("mrestrepo","1010",null,TipoUsuario.CLIENTE);
        Client client = new Client("0001","Mario Restrepo","mariorestre@mail.com","32010100","Calle 50",null);
        client.setUser(user);
        user.setPerson(client);
        agregarPersona(client);

    }

    public void agregarPersona(Person person){
        listPersons.add(person);
    }

    public Person validarUsuario(String usuario,String contrasenia){
        for(Person person: listPersons){
            if(person.getUser().getUsuario().equals(usuario)&&person.getUser().getContrasenia().equals(contrasenia)){
                return person;
            }
        }
        return null;
    }
}