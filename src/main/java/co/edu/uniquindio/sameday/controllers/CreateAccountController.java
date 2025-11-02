package co.edu.uniquindio.sameday.controllers;

import co.edu.uniquindio.sameday.models.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class CreateAccountController {

    // PATRON DE DISEÑO CREACIONAL: SINGLETON
    SameDay sameDay = SameDay.getInstance();

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtDireccion;

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasenia;

    @FXML
    private Button btnRegistrar;

    @FXML
    private Button btnCancelar;

    /**
     * Método que se ejecuta al hacer clic en el botón REGISTRAR
     * Valida los campos y crea un nuevo usuario en el sistema
     */
    @FXML
    void onRegistrar(ActionEvent event) {
        // Obtener los valores de los campos
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String contrasenia = txtContrasenia.getText();

        // Validar que todos los campos estén llenos
        if (nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty() ||
                direccion.isEmpty() || usuario.isEmpty() || contrasenia.isEmpty()) {
            mostrarAlerta("Campos Incompletos",
                    "Por favor complete todos los campos obligatorios",
                    Alert.AlertType.WARNING);
            return;
        }

        // Validar formato del correo electrónico
        if (!validarCorreo(correo)) {
            mostrarAlerta("Correo Inválido",
                    "Por favor ingrese un correo electrónico válido",
                    Alert.AlertType.WARNING);
            return;
        }

        // Validar longitud de la contraseña
        if (contrasenia.length() < 4) {
            mostrarAlerta("Contraseña Débil",
                    "La contraseña debe tener al menos 4 caracteres",
                    Alert.AlertType.WARNING);
            return;
        }

        // Validar que el nombre de usuario no exista
        if (usuarioExiste(usuario)) {
            mostrarAlerta("Usuario Existente",
                    "El nombre de usuario ya está en uso. Por favor elija otro",
                    Alert.AlertType.WARNING);
            return;
        }

        // Generar ID único para el nuevo cliente
        String idCliente = generarIdCliente();

        // Crear el objeto User
        User newUser = new User(usuario, contrasenia, null, TipoUsuario.CLIENTE);

        // Crear el objeto Client (por defecto todos los registros son clientes)
        Client newClient = new Client(idCliente, nombre, correo, telefono, direccion, null);

        // Establecer las relaciones bidireccionales
        newClient.setUser(newUser);
        newUser.setPerson(newClient);

        // Agregar el nuevo cliente al sistema
        sameDay.agregarPersona(newClient);

        // Mostrar mensaje de éxito
        mostrarAlerta("Registro Exitoso",
                "¡Cuenta creada exitosamente! Ya puede iniciar sesión",
                Alert.AlertType.INFORMATION);

        // Limpiar los campos
        limpiarCampos();

        // Cerrar la ventana de registro y volver al login
        cerrarVentana();
    }

    /**
     * Método que se ejecuta al hacer clic en el botón CANCELAR
     * Cierra la ventana sin guardar cambios
     */
    @FXML
    void onCancelar(ActionEvent event) {
        // Confirmar si realmente desea cancelar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cancelación");
        confirmacion.setHeaderText("¿Está seguro que desea cancelar?");
        confirmacion.setContentText("Los datos ingresados se perderán");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            cerrarVentana();
        }
    }

    /**
     * Valida el formato del correo electrónico usando expresión regular
     * @param correo El correo a validar
     * @return true si el formato es válido, false en caso contrario
     */
    private boolean validarCorreo(String correo) {
        String regexCorreo = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(regexCorreo);
        return pattern.matcher(correo).matches();
    }

    /**
     * Verifica si un nombre de usuario ya existe en el sistema
     * @param usuario El nombre de usuario a verificar
     * @return true si el usuario ya existe, false en caso contrario
     */
    private boolean usuarioExiste(String usuario) {
        return sameDay.getListPersons().stream()
                .anyMatch(person -> person.getUser().getUsuario().equalsIgnoreCase(usuario));
    }

    /**
     * Genera un ID único para el nuevo cliente
     * @return String con el ID generado
     */
    private String generarIdCliente() {
        // Obtener el número de clientes actuales y sumarle 1
        int numeroClientes = (int) sameDay.getListPersons().stream()
                .filter(person -> person instanceof Client)
                .count();

        // Generar ID con formato 0001, 0002....
        return String.format("%04d", numeroClientes + 1);
    }

    /**
     * Limpia todos los campos del formulario
     */
    private void limpiarCampos() {
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtUsuario.clear();
        txtContrasenia.clear();
    }

    /**
     * Cierra la ventana actual
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * Muestra un cuadro de diálogo de alerta
     * @param titulo El título de la alerta
     * @param mensaje El mensaje a mostrar
     * @param tipo El tipo de alerta
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    void initialize() {
        // Se puede agregar lógica de inicialización si es necesario
        // Por ejemplo, establecer foco en el primer campo
        if (txtNombre != null) {
            txtNombre.requestFocus();
        }
    }
}