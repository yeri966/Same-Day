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
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador principal del Dashboard del Repartidor
 * Maneja la navegación del menú lateral y carga las vistas correspondientes
 */
public class DashboardDealerController {

    private SameDay sameDay = SameDay.getInstance();
    private Dealer repartidorActual;

    @FXML private Label lblNombreUsuario;
    @FXML private Button btnGestionEnvios;
    @FXML private Button btnCerrarSesion;
    @FXML private AnchorPane contentArea;

    @FXML
    void initialize() {
        System.out.println("=== INICIALIZANDO DASHBOARD REPARTIDOR ===");

        // Obtener el repartidor actual
        Person usuarioActivo = sameDay.getUserActive();
        if (usuarioActivo instanceof Dealer) {
            repartidorActual = (Dealer) usuarioActivo;
            lblNombreUsuario.setText(repartidorActual.getNombre());
            System.out.println("Repartidor activo: " + repartidorActual.getNombre());
        } else {
            System.out.println("ERROR: Usuario activo no es un repartidor");
            lblNombreUsuario.setText("Usuario no válido");
        }

        System.out.println("=== DASHBOARD REPARTIDOR INICIALIZADO ===");
    }

    /**
     * Carga la vista de Gestión de Envíos cuando se hace clic en el botón del menú
     */
    @FXML
    void onGestionEnvios(ActionEvent event) {
        cargarVista("/co/edu/uniquindio/sameday/gestionEnviosRepartidor.fxml");
        resaltarBotonActivo(btnGestionEnvios);
    }

    /**
     * Cierra sesión y regresa al login
     */
    @FXML
    void onCerrarSesion(ActionEvent event) {
        try {
            // Cargar la ventana de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/sameday/login.fxml"));
            Parent root = loader.load();

            // Obtener el Stage actual y cambiar la escena
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SameDay - Login");
            stage.show();

            System.out.println("Sesión cerrada correctamente");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cerrar sesión: " + e.getMessage());
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

            // Limpiar el área de contenido
            contentArea.getChildren().clear();

            // Agregar la nueva vista
            contentArea.getChildren().add(vista);

            // Ajustar la vista al tamaño del AnchorPane
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);

            System.out.println("Vista cargada: " + fxmlPath);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la vista: " + fxmlPath);
            System.err.println("Detalle: " + e.getMessage());
        }
    }

    /**
     * Resalta el botón activo en el menú lateral
     *
     * @param botonActivo El botón que debe resaltarse
     */
    private void resaltarBotonActivo(Button botonActivo) {
        // Resetear todos los botones al estilo normal
        btnGestionEnvios.setStyle("-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-font-size: 14px; -fx-cursor: hand;");

        // Resaltar el botón activo
        botonActivo.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-font-weight: bold;");
    }
}