package co.edu.uniquindio.sameday.controllers;

import java.io.IOException;

import co.edu.uniquindio.sameday.models.Admin;
import co.edu.uniquindio.sameday.models.Client;
import co.edu.uniquindio.sameday.models.Person;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
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
    private SameDay sameDay;
    private Admin adminActual;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnCliente;

    @FXML
    private Button btnRepartidor;

    @FXML
    private Button btnAsignarEnvios; // NUEVO

    @FXML
    private Button btnReporte;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Label lblNombreUsuario;

    @FXML
    void initialize() {
        sameDay=SameDay.getInstance();

        Person usuarioActivo = sameDay.getUserActive();
        if (usuarioActivo instanceof Admin) {
            adminActual = (Admin) usuarioActivo;
            lblNombreUsuario.setText(adminActual.getNombre());

        } else {
            lblNombreUsuario.setText("Usuario no válido");
        }
    }

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
        System.out.println("Gestión Cliente clickeado");
        cargarVistaEnContentArea("/co/edu/uniquindio/sameday/GestionCliente.fxml");
    }

    @FXML
    void onRepartidor(ActionEvent event) {
        System.out.println("Gestión Repartidor clickeado");
        cargarVistaEnContentArea("/co/edu/uniquindio/sameday/GestionRepartidor.fxml");
    }

    // NUEVO MÉTODO
    @FXML
    void onAsignarEnvios(ActionEvent event) {
        System.out.println("Asignación de Envíos clickeado");
        cargarVistaEnContentArea("/co/edu/uniquindio/sameday/Prueba.fxml");

//        System.out.println("Asignación de Envíos clickeado");
//        cargarVistaEnContentArea("/co/edu/uniquindio/sameday/AsignacionEnvios.fxml");
    }

    @FXML
    void onReporte(ActionEvent event) {
        System.out.println("Generar Reporte clickeado");
        cargarVistaEnContentArea("/co/edu/uniquindio/sameday/Estadistica.fxml");
    }

    /**
     * Carga una vista FXML en el área de contenido central
     */
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

            System.out.println("✅ Vista cargada exitosamente: " + fxmlPath);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlPath, Alert.AlertType.ERROR);
            System.err.println("❌ Error al cargar vista: " + fxmlPath);
        }
    }

    /**
     * Carga una vista temporal de "Próximamente"
     */
    private void cargarVistaProximamente(String mensaje) {
        // Limpiar el área de contenido
        contentArea.getChildren().clear();

        // Crear un label con el mensaje
        Label label = new Label(mensaje);
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e293b;");
        label.setLayoutX(300);
        label.setLayoutY(300);

        // Agregar el label al contentArea
        contentArea.getChildren().add(label);
    }

    /**
     * Muestra un cuadro de diálogo de alerta
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}