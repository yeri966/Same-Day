package co.edu.uniquindio.sameday.controllers;

import co.edu.uniquindio.sameday.models.*;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para el Dashboard del Repartidor
 * Permite ver y actualizar el estado de los envíos asignados
 */
public class GestionEnviosRepartidorController {

    private SameDay sameDay = SameDay.getInstance();
    private Dealer repartidorActual;
    private Envio envioSeleccionado;

    // Tabla de envíos
    @FXML private TableView<Envio> tablaEnvios;
    @FXML private TableColumn<Envio, String> colId;
    @FXML private TableColumn<Envio, String> colFecha;
    @FXML private TableColumn<Envio, String> colOrigen;
    @FXML private TableColumn<Envio, String> colDestino;
    @FXML private TableColumn<Envio, String> colDestinatario;
    @FXML private TableColumn<Envio, String> colContenido;
    @FXML private TableColumn<Envio, String> colEstado;

    // Detalles del envío seleccionado
    @FXML private Label lblNumeroEnvio;
    @FXML private Label lblOrigenDetalle;
    @FXML private Label lblDestinoDetalle;
    @FXML private Label lblDestinatarioNombre;
    @FXML private Label lblDestinatarioCedula;
    @FXML private Label lblDestinatarioTelefono;
    @FXML private Label lblContenido;
    @FXML private Label lblPeso;
    @FXML private Label lblServicios;

    // Gestión de estado
    @FXML private ComboBox<EstadoEntrega> cmbNuevoEstado;
    @FXML private TextArea txtObservaciones;
    @FXML private Button btnActualizarEstado;

    // Estadísticas
    @FXML private Label lblTotalAsignados;
    @FXML private Label lblEnRuta;
    @FXML private Label lblEntregados;
    @FXML private Label lblPendientes;

    // Filtros
    @FXML private ComboBox<EstadoEntrega> cmbFiltroEstado;
    @FXML private Button btnFiltrar;
    @FXML private Button btnLimpiarFiltro;

    @FXML
    void initialize() {
        System.out.println("=== INICIALIZANDO DASHBOARD REPARTIDOR ===");

        // Obtener el repartidor actual
        Person usuarioActivo = sameDay.getUserActive();
        if (usuarioActivo instanceof Dealer) {
            repartidorActual = (Dealer) usuarioActivo;
            System.out.println("Repartidor activo: " + repartidorActual.getNombre());
        } else {
            System.out.println("ERROR: Usuario activo no es un repartidor");
            return;
        }

        configurarTabla();
        configurarComboBoxEstados();
        configurarSeleccionTabla();
        cargarEnviosAsignados();
        actualizarEstadisticas();

        btnActualizarEstado.setDisable(true);

        System.out.println("=== DASHBOARD REPARTIDOR INICIALIZADO ===");
    }

    /**
     * Configura las columnas de la tabla de envíos
     */
    private void configurarTabla() {
        colId.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getId()));

        colFecha.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFechaCreacion() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                return new SimpleStringProperty(
                        cellData.getValue().getFechaCreacion().format(formatter)
                );
            }
            return new SimpleStringProperty("-");
        });

        colOrigen.setCellValueFactory(cellData -> {
            Address origen = cellData.getValue().getOrigen();
            return new SimpleStringProperty(
                    origen != null ? origen.getCity().name() : "-"  // CORREGIDO: usar name()
            );
        });

        colDestino.setCellValueFactory(cellData -> {
            Address destino = cellData.getValue().getDestino();
            return new SimpleStringProperty(
                    destino != null ? destino.getCity().name() : "-"  // CORREGIDO: usar name()
            );
        });

        colDestinatario.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNombreDestinatario()));

        colContenido.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getContenido()));

        colEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstadoEntregaString()));

        // Aplicar estilo condicional a la columna de estado
        colEstado.setCellFactory(column -> new TableCell<Envio, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    // Aplicar colores según el estado
                    if (item.contains("Asignado")) {
                        setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                    } else if (item.contains("Recogido")) {
                        setStyle("-fx-text-fill: #8b5cf6; -fx-font-weight: bold;");
                    } else if (item.contains("En Ruta")) {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                    } else if (item.contains("Entregado")) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    } else if (item.contains("Incidencia")) {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    /**
     * Configura los ComboBox de estados
     */
    private void configurarComboBoxEstados() {
        // ComboBox para cambiar el estado
        ObservableList<EstadoEntrega> estados = FXCollections.observableArrayList(
                EstadoEntrega.values()
        );
        cmbNuevoEstado.setItems(estados);

        // ComboBox de filtro (con opción "Todos")
        ObservableList<EstadoEntrega> estadosFiltro = FXCollections.observableArrayList(
                EstadoEntrega.values()
        );
        cmbFiltroEstado.setItems(estadosFiltro);
    }

    /**
     * Configura el listener para la selección de la tabla
     */
    private void configurarSeleccionTabla() {
        tablaEnvios.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        envioSeleccionado = newValue;
                        mostrarDetalleEnvio(newValue);
                        btnActualizarEstado.setDisable(
                                newValue.getEstadoEntrega() == EstadoEntrega.ENTREGADO
                        );
                    }
                }
        );
    }

    /**
     * Carga los envíos asignados al repartidor actual
     */
    private void cargarEnviosAsignados() {
        System.out.println("\n=== CARGANDO ENVÍOS ASIGNADOS ===");

        List<Envio> enviosAsignados = sameDay.getListEnvios().stream()
                .filter(envio -> envio.getRepartidorAsignado() != null)
                .filter(envio -> envio.getRepartidorAsignado().getId().equals(repartidorActual.getId()))
                .collect(Collectors.toList());

        ObservableList<Envio> enviosObservable = FXCollections.observableArrayList(enviosAsignados);
        tablaEnvios.setItems(enviosObservable);

        System.out.println("Total envíos asignados: " + enviosAsignados.size());
        System.out.println("=== ENVÍOS CARGADOS ===\n");
    }

    /**
     * Muestra el detalle del envío seleccionado en los labels
     */
    private void mostrarDetalleEnvio(Envio envio) {
        lblNumeroEnvio.setText(envio.getId());

        if (envio.getOrigen() != null) {
            lblOrigenDetalle.setText(envio.getOrigen().getFullAddress());
        }

        if (envio.getDestino() != null) {
            lblDestinoDetalle.setText(envio.getDestino().getFullAddress());
        }

        lblDestinatarioNombre.setText(envio.getNombreDestinatario());
        lblDestinatarioCedula.setText(envio.getCedulaDestinatario());
        lblDestinatarioTelefono.setText(envio.getTelefonoDestinatario());
        lblContenido.setText(envio.getContenido());
        lblPeso.setText(String.format("%.2f kg", envio.getPeso()));
        lblServicios.setText(envio.getServiciosAdicionalesString());

        // Establecer el estado actual en el ComboBox
        cmbNuevoEstado.setValue(envio.getEstadoEntrega());

        // Mostrar observaciones existentes
        txtObservaciones.setText(
                envio.getObservaciones() != null ? envio.getObservaciones() : ""
        );
    }

    /**
     * Actualiza el estado del envío seleccionado
     */
    @FXML
    void onActualizarEstado(ActionEvent event) {
        if (envioSeleccionado == null) {
            mostrarAlerta("Selección requerida",
                    "Debe seleccionar un envío de la tabla",
                    Alert.AlertType.WARNING);
            return;
        }

        EstadoEntrega nuevoEstado = cmbNuevoEstado.getValue();

        if (nuevoEstado == null) {
            mostrarAlerta("Estado requerido",
                    "Debe seleccionar un nuevo estado",
                    Alert.AlertType.WARNING);
            return;
        }

        // Validar transición de estado
        if (!validarTransicionEstado(envioSeleccionado.getEstadoEntrega(), nuevoEstado)) {
            mostrarAlerta("Transición inválida",
                    "No puede cambiar de " + envioSeleccionado.getEstadoEntregaString() +
                            " a " + nuevoEstado.toString(),
                    Alert.AlertType.WARNING);
            return;
        }

        // Si el estado es CON_INCIDENCIA, las observaciones son obligatorias
        if (nuevoEstado == EstadoEntrega.CON_INCIDENCIA) {
            if (txtObservaciones.getText().trim().isEmpty()) {
                mostrarAlerta("Observaciones requeridas",
                        "Debe especificar el motivo de la incidencia",
                        Alert.AlertType.WARNING);
                return;
            }
        }

        // Confirmar actualización
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Actualización");
        confirmacion.setHeaderText("¿Actualizar el estado del envío?");
        confirmacion.setContentText(
                "Envío: " + envioSeleccionado.getId() + "\n" +
                        "Estado actual: " + envioSeleccionado.getEstadoEntregaString() + "\n" +
                        "Nuevo estado: " + nuevoEstado.toString()
        );

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            // Actualizar el envío
            envioSeleccionado.setEstadoEntrega(nuevoEstado);
            envioSeleccionado.setObservaciones(txtObservaciones.getText().trim());

            // Si se entrega el envío, marcar al repartidor como disponible nuevamente
            if (nuevoEstado == EstadoEntrega.ENTREGADO) {
                repartidorActual.setDisponible(true);
            }

            // Guardar cambios en el sistema
            sameDay.updateEnvio(envioSeleccionado);

            mostrarAlerta("Estado Actualizado",
                    "El estado del envío ha sido actualizado exitosamente",
                    Alert.AlertType.INFORMATION);

            // Recargar la tabla y estadísticas
            cargarEnviosAsignados();
            actualizarEstadisticas();
            limpiarDetalles();
        }
    }

    /**
     * Valida si una transición de estado es válida
     */
    private boolean validarTransicionEstado(EstadoEntrega estadoActual, EstadoEntrega nuevoEstado) {
        if (estadoActual == null) {
            return nuevoEstado == EstadoEntrega.ASIGNADO;
        }

        switch (estadoActual) {
            case ASIGNADO:
                return nuevoEstado == EstadoEntrega.RECOGIDO ||
                        nuevoEstado == EstadoEntrega.CON_INCIDENCIA;

            case RECOGIDO:
                return nuevoEstado == EstadoEntrega.EN_RUTA ||
                        nuevoEstado == EstadoEntrega.CON_INCIDENCIA;

            case EN_RUTA:
                return nuevoEstado == EstadoEntrega.ENTREGADO ||
                        nuevoEstado == EstadoEntrega.CON_INCIDENCIA;

            case ENTREGADO:
                return false; // No se puede cambiar de ENTREGADO

            case CON_INCIDENCIA:
                return nuevoEstado == EstadoEntrega.RECOGIDO ||
                        nuevoEstado == EstadoEntrega.EN_RUTA;

            default:
                return false;
        }
    }

    /**
     * Aplica el filtro por estado seleccionado
     */
    @FXML
    void onFiltrar(ActionEvent event) {
        EstadoEntrega estadoFiltro = cmbFiltroEstado.getValue();

        if (estadoFiltro == null) {
            cargarEnviosAsignados();
            return;
        }

        List<Envio> enviosFiltrados = sameDay.getListEnvios().stream()
                .filter(envio -> envio.getRepartidorAsignado() != null)
                .filter(envio -> envio.getRepartidorAsignado().getId().equals(repartidorActual.getId()))
                .filter(envio -> envio.getEstadoEntrega() == estadoFiltro)
                .collect(Collectors.toList());

        ObservableList<Envio> enviosObservable = FXCollections.observableArrayList(enviosFiltrados);
        tablaEnvios.setItems(enviosObservable);
    }

    /**
     * Limpia el filtro y muestra todos los envíos
     */
    @FXML
    void onLimpiarFiltro(ActionEvent event) {
        cmbFiltroEstado.setValue(null);
        cargarEnviosAsignados();
    }

    /**
     * Actualiza las estadísticas mostradas
     */
    private void actualizarEstadisticas() {
        List<Envio> misEnvios = sameDay.getListEnvios().stream()
                .filter(envio -> envio.getRepartidorAsignado() != null)
                .filter(envio -> envio.getRepartidorAsignado().getId().equals(repartidorActual.getId()))
                .collect(Collectors.toList());

        long total = misEnvios.size();

        long enRuta = misEnvios.stream()
                .filter(e -> e.getEstadoEntrega() == EstadoEntrega.EN_RUTA)
                .count();

        long entregados = misEnvios.stream()
                .filter(e -> e.getEstadoEntrega() == EstadoEntrega.ENTREGADO)
                .count();

        long pendientes = misEnvios.stream()
                .filter(e -> e.getEstadoEntrega() == EstadoEntrega.ASIGNADO ||
                        e.getEstadoEntrega() == EstadoEntrega.RECOGIDO)
                .count();

        lblTotalAsignados.setText("Total: " + total);
        lblEnRuta.setText("En Ruta: " + enRuta);
        lblEntregados.setText("Entregados: " + entregados);
        lblPendientes.setText("Pendientes: " + pendientes);
    }

    /**
     * Limpia los detalles mostrados
     */
    private void limpiarDetalles() {
        tablaEnvios.getSelectionModel().clearSelection();
        envioSeleccionado = null;
        lblNumeroEnvio.setText("-");
        lblOrigenDetalle.setText("-");
        lblDestinoDetalle.setText("-");
        lblDestinatarioNombre.setText("-");
        lblDestinatarioCedula.setText("-");
        lblDestinatarioTelefono.setText("-");
        lblContenido.setText("-");
        lblPeso.setText("-");
        lblServicios.setText("-");
        cmbNuevoEstado.setValue(null);
        txtObservaciones.clear();
        btnActualizarEstado.setDisable(true);
    }

    /**
     * Muestra un cuadro de diálogo de alerta
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}