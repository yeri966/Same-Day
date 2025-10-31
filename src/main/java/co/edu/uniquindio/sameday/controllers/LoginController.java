package co.edu.uniquindio.sameday.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.edu.uniquindio.sameday.models.*;
import com.sun.nio.sctp.PeerAddressChangeNotification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


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

        String usuarioIngresado = txtUsuario.getText();
        String contraseniaIngresada = txtContrasenia.getText();

        Person personaEncontrada = sameDay.validarUsuario(usuarioIngresado, contraseniaIngresada);

        if (personaEncontrada != null) {
            String fxml = "";
            String titulo = "";
            if (personaEncontrada instanceof Client) {
                fxml = "/co/edu/uniquindio/sameday/dashboardCliente.fxml";
                titulo = "Dashboard Cliente";
            } else if (personaEncontrada instanceof Dealer) {
                fxml = "/co/edu/uniquindio/sameday/dashboardCliente.fxml";
                titulo = "Dashboard Repartidor";
            } else if (personaEncontrada instanceof Admin) {
                fxml = "/co/edu/uniquindio/sameday/dashboardCliente.fxml";
                titulo = "Dashboard Administrador";
            }

            abrirVentana(fxml, titulo);

        } else {
            mostrarAlerta("Error", "Usuario y contraseña no encontrada", Alert.AlertType.ERROR);
        }

    }

    @FXML
    void onSalir(ActionEvent event) {

    }

    @FXML
    void initialize() {

    }

    public void abrirVentana(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

            txtUsuario.getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
