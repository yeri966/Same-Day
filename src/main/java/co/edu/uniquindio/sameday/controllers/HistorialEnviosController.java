package co.edu.uniquindio.sameday.controllers;

import co.edu.uniquindio.sameday.models.*;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
import co.edu.uniquindio.sameday.models.structural.adapter.PdfGenerator;
import co.edu.uniquindio.sameday.models.structural.adapter.EnvioPdfAdapter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para el historial de envíos del cliente
 * Permite consultar, filtrar y descargar comprobantes de envíos en PDF
 * PATRÓN ESTRUCTURAL: ADAPTER - Usa EnvioPdfAdapter para generar PDFs
 */
public class HistorialEnviosController {

    private SameDay sameDay = SameDay.getInstance();
    private ObservableList<Envio> todosLosEnvios = FXCollections.observableArrayList();

    // PATRÓN ADAPTER: Generador de PDFs
    private PdfGenerator pdfGenerator = new EnvioPdfAdapter();

    @FXML private TableView<Envio> tablaHistorial;
    @FXML private TableColumn<Envio, String> colNumeroRastreo;
    @FXML private TableColumn<Envio, String> colFecha;
    @FXML private TableColumn<Envio, String> colOrigen;
    @FXML private TableColumn<Envio, String> colDestino;
    @FXML private TableColumn<Envio, String> colDestinatario;
    @FXML private TableColumn<Envio, String> colContenido;
    @FXML private TableColumn<Envio, String> colPeso;
    @FXML private TableColumn<Envio, String> colCosto;
    @FXML private TableColumn<Envio, String> colRepartidor;
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

        // Repartidor
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

        // Estado de envío - MUESTRA EL ESTADO REAL DE ENTREGA
        colEstado.setCellValueFactory(cellData -> {
            Envio envio = cellData.getValue();

            // Si tiene repartidor asignado y estado de entrega, mostrarlo
            if (envio.getRepartidorAsignado() != null && envio.getEstadoEntrega() != null) {
                return new SimpleStringProperty(envio.getEstadoEntregaString());
            }

            // Si tiene repartidor pero no estado, está asignado
            if (envio.getRepartidorAsignado() != null) {
                return new SimpleStringProperty("📋 Asignado");
            }

            // Si no tiene repartidor, está pendiente de asignación
            return new SimpleStringProperty("⏳ Sin asignar");
        });

        // Aplicar estilo a la columna de estado según el estado de entrega
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
                    if (item.contains("Sin asignar")) {
                        setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: bold;");
                    } else if (item.contains("Asignado")) {
                        setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                    } else if (item.contains("Recogido")) {
                        setStyle("-fx-text-fill: #8b5cf6; -fx-font-weight: bold;");
                    } else if (item.contains("En Ruta")) {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                    } else if (item.contains("Entregado")) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    } else if (item.contains("Incidencia")) {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    /**
     * Configura los ComboBox de filtros
     */
    private void configurarFiltros() {
        // ComboBox de Estado - ahora con estados reales
        ObservableList<String> estados = FXCollections.observableArrayList(
                "Todos",
                "Sin asignar",
                "Asignado",
                "Recogido",
                "En Ruta",
                "Entregado",
                "Con Incidencia"
        );
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
     * Filtra por estado - ACTUALIZADO para estados reales
     */
    private boolean filtrarPorEstado(Envio envio) {
        String estadoSeleccionado = cmbEstado.getValue();

        if (estadoSeleccionado == null || estadoSeleccionado.equals("Todos")) {
            return true;
        }

        // Obtener el estado actual del envío
        String estadoEnvio = "";

        if (envio.getRepartidorAsignado() != null && envio.getEstadoEntrega() != null) {
            estadoEnvio = envio.getEstadoEntrega().getDisplayName();
        } else if (envio.getRepartidorAsignado() != null) {
            estadoEnvio = "Asignado";
        } else {
            estadoEnvio = "Sin asignar";
        }

        return estadoEnvio.equals(estadoSeleccionado);
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
     * Actualiza las estadísticas mostradas - ACTUALIZADO
     */
    private void actualizarEstadisticas() {
        int total = tablaHistorial.getItems().size();

        // Contar envíos entregados (con estado ENTREGADO)
        long entregados = tablaHistorial.getItems().stream()
                .filter(envio -> envio.getEstadoEntrega() == EstadoEntrega.ENTREGADO)
                .count();

        // Contar pendientes (sin repartidor o con estados ASIGNADO, RECOGIDO, EN_RUTA)
        long pendientes = tablaHistorial.getItems().stream()
                .filter(envio ->
                        envio.getRepartidorAsignado() == null ||
                                envio.getEstadoEntrega() == EstadoEntrega.ASIGNADO ||
                                envio.getEstadoEntrega() == EstadoEntrega.RECOGIDO ||
                                envio.getEstadoEntrega() == EstadoEntrega.EN_RUTA ||
                                envio.getEstadoEntrega() == EstadoEntrega.CON_INCIDENCIA
                )
                .count();

        lblTotalEnvios.setText("Total: " + total);
        lblEnviosEntregados.setText("Entregados: " + entregados);
        lblEnviosPendientes.setText("Pendientes: " + pendientes);
    }

    /**
     * NUEVO: Descarga el comprobante del envío en PDF usando el patrón ADAPTER
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

        // Confirmar descarga
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Descargar Comprobante PDF");
        confirmacion.setHeaderText("¿Desea descargar el comprobante en PDF?");
        confirmacion.setContentText(
                "Envío: " + envioSeleccionado.getId() + "\n" +
                        "Destinatario: " + envioSeleccionado.getNombreDestinatario() + "\n" +
                        "Estado: " + envioSeleccionado.getEstadoEntregaString()
        );

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            try {
                // Definir ruta de salida en la carpeta de Descargas del usuario
                String fileName = "Comprobante_Envio_" + envioSeleccionado.getId() + "_" +
                        System.currentTimeMillis() + ".pdf";
                String outputPath = System.getProperty("user.home") + "/Downloads/" + fileName;

                System.out.println("🔄 Generando PDF: " + outputPath);

                // USAR EL PATRÓN ADAPTER para generar el PDF
                File pdfFile = pdfGenerator.generateEnvioPdf(envioSeleccionado, outputPath);

                if (pdfFile != null && pdfFile.exists()) {
                    System.out.println("✅ PDF generado exitosamente");

                    // Intentar abrir el PDF automáticamente
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().open(pdfFile);
                            System.out.println("📂 Abriendo PDF...");
                        } catch (Exception e) {
                            System.err.println("⚠️ No se pudo abrir el PDF automáticamente: " + e.getMessage());
                        }
                    }

                    // Mostrar mensaje de éxito con la ruta
                    mostrarAlerta("✅ PDF Generado Exitosamente",
                            "El comprobante se ha descargado en:\n\n" +
                                    pdfFile.getAbsolutePath() + "\n\n" +
                                    "Tamaño: " + (pdfFile.length() / 1024) + " KB",
                            Alert.AlertType.INFORMATION);
                } else {
                    System.err.println("❌ Error: PDF no generado");
                    mostrarAlerta("Error al Generar PDF",
                            "No se pudo generar el archivo PDF.\n" +
                                    "Verifique los permisos de escritura en la carpeta de Descargas.",
                            Alert.AlertType.ERROR);
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("❌ Excepción al generar PDF: " + e.getMessage());
                mostrarAlerta("Error Inesperado",
                        "Ocurrió un error al generar el PDF:\n\n" +
                                e.getMessage() + "\n\n" +
                                "Por favor, contacte al administrador del sistema.",
                        Alert.AlertType.ERROR);
            }
        }
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