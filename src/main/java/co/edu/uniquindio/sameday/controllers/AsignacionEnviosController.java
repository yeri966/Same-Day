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

public class AsignacionEnviosController {

    private SameDay sameDay = SameDay.getInstance();
    private Envio envioSeleccionado = null;

    @FXML private TableView<Envio> tablaEnvios;
    @FXML private TableColumn<Envio, String> colId;
    @FXML private TableColumn<Envio, String> colFecha;
    @FXML private TableColumn<Envio, String> colOrigen;
    @FXML private TableColumn<Envio, String> colDestino;
    @FXML private TableColumn<Envio, String> colDestinatario;
    @FXML private TableColumn<Envio, String> colPeso;
    @FXML private TableColumn<Envio, String> colCosto;
    @FXML private TableColumn<Envio, String> colEstado;

    @FXML private TableView<Dealer> tablaRepartidores;
    @FXML private TableColumn<Dealer, String> colRepartidorId;
    @FXML private TableColumn<Dealer, String> colRepartidorNombre;
    @FXML private TableColumn<Dealer, String> colRepartidorCiudad;
    @FXML private TableColumn<Dealer, String> colRepartidorDisponible;

    @FXML private Label lblEnviosPendientes;
    @FXML private Label lblEnviosAsignados;
    @FXML private Label lblRepartidoresDisponibles;

    @FXML private Button btnAsignar;
    @FXML private Button btnDesasignar;

    @FXML private ComboBox<City> cmbFiltrarCiudad;

    @FXML
    void initialize() {
        System.out.println("=== INICIALIZANDO CONTROLADOR ASIGNACIÓN DE ENVÍOS ===");
        configurarTablaEnvios();
        configurarTablaRepartidores();
        configurarFiltros();
        cargarEnviosPagados();
        cargarRepartidores();
        configurarSeleccionEnvio();
        actualizarEstadisticas();
        btnDesasignar.setDisable(true);
    }

    private void configurarTablaEnvios() {
        colId.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getId()));

        colFecha.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFechaCreacion() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                String fecha = cellData.getValue().getFechaCreacion().format(formatter);
                return new SimpleStringProperty(fecha);
            }
            return new SimpleStringProperty("Sin fecha");
        });

        colOrigen.setCellValueFactory(cellData -> {
            Address origen = cellData.getValue().getOrigen();
            if (origen != null) {
                return new SimpleStringProperty(origen.getCity().toString());
            }
            return new SimpleStringProperty("");
        });

        colDestino.setCellValueFactory(cellData -> {
            Address destino = cellData.getValue().getDestino();
            if (destino != null) {
                return new SimpleStringProperty(destino.getCity().toString());
            }
            return new SimpleStringProperty("");
        });

        colDestinatario.setCellValueFactory(cellData -> {
            String nombreDestinatario = cellData.getValue().getNombreDestinatario();
            return new SimpleStringProperty(nombreDestinatario != null ? nombreDestinatario : "");
        });

        colPeso.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f kg", cellData.getValue().getPeso())));

        colCosto.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("$%,.0f", cellData.getValue().getCostoTotal())));

        colEstado.setCellValueFactory(cellData -> {
            Envio envio = cellData.getValue();
            String estadoTexto = envio.getRepartidorAsignado() != null
                    ? "✅ Asignado a " + envio.getRepartidorAsignado().getNombre()
                    : "⏳ Sin asignar";
            return new SimpleStringProperty(estadoTexto);
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
                    if (item.contains("Asignado")) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void configurarTablaRepartidores() {
        colRepartidorId.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getId()));

        colRepartidorNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNombre()));

        colRepartidorCiudad.setCellValueFactory(cellData -> {
            City city = cellData.getValue().getCity();
            return new SimpleStringProperty(city != null ? city.toString() : "Sin ciudad");
        });

        colRepartidorDisponible.setCellValueFactory(cellData -> {
            String disponible = cellData.getValue().isDisponible() ? "✅ Disponible" : "❌ No disponible";
            return new SimpleStringProperty(disponible);
        });

        // Aplicar estilo a la columna de disponibilidad
        colRepartidorDisponible.setCellFactory(column -> new TableCell<Dealer, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Disponible")) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void configurarFiltros() {
        cmbFiltrarCiudad.setItems(FXCollections.observableArrayList(City.values()));
        cmbFiltrarCiudad.setPromptText("Todas las ciudades");
    }

    private void configurarSeleccionEnvio() {
        tablaEnvios.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        envioSeleccionado = newValue;
                        filtrarRepartidoresPorZona(newValue);

                        // Habilitar/deshabilitar botones según el estado del envío
                        btnAsignar.setDisable(newValue.getRepartidorAsignado() != null);
                        btnDesasignar.setDisable(newValue.getRepartidorAsignado() == null);
                    }
                }
        );
    }

    private void filtrarRepartidoresPorZona(Envio envio) {
        if (envio.getDestino() == null || envio.getDestino().getCity() == null) {
            cargarRepartidores();
            return;
        }

        City ciudadDestino = envio.getDestino().getCity();

        List<Dealer> repartidoresFiltrados = sameDay.getListPersons().stream()
                .filter(person -> person instanceof Dealer)
                .map(person -> (Dealer) person)
                .filter(dealer -> dealer.getCity() == ciudadDestino)
                .filter(Dealer::isDisponible)
                .collect(Collectors.toList());

        ObservableList<Dealer> repartidoresObservable = FXCollections.observableArrayList(repartidoresFiltrados);
        tablaRepartidores.setItems(repartidoresObservable);

        System.out.println("Repartidores disponibles en " + ciudadDestino + ": " + repartidoresFiltrados.size());
    }

    @FXML
    void onAsignar(ActionEvent event) {
        if (envioSeleccionado == null) {
            mostrarAlerta("Selección requerida",
                    "Debe seleccionar un envío de la tabla",
                    Alert.AlertType.WARNING);
            return;
        }

        if (envioSeleccionado.getRepartidorAsignado() != null) {
            mostrarAlerta("Envío ya asignado",
                    "Este envío ya tiene un repartidor asignado",
                    Alert.AlertType.WARNING);
            return;
        }

        Dealer repartidorSeleccionado = tablaRepartidores.getSelectionModel().getSelectedItem();

        if (repartidorSeleccionado == null) {
            mostrarAlerta("Repartidor no seleccionado",
                    "Debe seleccionar un repartidor de la tabla",
                    Alert.AlertType.WARNING);
            return;
        }

        if (!repartidorSeleccionado.isDisponible()) {
            mostrarAlerta("Repartidor no disponible",
                    "El repartidor seleccionado no está disponible",
                    Alert.AlertType.WARNING);
            return;
        }

        // Validar que el repartidor esté en la misma zona
        if (envioSeleccionado.getDestino() != null
                && repartidorSeleccionado.getCity() != envioSeleccionado.getDestino().getCity()) {
            mostrarAlerta("Zona incorrecta",
                    "El repartidor debe estar en la misma zona que el destino del envío",
                    Alert.AlertType.WARNING);
            return;
        }

        // Confirmar asignación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Asignación");
        confirmacion.setHeaderText("¿Está seguro que desea asignar este envío?");
        confirmacion.setContentText("Envío: " + envioSeleccionado.getId() + "\n" +
                "Repartidor: " + repartidorSeleccionado.getNombre() + "\n" +
                "Ciudad: " + repartidorSeleccionado.getCity());

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            // Asignar el repartidor al envío
            envioSeleccionado.setRepartidorAsignado(repartidorSeleccionado);

            // Marcar al repartidor como no disponible
            repartidorSeleccionado.setDisponible(false);

            // Actualizar en el sistema
            sameDay.updateEnvio(envioSeleccionado);

            mostrarAlerta("Asignación Exitosa",
                    "El envío ha sido asignado correctamente al repartidor",
                    Alert.AlertType.INFORMATION);

            // Recargar las tablas
            cargarEnviosPagados();
            cargarRepartidores();
            actualizarEstadisticas();

            // Limpiar selección
            tablaEnvios.getSelectionModel().clearSelection();
            envioSeleccionado = null;
        }
    }

    @FXML
    void onDesasignar(ActionEvent event) {
        if (envioSeleccionado == null) {
            mostrarAlerta("Selección requerida",
                    "Debe seleccionar un envío de la tabla",
                    Alert.AlertType.WARNING);
            return;
        }

        if (envioSeleccionado.getRepartidorAsignado() == null) {
            mostrarAlerta("Sin asignación",
                    "Este envío no tiene un repartidor asignado",
                    Alert.AlertType.WARNING);
            return;
        }

        // Confirmar desasignación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Desasignación");
        confirmacion.setHeaderText("¿Está seguro que desea desasignar este envío?");
        confirmacion.setContentText("Envío: " + envioSeleccionado.getId() + "\n" +
                "Repartidor: " + envioSeleccionado.getRepartidorAsignado().getNombre());

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            // Liberar al repartidor
            envioSeleccionado.getRepartidorAsignado().setDisponible(true);

            // Desasignar el repartidor del envío
            envioSeleccionado.setRepartidorAsignado(null);

            // Actualizar en el sistema
            sameDay.updateEnvio(envioSeleccionado);

            mostrarAlerta("Desasignación Exitosa",
                    "El repartidor ha sido liberado correctamente",
                    Alert.AlertType.INFORMATION);

            // Recargar las tablas
            cargarEnviosPagados();
            cargarRepartidores();
            actualizarEstadisticas();

            // Limpiar selección
            tablaEnvios.getSelectionModel().clearSelection();
            envioSeleccionado = null;
        }
    }

    @FXML
    void onFiltrarPorCiudad(ActionEvent event) {
        City ciudadSeleccionada = cmbFiltrarCiudad.getValue();

        if (ciudadSeleccionada == null) {
            cargarRepartidores();
            return;
        }

        List<Dealer> repartidoresFiltrados = sameDay.getListPersons().stream()
                .filter(person -> person instanceof Dealer)
                .map(person -> (Dealer) person)
                .filter(dealer -> dealer.getCity() == ciudadSeleccionada)
                .collect(Collectors.toList());

        ObservableList<Dealer> repartidoresObservable = FXCollections.observableArrayList(repartidoresFiltrados);
        tablaRepartidores.setItems(repartidoresObservable);
    }

    @FXML
    void onLimpiarFiltro(ActionEvent event) {
        cmbFiltrarCiudad.setValue(null);
        cargarRepartidores();
    }

    private void cargarEnviosPagados() {
        System.out.println("\n=== CARGANDO ENVÍOS PAGADOS ===");

        // Filtrar solo los envíos pagados
        List<Envio> enviosPagados = sameDay.getListEnvios().stream()
                .filter(envio -> "PAGADO".equals(envio.getEstado()))
                .collect(Collectors.toList());

        ObservableList<Envio> enviosObservable = FXCollections.observableArrayList(enviosPagados);
        tablaEnvios.setItems(enviosObservable);

        System.out.println("Total envíos pagados: " + enviosPagados.size());
        System.out.println("=== ENVÍOS CARGADOS ===\n");
    }

    private void cargarRepartidores() {
        System.out.println("\n=== CARGANDO REPARTIDORES ===");

        List<Dealer> repartidores = sameDay.getListPersons().stream()
                .filter(person -> person instanceof Dealer)
                .map(person -> (Dealer) person)
                .collect(Collectors.toList());

        ObservableList<Dealer> repartidoresObservable = FXCollections.observableArrayList(repartidores);
        tablaRepartidores.setItems(repartidoresObservable);

        System.out.println("Total repartidores: " + repartidores.size());
        System.out.println("=== REPARTIDORES CARGADOS ===\n");
    }

    private void actualizarEstadisticas() {
        long pendientes = sameDay.getListEnvios().stream()
                .filter(envio -> "PAGADO".equals(envio.getEstado()))
                .filter(envio -> envio.getRepartidorAsignado() == null)
                .count();

        long asignados = sameDay.getListEnvios().stream()
                .filter(envio -> "PAGADO".equals(envio.getEstado()))
                .filter(envio -> envio.getRepartidorAsignado() != null)
                .count();

        long repartidoresDisponibles = sameDay.getListPersons().stream()
                .filter(person -> person instanceof Dealer)
                .map(person -> (Dealer) person)
                .filter(Dealer::isDisponible)
                .count();

        lblEnviosPendientes.setText("Pendientes: " + pendientes);
        lblEnviosAsignados.setText("Asignados: " + asignados);
        lblRepartidoresDisponibles.setText("Disponibles: " + repartidoresDisponibles);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}