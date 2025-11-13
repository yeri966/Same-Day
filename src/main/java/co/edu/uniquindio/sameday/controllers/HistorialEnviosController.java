package co.edu.uniquindio.sameday.controllers;

import co.edu.uniquindio.sameday.models.*;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class HistorialEnviosController {

    private SameDay sameDay = SameDay.getInstance();
    private ObservableList<Envio> todosLosEnvios = FXCollections.observableArrayList();

    @FXML private TableView<Envio> tablaHistorial;
    @FXML private TableColumn<Envio, String> colNumeroRastreo;
    @FXML private TableColumn<Envio, String> colFecha;
    @FXML private TableColumn<Envio, String> colOrigen;
    @FXML private TableColumn<Envio, String> colDestino;
    @FXML private TableColumn<Envio, String> colDestinatario;
    @FXML private TableColumn<Envio, String> colContenido;
    @FXML private TableColumn<Envio, String> colPeso;
    @FXML private TableColumn<Envio, String> colCosto;
    @FXML private TableColumn<Envio, String> colRepartidor; // NUEVA COLUMNA
    @FXML private TableColumn<Envio, String> colEstado;

    // Filtros
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<City> cmbZona;
    @FXML private Button btnFiltrar;
    @FXML private Button btnLimpiarFiltros;

    // Labels de estadísticas
    @FXML private Label lblTotalEnvios;
    @FXML private Label lblEnviosEntregados;
    @FXML private Label lblEnviosPendientes;

    @FXML
    void initialize() {
        System.out.println("=== INICIALIZANDO CONTROLADOR HISTORIAL DE ENVÍOS ===");
        configurarTabla();
        configurarFiltros();
        cargarEnviosPagados();
        actualizarEstadisticas();
    }

    /**
     * Configura las columnas de la tabla
     */
    private void configurarTabla() {
        // Número de Rastreo
        colNumeroRastreo.setCellValueFactory(cellData -> {
            String numeroRastreo = cellData.getValue().getId();
            return new SimpleStringProperty(numeroRastreo != null ? numeroRastreo : "");
        });

        // Fecha de Creación
        colFecha.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFechaCreacion() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                String fecha = cellData.getValue().getFechaCreacion().format(formatter);
                return new SimpleStringProperty(fecha);
            }
            return new SimpleStringProperty("Sin fecha");
        });

        // Origen
        colOrigen.setCellValueFactory(cellData -> {
            Address origen = cellData.getValue().getOrigen();
            if (origen != null) {
                return new SimpleStringProperty(origen.getCity().toString());
            }
            return new SimpleStringProperty("");
        });

        // Destino
        colDestino.setCellValueFactory(cellData -> {
            Address destino = cellData.getValue().getDestino();
            if (destino != null) {
                return new SimpleStringProperty(destino.getCity().toString());
            }
            return new SimpleStringProperty("");
        });

        // Destinatario
        colDestinatario.setCellValueFactory(cellData -> {
            String nombreDestinatario = cellData.getValue().getNombreDestinatario();
            return new SimpleStringProperty(nombreDestinatario != null ? nombreDestinatario : "");
        });

        // Contenido
        colContenido.setCellValueFactory(cellData -> {
            String contenido = cellData.getValue().getContenido();
            return new SimpleStringProperty(contenido != null ? contenido : "");
        });

        // Peso
        colPeso.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f kg", cellData.getValue().getPeso())));

        // Costo
        colCosto.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("$%,.0f", cellData.getValue().getCostoTotal())));

        // Repartidor - NUEVO
        colRepartidor.setCellValueFactory(cellData -> {
            Dealer repartidor = cellData.getValue().getRepartidorAsignado();
            if (repartidor != null) {
                return new SimpleStringProperty(repartidor.getNombre());
            }
            return new SimpleStringProperty("-");
        });

        // Aplicar estilo a la columna de repartidor
        colRepartidor.setCellFactory(column -> new TableCell<Envio, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("-")) {
                        setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic;");
                    } else {
                        setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Estado de envío (EN_RUTA, ENTREGADO) - por ahora null
        colEstado.setCellValueFactory(cellData -> {
            // Por ahora el estado de ruta/entrega es null
            // Solo mostramos "-" para indicar que está pendiente
            return new SimpleStringProperty("-");
        });

        // Aplicar estilo a la columna de estado
        colEstado.setCellFactory(column -> new TableCell<Envio, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    // Por ahora todos aparecen en gris como pendientes
                    setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");
                }
            }
        });
    }

    /**
     * Configura los ComboBox de filtros
     */
    private void configurarFiltros() {
        // ComboBox de Estado - por ahora solo "Todos" ya que no hay estados de ruta
        ObservableList<String> estados = FXCollections.observableArrayList("Todos");
        cmbEstado.setItems(estados);
        cmbEstado.setValue("Todos");

        // ComboBox de Zona (ciudades)
        ObservableList<City> ciudades = FXCollections.observableArrayList(City.values());
        cmbZona.setItems(ciudades);
    }

    /**
     * Carga solo los envíos que ya están pagados
     */
    private void cargarEnviosPagados() {
        System.out.println("\n=== CARGANDO ENVÍOS PAGADOS ===");

        // Filtrar solo los envíos pagados
        List<Envio> enviosPagados = sameDay.getListEnvios().stream()
                .filter(envio -> "PAGADO".equals(envio.getEstado()))
                .collect(Collectors.toList());

        todosLosEnvios = FXCollections.observableArrayList(enviosPagados);
        tablaHistorial.setItems(todosLosEnvios);

        System.out.println("Total envíos pagados: " + enviosPagados.size());
        System.out.println("=== ENVÍOS CARGADOS ===\n");
    }

    /**
     * Aplica los filtros seleccionados
     */
    @FXML
    void onFiltrar(ActionEvent event) {
        List<Envio> enviosFiltrados = todosLosEnvios.stream()
                .filter(this::filtrarPorFecha)
                .filter(this::filtrarPorEstado)
                .filter(this::filtrarPorZona)
                .collect(Collectors.toList());

        tablaHistorial.setItems(FXCollections.observableArrayList(enviosFiltrados));
        actualizarEstadisticas();
    }

    /**
     * Limpia todos los filtros
     */
    @FXML
    void onLimpiarFiltros(ActionEvent event) {
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
        cmbEstado.setValue("Todos");
        cmbZona.setValue(null);

        tablaHistorial.setItems(todosLosEnvios);
        actualizarEstadisticas();
    }

    /**
     * Filtra por rango de fechas
     */
    private boolean filtrarPorFecha(Envio envio) {
        LocalDate fechaInicio = dpFechaInicio.getValue();
        LocalDate fechaFin = dpFechaFin.getValue();

        if (fechaInicio == null && fechaFin == null) {
            return true; // Sin filtro de fecha
        }

        if (envio.getFechaCreacion() == null) {
            return false;
        }

        LocalDate fechaEnvio = envio.getFechaCreacion().toLocalDate();

        if (fechaInicio != null && fechaEnvio.isBefore(fechaInicio)) {
            return false;
        }

        if (fechaFin != null && fechaEnvio.isAfter(fechaFin)) {
            return false;
        }

        return true;
    }

    /**
     * Filtra por estado - por ahora siempre retorna true ya que no hay estados de ruta
     */
    private boolean filtrarPorEstado(Envio envio) {
        // Por ahora no hay estados de ruta/entrega, así que no filtramos por esto
        return true;
    }

    /**
     * Filtra por zona (ciudad de destino)
     */
    private boolean filtrarPorZona(Envio envio) {
        City zonaSeleccionada = cmbZona.getValue();

        if (zonaSeleccionada == null) {
            return true; // Sin filtro de zona
        }

        if (envio.getDestino() == null) {
            return false;
        }

        return zonaSeleccionada == envio.getDestino().getCity();
    }

    /**
     * Actualiza las estadísticas mostradas
     */
    private void actualizarEstadisticas() {
        int total = tablaHistorial.getItems().size();

        // Por ahora no hay estados de ruta/entrega, todos son envíos pagados pendientes
        long entregados = 0; // Será usado más adelante
        long pendientes = total; // Por ahora todos están pendientes de entrega

        lblTotalEnvios.setText("Total: " + total);
        lblEnviosEntregados.setText("Entregados: " + entregados);
        lblEnviosPendientes.setText("Pendientes: " + pendientes);
    }

    /**
     * Muestra el detalle completo de un envío
     */
    @FXML
    void onVerDetalle(ActionEvent event) {
        Envio envioSeleccionado = tablaHistorial.getSelectionModel().getSelectedItem();

        if (envioSeleccionado == null) {
            mostrarAlerta("Selección requerida",
                    "Debe seleccionar un envío de la tabla",
                    Alert.AlertType.WARNING);
            return;
        }

        mostrarDetalleEnvio(envioSeleccionado);
    }

    /**
     * Muestra un diálogo con el detalle completo del envío
     */
    private void mostrarDetalleEnvio(Envio envio) {
        StringBuilder detalle = new StringBuilder();
        detalle.append("📦 DETALLE DEL ENVÍO\n\n");
        detalle.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        detalle.append("Número de Rastreo: ").append(envio.getId()).append("\n\n");

        if (envio.getFechaCreacion() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            detalle.append("Fecha de Creación: ").append(envio.getFechaCreacion().format(formatter)).append("\n\n");
        }

        detalle.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        detalle.append("ORIGEN:\n");
        if (envio.getOrigen() != null) {
            detalle.append("  • ").append(envio.getOrigen().getFullAddress()).append("\n\n");
        }

        detalle.append("DESTINO:\n");
        if (envio.getDestino() != null) {
            detalle.append("  • ").append(envio.getDestino().getFullAddress()).append("\n\n");
        }

        detalle.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        detalle.append("DESTINATARIO:\n");
        detalle.append("  • Nombre: ").append(envio.getNombreDestinatario()).append("\n");
        detalle.append("  • Cédula: ").append(envio.getCedulaDestinatario()).append("\n");
        detalle.append("  • Teléfono: ").append(envio.getTelefonoDestinatario()).append("\n\n");

        // NUEVO: Información del repartidor
        detalle.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        detalle.append("REPARTIDOR ASIGNADO:\n");
        if (envio.getRepartidorAsignado() != null) {
            Dealer repartidor = envio.getRepartidorAsignado();
            detalle.append("  • Nombre: ").append(repartidor.getNombre()).append("\n");
            detalle.append("  • ID: ").append(repartidor.getId()).append("\n");
            detalle.append("  • Ciudad: ").append(repartidor.getCity()).append("\n");
        } else {
            detalle.append("  • Sin repartidor asignado\n");
        }
        detalle.append("\n");

        detalle.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        detalle.append("INFORMACIÓN DEL PAQUETE:\n");
        detalle.append("  • Contenido: ").append(envio.getContenido()).append("\n");
        detalle.append("  • Peso: ").append(String.format("%.2f kg", envio.getPeso())).append("\n");
        detalle.append("  • Dimensiones: ").append(envio.getDimensiones()).append("\n");
        detalle.append("  • Volumen: ").append(String.format("%.2f cm³", envio.getVolumen())).append("\n");
        detalle.append("  • Servicios: ").append(envio.getServiciosAdicionalesString()).append("\n\n");

        detalle.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        detalle.append("Costo Total: $").append(String.format("%,.0f", envio.getCostoTotal())).append("\n");
        detalle.append("Estado: ").append(envio.getEstado()).append("\n");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalle del Envío");
        alert.setHeaderText(null);
        alert.setContentText(detalle.toString());
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
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