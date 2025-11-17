package co.edu.uniquindio.sameday.controllers;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import co.edu.uniquindio.sameday.models.*;
import co.edu.uniquindio.sameday.models.behavioral.state.SuspendedState;
import co.edu.uniquindio.sameday.models.creational.factoryMethod.DealerFactory;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
import javafx.beans.property.SimpleStringProperty;
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

    @FXML private ResourceBundle resources;
    @FXML private URL location;

    @FXML private TextField txtId;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<City> cboxCity;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;
    @FXML private Button btnSuspender;
    @FXML private Button btnActivar;

    @FXML private TableView<Dealer> dealerTable;
    @FXML private TableColumn<Dealer, String> colId;
    @FXML private TableColumn<Dealer, String> colDocumento;
    @FXML private TableColumn<Dealer, String> colNombre;
    @FXML private TableColumn<Dealer, String> colCorreo;
    @FXML private TableColumn<Dealer, String> colTelefono;
    @FXML private TableColumn<Dealer, String> colCity;
    @FXML private TableColumn<Dealer, String> colUsuario;
    @FXML private TableColumn<Dealer, String> colDisponible;
    @FXML private TableColumn<Dealer, String> colEstadoCuenta;

    @FXML private Label lblTotalRepartidores;
    @FXML private Label lblRepartidoresDisponibles;

    @FXML
    void initialize() {
        sameDay = SameDay.getInstance();
        listDealer = FXCollections.observableArrayList();

        setupCityComboBox();
        setupTableColumns();
        setupTableSelection();
        loadDealers();
        actualizarEstadisticas();
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
                new SimpleStringProperty(
                        cellData.getValue().getCity() != null
                                ? cellData.getValue().getCity().name()
                                : "N/A"
                )
        );

        colUsuario.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getUserAccount() != null
                                ? cellData.getValue().getUserAccount().getUser()
                                : "Sin usuario"
                )
        );

        colDisponible.setCellValueFactory(cellData -> {
            Dealer dealer = cellData.getValue();

            if (!dealer.isDisponibleManual()) {
                return new SimpleStringProperty("No disponible");
            }

            boolean disponible = dealer.isDisponible();
            return new SimpleStringProperty(disponible ? "Disponible" : "Ocupado");
        });

        // Nueva columna para estado de cuenta (Patrón State)
        colEstadoCuenta.setCellValueFactory(cellData -> {
            Dealer dealer = cellData.getValue();
            if (dealer.getUserAccount() != null) {
                String estado = dealer.getUserAccount().getAccountState().getStateName();
                return new SimpleStringProperty(estado);
            }
            return new SimpleStringProperty("Sin cuenta");
        });

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

        if (dealer.getUserAccount() != null) {
            txtUsuario.setText(dealer.getUserAccount().getUser());
        } else {
            txtUsuario.clear();
        }

        txtPassword.clear();
        txtPassword.setPromptText("Dejar vacío para mantener la actual");
    }

    @FXML
    void onSuspenderCuenta(ActionEvent event) {
        if (selecionadoDealer == null) {
            showAlert(Alert.AlertType.WARNING, "Selección Requerida",
                    "Debe seleccionar un repartidor de la tabla");
            return;
        }

        if (selecionadoDealer.getUserAccount() == null) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "El repartidor no tiene cuenta de usuario");
            return;
        }

        if (selecionadoDealer.getUserAccount().getAccountState() instanceof SuspendedState) {
            showAlert(Alert.AlertType.WARNING, "Cuenta Ya Suspendida",
                    "Esta cuenta ya se encuentra suspendida");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("Violación de términos de servicio");
        dialog.setTitle("Suspender Cuenta");
        dialog.setHeaderText("Suspender cuenta de: " + selecionadoDealer.getNombre());
        dialog.setContentText("Razón de suspensión:");

        dialog.showAndWait().ifPresent(razon -> {
            String usuario = selecionadoDealer.getUserAccount().getUser();

            if (sameDay.suspenderCuenta(usuario, razon)) {
                showAlert(Alert.AlertType.INFORMATION, "Cuenta Suspendida",
                        "La cuenta ha sido suspendida correctamente.\nRazón: " + razon);

                dealerTable.refresh();
                actualizarEstadisticas();
                handleClear(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "No se pudo suspender la cuenta");
            }
        });
    }

    @FXML
    void onActivarCuenta(ActionEvent event) {
        if (selecionadoDealer == null) {
            showAlert(Alert.AlertType.WARNING, "Selección Requerida",
                    "Debe seleccionar un repartidor de la tabla");
            return;
        }

        if (selecionadoDealer.getUserAccount() == null) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "El repartidor no tiene cuenta de usuario");
            return;
        }

        if (selecionadoDealer.getUserAccount().getAccountState().getStateName().equals("ACTIVA")) {
            showAlert(Alert.AlertType.INFORMATION, "Cuenta Activa",
                    "La cuenta ya se encuentra activa");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Activar Cuenta");
        confirmacion.setHeaderText("¿Activar la cuenta de " + selecionadoDealer.getNombre() + "?");
        confirmacion.setContentText("La cuenta volverá a estar activa y el repartidor podrá iniciar sesión normalmente.");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            String usuario = selecionadoDealer.getUserAccount().getUser();

            if (sameDay.activarCuenta(usuario)) {
                showAlert(Alert.AlertType.INFORMATION, "Cuenta Activada",
                        "La cuenta ha sido activada correctamente");

                dealerTable.refresh();
                actualizarEstadisticas();
                handleClear(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "No se pudo activar la cuenta");
            }
        }
    }

    @FXML
    void handleAdd(ActionEvent event) {
        if (!validateFields(true)) {
            return;
        }

        String id = generarIdRepartidor();

        UserAccount userAccount = new UserAccount(
                txtUsuario.getText().trim(),
                txtPassword.getText().trim(),
                null,
                TypeUser.DEALER
        );

        DealerFactory factory = new DealerFactory(true, cboxCity.getValue());
        Dealer newDealer = (Dealer) factory.crearPerson(id,
                txtDocumento.getText().trim(),
                txtNombre.getText().trim(),
                txtCorreo.getText().trim(),
                txtTelefono.getText().trim(),
                userAccount
        );

        sameDay.agregarPersona(newDealer);
        listDealer.add(newDealer);

        showAlert(Alert.AlertType.INFORMATION, "Éxito",
                "Repartidor agregado correctamente con usuario: " + userAccount.getUser());

        handleClear(event);
        actualizarEstadisticas();
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        if (selecionadoDealer == null || !validateFields(false)) {
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Actualización");
        confirmAlert.setHeaderText("¿Actualizar repartidor?");
        confirmAlert.setContentText(
                "¿Está seguro de actualizar la información de " +
                        selecionadoDealer.getNombre() + "?"
        );

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            selecionadoDealer.setDocumento(txtDocumento.getText().trim());
            selecionadoDealer.setNombre(txtNombre.getText().trim());
            selecionadoDealer.setCorreo(txtCorreo.getText().trim());
            selecionadoDealer.setTelefono(txtTelefono.getText().trim());
            selecionadoDealer.setCity(cboxCity.getValue());

            if (selecionadoDealer.getUserAccount() != null) {
                selecionadoDealer.getUserAccount().setUser(txtUsuario.getText().trim());

                if (!txtPassword.getText().trim().isEmpty()) {
                    selecionadoDealer.getUserAccount().setContrasenia(txtPassword.getText().trim());
                }
            } else {
                if (!txtUsuario.getText().trim().isEmpty() && !txtPassword.getText().trim().isEmpty()) {
                    UserAccount newAccount = new UserAccount(
                            txtUsuario.getText().trim(),
                            txtPassword.getText().trim(),
                            selecionadoDealer,
                            TypeUser.DEALER
                    );
                    selecionadoDealer.setUserAccount(newAccount);
                }
            }

            dealerTable.refresh();
            showAlert(Alert.AlertType.INFORMATION, "Éxito",
                    "Repartidor actualizado correctamente");

            handleClear(event);
            actualizarEstadisticas();
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        if (selecionadoDealer == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección",
                    "Debe seleccionar un repartidor para eliminar");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Eliminación");
        confirmAlert.setHeaderText("¿Eliminar repartidor?");
        confirmAlert.setContentText(
                "¿Está seguro de eliminar a " + selecionadoDealer.getNombre() +
                        "?\n\nEsta acción no se puede deshacer y se eliminarán " +
                        "también sus credenciales de acceso."
        );

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            sameDay.eliminarPersona(selecionadoDealer);
            listDealer.remove(selecionadoDealer);

            showAlert(Alert.AlertType.INFORMATION, "Éxito",
                    "Repartidor eliminado correctamente");

            handleClear(event);
            actualizarEstadisticas();
        }
    }

    @FXML
    void handleClear(ActionEvent event) {
        txtId.clear();
        txtDocumento.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtUsuario.clear();
        txtPassword.clear();
        cboxCity.setValue(null);

        txtPassword.setPromptText("Contraseña de acceso");

        dealerTable.getSelectionModel().clearSelection();
        selecionadoDealer = null;

        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private boolean validateFields(boolean esNuevo) {
        if (txtDocumento.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación",
                    "El documento es obligatorio");
            txtDocumento.requestFocus();
            return false;
        }

        if (txtNombre.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación",
                    "El nombre es obligatorio");
            txtNombre.requestFocus();
            return false;
        }

        if (txtCorreo.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación",
                    "El correo es obligatorio");
            txtCorreo.requestFocus();
            return false;
        }

        if (!validarEmail(txtCorreo.getText().trim())) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación",
                    "El formato del correo no es válido");
            txtCorreo.requestFocus();
            return false;
        }

        if (txtTelefono.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación",
                    "El teléfono es obligatorio");
            txtTelefono.requestFocus();
            return false;
        }

        if (!validarTelefono(txtTelefono.getText().trim())) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación",
                    "El teléfono debe contener solo números y tener entre 7 y 10 dígitos");
            txtTelefono.requestFocus();
            return false;
        }

        if (cboxCity.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación",
                    "Debe seleccionar una ciudad");
            cboxCity.requestFocus();
            return false;
        }

        if (txtUsuario.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación",
                    "El nombre de usuario es obligatorio");
            txtUsuario.requestFocus();
            return false;
        }

        if (txtUsuario.getText().trim().length() < 3) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación",
                    "El usuario debe tener al menos 3 caracteres");
            txtUsuario.requestFocus();
            return false;
        }

        if (esNuevo && txtPassword.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación",
                    "La contraseña es obligatoria para nuevos repartidores");
            txtPassword.requestFocus();
            return false;
        }

        if (!txtPassword.getText().trim().isEmpty() && txtPassword.getText().trim().length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación",
                    "La contraseña debe tener al menos 4 caracteres");
            txtPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validarEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private boolean validarTelefono(String telefono) {
        return telefono.matches("\\d{7,10}");
    }

    private void actualizarEstadisticas() {
        int total = listDealer.size();
        long disponibles = listDealer.stream()
                .filter(Dealer::isDisponible)
                .count();

        lblTotalRepartidores.setText("📊 Total: " + total);
        lblRepartidoresDisponibles.setText("✅ Disponibles: " + disponibles);
    }

    private String generarIdRepartidor() {
        int numeroRepartidores = (int) sameDay.getListPersons().stream()
                .filter(person -> person instanceof Dealer)
                .count();

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