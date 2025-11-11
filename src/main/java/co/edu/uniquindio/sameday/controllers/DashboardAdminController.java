package co.edu.uniquindio.sameday.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DashboardAdminController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnCliente;

    @FXML
    private Button btnEditarPerfil;

    @FXML
    private Button btnRepartidor;

    @FXML
    private Button btnReporte;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Label lblNombreUsuario;


    @FXML
    void onCerrarSesion(ActionEvent event) {
        try {
            // Cargar la ventana de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/sameday/login.fxml"));
            Parent root = loader.load();

            // Obtener el stage actual y cambiar la escena
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - SameDay");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cerrar sesión", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onCliente(ActionEvent event) {
        cargarVistaEnContentArea("/co/edu/uniquindio/sameday/GestionCliente.fxml");

    }

    @FXML
    void onEditarPerfil(ActionEvent event) {

    }

    @FXML
    void onRepartidor(ActionEvent event) {

    }

    @FXML
    void onReporte(ActionEvent event) {

    }

    @FXML
    void initialize() {

    }

    private void cargarVistaEnContentArea(String fxmlPath) {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node vista = loader.load();

            // Limpiar el área de contenido
            contentArea.getChildren().clear();

            // Agregar la nueva vista
            contentArea.getChildren().add(vista);

            // Hacer que la vista ocupe todo el espacio disponible
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);

            System.out.println("Vista cargada exitosamente: " + fxmlPath);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlPath, Alert.AlertType.ERROR);
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
