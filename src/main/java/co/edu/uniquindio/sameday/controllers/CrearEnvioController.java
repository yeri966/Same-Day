package co.edu.uniquindio.sameday.controllers;

import co.edu.uniquindio.sameday.models.*;
import co.edu.uniquindio.sameday.models.entities.*;
import co.edu.uniquindio.sameday.models.decorator.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la gestión de envíos
 * Implementa el patrón DECORATOR para servicios adicionales
 */
public class CrearEnvioController {

    private SameDay sameDay = SameDay.getInstance();
    private Envio selectedEnvio = null;
    private double cotizacionActual = 0.0;

    @FXML private TextField txtId;
    @FXML private ComboBox<Address> cmbOrigen;
    @FXML private ComboBox<Address> cmbDestino;
    @FXML private TextField txtPeso;
    @FXML private TextField txtDimensiones;
    @FXML private TextField txtVolumen;
    @FXML private ComboBox<ServicioAdicional> cmbServiciosAdicionales;
    @FXML private Label lblCotizacion;
    @FXML private Button btnAgregar;
    @FXML private Button btnActualizar;
    @FXML private Button btnCotizar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnEliminar;
    @FXML private TableView<Envio> tablaEnvios;
    @FXML private TableColumn<Envio, String> colId;
    @FXML private TableColumn<Envio, String> colOrigen;
    @FXML private TableColumn<Envio, String> colDestino;
    @FXML private TableColumn<Envio, String> colPeso;
    @FXML private TableColumn<Envio, String> colServicios;
    @FXML private TableColumn<Envio, String> colCosto;
    @FXML private TableColumn<Envio, String> colEstado;

    private List<ServicioAdicional> serviciosSeleccionados = new ArrayList<>();

    @FXML
    void initialize() {
        System.out.println("=== INICIALIZANDO CONTROLADOR CREAR ENVÍO ===");
        configureComboBoxes();
        configureTable();
        loadTable();
        configureTableSelection();
        btnActualizar.setDisable(true);
        txtId.setText(generateEnvioId());
    }

    private void configureComboBoxes() {
        // Cargar direcciones de REMITENTE en Origen
        List<Address> direccionesRemitente = new ArrayList<>();
        for (Address address : sameDay.getListAddresses()) {
            if (address.getType() == AddressType.REMITENTE) {
                direccionesRemitente.add(address);
            }
        }
        cmbOrigen.setItems(FXCollections.observableArrayList(direccionesRemitente));

        // Cargar direcciones de DESTINATARIO en Destino
        List<Address> direccionesDestinatario = new ArrayList<>();
        for (Address address : sameDay.getListAddresses()) {
            if (address.getType() == AddressType.DESTINATARIO) {
                direccionesDestinatario.add(address);
            }
        }
        cmbDestino.setItems(FXCollections.observableArrayList(direccionesDestinatario));

        // Cargar servicios adicionales
        cmbServiciosAdicionales.setItems(FXCollections.observableArrayList(ServicioAdicional.values()));
        cmbServiciosAdicionales.setOnAction(event -> onServicioSeleccionado());
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

        colPeso.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f", cellData.getValue().getPeso())));

        colServicios.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getServiciosAdicionalesString()));

        colCosto.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("$%.0f", cellData.getValue().getCostoTotal())));

        colEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstado()));
    }

    private void configureTableSelection() {
        tablaEnvios.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectedEnvio = newValue;
                        loadEnvioInForm(newValue);
                        btnActualizar.setDisable(false);
                        btnAgregar.setDisable(true);
                    }
                }
        );
    }

    private void loadEnvioInForm(Envio envio) {
        txtId.setText(envio.getId());
        cmbOrigen.setValue(envio.getOrigen());
        cmbDestino.setValue(envio.getDestino());
        txtPeso.setText(String.valueOf(envio.getPeso()));
        txtDimensiones.setText(envio.getDimensiones());
        txtVolumen.setText(String.valueOf(envio.getVolumen()));
        serviciosSeleccionados = new ArrayList<>(envio.getServiciosAdicionales());
        lblCotizacion.setText(String.format("Costo Total: $%.0f", envio.getCostoTotal()));
        cotizacionActual = envio.getCostoTotal();
    }

    private void onServicioSeleccionado() {
        ServicioAdicional servicio = cmbServiciosAdicionales.getValue();
        if (servicio != null && !serviciosSeleccionados.contains(servicio)) {
            serviciosSeleccionados.add(servicio);
            showAlert("Servicio Agregado",
                    "Se agregó: " + servicio.toString(),
                    Alert.AlertType.INFORMATION);
            cmbServiciosAdicionales.setValue(null);
        }
    }

    @FXML
    void onCotizar(ActionEvent event) {
        if (!validateFields()) {
            return;
        }

        try {
            double peso = Double.parseDouble(txtPeso.getText().trim());

            // PATRÓN DECORATOR: Crear envío básico
            EnvioComponent envio = new EnvioBasico(peso);

            // PATRÓN DECORATOR: Aplicar decoradores según servicios seleccionados
            for (ServicioAdicional servicio : serviciosSeleccionados) {
                envio = aplicarDecorador(envio, servicio);
            }

            // Calcular costo total
            cotizacionActual = envio.calcularCosto();

            // Mostrar cotización
            lblCotizacion.setText(String.format("💰 Costo Total: $%,.0f", cotizacionActual));
            lblCotizacion.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");

            showAlert("Cotización Exitosa",
                    String.format("El costo total del envío es: $%,.0f\n\nDetalles:\n%s",
                            cotizacionActual, envio.getDescripcion()),
                    Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Error de Formato",
                    "El peso debe ser un número válido",
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * PATRÓN DECORATOR: Aplica el decorador correspondiente según el servicio
     */
    private EnvioComponent aplicarDecorador(EnvioComponent envio, ServicioAdicional servicio) {
        switch (servicio) {
            case SEGURO:
                return new SeguroDecorator(envio);
            case FRAGIL:
                return new FragilDecorator(envio);
            case FIRMA_REQUERIDA:
                return new FirmaRequeridaDecorator(envio);
            case PRIORIDAD:
                return new PrioridadDecorator(envio);
            default:
                return envio;
        }
    }

    @FXML
    void onAgregar(ActionEvent event) {
        if (!validateFields()) {
            return;
        }

        if (cotizacionActual == 0.0) {
            showAlert("Falta Cotización",
                    "Debe cotizar el envío antes de agregarlo",
                    Alert.AlertType.WARNING);
            return;
        }

        try {
            Envio nuevoEnvio = new Envio();
            nuevoEnvio.setId(txtId.getText().trim());
            nuevoEnvio.setOrigen(cmbOrigen.getValue());
            nuevoEnvio.setDestino(cmbDestino.getValue());
            nuevoEnvio.setPeso(Double.parseDouble(txtPeso.getText().trim()));
            nuevoEnvio.setDimensiones(txtDimensiones.getText().trim());
            nuevoEnvio.setVolumen(Double.parseDouble(txtVolumen.getText().trim()));
            nuevoEnvio.setServiciosAdicionales(new ArrayList<>(serviciosSeleccionados));
            nuevoEnvio.setCostoTotal(cotizacionActual);

            sameDay.addEnvio(nuevoEnvio);

            showAlert("Éxito",
                    "Envío agregado correctamente",
                    Alert.AlertType.INFORMATION);

            loadTable();
            clearForm();

        } catch (NumberFormatException e) {
            showAlert("Error de Formato",
                    "Verifique que todos los campos numéricos sean válidos",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onActualizar(ActionEvent event) {
        if (selectedEnvio == null) {
            showAlert("Error",
                    "Debe seleccionar un envío de la tabla",
                    Alert.AlertType.WARNING);
            return;
        }

        if (!validateFields()) {
            return;
        }

        if (cotizacionActual == 0.0) {
            showAlert("Falta Cotización",
                    "Debe cotizar el envío antes de actualizarlo",
                    Alert.AlertType.WARNING);
            return;
        }

        try {
            selectedEnvio.setOrigen(cmbOrigen.getValue());
            selectedEnvio.setDestino(cmbDestino.getValue());
            selectedEnvio.setPeso(Double.parseDouble(txtPeso.getText().trim()));
            selectedEnvio.setDimensiones(txtDimensiones.getText().trim());
            selectedEnvio.setVolumen(Double.parseDouble(txtVolumen.getText().trim()));
            selectedEnvio.setServiciosAdicionales(new ArrayList<>(serviciosSeleccionados));
            selectedEnvio.setCostoTotal(cotizacionActual);

            sameDay.updateEnvio(selectedEnvio);

            showAlert("Éxito",
                    "Envío actualizado correctamente",
                    Alert.AlertType.INFORMATION);

            loadTable();
            clearForm();

        } catch (NumberFormatException e) {
            showAlert("Error de Formato",
                    "Verifique que todos los campos numéricos sean válidos",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onEliminar(ActionEvent event) {
        Envio selected = tablaEnvios.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Error",
                    "Debe seleccionar un envío de la tabla",
                    Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar este envío?");
        confirmacion.setContentText("ID: " + selected.getId());

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            sameDay.deleteEnvio(selected.getId());

            showAlert("Éxito",
                    "Envío eliminado correctamente",
                    Alert.AlertType.INFORMATION);

            loadTable();
            clearForm();
        }
    }

    @FXML
    void onLimpiar(ActionEvent event) {
        clearForm();
    }

    private void clearForm() {
        txtId.setText(generateEnvioId());
        cmbOrigen.setValue(null);
        cmbDestino.setValue(null);
        txtPeso.clear();
        txtDimensiones.clear();
        txtVolumen.clear();
        cmbServiciosAdicionales.setValue(null);
        serviciosSeleccionados.clear();
        lblCotizacion.setText("Presione 'Cotizar' para calcular el costo del envío");
        lblCotizacion.setStyle("-fx-text-fill: #1e40af;");
        cotizacionActual = 0.0;
        selectedEnvio = null;
        tablaEnvios.getSelectionModel().clearSelection();
        btnAgregar.setDisable(false);
        btnActualizar.setDisable(true);
    }

    private boolean validateFields() {
        if (cmbOrigen.getValue() == null) {
            showAlert("Campos Incompletos",
                    "Debe seleccionar una dirección de origen",
                    Alert.AlertType.WARNING);
            return false;
        }

        if (cmbDestino.getValue() == null) {
            showAlert("Campos Incompletos",
                    "Debe seleccionar una dirección de destino",
                    Alert.AlertType.WARNING);
            return false;
        }

        if (txtPeso.getText().trim().isEmpty()) {
            showAlert("Campos Incompletos",
                    "Debe ingresar el peso del paquete",
                    Alert.AlertType.WARNING);
            txtPeso.requestFocus();
            return false;
        }

        if (txtDimensiones.getText().trim().isEmpty()) {
            showAlert("Campos Incompletos",
                    "Debe ingresar las dimensiones del paquete",
                    Alert.AlertType.WARNING);
            txtDimensiones.requestFocus();
            return false;
        }

        if (txtVolumen.getText().trim().isEmpty()) {
            showAlert("Campos Incompletos",
                    "Debe ingresar el volumen del paquete",
                    Alert.AlertType.WARNING);
            txtVolumen.requestFocus();
            return false;
        }

        try {
            double peso = Double.parseDouble(txtPeso.getText().trim());
            if (peso <= 0) {
                showAlert("Valor Inválido",
                        "El peso debe ser mayor a 0",
                        Alert.AlertType.WARNING);
                return false;
            }

            double volumen = Double.parseDouble(txtVolumen.getText().trim());
            if (volumen <= 0) {
                showAlert("Valor Inválido",
                        "El volumen debe ser mayor a 0",
                        Alert.AlertType.WARNING);
                return false;
            }

        } catch (NumberFormatException e) {
            showAlert("Error de Formato",
                    "El peso y volumen deben ser números válidos",
                    Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    public void loadTable() {
        System.out.println("\n=== CARGANDO TABLA DE ENVÍOS ===");
        ObservableList<Envio> enviosList = FXCollections.observableArrayList(sameDay.getListEnvios());
        tablaEnvios.setItems(enviosList);
        tablaEnvios.refresh();
        System.out.println("=== TABLA CARGADA ===\n");
    }

    private String generateEnvioId() {
        int count = sameDay.getListEnvios().size();
        return String.format("ENV%04d", count + 1);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}