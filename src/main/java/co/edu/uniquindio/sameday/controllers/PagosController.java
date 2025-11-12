package co.edu.uniquindio.sameday.controllers;

import co.edu.uniquindio.sameday.models.*;
import co.edu.uniquindio.sameday.models.Client;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.Optional;

public class PagosController {

    private SameDay sameDay = SameDay.getInstance();
    private Envio selectedEnvio = null;

    @FXML private TableView<Envio> tablaEnvios;
    @FXML private TableColumn<Envio, String> colId;
    @FXML private TableColumn<Envio, String> colOrigen;
    @FXML private TableColumn<Envio, String> colDestino;
    @FXML private TableColumn<Envio, String> colDestinatario; // NUEVA COLUMNA
    @FXML private TableColumn<Envio, String> colContenido;
    @FXML private TableColumn<Envio, String> colPeso;
    @FXML private TableColumn<Envio, String> colServicios;
    @FXML private TableColumn<Envio, String> colCosto;
    @FXML private TableColumn<Envio, String> colEstado;
    @FXML private Button btnPagar;
    @FXML private javafx.scene.layout.VBox vboxSimulador;
    @FXML private Label lblSimuladorTitulo;
    @FXML private ProgressIndicator progressPago;
    @FXML private Label lblSimuladorMensaje;

    @FXML
    void initialize() {
        System.out.println("=== INICIALIZANDO CONTROLADOR PAGOS ===");
        configureTable();
        loadTable();
        configureTableSelection();
    }

    private void configureTable() {
        colId.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getId()));

        colOrigen.setCellValueFactory(cellData -> {
            Address origen = cellData.getValue().getOrigen();
            return new SimpleStringProperty(origen != null ? origen.getAlias() : "");
        });

        colDestino.setCellValueFactory(cellData -> {
            Address destino = cellData.getValue().getDestino();
            return new SimpleStringProperty(destino != null ? destino.getAlias() : "");
        });

        // NUEVA COLUMNA: Destinatario
        colDestinatario.setCellValueFactory(cellData -> {
            String nombreDestinatario = cellData.getValue().getNombreDestinatario();
            return new SimpleStringProperty(nombreDestinatario != null ? nombreDestinatario : "");
        });

        colContenido.setCellValueFactory(cellData -> {
            String contenido = cellData.getValue().getContenido();
            return new SimpleStringProperty(contenido != null ? contenido : "");
        });

        colPeso.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f", cellData.getValue().getPeso())));

        colServicios.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getServiciosAdicionalesString()));

        colCosto.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("$%.0f", cellData.getValue().getCostoTotal())));

        colEstado.setCellValueFactory(cellData -> {
            String estado = cellData.getValue().getEstado();
            String estadoPago = estado.equals("PAGADO") ? "✅ Pagado" : "⏳ Sin pagar";
            return new SimpleStringProperty(estadoPago);
        });

        colEstado.setCellFactory(column -> new TableCell<Envio, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Pagado")) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void configureTableSelection() {
        tablaEnvios.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectedEnvio = newValue;
                        // Habilitar el botón solo si el envío no está pagado
                        btnPagar.setDisable(newValue.getEstado().equals("PAGADO"));
                    }
                }
        );
    }

    @FXML
    void onPagar(ActionEvent event) {
        if (selectedEnvio == null) {
            showAlert("Error", "Debe seleccionar un envío de la tabla", Alert.AlertType.WARNING);
            return;
        }

        if (selectedEnvio.getEstado().equals("PAGADO")) {
            showAlert("Envío ya pagado",
                    "Este envío ya ha sido pagado anteriormente",
                    Alert.AlertType.INFORMATION);
            return;
        }

        mostrarDialogoPago();
    }

    private void mostrarDialogoPago() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Realizar Pago");
        dialog.setHeaderText("Ingrese los datos para realizar el pago");

        ButtonType btnRealizarPago = new ButtonType("Realizar Pago", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnRealizarPago, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre completo");
        txtNombre.setPrefWidth(300);

        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("Número de teléfono");
        txtTelefono.setPrefWidth(300);

        Label lblCosto = new Label(String.format("Monto a pagar: $%.0f", selectedEnvio.getCostoTotal()));
        lblCosto.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1e293b;");

        // NUEVA INFORMACIÓN: Mostrar destinatario
        Label lblDestinatario = new Label();
        if (selectedEnvio.getNombreDestinatario() != null && !selectedEnvio.getNombreDestinatario().isEmpty()) {
            lblDestinatario.setText("Destinatario: " + selectedEnvio.getNombreDestinatario());
            lblDestinatario.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        }

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Teléfono:"), 0, 1);
        grid.add(txtTelefono, 1, 1);
        grid.add(lblCosto, 0, 2, 2, 1);

        // Agregar label del destinatario si existe
        if (selectedEnvio.getNombreDestinatario() != null && !selectedEnvio.getNombreDestinatario().isEmpty()) {
            grid.add(lblDestinatario, 0, 3, 2, 1);
        }

        dialog.getDialogPane().setContent(grid);
        txtNombre.requestFocus();

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == btnRealizarPago) {
            String nombre = txtNombre.getText().trim();
            String telefono = txtTelefono.getText().trim();

            if (nombre.isEmpty() || telefono.isEmpty()) {
                showAlert("Campos Incompletos",
                        "Debe completar todos los campos",
                        Alert.AlertType.WARNING);
                return;
            }

            Client clienteActual = obtenerClienteActual();
            if (clienteActual == null) {
                showAlert("Error",
                        "No se pudo obtener la información del cliente",
                        Alert.AlertType.ERROR);
                return;
            }

            String telefonoRegistrado = clienteActual.getTelefono();
            iniciarSimulacionPago(telefono, telefonoRegistrado);
        }
    }

    private void iniciarSimulacionPago(String telefonoIngresado, String telefonoRegistrado) {
        vboxSimulador.setVisible(true);
        progressPago.setVisible(true);
        btnPagar.setDisable(true);

        lblSimuladorTitulo.setText("Procesando Pago...");
        lblSimuladorMensaje.setText("🔍 Verificando pago...");
        lblSimuladorMensaje.setStyle("-fx-text-fill: #3b82f6;");

        PauseTransition paso1 = new PauseTransition(Duration.seconds(2.5));
        paso1.setOnFinished(e -> {
            lblSimuladorMensaje.setText("⚙️ Creando pago...");
            lblSimuladorMensaje.setStyle("-fx-text-fill: #f59e0b;");

            PauseTransition paso2 = new PauseTransition(Duration.seconds(2.5));
            paso2.setOnFinished(e2 -> {
                progressPago.setVisible(false);

                boolean pagoAprobado = telefonoIngresado.equals(telefonoRegistrado);

                if (pagoAprobado) {
                    lblSimuladorTitulo.setText("¡Pago Aprobado!");
                    lblSimuladorMensaje.setText("✅ El pago ha sido procesado exitosamente");
                    lblSimuladorMensaje.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");

                    selectedEnvio.setEstado("PAGADO");
                    sameDay.updateEnvio(selectedEnvio);
                    loadTable();

                    showAlert("Pago Exitoso",
                            "El pago del envío " + selectedEnvio.getId() + " ha sido procesado correctamente",
                            Alert.AlertType.INFORMATION);
                } else {
                    lblSimuladorTitulo.setText("Pago Rechazado");
                    lblSimuladorMensaje.setText("❌ El número de teléfono no coincide con el registrado");
                    lblSimuladorMensaje.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");

                    showAlert("Pago Rechazado",
                            "El número de teléfono ingresado no coincide con el registrado en su cuenta",
                            Alert.AlertType.ERROR);
                }

                // IMPORTANTE: Limpiar selección y reactivar botón después del simulador
                PauseTransition ocultarSimulador = new PauseTransition(Duration.seconds(3));
                ocultarSimulador.setOnFinished(e3 -> {
                    vboxSimulador.setVisible(false);
                    progressPago.setVisible(true); // Resetear para próximo uso

                    // Limpiar selección de la tabla
                    tablaEnvios.getSelectionModel().clearSelection();
                    selectedEnvio = null;

                    // Reactivar botón
                    btnPagar.setDisable(false);

                    System.out.println("Simulador ocultado - Listo para siguiente pago");
                });
                ocultarSimulador.play();
            });
            paso2.play();
        });
        paso1.play();
    }

    private Client obtenerClienteActual() {
        if (sameDay.getUserActive() instanceof Client) {
            return (Client) sameDay.getUserActive();
        }
        return null;
    }

    private void loadTable() {
        System.out.println("\n=== CARGANDO TABLA DE PAGOS ===");
        ObservableList<Envio> enviosList = FXCollections.observableArrayList(sameDay.getListEnvios());
        tablaEnvios.setItems(enviosList);
        tablaEnvios.refresh();
        System.out.println("=== TABLA CARGADA ===\n");
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}