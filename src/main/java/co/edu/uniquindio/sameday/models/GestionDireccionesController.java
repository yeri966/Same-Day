package co.edu.uniquindio.sameday.controllers;

import co.edu.uniquindio.sameday.models.SameDay;
import co.edu.uniquindio.sameday.models.AddressType;
import co.edu.uniquindio.sameday.models.City;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador para la gestión de direcciones en el sistema
 * Permite realizar operaciones CRUD sobre las direcciones
 */
public class GestionDireccionesController {

    // Referencia al modelo principal (Singleton)
    private SameDay sameDay = SameDay.getInstance();

    // Lista observable para la tabla
    private ObservableList<co.edu.uniquindio.sameday.models.entities.Address> addressList = FXCollections.observableArrayList();

    // Dirección seleccionada para editar
    private co.edu.uniquindio.sameday.models.entities.Address selectedAddress = null;

    // ==================== COMPONENTES FXML ====================

    @FXML
    private TextField txtAlias;

    @FXML
    private TextField txtCalle;

    @FXML
    private TextField txtDescripcionLugar;

    @FXML
    private TextField txtInfoAdicional;

    @FXML
    private ComboBox<AddressType> cmbTipo;

    @FXML
    private ComboBox<City> cmbCiudad;

    @FXML
    private Button btnAgregar;

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnLimpiar;

    @FXML
    private Button btnEliminar;

    @FXML
    private TableView<Address> tablaDirecciones;

    @FXML
    private TableColumn<Address, String> colAlias;

    @FXML
    private TableColumn<Address, AddressType> colTipo;

    @FXML
    private TableColumn<Address, String> colCalle;

    @FXML
    private TableColumn<Address, City> colCiudad;

    @FXML
    private TableColumn<Address, String> colDescripcion;

    @FXML
    private TableColumn<Address, String> colInfoAdicional;

    // ==================== INICIALIZACIÓN ====================

    /**
     * Método de inicialización que se ejecuta automáticamente al cargar el FXML
     */
    @FXML
    void initialize() {
        configureComboBoxes();
        configureTable();
        loadAddresses();
        configureTableSelection();
        btnActualizar.setDisable(true);
    }

    /**
     * Configura los ComboBoxes con los valores de los enums
     */
    private void configureComboBoxes() {
        cmbTipo.setItems(FXCollections.observableArrayList(AddressType.values()));
        cmbCiudad.setItems(FXCollections.observableArrayList(City.values()));
    }

    /**
     * Configura las columnas de la tabla
     */
    private void configureTable() {
        colAlias.setCellValueFactory(new PropertyValueFactory<>("alias"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("type"));
        colCalle.setCellValueFactory(new PropertyValueFactory<>("street"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("city"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("placeDescription"));
        colInfoAdicional.setCellValueFactory(new PropertyValueFactory<>("additionalInfo"));
        tablaDirecciones.setItems(addressList);
    }

    /**
     * Configura el listener para la selección de filas en la tabla
     */
    private void configureTableSelection() {
        tablaDirecciones.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectedAddress = newValue;
                        loadAddressInForm(newValue);
                        btnActualizar.setDisable(false);
                        btnAgregar.setDisable(true);
                    }
                }
        );
    }

    /**
     * Carga una dirección seleccionada en el formulario para edición
     */
    private void loadAddressInForm(Address address) {
        txtAlias.setText(address.getAlias());
        txtCalle.setText(address.getStreet());
        txtDescripcionLugar.setText(address.getPlaceDescription());
        txtInfoAdicional.setText(address.getAdditionalInfo());
        cmbTipo.setValue(address.getType());
        cmbCiudad.setValue(address.getCity());
    }

    // ==================== OPERACIONES CRUD ====================

    /**
     * Agrega una nueva dirección al sistema
     */
    @FXML
    void onAgregar(ActionEvent event) {
        if (!validateFields()) {
            return;
        }

        String id = generateAddressId();

        Address newAddress = new Address(
                id,
                txtAlias.getText().trim(),
                txtCalle.getText().trim(),
                cmbCiudad.getValue(),
                cmbTipo.getValue(),
                txtDescripcionLugar.getText().trim(),
                txtInfoAdicional.getText().trim()
        );

        sameDay.addAddress(newAddress);
        addressList.add(newAddress);
        showAlert("Éxito", "Dirección agregada correctamente", Alert.AlertType.INFORMATION);
        clearForm();
    }

    /**
     * Actualiza la dirección seleccionada
     */
    @FXML
    void onActualizar(ActionEvent event) {
        if (selectedAddress == null) {
            showAlert("Error", "Debe seleccionar una dirección de la tabla", Alert.AlertType.WARNING);
            return;
        }

        if (!validateFields()) {
            return;
        }

        selectedAddress.setAlias(txtAlias.getText().trim());
        selectedAddress.setStreet(txtCalle.getText().trim());
        selectedAddress.setCity(cmbCiudad.getValue());
        selectedAddress.setType(cmbTipo.getValue());
        selectedAddress.setPlaceDescription(txtDescripcionLugar.getText().trim());
        selectedAddress.setAdditionalInfo(txtInfoAdicional.getText().trim());

        sameDay.updateAddress(selectedAddress);
        tablaDirecciones.refresh();
        showAlert("Éxito", "Dirección actualizada correctamente", Alert.AlertType.INFORMATION);
        clearForm();
    }

    /**
     * Elimina la dirección seleccionada
     */
    @FXML
    void onEliminar(ActionEvent event) {
        Address selected = tablaDirecciones.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Error", "Debe seleccionar una dirección de la tabla", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar esta dirección?");
        confirmacion.setContentText(selected.getAlias() + " - " + selected.getFullAddress());

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            sameDay.deleteAddress(selected.getId());
            addressList.remove(selected);
            showAlert("Éxito", "Dirección eliminada correctamente", Alert.AlertType.INFORMATION);
            clearForm();
        }
    }

    /**
     * Limpia el formulario
     */
    @FXML
    void onLimpiar(ActionEvent event) {
        clearForm();
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Limpia todos los campos del formulario
     */
    private void clearForm() {
        txtAlias.clear();
        txtCalle.clear();
        txtDescripcionLugar.clear();
        txtInfoAdicional.clear();
        cmbTipo.setValue(null);
        cmbCiudad.setValue(null);
        selectedAddress = null;
        tablaDirecciones.getSelectionModel().clearSelection();
        btnAgregar.setDisable(false);
        btnActualizar.setDisable(true);
    }

    /**
     * Valida que todos los campos obligatorios estén llenos
     */
    private boolean validateFields() {
        if (txtAlias.getText().trim().isEmpty()) {
            showAlert("Campos Incompletos", "Debe ingresar un alias para la dirección", Alert.AlertType.WARNING);
            txtAlias.requestFocus();
            return false;
        }

        if (txtCalle.getText().trim().isEmpty()) {
            showAlert("Campos Incompletos", "Debe ingresar la dirección (calle)", Alert.AlertType.WARNING);
            txtCalle.requestFocus();
            return false;
        }

        if (cmbTipo.getValue() == null) {
            showAlert("Campos Incompletos", "Debe seleccionar el tipo de dirección", Alert.AlertType.WARNING);
            cmbTipo.requestFocus();
            return false;
        }

        if (cmbCiudad.getValue() == null) {
            showAlert("Campos Incompletos", "Debe seleccionar el municipio", Alert.AlertType.WARNING);
            cmbCiudad.requestFocus();
            return false;
        }

        if (txtDescripcionLugar.getText().trim().isEmpty()) {
            showAlert("Campos Incompletos", "Debe ingresar la descripción del lugar", Alert.AlertType.WARNING);
            txtDescripcionLugar.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Carga las direcciones desde el sistema a la tabla
     */
    private void loadAddresses() {
        addressList.clear();
        addressList.addAll(sameDay.getListAddresses());
    }

    /**
     * Genera un ID único para una nueva dirección
     */
    private String generateAddressId() {
        int count = sameDay.getListAddresses().size();
        return String.format("DIR%03d", count + 1);
    }

    /**
     * Muestra un cuadro de diálogo de alerta
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}