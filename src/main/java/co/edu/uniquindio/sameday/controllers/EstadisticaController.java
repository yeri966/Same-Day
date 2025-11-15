package co.edu.uniquindio.sameday.controllers;

import co.edu.uniquindio.sameday.models.*;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para el módulo de estadísticas del dashboard de administrador.
 * Genera visualizaciones y métricas clave del sistema SameDay.
 */
public class EstadisticaController {

    // ==================== COMPONENTES FXML ====================

    // Métricas principales (cards)
    @FXML private Label lblTotalEnvios;
    @FXML private Label lblIngresosTotales;
    @FXML private Label lblEnviosEntregados;
    @FXML private Label lblTasaExito;
    @FXML private Label lblPromedioEnvio;
    @FXML private Label lblRepartidoresActivos;

    // Gráficos
    @FXML private PieChart pieEstadoEntrega;
    @FXML private BarChart<String, Number> barServiciosAdicionales;
    @FXML private CategoryAxis xAxisServicios;
    @FXML private NumberAxis yAxisServicios;
    @FXML private LineChart<String, Number> lineEnviosPorDia;
    @FXML private CategoryAxis xAxisDias;
    @FXML private NumberAxis yAxisDias;
    @FXML private BarChart<String, Number> barIngresosPorCiudad;
    @FXML private CategoryAxis xAxisCiudades;
    @FXML private NumberAxis yAxisCiudades;

    // Contenedor principal
    @FXML private VBox contenedorPrincipal;

    // Referencia al singleton
    private SameDay sameDay;

    @FXML
    void initialize() {
        sameDay = SameDay.getInstance();
        cargarTodasLasEstadisticas();
    }

    /**
     * Carga todas las estadísticas y gráficos
     */
    private void cargarTodasLasEstadisticas() {
        cargarMetricasPrincipales();
        cargarGraficoEstadoEntrega();
        cargarGraficoServiciosAdicionales();
        cargarGraficoEnviosPorDia();
        cargarGraficoIngresosPorCiudad();
    }

    /**
     * Calcula y muestra las métricas principales en las cards superiores
     */
    private void cargarMetricasPrincipales() {
        ArrayList<Envio> envios = sameDay.getListEnvios();
        ArrayList<Person> personas = sameDay.getListPersons();

        // Total de envíos
        int totalEnvios = envios.size();
        lblTotalEnvios.setText(String.valueOf(totalEnvios));

        // Ingresos totales
        double ingresosTotales = envios.stream()
                .mapToDouble(Envio::getCostoTotal)
                .sum();
        lblIngresosTotales.setText(formatearMoneda(ingresosTotales));

        // Envíos entregados
        long enviosEntregados = envios.stream()
                .filter(e -> e.getEstadoEntrega() == EstadoEntrega.ENTREGADO)
                .count();
        lblEnviosEntregados.setText(String.valueOf(enviosEntregados));

        // Tasa de éxito (entregados / total con estado)
        long enviosConEstado = envios.stream()
                .filter(e -> e.getEstadoEntrega() != null)
                .count();
        double tasaExito = enviosConEstado > 0
                ? (enviosEntregados * 100.0 / enviosConEstado)
                : 0;
        lblTasaExito.setText(String.format("%.1f%%", tasaExito));

        // Promedio por envío
        double promedioEnvio = totalEnvios > 0
                ? ingresosTotales / totalEnvios
                : 0;
        lblPromedioEnvio.setText(formatearMoneda(promedioEnvio));

        // Repartidores activos (disponibles)
        long repartidoresActivos = personas.stream()
                .filter(p -> p instanceof Dealer)
                .map(p -> (Dealer) p)
                .filter(Dealer::isDisponible)
                .count();
        lblRepartidoresActivos.setText(String.valueOf(repartidoresActivos));
    }

    /**
     * Genera el gráfico de pastel con la distribución de estados de entrega
     */
    private void cargarGraficoEstadoEntrega() {
        ArrayList<Envio> envios = sameDay.getListEnvios();

        // Contar envíos por estado
        Map<String, Long> conteoEstados = new HashMap<>();

        // Inicializar con todos los estados posibles
        conteoEstados.put("Sin Asignar", 0L);
        for (EstadoEntrega estado : EstadoEntrega.values()) {
            conteoEstados.put(estado.getDisplayName(), 0L);
        }

        // Contar
        for (Envio envio : envios) {
            if (envio.getEstadoEntrega() == null) {
                conteoEstados.put("Sin Asignar", conteoEstados.get("Sin Asignar") + 1);
            } else {
                String nombreEstado = envio.getEstadoEntrega().getDisplayName();
                conteoEstados.put(nombreEstado, conteoEstados.get(nombreEstado) + 1);
            }
        }

        // Crear datos para el gráfico
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Long> entry : conteoEstados.entrySet()) {
            if (entry.getValue() > 0) {
                pieData.add(new PieChart.Data(
                        entry.getKey() + " (" + entry.getValue() + ")",
                        entry.getValue()
                ));
            }
        }

        pieEstadoEntrega.setData(pieData);
        pieEstadoEntrega.setTitle("Distribución por Estado de Entrega");
        pieEstadoEntrega.setLabelsVisible(true);
        pieEstadoEntrega.setLegendVisible(true);
    }

    /**
     * Genera el gráfico de barras con los servicios adicionales más solicitados
     */
    private void cargarGraficoServiciosAdicionales() {
        ArrayList<Envio> envios = sameDay.getListEnvios();

        // Contar servicios
        Map<ServicioAdicional, Long> conteoServicios = new HashMap<>();
        for (ServicioAdicional servicio : ServicioAdicional.values()) {
            conteoServicios.put(servicio, 0L);
        }

        for (Envio envio : envios) {
            for (ServicioAdicional servicio : envio.getServiciosAdicionales()) {
                conteoServicios.put(servicio, conteoServicios.get(servicio) + 1);
            }
        }

        // Crear serie de datos
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Cantidad de Solicitudes");

        for (ServicioAdicional servicio : ServicioAdicional.values()) {
            series.getData().add(new XYChart.Data<>(
                    servicio.toString(),
                    conteoServicios.get(servicio)
            ));
        }

        barServiciosAdicionales.getData().clear();
        barServiciosAdicionales.getData().add(series);
        barServiciosAdicionales.setTitle("Servicios Adicionales Más Solicitados");
        xAxisServicios.setLabel("Servicio");
        yAxisServicios.setLabel("Cantidad");
    }

    /**
     * Genera el gráfico de líneas con la tendencia de envíos por día
     */
    private void cargarGraficoEnviosPorDia() {
        ArrayList<Envio> envios = sameDay.getListEnvios();

        // Agrupar envíos por fecha
        Map<LocalDate, Long> enviosPorDia = envios.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getFechaCreacion().toLocalDate(),
                        TreeMap::new,
                        Collectors.counting()
                ));

        // Crear serie de datos
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Envíos Creados");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (Map.Entry<LocalDate, Long> entry : enviosPorDia.entrySet()) {
            series.getData().add(new XYChart.Data<>(
                    entry.getKey().format(formatter),
                    entry.getValue()
            ));
        }

        lineEnviosPorDia.getData().clear();
        lineEnviosPorDia.getData().add(series);
        lineEnviosPorDia.setTitle("Tendencia de Envíos por Día");
        xAxisDias.setLabel("Fecha");
        yAxisDias.setLabel("Cantidad de Envíos");
    }

    /**
     * Genera el gráfico de barras con ingresos por ciudad de destino
     */
    private void cargarGraficoIngresosPorCiudad() {
        ArrayList<Envio> envios = sameDay.getListEnvios();

        // Agrupar ingresos por ciudad de destino
        Map<City, Double> ingresosPorCiudad = new HashMap<>();
        for (City ciudad : City.values()) {
            ingresosPorCiudad.put(ciudad, 0.0);
        }

        for (Envio envio : envios) {
            if (envio.getDestino() != null) {
                City ciudadDestino = envio.getDestino().getCity();
                ingresosPorCiudad.put(
                        ciudadDestino,
                        ingresosPorCiudad.get(ciudadDestino) + envio.getCostoTotal()
                );
            }
        }

        // Ordenar por ingresos (mayor a menor) y tomar top 5
        List<Map.Entry<City, Double>> topCiudades = ingresosPorCiudad.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .sorted(Map.Entry.<City, Double>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        // Crear serie de datos
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ingresos ($)");

        for (Map.Entry<City, Double> entry : topCiudades) {
            series.getData().add(new XYChart.Data<>(
                    entry.getKey().toString(),
                    entry.getValue()
            ));
        }

        barIngresosPorCiudad.getData().clear();
        barIngresosPorCiudad.getData().add(series);
        barIngresosPorCiudad.setTitle("Top 5 Ciudades por Ingresos");
        xAxisCiudades.setLabel("Ciudad");
        yAxisCiudades.setLabel("Ingresos ($)");
    }

    /**
     * Formatea un valor numérico como moneda colombiana
     */
    private String formatearMoneda(double valor) {
        return String.format("$%,.0f", valor);
    }

    /**
     * Método público para refrescar todas las estadísticas
     * Puede ser llamado desde otros controladores si es necesario
     */
    public void refrescarEstadisticas() {
        cargarTodasLasEstadisticas();
    }
}