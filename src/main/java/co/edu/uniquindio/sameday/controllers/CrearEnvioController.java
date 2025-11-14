package co.edu.uniquindio.sameday.controllers;

import co.edu.uniquindio.sameday.models.*;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
import co.edu.uniquindio.sameday.models.structural.facade.EnvioFacade;
import co.edu.uniquindio.sameday.models.structural.facade.ResultadoOperacion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class CrearEnvioController {

    // PATRÓN FACADE: Simplifica toda la lógica de gestión de envíos
    private EnvioFacade envioFacade = new EnvioFacade();
    private SameDay sameDay = SameDay.getInstance();
    private Envio selectedEnvio = null;
    private double cotizacionActual = 0.0;

    @FXML private TextField txtId;
    @FXML private ComboBox<Address> cmbOrigen;
    @FXML private ComboBox<Address> cmbDestino;

    // Campos del destinatario - NUEVO
    @FXML private TextField txtNombreDestinatario;
    @FXML private TextField txtCedulaDestinatario;
    @FXML private TextField txtTelefonoDestinatario;

    @FXML private TextField txtContenido;
    @FXML private TextField txtPeso;
    @FXML private TextField txtDimensiones;
    @FXML private TextField txtVolumen;

    // CheckBoxes para servicios adicionales
    @FXML private CheckBox chkSeguro;
    @FXML private CheckBox chkFragil;
    @FXML private CheckBox chkFirmaRequerida;
    @FXML private CheckBox chkPrioridad;

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
    @FXML private TableColumn<Envio, String> colDestinatario; // NUEVA COLUMNA
    @FXML private TableColumn<Envio, String> colContenido;
    @FXML private TableColumn<Envio, String> colPeso;
    @FXML private TableColumn<Envio, String> colServicios;
    @FXML private TableColumn<Envio, String> colCosto;

    @FXML
    void initialize() {
        System.out.println("=== INICIALIZANDO CONTROLADOR CREAR ENVÍO (CON FACADE) ===");
        configureComboBoxes();
        configureTable();
        loadTable();
        configureTableSelection();
        btnActualizar.setDisable(true);
        txtId.setText(envioFacade.generarIdEnvio());
    }

    private void configureComboBoxes() {
        List<Address> direccionesRemitente = new ArrayList<>();
        for (Address address : sameDay.getListAddresses()) {
            if (address.getType() == AddressType.REMITENTE) {
                direccionesRemitente.add(address);
            }
        }
        cmbOrigen.setItems(FXCollections.observableArrayList(direccionesRemitente));

        List<Address> direccionesDestinatario = new ArrayList<>();
        for (Address address : sameDay.getListAddresses()) {
            if (address.getType() == AddressType.DESTINATARIO) {
                direccionesDestinatario.add(address);
            }
        }
        cmbDestino.setItems(FXCollections.observableArrayList(direccionesDestinatario));
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

        // Cargar información del destinatario - NUEVO
        txtNombreDestinatario.setText(envio.getNombreDestinatario());
        txtCedulaDestinatario.setText(envio.getCedulaDestinatario());
        txtTelefonoDestinatario.setText(envio.getTelefonoDestinatario());

        txtContenido.setText(envio.getContenido());
        txtPeso.setText(String.valueOf(envio.getPeso()));
        txtDimensiones.setText(envio.getDimensiones());
        txtVolumen.setText(String.valueOf(envio.getVolumen()));

        // Cargar servicios en los checkboxes
        chkSeguro.setSelected(envio.getServiciosAdicionales().contains(ServicioAdicional.SEGURO));
        chkFragil.setSelected(envio.getServiciosAdicionales().contains(ServicioAdicional.FRAGIL));
        chkFirmaRequerida.setSelected(envio.getServiciosAdicionales().contains(ServicioAdicional.FIRMA_REQUERIDA));
        chkPrioridad.setSelected(envio.getServiciosAdicionales().contains(ServicioAdicional.PRIORIDAD));

        lblCotizacion.setText(String.format("Costo Total: $%.0f", envio.getCostoTotal()));
        cotizacionActual = envio.getCostoTotal();
    }

    private List<ServicioAdicional> getServiciosSeleccionados() {
        List<ServicioAdicional> servicios = new ArrayList<>();

        if (chkSeguro.isSelected()) {
            servicios.add(ServicioAdicional.SEGURO);
        }
        if (chkFragil.isSelected()) {
            servicios.add(ServicioAdicional.FRAGIL);
        }
        if (chkFirmaRequerida.isSelected()) {
            servicios.add(ServicioAdicional.FIRMA_REQUERIDA);
        }
        if (chkPrioridad.isSelected()) {
            servicios.add(ServicioAdicional.PRIORIDAD);
        }

        return servicios;
    }

    @FXML
    void onCotizar(ActionEvent event) {
        try {
            double peso = Double.parseDouble(txtPeso.getText().trim());
            List<ServicioAdicional> servicios = getServiciosSeleccionados();

            // USANDO FACADE: Simplifica el cálculo de costos
            ResultadoOperacion resultado = envioFacade.calcularCostoEnvio(peso, servicios);

            if (resultado.isExitoso()) {
                EnvioFacade.CotizacionResult cotizacion =
                        (EnvioFacade.CotizacionResult) resultado.getDato();

                cotizacionActual = cotizacion.getCostoTotal();
                lblCotizacion.setText(String.format("💰 Costo Total: $%,.0f", cotizacionActual));
                lblCotizacion.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");

                showAlert("Cotización Exitosa",
                        String.format("El costo total del envío es: $%,.0f\n\nDetalles:\n%s",
                                cotizacionActual, cotizacion.getDescripcion()),
                        Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", resultado.getMensaje(), Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            showAlert("Error de Formato",
                    "El peso debe ser un número válido",
                    Alert.AlertType.ERROR);
        }
    }
    @FXML
    void onAgregar(ActionEvent event) {
        // Validar campos del destinatario
        if (!validarCamposDestinatario()) {
            return;
        }

        try {
            double peso = Double.parseDouble(txtPeso.getText().trim());
            double volumen = Double.parseDouble(txtVolumen.getText().trim());

            // USANDO FACADE (ahora pasa datos del destinatario) ✨
            ResultadoOperacion resultado = envioFacade.crearEnvio(
                    cmbOrigen.getValue(),
                    cmbDestino.getValue(),
                    txtContenido.getText().trim(),
                    peso,
                    txtDimensiones.getText().trim(),
                    volumen,
                    getServiciosSeleccionados(),
                    cotizacionActual,
                    txtNombreDestinatario.getText().trim(),  // ✨ AGREGADO
                    txtCedulaDestinatario.getText().trim(),  // ✨ AGREGADO
                    txtTelefonoDestinatario.getText().trim() // ✨ AGREGADO
            );

            if (resultado.isExitoso()) {
                showAlert("Éxito", resultado.getMensaje(), Alert.AlertType.INFORMATION);
                loadTable();
                clearForm();
            } else {
                showAlert("Error", resultado.getMensaje(), Alert.AlertType.WARNING);
            }

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

        if (!validarCamposDestinatario()) {
            return;
        }

        try {
            double peso = Double.parseDouble(txtPeso.getText().trim());
            double volumen = Double.parseDouble(txtVolumen.getText().trim());

            // USANDO FACADE (ahora pasa datos del destinatario) ✨
            ResultadoOperacion resultado = envioFacade.actualizarEnvio(
                    selectedEnvio.getId(),
                    cmbOrigen.getValue(),
                    cmbDestino.getValue(),
                    txtContenido.getText().trim(),
                    peso,
                    txtDimensiones.getText().trim(),
                    volumen,
                    getServiciosSeleccionados(),
                    cotizacionActual,
                    txtNombreDestinatario.getText().trim(),  // ✨ AGREGADO
                    txtCedulaDestinatario.getText().trim(),  // ✨ AGREGADO
                    txtTelefonoDestinatario.getText().trim() // ✨ AGREGADO
            );

            if (resultado.isExitoso()) {
                showAlert("Éxito", resultado.getMensaje(), Alert.AlertType.INFORMATION);
                loadTable();
                clearForm();
            } else {
                showAlert("Error", resultado.getMensaje(), Alert.AlertType.WARNING);
            }

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
            // USANDO FACADE: Simplifica la eliminación
            ResultadoOperacion resultado = envioFacade.eliminarEnvio(selected.getId());

            if (resultado.isExitoso()) {
                showAlert("Éxito", resultado.getMensaje(), Alert.AlertType.INFORMATION);
                loadTable();
                clearForm();
            } else {
                showAlert("Error", resultado.getMensaje(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void onLimpiar(ActionEvent event) {
        clearForm();
    }

    /**
     * Valida los campos del destinatario - NUEVO
     */
    private boolean validarCamposDestinatario() {
        if (txtNombreDestinatario.getText().trim().isEmpty()) {
            showAlert("Campos Incompletos",
                    "Debe ingresar el nombre del destinatario",
                    Alert.AlertType.WARNING);
            txtNombreDestinatario.requestFocus();
            return false;
        }

        if (txtCedulaDestinatario.getText().trim().isEmpty()) {
            showAlert("Campos Incompletos",
                    "Debe ingresar la cédula del destinatario",
                    Alert.AlertType.WARNING);
            txtCedulaDestinatario.requestFocus();
            return false;
        }

        if (txtTelefonoDestinatario.getText().trim().isEmpty()) {
            showAlert("Campos Incompletos",
                    "Debe ingresar el teléfono del destinatario",
                    Alert.AlertType.WARNING);
            txtTelefonoDestinatario.requestFocus();
            return false;
        }

        // Validar que la cédula sea numérica
        if (!txtCedulaDestinatario.getText().trim().matches("\\d+")) {
            showAlert("Formato Inválido",
                    "La cédula debe contener solo números",
                    Alert.AlertType.WARNING);
            txtCedulaDestinatario.requestFocus();
            return false;
        }

        // Validar que el teléfono sea numérico y tenga longitud adecuada
        String telefono = txtTelefonoDestinatario.getText().trim();
        if (!telefono.matches("\\d{10}")) {
            showAlert("Formato Inválido",
                    "El teléfono debe tener 10 dígitos",
                    Alert.AlertType.WARNING);
            txtTelefonoDestinatario.requestFocus();
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtId.setText(envioFacade.generarIdEnvio());
        cmbOrigen.setValue(null);
        cmbDestino.setValue(null);

        // Limpiar campos del destinatario - NUEVO
        txtNombreDestinatario.clear();
        txtCedulaDestinatario.clear();
        txtTelefonoDestinatario.clear();

        txtContenido.clear();
        txtPeso.clear();
        txtDimensiones.clear();
        txtVolumen.clear();

        // Limpiar checkboxes
        chkSeguro.setSelected(false);
        chkFragil.setSelected(false);
        chkFirmaRequerida.setSelected(false);
        chkPrioridad.setSelected(false);

        lblCotizacion.setText("Presione 'Cotizar' para calcular el costo");
        lblCotizacion.setStyle("-fx-text-fill: #1e40af;");
        cotizacionActual = 0.0;
        selectedEnvio = null;
        tablaEnvios.getSelectionModel().clearSelection();
        btnAgregar.setDisable(false);
        btnActualizar.setDisable(true);
    }

    public void loadTable() {
        System.out.println("\n=== CARGANDO TABLA DE ENVÍOS ===");
        // USANDO FACADE: Obtener todos los envíos
        ObservableList<Envio> enviosList =
                FXCollections.observableArrayList(envioFacade.obtenerTodosLosEnvios());
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
