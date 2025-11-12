package co.edu.uniquindio.sameday.controllers;

import co.edu.uniquindio.sameday.models.Client;
import co.edu.uniquindio.sameday.models.Person;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.regex.Pattern;

public class EditarPerfilController {

    // Referencia al modelo principal (Singleton)
    private SameDay sameDay = SameDay.getInstance();

    // Usuario actual que está editando su perfil
    private Client clienteActual;

    @FXML
    private TextField txtId;

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
    private PasswordField txtConfirmarContrasenia;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelar;


    @FXML
    void initialize() {
        System.out.println("=== INICIALIZANDO CONTROLADOR EDITAR PERFIL ===");

        // Cargar datos del usuario actualmente logueado
        cargarDatosUsuarioActivo();
    }

    /**
     * Carga los datos del usuario actualmente logueado en el sistema
     */
    private void cargarDatosUsuarioActivo() {
        // Obtener el usuario activo del sistema
        Person usuarioActivo = sameDay.getUserActive();

        if (usuarioActivo instanceof Client) {
            clienteActual = (Client) usuarioActivo;
            System.out.println("Cargando datos del cliente logueado: " + clienteActual.getNombre());

            // Cargar datos en los campos
            txtId.setText(clienteActual.getId());
            txtNombre.setText(clienteActual.getNombre());
            txtCorreo.setText(clienteActual.getCorreo());
            txtTelefono.setText(clienteActual.getTelefono());
            txtDireccion.setText(clienteActual.getDireccion());

            if (clienteActual.getUserAccount() != null) {
                txtUsuario.setText(clienteActual.getUserAccount().getUser());
            }

            // Los campos de contraseña se dejan vacíos por seguridad
            txtContrasenia.clear();
            txtConfirmarContrasenia.clear();
        } else {
            showAlert("Error",
                    "No hay un cliente activo en el sistema. Por favor inicie sesión nuevamente.",
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Método público para establecer el cliente que editará su perfil
     * Este método debe ser llamado desde el controlador que abre esta vista
     * @param cliente El cliente que editará su perfil
     */
    public void setCliente(Client cliente) {
        this.clienteActual = cliente;
        cargarDatosClienteEspecifico();
    }

    /**
     * Carga los datos de un cliente específico
     */
    private void cargarDatosClienteEspecifico() {
        if (clienteActual != null) {
            txtId.setText(clienteActual.getId());
            txtNombre.setText(clienteActual.getNombre());
            txtCorreo.setText(clienteActual.getCorreo());
            txtTelefono.setText(clienteActual.getTelefono());
            txtDireccion.setText(clienteActual.getDireccion());

            if (clienteActual.getUserAccount() != null) {
                txtUsuario.setText(clienteActual.getUserAccount().getUser());
            }
        }
    }


    /**
     * Guarda los cambios realizados en el perfil del usuario
     */
    @FXML
    void onGuardar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        // Obtener los valores de los campos
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String contrasenia = txtContrasenia.getText();
        String confirmarContrasenia = txtConfirmarContrasenia.getText();

        // Validar formato del correo
        if (!validarCorreo(correo)) {
            showAlert("Correo Inválido",
                    "Por favor ingrese un correo electrónico válido",
                    Alert.AlertType.WARNING);
            return;
        }

        // Validar que el usuario no esté en uso por otro usuario
        if (usuarioExisteParaOtro(usuario)) {
            showAlert("Usuario Existente",
                    "El nombre de usuario ya está en uso por otro cliente",
                    Alert.AlertType.WARNING);
            return;
        }

        // Validar contraseña si se está cambiando
        if (!contrasenia.isEmpty() || !confirmarContrasenia.isEmpty()) {
            if (!contrasenia.equals(confirmarContrasenia)) {
                showAlert("Contraseñas no coinciden",
                        "La nueva contraseña y la confirmación no coinciden",
                        Alert.AlertType.WARNING);
                return;
            }

            if (contrasenia.length() < 4) {
                showAlert("Contraseña Débil",
                        "La contraseña debe tener al menos 4 caracteres",
                        Alert.AlertType.WARNING);
                return;
            }
        }

        // Actualizar los datos del cliente
        clienteActual.setNombre(nombre);
        clienteActual.setCorreo(correo);
        clienteActual.setTelefono(telefono);
        clienteActual.setDireccion(direccion);

        // Actualizar datos del usuario
        if (clienteActual.getUserAccount() != null) {
            clienteActual.getUserAccount().setUser(usuario);

            // Solo actualizar contraseña si se ingresó una nueva
            if (!contrasenia.isEmpty()) {
                clienteActual.getUserAccount().setContrasenia(contrasenia);
            }
        }

        // Mostrar mensaje de éxito
        showAlert("Éxito",
                "¡Perfil actualizado correctamente!",
                Alert.AlertType.INFORMATION);

        System.out.println("Perfil actualizado para: " + clienteActual.getNombre());

        // Limpiar campos de contraseña por seguridad
        txtContrasenia.clear();
        txtConfirmarContrasenia.clear();
    }

    /**
     * Cancela la edición y restaura los valores originales
     */
    @FXML
    void onCancelar(ActionEvent event) {
        // Confirmar si realmente desea cancelar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cancelación");
        confirmacion.setHeaderText("¿Está seguro que desea cancelar?");
        confirmacion.setContentText("Los cambios realizados no se guardarán");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            // Recargar los datos originales
            cargarDatosUsuarioActivo();

            showAlert("Cancelado",
                    "Los cambios han sido descartados",
                    Alert.AlertType.INFORMATION);
        }
    }

    /**
     * Valida que todos los campos obligatorios estén llenos
     */
    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            showAlert("Campos Incompletos",
                    "Debe ingresar su nombre completo",
                    Alert.AlertType.WARNING);
            txtNombre.requestFocus();
            return false;
        }

        if (txtCorreo.getText().trim().isEmpty()) {
            showAlert("Campos Incompletos",
                    "Debe ingresar su correo electrónico",
                    Alert.AlertType.WARNING);
            txtCorreo.requestFocus();
            return false;
        }

        if (txtTelefono.getText().trim().isEmpty()) {
            showAlert("Campos Incompletos",
                    "Debe ingresar su número de teléfono",
                    Alert.AlertType.WARNING);
            txtTelefono.requestFocus();
            return false;
        }

        if (txtDireccion.getText().trim().isEmpty()) {
            showAlert("Campos Incompletos",
                    "Debe ingresar su dirección",
                    Alert.AlertType.WARNING);
            txtDireccion.requestFocus();
            return false;
        }

        if (txtUsuario.getText().trim().isEmpty()) {
            showAlert("Campos Incompletos",
                    "Debe ingresar su nombre de usuario",
                    Alert.AlertType.WARNING);
            txtUsuario.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Valida el formato del correo electrónico usando expresión regular
     */
    private boolean validarCorreo(String correo) {
        String regexCorreo = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(regexCorreo);
        return pattern.matcher(correo).matches();
    }

    /**
     * Verifica si un nombre de usuario ya existe para otro cliente
     */
    private boolean usuarioExisteParaOtro(String usuario) {
        return sameDay.getListPersons().stream()
                .filter(person -> person instanceof Client)
                .filter(person -> !person.getId().equals(clienteActual.getId())) // Excluir al cliente actual
                .anyMatch(person -> person.getUserAccount().getUser().equalsIgnoreCase(usuario));
    }

    /**
     * Muestra un cuadro de diálogo de alerta
     */
    private void showAlert(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}