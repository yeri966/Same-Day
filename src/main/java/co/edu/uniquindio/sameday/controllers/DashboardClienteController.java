package co.edu.uniquindio.sameday.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardClienteController {

    @FXML
    private Label lblNombreUsuario;

    @FXML
    private Button btnCrearEnvio;

    @FXML
    private Button btnEnvios;

    @FXML
    private Button btnPagos;

    @FXML
    private Button btnHistorial;

    @FXML
    private Button btnDirecciones;

    @FXML
    private Button btnEditarPerfil;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private AnchorPane contentArea; // Área donde se cargarán las vistas dinámicamente

    private Button botonActivo = null; // Para mantener el estado del botón seleccionado

    /**
     * Inicializa el controlador
     * Se ejecuta automáticamente después de cargar el FXML
     */
    @FXML
    void initialize() {
        // Aquí puedes establecer el nombre del usuario logueado
        lblNombreUsuario.setText("Cliente Demo");

        // Aplicar efecto hover a los botones del menú
        aplicarEfectoHover(btnEnvios);
        aplicarEfectoHover(btnPagos);
        aplicarEfectoHover(btnHistorial);
        aplicarEfectoHover(btnDirecciones);
        aplicarEfectoHover(btnEditarPerfil);
        aplicarEfectoHover(btnCerrarSesion);
    }

    /**
     * Crea un nuevo envío
     */
    @FXML
    void onCrearEnvio(ActionEvent event) {
        System.out.println("Crear Envío clickeado");
        cambiarEstiloBotonActivo(btnCrearEnvio);
        // Aquí cargarás la vista de crear envío
        cargarVista("Crear Envío - Próximamente");
    }

    /**
     * Muestra la vista de envíos
     */
    @FXML
    void onEnvios(ActionEvent event) {
        System.out.println("Envíos clickeado");
        cambiarEstiloBotonActivo(btnEnvios);
        cargarVista("Lista de Envíos - Próximamente");
    }

    /**
     * Muestra la vista de pagos
     */
    @FXML
    void onPagos(ActionEvent event) {
        System.out.println("Pagos clickeado");
        cambiarEstiloBotonActivo(btnPagos);
        cargarVista("Pagos - Próximamente");
    }

    /**
     * Muestra el historial de envíos
     */
    @FXML
    void onHistorial(ActionEvent event) {
        System.out.println("Historial clickeado");
        cambiarEstiloBotonActivo(btnHistorial);
        cargarVista("Historial de Envíos - Próximamente");
    }

    /**
     * Muestra la vista de direcciones
     */
    @FXML
    void onDirecciones(ActionEvent event) {
        System.out.println("Direcciones clickeado");
        cambiarEstiloBotonActivo(btnDirecciones);
        cargarVista("Direcciones - Próximamente");
    }

    /**
     * Muestra la vista de editar perfil
     */
    @FXML
    void onEditarPerfil(ActionEvent event) {
        System.out.println("Editar Perfil clickeado");
        cambiarEstiloBotonActivo(btnEditarPerfil);
        cargarVista("Editar Perfil - Próximamente");
    }

    /**
     * Cierra la sesión y vuelve al login
     */
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

    /**
     * Cambia el estilo del botón activo en el menú
     * @param boton El botón que fue clickeado
     */
    private void cambiarEstiloBotonActivo(Button boton) {
        // Restaurar estilo del botón anterior
        if (botonActivo != null && botonActivo != btnCrearEnvio) {
            botonActivo.setStyle("-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-font-size: 14px; -fx-cursor: hand;");
        }

        // Aplicar estilo al botón activo (excepto el botón de crear envío)
        if (boton != btnCrearEnvio && boton != btnCerrarSesion) {
            boton.setStyle("-fx-background-color: #2d3748; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
            botonActivo = boton;
        }
    }

    /**
     * Aplica efecto hover a los botones del menú
     * @param boton El botón al que se le aplicará el efecto
     */
    private void aplicarEfectoHover(Button boton) {
        String estiloOriginal = boton.getStyle();

        boton.setOnMouseEntered(e -> {
            if (boton != botonActivo && boton != btnCrearEnvio) {
                boton.setStyle("-fx-background-color: #2d3748; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
            }
        });

        boton.setOnMouseExited(e -> {
            if (boton != botonActivo && boton != btnCrearEnvio) {
                boton.setStyle(estiloOriginal);
            }
        });
    }

    /**
     * Carga una vista temporal en el área de contenido
     * Más adelante este método cargará archivos FXML específicos
     * @param mensaje El mensaje a mostrar temporalmente
     */
    private void cargarVista(String mensaje) {
        // Por ahora solo mostramos un mensaje
        // Más adelante aquí cargaremos los FXML específicos de cada sección
        contentArea.getChildren().clear();

        Label label = new Label(mensaje);
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #1e293b;");
        label.setLayoutX(300);
        label.setLayoutY(300);

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