package co.edu.uniquindio.sameday.controllers;

import co.edu.uniquindio.sameday.models.SameDay;
import co.edu.uniquindio.sameday.models.entities.Address;
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

    // Dirección seleccionada para editar
    private Address selectedAddress = null;

    // ==================== COMPONENTES FXML ====================

    @FXML
    private TextField txtAlias;

    @FXML
    private TextField txtCalle;

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
    private TableColumn<Address, String> colInfoAdicional;

    // ==================== INICIALIZACIÓN ====================

    @FXML
    void initialize() {
        configureComboBoxes();
        configureTable();
        loadTable();  // CAMBIO: usar loadTable en lugar de loadAddresses
        configureTableSelection();
        btnActualizar.setDisable(true);
    }

    private void configureComboBoxes() {
        cmbTipo.setItems(FXCollections.observableArrayList(AddressType.values()));
        cmbCiudad.setItems(FXCollections.observableArrayList(City.values()));
    }

    private void configureTable() {
        colAlias.setCellValueFactory(new PropertyValueFactory<>("alias"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("type"));
        colCalle.setCellValueFactory(new PropertyValueFactory<>("street"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("city"));
        colInfoAdicional.setCellValueFactory(new PropertyValueFactory<>("additionalInfo"));
    }

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

    private void loadAddressInForm(Address address) {
        txtAlias.setText(address.getAlias());
        txtCalle.setText(address.getStreet());
        txtInfoAdicional.setText(address.getAdditionalInfo());
        cmbTipo.setValue(address.getType());
        cmbCiudad.setValue(address.getCity());
    }

    // ==================== OPERACIONES CRUD ====================

    /**
     * Agrega una nueva dirección al sistema
     * Se actualiza automáticamente la tabla y se limpian los campos
     */
    @FXML
    void onAgregar(ActionEvent event) {
        if (!validateFields()) {
            return;
        }

        String id = generateAddressId();

        // Crear nueva dirección sin descripción del lugar
        Address newAddress = new Address(
                id,
                txtAlias.getText().trim(),
                txtCalle.getText().trim(),
                cmbCiudad.getValue(),
                cmbTipo.getValue(),
                "Sin descripción",
                txtInfoAdicional.getText().trim()
        );

        // Agregar al sistema
        sameDay.addAddress(newAddress);

        // Mostrar mensaje de éxito
        showAlert("Éxito", "Dirección agregada correctamente", Alert.AlertType.INFORMATION);

        // Recargar la tabla completa desde SameDay
        loadTable();

        // Limpiar campos automáticamente
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
        selectedAddress.setAdditionalInfo(txtInfoAdicional.getText().trim());

        sameDay.updateAddress(selectedAddress);

        showAlert("Éxito", "Dirección actualizada correctamente", Alert.AlertType.INFORMATION);

        // Recargar la tabla
        loadTable();

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

            showAlert("Éxito", "Dirección eliminada correctamente", Alert.AlertType.INFORMATION);

            // Recargar la tabla
            loadTable();

            clearForm();
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Limpia todos los campos del formulario
     */
    private void clearForm() {
        txtAlias.clear();
        txtCalle.clear();
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

        return true;
    }

    /**
     * Carga/Recarga la tabla con los datos actuales de SameDay
     * Similar a loadTable() en InmobiliariaViewController
     */
    public void loadTable() {
        // Crear una nueva ObservableList con los datos actuales
        ObservableList<Address> addressList = FXCollections.observableArrayList(sameDay.getListAddresses());

        // Asignar a la tabla
        tablaDirecciones.setItems(addressList);

        // Refrescar la tabla
        tablaDirecciones.refresh();
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