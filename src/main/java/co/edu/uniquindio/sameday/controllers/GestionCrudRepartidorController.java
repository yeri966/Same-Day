package co.edu.uniquindio.sameday.controllers;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import co.edu.uniquindio.sameday.models.City;
import co.edu.uniquindio.sameday.models.Client;
import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class GestionCrudRepartidorController {
    private SameDay sameDay;
    private ObservableList<Dealer> listDealer;
    private Dealer selecionadoDealer;


    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addButton;

    @FXML
    private TableColumn<Dealer, String> colCity;

    @FXML
    private ComboBox<City> cboxCity;

    @FXML
    private Button clearButton;

    @FXML
    private TableColumn<Dealer, String> colCorreo;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TableView<Dealer> dealerTable;

    @FXML
    private Button deleteButton;

    @FXML
    private CheckBox disponibleCheckBox;

    @FXML
    private TableColumn<Dealer, String> colDisponible;

    @FXML
    private TableColumn<Dealer, String> colDocumento;

    @FXML
    private TextField txtDocumento;

    @FXML
    private TableColumn<Dealer, String> colId;

    @FXML
    private TextField txtId;

    @FXML
    private TableColumn<Dealer, String> colNombre;

    @FXML
    private TextField txtNombre;

    @FXML
    private TableColumn<Dealer, String> colTelefono;

    @FXML
    private TextField txtTelefono;

    @FXML
    private Button updateButton;

    @FXML
    void handleAdd(ActionEvent event) {
        if (!validateFields()) {
            return;
        }

        // Generar ID secuencial en lugar de UUID
        String id = generarIdRepartidor();

        Dealer newDealer = new Dealer(
                id,
                txtDocumento.getText().trim(),
                txtNombre.getText().trim(),
                txtCorreo.getText().trim(),
                txtTelefono.getText().trim(),
                null,
                disponibleCheckBox.isSelected(),
                cboxCity.getValue()
        );

        newDealer.setCity(cboxCity.getValue());
        newDealer.setDisponible(disponibleCheckBox.isSelected());

        sameDay.agregarPersona(newDealer);
        listDealer.add(newDealer);

        showAlert(Alert.AlertType.INFORMATION, "Éxito", "Repartidor agregado correctamente");
        handleClear(event);
    }

    @FXML
    void handleClear(ActionEvent event) {
        txtId.clear();
        txtDocumento.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        cboxCity.setValue(null);
        disponibleCheckBox.setSelected(true);

        dealerTable.getSelectionModel().clearSelection();
        selecionadoDealer = null;

        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

    }

    @FXML
    void handleDelete(ActionEvent event) {
        if (selecionadoDealer == null) {
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Eliminación");
        confirmAlert.setHeaderText("¿Eliminar repartidor?");
        confirmAlert.setContentText("¿Está seguro de eliminar a " + selecionadoDealer.getNombre() +
                "? Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            sameDay.eliminarPersona(selecionadoDealer);
            listDealer.remove(selecionadoDealer);
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Repartidor eliminado correctamente");
            handleClear(event);
        }

    }

    @FXML
    void handleUpdate(ActionEvent event) {
        if (selecionadoDealer == null || !validateFields()) {
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Actualización");
        confirmAlert.setHeaderText("¿Actualizar repartidor?");
        confirmAlert.setContentText("¿Está seguro de actualizar la información de " +
                selecionadoDealer
                        .getNombre() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            selecionadoDealer.setDocumento(txtDocumento.getText().trim());
            selecionadoDealer.setNombre(txtNombre.getText().trim());
            selecionadoDealer.setCorreo(txtCorreo.getText().trim());
            selecionadoDealer.setTelefono(txtTelefono.getText().trim());
            selecionadoDealer.setCity(cboxCity.getValue());
            selecionadoDealer.setDisponible(disponibleCheckBox.isSelected());

            dealerTable.refresh();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Repartidor actualizado correctamente");
            handleClear(event);
        }

    }

    @FXML
    void initialize() {
        sameDay = SameDay.getInstance();
        listDealer = FXCollections.observableArrayList();

        setupCityComboBox();
        setupTableColumns();
        setupTableSelection();
        loadDealers();

        disponibleCheckBox.setSelected(true);

    }

    private void setupCityComboBox() {
        cboxCity.setItems(FXCollections.observableArrayList(City.values()));
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDocumento.setCellValueFactory(new PropertyValueFactory<>("documento"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        colCity.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getCity() != null ?
                                cellData.getValue().getCity().name() : "N/A"
                )
        );

        colDisponible.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().isDisponible() ? "Disponible" : "No disponible"
                )
        );

        dealerTable.setItems(listDealer);
    }

    private void setupTableSelection() {
        dealerTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selecionadoDealer = newValue;
                        populateFields(newValue);
                        updateButton.setDisable(false);
                        deleteButton.setDisable(false);
                        addButton.setDisable(true);
                    }
                }
        );
    }

    private void loadDealers() {
        listDealer.clear();
        for (var person : sameDay.getListPersons()) {
            if (person instanceof Dealer) {
                listDealer.add((Dealer) person);
            }
        }
    }

    private void populateFields(Dealer dealer) {
        txtId.setText(dealer.getId());
        txtDocumento.setText(dealer.getDocumento());
        txtNombre.setText(dealer.getNombre());
        txtCorreo.setText(dealer.getCorreo());
        txtTelefono.setText(dealer.getTelefono());
        cboxCity.setValue(dealer.getCity());
        disponibleCheckBox.setSelected(dealer.isDisponible());
    }

    private boolean validateFields() {
        if (txtDocumento.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación", "El documento es obligatorio");
            return false;
        }

        if (txtNombre.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación", "El nombre es obligatorio");
            return false;
        }

        if (txtCorreo.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación", "El correo es obligatorio");
            return false;
        }

        if (!txtCorreo.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación", "El formato del correo no es válido");
            return false;
        }

        if (txtTelefono.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación", "El teléfono es obligatorio");
            return false;
        }

        if (cboxCity.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación", "Debe seleccionar una ciudad");
            return false;
        }

        return true;
    }

    /**
     * Genera un ID único para un nuevo repartidor
     * Formato: R0001, R0002, R0003, etc.
     *
     * @return String con el ID generado
     */
    private String generarIdRepartidor() {
        // Contar cuántos repartidores existen actualmente
        int numeroRepartidores = (int) sameDay.getListPersons().stream()
                .filter(person -> person instanceof Dealer)
                .count();

        // Generar ID con formato R0001, R0002, etc.
        return String.format("R%04d", numeroRepartidores + 1);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}