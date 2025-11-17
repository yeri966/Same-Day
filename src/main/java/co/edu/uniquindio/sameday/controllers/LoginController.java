package co.edu.uniquindio.sameday.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import co.edu.uniquindio.sameday.models.Admin;
import co.edu.uniquindio.sameday.models.Client;
import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.Person;
import co.edu.uniquindio.sameday.models.UserAccount;
import co.edu.uniquindio.sameday.models.behavioral.state.BlockedState;
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

    @FXML
    void onIngresar(ActionEvent event) {
        String usuarioIngresado = txtUsuario.getText().trim();
        String contraseniaIngresada = txtContrasenia.getText();

        // Validar campos vacíos
        if (usuarioIngresado.isEmpty() || contraseniaIngresada.isEmpty()) {
            mostrarAlerta("Campos Vacíos",
                    "Por favor ingrese usuario y contraseña",
                    Alert.AlertType.WARNING);
            return;
        }

        // Buscar la cuenta de usuario
        UserAccount cuenta = sameDay.buscarCuentaPorUsuario(usuarioIngresado);

        if (cuenta == null) {
            mostrarAlerta("Usuario no encontrado",
                    "El usuario ingresado no existe en el sistema",
                    Alert.AlertType.ERROR);
            return;
        }

        // PATRÓN STATE: Verificar si la cuenta puede hacer login
        if (!cuenta.canLogin()) {
            String mensaje = cuenta.getAccountState().getStateMessage();

            // Si está bloqueada, mostrar tiempo restante
            if (cuenta.getAccountState() instanceof BlockedState) {
                BlockedState blockedState = (BlockedState) cuenta.getAccountState();
                long remainingMs = blockedState.getRemainingBlockTime(cuenta);
                long remainingMinutes = remainingMs / 60000;
                long remainingSeconds = (remainingMs % 60000) / 1000;
                mensaje += String.format("\nTiempo restante: %d minutos y %d segundos",
                        remainingMinutes, remainingSeconds);
            }

            mostrarAlerta("Acceso Denegado", mensaje, Alert.AlertType.ERROR);
            return;
        }

        // Verificar contraseña
        if (!cuenta.getContrasenia().equals(contraseniaIngresada)) {
            // PATRÓN STATE: Manejar intento fallido
            cuenta.handleFailedLogin();

            int intentosRestantes = 3 - cuenta.getFailedAttempts();
            String mensaje = "Contraseña incorrecta";

            if (intentosRestantes > 0) {
                mensaje += "\nIntentos restantes: " + intentosRestantes;
                mostrarAlerta("Error de Autenticación", mensaje, Alert.AlertType.WARNING);
            } else {
                mensaje = "Cuenta bloqueada por múltiples intentos fallidos.\n" +
                        "Intente nuevamente en 5 minutos.";
                mostrarAlerta("Cuenta Bloqueada", mensaje, Alert.AlertType.ERROR);
            }
            return;
        }

        // PATRÓN STATE: Login exitoso
        cuenta.handleSuccessfulLogin();
        Person personaEncontrada = cuenta.getPerson();

        String fxml = "";
        String titulo = "";

        // Determinar qué ventana abrir según el tipo de usuario
        if (personaEncontrada instanceof Client) {
            fxml = "/co/edu/uniquindio/sameday/dashboardCliente.fxml";
            titulo = "Dashboard Cliente";
        } else if (personaEncontrada instanceof Dealer) {
            fxml = "/co/edu/uniquindio/sameday/dashboardRepartidor.fxml";
            titulo = "Dashboard Repartidor";
        } else if (personaEncontrada instanceof Admin) {
            fxml = "/co/edu/uniquindio/sameday/dashboardAdmin.fxml";
            titulo = "Dashboard Administrador";
        }

        sameDay.setUserActive(personaEncontrada);
        abrirVentana(fxml, titulo);
    }

    @FXML
    void onSalir(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onCrearCuenta(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/sameday/CreateAccount.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear Cuenta - SameDay");
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de registro", Alert.AlertType.ERROR);
        }
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

            // Cerrar la ventana de login
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