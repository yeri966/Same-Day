package co.edu.uniquindio.sameday.controllers;

import co.edu.uniquindio.sameday.models.behavioral.state;
import co.edu.uniquindio.sameday.models.Client;
import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.services.SameDayFacade;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Button btnLogin;
    @FXML private Label lblMensaje;
    @FXML private Label lblIntentos;
    @FXML private ProgressIndicator progressIndicator;

    private SameDayFacade facade;
    private ContextoLogin contextoLogin;
    private Timeline bloqueoTimeline;

    @FXML
    public void initialize() {
        facade = SameDayFacade.getInstance();
        contextoLogin = new ContextoLogin();
        actualizarUI();
    }

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarMensaje("Por favor complete todos los campos", "warning");
            return;
        }

        // Verificar si está bloqueado
        if (contextoLogin.getEstado() instanceof EstadoBloqueado) {
            contextoLogin.manejarEstado();
            if (contextoLogin.estaBloqueado()) {
                iniciarContadorBloqueo();
                return;
            }
        }

        if (!contextoLogin.puedeIntentarLogin()) {
            return;
        }

        // Cambiar a estado validando
        contextoLogin.setEstado(new EstadoValidando());
        actualizarUI();

        // Simular delay de validación
        Timeline validacionDelay = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            validarCredenciales(usuario, contrasena);
        }));
        validacionDelay.play();
    }

    private void validarCredenciales(String usuario, String contrasena) {
        // Buscar en clientes
        Client clienteEncontrado = facade.getClients().stream()
                .filter(c -> c.getUserAccount().getUsername().equals(usuario) &&
                        c.getUserAccount().getPassword().equals(contrasena))
                .findFirst()
                .orElse(null);

        if (clienteEncontrado != null) {
            loginExitoso(clienteEncontrado, "CLIENT");
            return;
        }

        // Buscar en repartidores
        Dealer repartidorEncontrado = facade.getDealers().stream()
                .filter(d -> d.getUserAccount().getUsername().equals(usuario) &&
                        d.getUserAccount().getPassword().equals(contrasena))
                .findFirst()
                .orElse(null);

        if (repartidorEncontrado != null) {
            loginExitoso(repartidorEncontrado, "DEALER");
            return;
        }

        // Verificar admin
        if (usuario.equals("admin") && contrasena.equals("admin123")) {
            loginExitoso(null, "ADMIN");
            return;
        }

        // Login fallido
        loginFallido();
    }

    private void loginExitoso(Object usuario, String tipoUsuario) {
        contextoLogin.setEstado(new EstadoAutenticado());
        contextoLogin.manejarEstado();
        actualizarUI();

        mostrarMensaje(contextoLogin.getMensaje(), "success");

        // Redirigir después de un momento
        Timeline redirectDelay = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            abrirDashboard(usuario, tipoUsuario);
        }));
        redirectDelay.play();
    }

    private void loginFallido() {
        contextoLogin.setEstado(new EstadoFallido());
        contextoLogin.manejarEstado();
        actualizarUI();

        int intentosRestantes = contextoLogin.getMaxIntentos() - contextoLogin.getIntentosFallidos();

        if (contextoLogin.getEstado() instanceof EstadoBloqueado) {
            mostrarMensaje(contextoLogin.getMensaje(), "error");
            iniciarContadorBloqueo();
        } else {
            mostrarMensaje("Credenciales incorrectas. Intentos restantes: " + intentosRestantes, "warning");
        }
    }

    private void iniciarContadorBloqueo() {
        btnLogin.setDisable(true);
        txtUsuario.setDisable(true);
        txtContrasena.setDisable(true);

        if (bloqueoTimeline != null) {
            bloqueoTimeline.stop();
        }

        bloqueoTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            long tiempoRestante = contextoLogin.getTiempoRestanteBloqueo();

            if (tiempoRestante > 0) {
                int segundos = (int) (tiempoRestante / 1000);
                lblIntentos.setText("Cuenta bloqueada. Espere " + segundos + " segundos");
            } else {
                contextoLogin.manejarEstado();
                actualizarUI();
                lblIntentos.setText("");
                bloqueoTimeline.stop();
            }
        }));
        bloqueoTimeline.setCycleCount(Timeline.INDEFINITE);
        bloqueoTimeline.play();
    }

    private void actualizarUI() {
        EstadoLogin estadoActual = contextoLogin.getEstado();

        boolean habilitado = estadoActual.puedeIntentarLogin();
        btnLogin.setDisable(!habilitado);
        txtUsuario.setDisable(!habilitado);
        txtContrasena.setDisable(!habilitado);

        if (estadoActual instanceof EstadoValidando) {
            progressIndicator.setVisible(true);
        } else {
            progressIndicator.setVisible(false);
        }

        // Mostrar intentos restantes si hay fallos
        if (contextoLogin.getIntentosFallidos() > 0 &&
                !(estadoActual instanceof EstadoBloqueado)) {
            int restantes = contextoLogin.getMaxIntentos() - contextoLogin.getIntentosFallidos();
            lblIntentos.setText("Intentos restantes: " + restantes);
        }
    }

    private void mostrarMensaje(String mensaje, String tipo) {
        lblMensaje.setText(mensaje);

        switch (tipo) {
            case "success":
                lblMensaje.setStyle("-fx-text-fill: #27ae60;");
                break;
            case "error":
                lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
                break;
            case "warning":
                lblMensaje.setStyle("-fx-text-fill: #f39c12;");
                break;
            default:
                lblMensaje.setStyle("-fx-text-fill: #2c3e50;");
        }
    }

    private void abrirDashboard(Object usuario, String tipoUsuario) {
        try {
            String fxmlPath;
            String titulo;

            switch (tipoUsuario) {
                case "CLIENT":
                    fxmlPath = "/co/edu/uniquindio/sameday/views/dashboard-client.fxml";
                    titulo = "Dashboard Cliente";
                    break;
                case "DEALER":
                    fxmlPath = "/co/edu/uniquindio/sameday/views/dashboard-dealer.fxml";
                    titulo = "Dashboard Repartidor";
                    break;
                case "ADMIN":
                    fxmlPath = "/co/edu/uniquindio/sameday/views/dashboard-admin.fxml";
                    titulo = "Dashboard Administrador";
                    break;
                default:
                    return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Pasar el usuario al controlador si es necesario
            // Object controller = loader.getController();
            // if (controller instanceof ClientDashboardController && usuario instanceof Client) {
            //     ((ClientDashboardController) controller).setCliente((Client) usuario);
            // }

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

        } catch (Exception e) {
            mostrarMensaje("Error al cargar el dashboard: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegistro() {
        // Abrir ventana de registro
    }
}