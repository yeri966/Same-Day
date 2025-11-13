package co.edu.uniquindio.sameday.controllers;

import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.Person;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador principal del Dashboard del Repartidor
 * Maneja la navegación del menú lateral, disponibilidad y carga de vistas
 */
public class DashboardDealerController {

    private SameDay sameDay = SameDay.getInstance();
    private Dealer repartidorActual;
    private boolean disponible = true;

    @FXML private Label lblNombreUsuario;
    @FXML private Label lblEstadoDisponibilidad;
    @FXML private Label lblTextoDisponibilidad;
    @FXML private Button btnToggleDisponibilidad;
    @FXML private Button btnGestionEnvios;
    @FXML private Button btnEditarPerfil;
    @FXML private Button btnCerrarSesion;
    @FXML private AnchorPane contentArea;

    @FXML
    void initialize() {
        Person usuarioActivo = sameDay.getUserActive();
        if (usuarioActivo instanceof Dealer) {
            repartidorActual = (Dealer) usuarioActivo;
            lblNombreUsuario.setText(repartidorActual.getNombre());

            // Cargar la disponibilidad MANUAL del repartidor
            disponible = repartidorActual.isDisponibleManual();
            actualizarEstadoDisponibilidad();
        } else {
            lblNombreUsuario.setText("Usuario no válido");
        }
    }

    /**
     * Alterna el estado de disponibilidad del repartidor
     */
    @FXML
    void onToggleDisponibilidad(ActionEvent event) {
        disponible = !disponible;

        // Actualizar solo la disponibilidad MANUAL del repartidor
        if (repartidorActual != null) {
            repartidorActual.setDisponibleManual(disponible);
        }

        actualizarEstadoDisponibilidad();
    }

    /**
     * Actualiza la interfaz según el estado de disponibilidad
     */
    private void actualizarEstadoDisponibilidad() {
        if (disponible) {
            // Estado DISPONIBLE - Verde
            lblEstadoDisponibilidad.setTextFill(Color.web("#10b981"));
            lblTextoDisponibilidad.setText("Disponible");
            lblTextoDisponibilidad.setTextFill(Color.web("#10b981"));
            btnToggleDisponibilidad.setText("Cambiar a No Disponible");
            btnToggleDisponibilidad.setStyle(
                    "-fx-background-color: #10b981; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 20; " +
                            "-fx-font-weight: bold; " +
                            "-fx-cursor: hand;"
            );
        } else {
            // Estado NO DISPONIBLE - Rojo
            lblEstadoDisponibilidad.setTextFill(Color.web("#ef4444"));
            lblTextoDisponibilidad.setText("No Disponible");
            lblTextoDisponibilidad.setTextFill(Color.web("#ef4444"));
            btnToggleDisponibilidad.setText("Cambiar a Disponible");
            btnToggleDisponibilidad.setStyle(
                    "-fx-background-color: #ef4444; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 20; " +
                            "-fx-font-weight: bold; " +
                            "-fx-cursor: hand;"
            );
        }
    }

    /**
     * Carga la vista de Gestión de Envíos
     */
    @FXML
    void onGestionEnvios(ActionEvent event) {
        cargarVista("/co/edu/uniquindio/sameday/gestionEnviosRepartidor.fxml");
        resaltarBotonActivo(btnGestionEnvios);
    }

    /**
     * Carga la vista de Editar Perfil
     */
    @FXML
    void onEditarPerfil(ActionEvent event) {
        cargarVista("/co/edu/uniquindio/sameday/editarPerfilRepartidor.fxml");
        resaltarBotonActivo(btnEditarPerfil);
    }

    /**
     * Cierra sesión y regresa al login
     */
    @FXML
    void onCerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/sameday/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SameDay - Login");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga una vista FXML en el área de contenido
     *
     * @param fxmlPath Ruta del archivo FXML a cargar
     */
    private void cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent vista = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(vista);

            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resalta el botón activo en el menú lateral
     *
     * @param botonActivo El botón que debe resaltarse
     */
    private void resaltarBotonActivo(Button botonActivo) {
        // Resetear todos los botones al estilo normal
        btnGestionEnvios.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #cbd5e1; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand;"
        );

        btnEditarPerfil.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #cbd5e1; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand;"
        );

        // Resaltar el botón activo
        botonActivo.setStyle(
                "-fx-background-color: #3b82f6; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-weight: bold;"
        );
    }
}