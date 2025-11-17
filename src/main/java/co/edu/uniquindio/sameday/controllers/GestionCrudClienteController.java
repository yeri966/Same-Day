package co.edu.uniquindio.sameday.controllers;

import java.util.List;
import java.util.stream.Collectors;

import co.edu.uniquindio.sameday.models.Client;
import co.edu.uniquindio.sameday.models.TypeUser;
import co.edu.uniquindio.sameday.models.UserAccount;
import co.edu.uniquindio.sameday.models.behavioral.state.SuspendedState;
import co.edu.uniquindio.sameday.models.creational.factoryMethod.ClienteFactory;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class GestionCrudClienteController {

    private ObservableList<Client> listaClientes;
    private SameDay sameDay;
    private Client clienteSeleccionado = null;

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnAgregar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnLimpiar;

    @FXML
    private Button btnSuspender;

    @FXML
    private Button btnActivar;

    @FXML
    private TableColumn<Client, String> colCedula;

    @FXML
    private TableColumn<Client, String> colDireccion;

    @FXML
    private TableColumn<Client, String> colEmail;

    @FXML
    private TableColumn<Client, String> colId;

    @FXML
    private TableColumn<Client, String> colNombre;

    @FXML
    private TableColumn<Client, String> colTelefono;

    @FXML
    private TableColumn<Client, String> colEstado;

    @FXML
    private Label lblClientesActivos;

    @FXML
    private Label lblTotalClientes;

    @FXML
    private TableView<Client> tablaClientes;

    @FXML
    private TextField txtCedula;

    @FXML
    private TextField txtDireccion;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtUsuario;

    @FXML
    void initialize() {
        System.out.println("=== INICIALIZANDO GESTIÓN CLIENTE ===");
        sameDay = SameDay.getInstance();

        listaClientes = FXCollections.observableArrayList();

        configurarTabla();
        cargarClientes();
        configurarSeleccionTabla();
        btnActualizar.setDisable(true);

        System.out.println("=== GESTIÓN CLIENTE INICIALIZADA ===");
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("documento"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        // Columna de estado usando el patrón State
        colEstado.setCellValueFactory(cellData -> {
            Client cliente = cellData.getValue();
            if (cliente.getUserAccount() != null) {
                String estado = cliente.getUserAccount().getAccountState().getStateName();
                return new SimpleStringProperty(estado);
            }
            return new SimpleStringProperty("Sin cuenta");
        });
    }

    private void configurarSeleccionTabla() {
        tablaClientes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        clienteSeleccionado = newValue;
                        cargarClienteEnFormulario(newValue);
                        btnActualizar.setDisable(false);
                        btnAgregar.setDisable(true);
                    }
                }
        );
    }

    private void cargarClienteEnFormulario(Client cliente) {
        txtId.setText(cliente.getId());
        txtNombre.setText(cliente.getNombre());
        txtCedula.setText(cliente.getDocumento());
        txtEmail.setText(cliente.getCorreo());
        txtTelefono.setText(cliente.getTelefono());
        txtDireccion.setText(cliente.getDireccion());

        if (cliente.getUserAccount() != null) {
            txtUsuario.setText(cliente.getUserAccount().getUser());
        }
    }

    private void cargarClientes() {
        System.out.println("🔄 Recargando clientes...");

        List<Client> soloClientes = sameDay.getListPersons().stream()
                .filter(persona -> persona instanceof Client)
                .map(persona -> (Client) persona)
                .collect(Collectors.toList());

        listaClientes.clear();
        listaClientes.addAll(soloClientes);

        tablaClientes.setItems(listaClientes);
        tablaClientes.refresh();

        actualizarEstadisticas();

        System.out.println("✅ " + soloClientes.size() + " clientes cargados");
    }

    private void actualizarEstadisticas() {
        lblTotalClientes.setText("📊 Total: " + listaClientes.size());

        // Contar solo clientes con cuentas activas
        long clientesActivos = listaClientes.stream()
                .filter(c -> c.getUserAccount() != null)
                .filter(c -> c.getUserAccount().getAccountState().getStateName().equals("ACTIVA"))
                .count();

        lblClientesActivos.setText("✅ Activos: " + clientesActivos);
    }

    @FXML
    void onSuspenderCuenta(ActionEvent event) {
        if (clienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Selección Requerida",
                    "Debe seleccionar un cliente de la tabla");
            return;
        }

        if (clienteSeleccionado.getUserAccount() == null) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error",
                    "El cliente no tiene cuenta de usuario");
            return;
        }

        // Verificar si ya está suspendida
        if (clienteSeleccionado.getUserAccount().getAccountState() instanceof SuspendedState) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Cuenta Ya Suspendida",
                    "Esta cuenta ya se encuentra suspendida");
            return;
        }

        // Pedir razón de suspensión
        TextInputDialog dialog = new TextInputDialog("Violación de términos de servicio");
        dialog.setTitle("Suspender Cuenta");
        dialog.setHeaderText("Suspender cuenta de: " + clienteSeleccionado.getNombre());
        dialog.setContentText("Razón de suspensión:");

        dialog.showAndWait().ifPresent(razon -> {
            String usuario = clienteSeleccionado.getUserAccount().getUser();

            if (sameDay.suspenderCuenta(usuario, razon)) {
                mostrarAlerta(Alert.AlertType.INFORMATION,
                        "Cuenta Suspendida",
                        "La cuenta ha sido suspendida correctamente.\nRazón: " + razon);

                tablaClientes.refresh();
                actualizarEstadisticas();
                limpiarFormulario();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR,
                        "Error",
                        "No se pudo suspender la cuenta");
            }
        });
    }

    @FXML
    void onActivarCuenta(ActionEvent event) {
        if (clienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Selección Requerida",
                    "Debe seleccionar un cliente de la tabla");
            return;
        }

        if (clienteSeleccionado.getUserAccount() == null) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error",
                    "El cliente no tiene cuenta de usuario");
            return;
        }

        // Verificar si necesita activación
        if (clienteSeleccionado.getUserAccount().getAccountState().getStateName().equals("ACTIVA")) {
            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Cuenta Activa",
                    "La cuenta ya se encuentra activa");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Activar Cuenta");
        confirmacion.setHeaderText("¿Activar la cuenta de " + clienteSeleccionado.getNombre() + "?");
        confirmacion.setContentText("La cuenta volverá a estar activa y el usuario podrá iniciar sesión normalmente.");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            String usuario = clienteSeleccionado.getUserAccount().getUser();

            if (sameDay.activarCuenta(usuario)) {
                mostrarAlerta(Alert.AlertType.INFORMATION,
                        "Cuenta Activada",
                        "La cuenta ha sido activada correctamente");

                tablaClientes.refresh();
                actualizarEstadisticas();
                limpiarFormulario();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR,
                        "Error",
                        "No se pudo activar la cuenta");
            }
        }
    }

    @FXML
    void onAgregar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        String nuevoId = generarIdCliente();

        String nombreUsuario = txtUsuario.getText().trim();
        if (nombreUsuario.isEmpty()) {
            nombreUsuario = "cliente" + nuevoId;
        }

        UserAccount userAccount = new UserAccount(
                nombreUsuario,
                "1234",
                null,
                TypeUser.CLIENT
        );

        ClienteFactory factory = new ClienteFactory(txtDireccion.getText().trim());
        Client nuevoCliente = (Client) factory.crearPerson(nuevoId,
                txtCedula.getText().trim(),
                txtNombre.getText().trim(),
                txtEmail.getText().trim(),
                txtTelefono.getText().trim(),
                userAccount
        );

        sameDay.agregarPersona(nuevoCliente);

        mostrarAlerta(Alert.AlertType.INFORMATION,
                "Cliente Agregado",
                "El cliente ha sido agregado exitosamente");

        cargarClientes();
        limpiarFormulario();
    }

    @FXML
    void onActualizar(ActionEvent event) {
        if (clienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Selección Requerida",
                    "Debe seleccionar un cliente de la tabla");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        clienteSeleccionado.setNombre(txtNombre.getText().trim());
        clienteSeleccionado.setDocumento(txtCedula.getText().trim());
        clienteSeleccionado.setCorreo(txtEmail.getText().trim());
        clienteSeleccionado.setTelefono(txtTelefono.getText().trim());
        clienteSeleccionado.setDireccion(txtDireccion.getText().trim());

        if (clienteSeleccionado.getUserAccount() != null && !txtUsuario.getText().trim().isEmpty()) {
            clienteSeleccionado.getUserAccount().setUser(txtUsuario.getText().trim());
        }

        mostrarAlerta(Alert.AlertType.INFORMATION,
                "Cliente Actualizado",
                "Los datos del cliente han sido actualizados exitosamente");

        tablaClientes.refresh();
        cargarClientes();
        limpiarFormulario();

        System.out.println("✅ Cliente actualizado: " + clienteSeleccionado.getNombre());
    }

    @FXML
    void onEliminar(ActionEvent event) {
        Client clienteAEliminar = tablaClientes.getSelectionModel().getSelectedItem();

        if (clienteAEliminar == null) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Selección Requerida",
                    "Debe seleccionar un cliente de la tabla para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar este cliente?");
        confirmacion.setContentText("Cliente: " + clienteAEliminar.getNombre());

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            sameDay.getListPersons().remove(clienteAEliminar);

            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Cliente Eliminado",
                    "El cliente ha sido eliminado exitosamente");

            cargarClientes();
            limpiarFormulario();
        }
    }

    @FXML
    void onLimpiar(ActionEvent event) {
        limpiarFormulario();
    }

    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            errores.append("• El nombre es obligatorio\n");
        } else if (txtNombre.getText().trim().length() < 3) {
            errores.append("• El nombre debe tener al menos 3 caracteres\n");
        }

        if (txtCedula.getText() == null || txtCedula.getText().trim().isEmpty()) {
            errores.append("• La cédula es obligatoria\n");
        } else if (!txtCedula.getText().matches("\\d+")) {
            errores.append("• La cédula debe contener solo números\n");
        }

        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty()) {
            errores.append("• El email es obligatorio\n");
        } else if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errores.append("• El email no tiene un formato válido\n");
        }

        if (txtTelefono.getText() == null || txtTelefono.getText().trim().isEmpty()) {
            errores.append("• El teléfono es obligatorio\n");
        } else if (!txtTelefono.getText().matches("\\d{10}")) {
            errores.append("• El teléfono debe tener 10 dígitos\n");
        }

        if (txtDireccion.getText() == null || txtDireccion.getText().trim().isEmpty()) {
            errores.append("• La dirección es obligatoria\n");
        }

        if (txtUsuario.getText() == null || txtUsuario.getText().trim().isEmpty()) {
            errores.append("• El usuario es obligatorio\n");
        } else if (txtUsuario.getText().trim().length() < 4) {
            errores.append("• El usuario debe tener al menos 4 caracteres\n");
        }

        if (errores.length() > 0) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Campos Incompletos",
                    "Por favor corrija los siguientes errores:\n\n" + errores.toString());
            return false;
        }
        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void limpiarFormulario() {
        txtId.clear();
        txtNombre.clear();
        txtCedula.clear();
        txtEmail.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtUsuario.clear();
        tablaClientes.getSelectionModel().clearSelection();
        clienteSeleccionado = null;
        btnActualizar.setDisable(true);
        btnAgregar.setDisable(false);
    }

    private String generarIdCliente() {
        int totalClientes = (int) sameDay.getListPersons().stream()
                .filter(person -> person instanceof Client)
                .count();
        return String.format("%04d", totalClientes + 1);
    }
}