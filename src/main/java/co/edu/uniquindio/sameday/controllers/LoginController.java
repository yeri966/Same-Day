package co.edu.uniquindio.sameday.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.edu.uniquindio.sameday.models.creational.builder.Admin;
import co.edu.uniquindio.sameday.models.creational.builder.Client;
import co.edu.uniquindio.sameday.models.creational.builder.Dealer;
import co.edu.uniquindio.sameday.models.creational.builder.Person;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private Hyperlink hyl;

    /**
     * Método que se ejecuta al hacer clic en el botón INGRESAR
     * Valida las credenciales y abre la ventana correspondiente según el tipo de usuario
     */
    @FXML
    void onIngresar(ActionEvent event) {

        String usuarioIngresado = txtUsuario.getText();
        String contraseniaIngresada = txtContrasenia.getText();

        Person personaEncontrada = sameDay.validarUsuario(usuarioIngresado, contraseniaIngresada);
        if (personaEncontrada != null) {
            sameDay.setUserActive(personaEncontrada);
            String fxml = "";
            String titulo = "";

            // Determinar qué ventana abrir según el tipo de usuario
            if (personaEncontrada instanceof Client) {
                fxml = "/co/edu/uniquindio/sameday/dashboardCliente.fxml";
                titulo = "Dashboard Cliente";
            } else if (personaEncontrada instanceof Dealer) {
                fxml = "/co/edu/uniquindio/sameday/dashboardCliente.fxml";
                titulo = "Dashboard Repartidor";
            } else if (personaEncontrada instanceof Admin) {
                fxml = "/co/edu/uniquindio/sameday/dashboardAdmin.fxml";
                titulo = "Dashboard Administrador";
            }

            abrirVentana(fxml, titulo);

        } else {
            mostrarAlerta("Error", "Usuario y contraseña no encontrada", Alert.AlertType.ERROR);
        }

    }

    /**
     * Método que se ejecuta al hacer clic en el botón SALIR
     * Cierra la aplicación completamente
     */
    @FXML
    void onSalir(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Método que se ejecuta al hacer clic en el hipervínculo "Crear una Cuenta"
     * Abre la ventana de registro de nuevos usuarios
     */
    @FXML
    void onCrearCuenta(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/sameday/CreateAccount.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear Cuenta - SameDay");
            stage.setResizable(false); // No permitir redimensionar la ventana
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de registro", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void initialize() {
        // Método de inicialización si se necesita configuración inicial
    }

    /**
     * Método auxiliar para abrir una nueva ventana y cerrar la actual
     * @param fxml Ruta del archivo FXML a cargar
     * @param titulo Título de la nueva ventana
     */
    public void abrirVentana(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

            // Cerrar la ventana de login
            txtUsuario.getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana", Alert.AlertType.ERROR);
        }
    }

    /**
     * Método auxiliar para mostrar alertas al usuario
     * @param titulo Título de la alerta
     * @param mensaje Contenido del mensaje
     * @param tipo Tipo de alerta (ERROR, WARNING, INFORMATION, etc.)
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}