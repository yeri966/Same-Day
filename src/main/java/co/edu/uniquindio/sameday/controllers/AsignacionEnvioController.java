package co.edu.uniquindio.sameday.controllers;

import co.edu.uniquindio.sameday.models.*;
import co.edu.uniquindio.sameday.models.behavioral.strategy.*;
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

public class AsignacionEnvioController {

    private SameDay sameDay = SameDay.getInstance();
    private Envio envioSeleccionado = null;
    private ObservableList<Envio> enviosObservableList;
    private ObservableList<Dealer> repartidoresObservableList;

    // Tabla de envíos
    @FXML
    private TableView<Envio> tablaEnvios;
    @FXML
    private TableColumn<Envio, String> colId;
    @FXML
    private TableColumn<Envio, String> colFecha;
    @FXML
    private TableColumn<Envio, String> colOrigen;
    @FXML
    private TableColumn<Envio, String> colDestino;
    @FXML
    private TableColumn<Envio, String> colDestinatario;
    @FXML
    private TableColumn<Envio, String> colPeso;
    @FXML
    private TableColumn<Envio, String> colCosto;
    @FXML
    private TableColumn<Envio, String> colEstado;

    // Tabla de repartidores
    @FXML
    private TableView<Dealer> tablaRepartidores;
    @FXML
    private TableColumn<Dealer, String> colRepartidorId;
    @FXML
    private TableColumn<Dealer, String> colRepartidorNombre;
    @FXML
    private TableColumn<Dealer, String> colRepartidorCiudad;
    @FXML
    private TableColumn<Dealer, String> colRepartidorDisponible;

    // Estadísticas
    @FXML
    private Label lblEnviosPendientes;
    @FXML
    private Label lblEnviosAsignados;
    @FXML
    private Label lblRepartidoresDisponibles;

    // Botones de acción
    @FXML
    private Button btnAsignar;
    @FXML
    private Button btnDesasignar;

    // Filtros
    @FXML
    private ComboBox<City> cmbFiltrarCiudad;

    // NUEVO: Estrategia
    @FXML
    private ComboBox<String> cmbEstrategia;

    @FXML
    void initialize() {
        enviosObservableList = FXCollections.observableArrayList();
        repartidoresObservableList = FXCollections.observableArrayList();

        tablaEnvios.setItems(enviosObservableList);
        tablaRepartidores.setItems(repartidoresObservableList);

        configurarTablaEnvios();
        configurarTablaRepartidores();
        configurarFiltros();
        configurarEstrategias(); // NUEVO
        configurarSeleccionEnvio();

        cargarEnviosPagados();
        cargarRepartidores();
        actualizarEstadisticas();

        btnDesasignar.setDisable(true);
    }

    // NUEVO: Configurar estrategias
    private void configurarEstrategias() {
        cmbEstrategia.setItems(FXCollections.observableArrayList(
                "Por Ciudad",
                "Por Menor Carga"
        ));
        cmbEstrategia.setValue("Por Ciudad");
    }

    // NUEVO: Asignación automática usando Strategy
    @FXML
    void onAsignarAutomatico(ActionEvent event) {
        // Seleccionar estrategia según ComboBox
        EstrategiaAsignacion estrategia;
        if ("Por Menor Carga".equals(cmbEstrategia.getValue())) {
            estrategia = new AsignacionPorMenorCarga();
        } else {
            estrategia = new AsignacionPorCiudad();
        }

        AsignadorEnvios asignador = new AsignadorEnvios(estrategia);

        // Obtener todos los repartidores
        List<Dealer> todosRepartidores = sameDay.getListPersons().stream()
                .filter(person -> person instanceof Dealer)
                .map(person -> (Dealer) person)
                .collect(Collectors.toList());

        // Obtener envíos pagados SIN asignar
        List<Envio> enviosSinAsignar = sameDay.getListEnvios().stream()
                .filter(envio -> "PAGADO".equals(envio.getEstado()))
                .filter(envio -> envio.getRepartidorAsignado() == null)
                .collect(Collectors.toList());

        if (enviosSinAsignar.isEmpty()) {
            mostrarAlerta("Sin envíos pendientes",
                    "No hay envíos pendientes de asignación",
                    Alert.AlertType.INFORMATION);
            return;
        }

        // Confirmar antes de asignar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Asignación Automática");
        confirmacion.setHeaderText("Estrategia: " + cmbEstrategia.getValue());
        confirmacion.setContentText(
                "Se asignarán automáticamente " + enviosSinAsignar.size() + " envíos.\n" +
                        "¿Desea continuar?"
        );

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            int asignados = 0;
            int noAsignados = 0;

            // Asignar TODOS los envíos pendientes
            for (Envio envio : enviosSinAsignar) {
                Dealer repartidor = asignador.asignar(envio, todosRepartidores);

                if (repartidor != null) {
                    envio.setRepartidorAsignado(repartidor);
                    envio.setEstadoEntrega(EstadoEntrega.ASIGNADO);
                    sameDay.updateEnvio(envio);
                    asignados++;
                } else {
                    noAsignados++;
                }
            }

            String mensaje = "Envíos asignados: " + asignados;
            if (noAsignados > 0) {
                mensaje += "\nEnvíos sin repartidor disponible: " + noAsignados;
            }

            mostrarAlerta("Asignación Completada", mensaje, Alert.AlertType.INFORMATION);

            // Refrescar UI
            tablaEnvios.refresh();
            tablaRepartidores.refresh();
            cargarEnviosPagados();
            cargarRepartidores();
            actualizarEstadisticas();
        }
    }

    private void configurarTablaEnvios() {
        colId.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getId()));

        colFecha.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFechaCreacion() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                return new SimpleStringProperty(
                        cellData.getValue().getFechaCreacion().format(formatter)
                );
            }
            return new SimpleStringProperty("Sin fecha");
        });

        colOrigen.setCellValueFactory(cellData -> {
            Address origen = cellData.getValue().getOrigen();
            return new SimpleStringProperty(
                    origen != null ? origen.getCity().toString() : "-"
            );
        });

        colDestino.setCellValueFactory(cellData -> {
            Address destino = cellData.getValue().getDestino();
            return new SimpleStringProperty(
                    destino != null ? destino.getCity().toString() : "-"
            );
        });

        colDestinatario.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getNombreDestinatario() != null
                                ? cellData.getValue().getNombreDestinatario()
                                : "-"
                )
        );

        colPeso.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        String.format("%.2f kg", cellData.getValue().getPeso())
                )
        );

        colCosto.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        String.format("$%,.0f", cellData.getValue().getCostoTotal())
                )
        );

        colEstado.setCellValueFactory(cellData -> {
            Envio envio = cellData.getValue();
            if (envio.getRepartidorAsignado() != null) {
                return new SimpleStringProperty(
                        "Asignado a " + envio.getRepartidorAsignado().getNombre()
                );
            }
            return new SimpleStringProperty("Sin asignar");
        });

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
            Dealer dealer = cellData.getValue();
            boolean disponible = dealer.isDisponible();
            return new SimpleStringProperty(
                    disponible ? "Disponible" : "Ocupado"
            );
        });

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

        repartidoresObservableList.clear();
        repartidoresObservableList.addAll(repartidoresFiltrados);
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
                    "El repartidor seleccionado no está disponible para asignaciones",
                    Alert.AlertType.WARNING);
            return;
        }

        if (envioSeleccionado.getDestino() != null
                && repartidorSeleccionado.getCity() != envioSeleccionado.getDestino().getCity()) {
            mostrarAlerta("Zona incorrecta",
                    "El repartidor debe estar en la misma ciudad que el destino del envío",
                    Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Asignación");
        confirmacion.setHeaderText("¿Está seguro que desea asignar este envío?");
        confirmacion.setContentText(
                "Envío: " + envioSeleccionado.getId() + "\n" +
                        "Repartidor: " + repartidorSeleccionado.getNombre() + "\n" +
                        "Ciudad: " + repartidorSeleccionado.getCity()
        );

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            envioSeleccionado.setRepartidorAsignado(repartidorSeleccionado);
            envioSeleccionado.setEstadoEntrega(EstadoEntrega.ASIGNADO);
            sameDay.updateEnvio(envioSeleccionado);

            mostrarAlerta("Asignación Exitosa",
                    "El envío ha sido asignado correctamente al repartidor",
                    Alert.AlertType.INFORMATION);

            tablaEnvios.refresh();
            tablaRepartidores.refresh();
            cargarEnviosPagados();
            cargarRepartidores();
            actualizarEstadisticas();

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

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Desasignación");
        confirmacion.setHeaderText("¿Está seguro que desea desasignar este envío?");
        confirmacion.setContentText(
                "Envío: " + envioSeleccionado.getId() + "\n" +
                        "Repartidor: " + envioSeleccionado.getRepartidorAsignado().getNombre()
        );

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            envioSeleccionado.setRepartidorAsignado(null);
            envioSeleccionado.setEstadoEntrega(null);
            sameDay.updateEnvio(envioSeleccionado);

            mostrarAlerta("Desasignación Exitosa",
                    "El repartidor ha sido liberado correctamente",
                    Alert.AlertType.INFORMATION);

            tablaEnvios.refresh();
            tablaRepartidores.refresh();
            cargarEnviosPagados();
            cargarRepartidores();
            actualizarEstadisticas();

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

        repartidoresObservableList.clear();
        repartidoresObservableList.addAll(repartidoresFiltrados);
    }

    @FXML
    void onLimpiarFiltro(ActionEvent event) {
        cmbFiltrarCiudad.setValue(null);
        cargarRepartidores();
    }

    private void cargarEnviosPagados() {
        List<Envio> enviosPagados = sameDay.getListEnvios().stream()
                .filter(envio -> "PAGADO".equals(envio.getEstado()))
                .collect(Collectors.toList());

        enviosObservableList.clear();
        enviosObservableList.addAll(enviosPagados);
    }

    private void cargarRepartidores() {
        List<Dealer> repartidores = sameDay.getListPersons().stream()
                .filter(person -> person instanceof Dealer)
                .map(person -> (Dealer) person)
                .collect(Collectors.toList());

        repartidoresObservableList.clear();
        repartidoresObservableList.addAll(repartidores);
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
