package co.edu.uniquindio.sameday.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import co.edu.uniquindio.sameday.models.Person;
import co.edu.uniquindio.sameday.models.SameDay;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class LoginController {
    SameDay sameDay = SameDay.getInstance();


    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnIngresar;

    @FXML
    private Button btnSalir;

    @FXML
    private PasswordField txtContrasenia;

    @FXML
    private TextField txtUsuario;

    @FXML
    void onIngresar(ActionEvent event) {

        String usuarioIngresado=txtUsuario.getText();
        String contraseniaIngresada=txtContrasenia.getText();

        Person personaEncontrada=sameDay.validarUsuario(usuarioIngresado,contraseniaIngresada);


        if(personaEncontrada!=null){
            System.out.println("Usuario encontrado " + personaEncontrada.getNombre());
            System.out.println("Tipo de usuario " + personaEncontrada.getClass().getSimpleName());
        }else{
            System.out.println("Usuario y contraseña incorrectos");
        }

    }

    @FXML
    void onSalir(ActionEvent event) {

    }

    @FXML
    void initialize() {

    }

}
