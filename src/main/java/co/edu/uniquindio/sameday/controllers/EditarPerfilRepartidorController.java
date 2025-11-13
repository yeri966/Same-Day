package co.edu.uniquindio.sameday.controllers;

import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.Person;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controlador para la vista de edición de perfil del repartidor
 * Permite actualizar información personal y contraseña
 */
public class EditarPerfilRepartidorController {

    private SameDay sameDay = SameDay.getInstance();
    private Dealer repartidorActual;

    @FXML private TextField txtNombre;
    @FXML private TextField txtCedula;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private PasswordField txtPasswordActual;
    @FXML private PasswordField txtPasswordNueva;
    @FXML private PasswordField txtPasswordConfirmar;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    @FXML
    void initialize() {
        Person usuarioActivo = sameDay.getUserActive();
        if (usuarioActivo instanceof Dealer) {
            repartidorActual = (Dealer) usuarioActivo;
            cargarDatosRepartidor();
        }
    }

    /**
     * Carga los datos actuales del repartidor en el formulario
     */
    private void cargarDatosRepartidor() {
        txtNombre.setText(repartidorActual.getNombre());
        txtCedula.setText(repartidorActual.getDocumento());
        txtCorreo.setText(repartidorActual.getCorreo());
        txtTelefono.setText(repartidorActual.getTelefono());
    }

    /**
     * Guarda los cambios realizados en el perfil
     */
    @FXML
    void onGuardar(ActionEvent event) {
        if (!validarFormulario()) {
            return;
        }

        // Actualizar información básica
        repartidorActual.setNombre(txtNombre.getText().trim());
        repartidorActual.setCorreo(txtCorreo.getText().trim());
        repartidorActual.setTelefono(txtTelefono.getText().trim());

        // Cambiar contraseña si se proporcionó
        if (!txtPasswordActual.getText().isEmpty()) {
            if (!cambiarPassword()) {
                return;
            }
        }

        mostrarMensaje(
                "Perfil Actualizado",
                "Tu perfil ha sido actualizado exitosamente.",
                Alert.AlertType.INFORMATION
        );

        limpiarCamposPassword();
    }

    /**
     * Cancela la edición y limpia los cambios
     */
    @FXML
    void onCancelar(ActionEvent event) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cancelación");
        confirmacion.setHeaderText("¿Deseas cancelar los cambios?");
        confirmacion.setContentText("Los cambios no guardados se perderán.");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            cargarDatosRepartidor();
            limpiarCamposPassword();
        }
    }

    /**
     * Valida todos los campos del formulario
     */
    private boolean validarFormulario() {
        // Validar nombre
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje(
                    "Campo Requerido",
                    "El nombre es obligatorio.",
                    Alert.AlertType.WARNING
            );
            txtNombre.requestFocus();
            return false;
        }

        // Validar correo
        if (txtCorreo.getText().trim().isEmpty()) {
            mostrarMensaje(
                    "Campo Requerido",
                    "El correo electrónico es obligatorio.",
                    Alert.AlertType.WARNING
            );
            txtCorreo.requestFocus();
            return false;
        }

        if (!validarEmail(txtCorreo.getText().trim())) {
            mostrarMensaje(
                    "Correo Inválido",
                    "Por favor ingresa un correo electrónico válido.",
                    Alert.AlertType.WARNING
            );
            txtCorreo.requestFocus();
            return false;
        }

        // Validar teléfono
        if (txtTelefono.getText().trim().isEmpty()) {
            mostrarMensaje(
                    "Campo Requerido",
                    "El teléfono es obligatorio.",
                    Alert.AlertType.WARNING
            );
            txtTelefono.requestFocus();
            return false;
        }

        if (!validarTelefono(txtTelefono.getText().trim())) {
            mostrarMensaje(
                    "Teléfono Inválido",
                    "El teléfono debe contener solo números y tener entre 7 y 10 dígitos.",
                    Alert.AlertType.WARNING
            );
            txtTelefono.requestFocus();
            return false;
        }

        // Validar campos de contraseña si se está intentando cambiar
        if (!txtPasswordActual.getText().isEmpty() ||
                !txtPasswordNueva.getText().isEmpty() ||
                !txtPasswordConfirmar.getText().isEmpty()) {

            if (txtPasswordActual.getText().isEmpty()) {
                mostrarMensaje(
                        "Campo Requerido",
                        "Debes ingresar tu contraseña actual para cambiarla.",
                        Alert.AlertType.WARNING
                );
                txtPasswordActual.requestFocus();
                return false;
            }

            if (txtPasswordNueva.getText().isEmpty()) {
                mostrarMensaje(
                        "Campo Requerido",
                        "Debes ingresar la nueva contraseña.",
                        Alert.AlertType.WARNING
                );
                txtPasswordNueva.requestFocus();
                return false;
            }

            if (txtPasswordNueva.getText().length() < 4) {
                mostrarMensaje(
                        "Contraseña Débil",
                        "La contraseña debe tener al menos 6 caracteres.",
                        Alert.AlertType.WARNING
                );
                txtPasswordNueva.requestFocus();
                return false;
            }

            if (!txtPasswordNueva.getText().equals(txtPasswordConfirmar.getText())) {
                mostrarMensaje(
                        "Contraseñas No Coinciden",
                        "La nueva contraseña y su confirmación no coinciden.",
                        Alert.AlertType.WARNING
                );
                txtPasswordConfirmar.requestFocus();
                return false;
            }
        }

        return true;
    }

    /**
     * Cambia la contraseña del usuario
     */
    private boolean cambiarPassword() {
        // Verificar que la contraseña actual sea correcta
        if (!repartidorActual.getUserAccount().getContrasenia().equals(txtPasswordActual.getText())) {
            mostrarMensaje(
                    "Contraseña Incorrecta",
                    "La contraseña actual no es correcta.",
                    Alert.AlertType.ERROR
            );
            txtPasswordActual.requestFocus();
            return false;
        }

        // Actualizar la contraseña
        repartidorActual.getUserAccount().setContrasenia(txtPasswordNueva.getText());
        return true;
    }

    /**
     * Valida el formato del correo electrónico
     */
    private boolean validarEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Valida el formato del teléfono
     */
    private boolean validarTelefono(String telefono) {
        return telefono.matches("\\d{7,10}");
    }

    /**
     * Limpia los campos de contraseña
     */
    private void limpiarCamposPassword() {
        txtPasswordActual.clear();
        txtPasswordNueva.clear();
        txtPasswordConfirmar.clear();
    }

    /**
     * Muestra un mensaje al usuario
     */
    private void mostrarMensaje(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}