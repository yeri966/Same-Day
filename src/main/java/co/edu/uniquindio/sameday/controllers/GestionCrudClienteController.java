package co.edu.uniquindio.sameday.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import co.edu.uniquindio.sameday.models.Client;
import co.edu.uniquindio.sameday.models.Person;
import co.edu.uniquindio.sameday.models.TypeUser;
import co.edu.uniquindio.sameday.models.UserAccount;
import co.edu.uniquindio.sameday.models.creational.factoryMethod.ClienteFactory;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
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

        // Inicializar la lista observable vacía
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

        // Cargar el usuario si existe
        if (cliente.getUserAccount() != null) {
            txtUsuario.setText(cliente.getUserAccount().getUser());
        }
    }

    private void cargarClientes() {
        System.out.println("🔄 Recargando clientes...");

        // Obtener DIRECTAMENTE la referencia a la lista de personas del Singleton
        // NO hacer copias, trabajar con la lista original
        List<Client> soloClientes = sameDay.getListPersons().stream()
                .filter(persona -> persona instanceof Client)
                .map(persona -> (Client) persona)
                .collect(Collectors.toList());

        // Limpiar y recargar la lista observable
        listaClientes.clear();
        listaClientes.addAll(soloClientes);

        // Establecer los items en la tabla
        tablaClientes.setItems(listaClientes);

        // Forzar el refresco visual de la tabla
        tablaClientes.refresh();

        // Actualizar estadísticas
        actualizarEstadisticas();

        System.out.println("✅ " + soloClientes.size() + " clientes cargados");
    }

    private void actualizarEstadisticas() {
        lblTotalClientes.setText("📊 Total: " + listaClientes.size());
        lblClientesActivos.setText("✅ Activos: " + listaClientes.size());
    }

    @FXML
    void onAgregar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        // Generar ID único
        String nuevoId = generarIdCliente();

        // Obtener el nombre de usuario del campo o generar uno por defecto
        String nombreUsuario = txtUsuario.getText().trim();
        if (nombreUsuario.isEmpty()) {
            nombreUsuario = "cliente" + nuevoId;
        }

        // Crear UserAccount para el nuevo cliente
        UserAccount userAccount = new UserAccount(
                nombreUsuario,
                "1234", // Contraseña por defecto
                null,
                TypeUser.CLIENT
        );

        // Crear el nuevo cliente
        ClienteFactory factory=new ClienteFactory(txtDireccion.getText().trim());
        Client nuevoCliente = (Client) factory.crearPerson(nuevoId,
                txtCedula.getText().trim(),
                txtNombre.getText().trim(),
                txtEmail.getText().trim(),
                txtTelefono.getText().trim(),
                userAccount
        );

        // Agregar al sistema
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

        // Actualizar datos del cliente seleccionado
        clienteSeleccionado.setNombre(txtNombre.getText().trim());
        clienteSeleccionado.setDocumento(txtCedula.getText().trim());
        clienteSeleccionado.setCorreo(txtEmail.getText().trim());
        clienteSeleccionado.setTelefono(txtTelefono.getText().trim());
        clienteSeleccionado.setDireccion(txtDireccion.getText().trim());

        // Actualizar el nombre de usuario si existe el UserAccount
        if (clienteSeleccionado.getUserAccount() != null && !txtUsuario.getText().trim().isEmpty()) {
            clienteSeleccionado.getUserAccount().setUser(txtUsuario.getText().trim());
        }

        mostrarAlerta(Alert.AlertType.INFORMATION,
                "Cliente Actualizado",
                "Los datos del cliente han sido actualizados exitosamente");

        // Refrescar la tabla forzando una recarga completa
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

        // Confirmación
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

        // Validar nombre
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            errores.append("• El nombre es obligatorio\n");
        } else if (txtNombre.getText().trim().length() < 3) {
            errores.append("• El nombre debe tener al menos 3 caracteres\n");
        }

        // Validar cédula
        if (txtCedula.getText() == null || txtCedula.getText().trim().isEmpty()) {
            errores.append("• La cédula es obligatoria\n");
        } else if (!txtCedula.getText().matches("\\d+")) {
            errores.append("• La cédula debe contener solo números\n");
        }

        // Validar email
        if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty()) {
            errores.append("• El email es obligatorio\n");
        } else if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errores.append("• El email no tiene un formato válido\n");
        }

        // Validar teléfono
        if (txtTelefono.getText() == null || txtTelefono.getText().trim().isEmpty()) {
            errores.append("• El teléfono es obligatorio\n");
        } else if (!txtTelefono.getText().matches("\\d{10}")) {
            errores.append("• El teléfono debe tener 10 dígitos\n");
        }

        // Validar dirección
        if (txtDireccion.getText() == null || txtDireccion.getText().trim().isEmpty()) {
            errores.append("• La dirección es obligatoria\n");
        }

        // Validar usuario
        if (txtUsuario.getText() == null || txtUsuario.getText().trim().isEmpty()) {
            errores.append("• El usuario es obligatorio\n");
        } else if (txtUsuario.getText().trim().length() < 4) {
            errores.append("• El usuario debe tener al menos 4 caracteres\n");
        }

        // Si hay errores, mostrar alerta
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