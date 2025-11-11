package co.edu.uniquindio.sameday.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import co.edu.uniquindio.sameday.models.Client;
import co.edu.uniquindio.sameday.models.Person;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;


import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class GestionCrudClienteController {

    private ObservableList<Client> listaClientes;
    private List<Person> todasLasPersonas = new ArrayList<>();
    private SameDay sameDay;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

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
    void onActualizar(ActionEvent event) {

    }

    @FXML
    void onAgregar(ActionEvent event) {
    }

    @FXML
    void onEliminar(ActionEvent event) {

    }

    @FXML
    void onLimpiar(ActionEvent event) {
        limpiarFormulario();

    }

    @FXML
    void initialize() {
        sameDay = SameDay.getInstance();
        configurarTabla();
        cargarClientes();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("documento"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
    }

    private void cargarClientes() {
        todasLasPersonas = obtenerTodasLasPersonas();
        List<Client> soloClientes = todasLasPersonas.stream()
                .filter(persona -> persona instanceof Client)
                .map(persona -> (Client) persona)
                .collect(Collectors.toList());

        // Cargar en la tabla
        listaClientes = FXCollections.observableArrayList(soloClientes);
        tablaClientes.setItems(listaClientes);

        // Actualizar estadísticas
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        lblTotalClientes.setText("📊 Total: " + listaClientes.size());
        lblClientesActivos.setText("✅ Activos: " + listaClientes.size());
    }

    private List<Person> obtenerTodasLasPersonas() {
        return SameDay.getInstance().getListPersons();
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
        tablaClientes.getSelectionModel().clearSelection();
    }

}
