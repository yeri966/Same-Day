package co.edu.uniquindio.sameday.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un envío en el sistema SameDay
 *
 * PATRONES APLICADOS:
 * - Builder: Para construcción fluida y legible de envíos complejos
 * - Decorator: Para servicios adicionales (en conjunto con ServicioDecorator)
 * - Facade: Para simplificar operaciones (EnvioFacade)
 */
public class Envio {
    private String id;
    private Address origen;
    private Address destino;
    private double peso;
    private String dimensiones;
    private double volumen;
    private String contenido;

    // Información del destinatario
    private String nombreDestinatario;
    private String telefonoDestinatario;
    private String cedulaDestinatario;

    private List<ServicioAdicional> serviciosAdicionales;
    private double costoTotal;
    private LocalDateTime fechaCreacion;
    private String estado; // Estado del pago: SOLICITADO, PAGADO

    // Asignación de repartidor
    private Dealer repartidorAsignado;

    // Estado de la entrega (para el flujo del repartidor)
    private EstadoEntrega estadoEntrega; // ASIGNADO, RECOGIDO, EN_RUTA, ENTREGADO, CON_INCIDENCIA
    private String observaciones; // Notas del repartidor sobre el envío o incidencias
    private LocalDateTime fechaActualizacionEstado; // Última vez que se actualizó el estado

    /**
     * Constructor por defecto
     * Inicializa valores por defecto para un envío nuevo
     */
    public Envio() {
        this.serviciosAdicionales = new ArrayList<>();
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "SOLICITADO";
        this.estadoEntrega = null;
        this.observaciones = "";
    }

    /**
     * Constructor con parámetros básicos
     * Mantiene compatibilidad con código existente
     */
    public Envio(String id, Address origen, Address destino, double peso,
                 String dimensiones, double volumen, String contenido) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
        this.dimensiones = dimensiones;
        this.volumen = volumen;
        this.contenido = contenido;
        this.serviciosAdicionales = new ArrayList<>();
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "SOLICITADO";
        this.estadoEntrega = null;
        this.observaciones = "";
    }

    // ==================== GETTERS Y SETTERS ====================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Address getOrigen() { return origen; }
    public void setOrigen(Address origen) { this.origen = origen; }

    public Address getDestino() { return destino; }
    public void setDestino(Address destino) { this.destino = destino; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public String getDimensiones() { return dimensiones; }
    public void setDimensiones(String dimensiones) { this.dimensiones = dimensiones; }

    public double getVolumen() { return volumen; }
    public void setVolumen(double volumen) { this.volumen = volumen; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    // Getters y Setters para información del destinatario
    public String getNombreDestinatario() { return nombreDestinatario; }
    public void setNombreDestinatario(String nombreDestinatario) {
        this.nombreDestinatario = nombreDestinatario;
    }

    public String getTelefonoDestinatario() { return telefonoDestinatario; }
    public void setTelefonoDestinatario(String telefonoDestinatario) {
        this.telefonoDestinatario = telefonoDestinatario;
    }

    public String getCedulaDestinatario() { return cedulaDestinatario; }
    public void setCedulaDestinatario(String cedulaDestinatario) {
        this.cedulaDestinatario = cedulaDestinatario;
    }

    public List<ServicioAdicional> getServiciosAdicionales() { return serviciosAdicionales; }
    public void setServiciosAdicionales(List<ServicioAdicional> serviciosAdicionales) {
        this.serviciosAdicionales = serviciosAdicionales;
    }

    public void addServicioAdicional(ServicioAdicional servicio) {
        if (!this.serviciosAdicionales.contains(servicio)) {
            this.serviciosAdicionales.add(servicio);
        }
    }

    public double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(double costoTotal) { this.costoTotal = costoTotal; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // Getters y Setters para repartidor asignado
    public Dealer getRepartidorAsignado() { return repartidorAsignado; }

    public void setRepartidorAsignado(Dealer repartidorAsignado) {
        this.repartidorAsignado = repartidorAsignado;
        // Solo establecer ASIGNADO si no hay estado previo
        if (repartidorAsignado != null && this.estadoEntrega == null) {
            this.estadoEntrega = EstadoEntrega.ASIGNADO;
            this.fechaActualizacionEstado = LocalDateTime.now();
        }
    }

    // Getters y Setters para el estado de entrega
    public EstadoEntrega getEstadoEntrega() { return estadoEntrega; }

    public void setEstadoEntrega(EstadoEntrega estadoEntrega) {
        this.estadoEntrega = estadoEntrega;
        this.fechaActualizacionEstado = LocalDateTime.now();
    }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFechaActualizacionEstado() { return fechaActualizacionEstado; }
    public void setFechaActualizacionEstado(LocalDateTime fechaActualizacionEstado) {
        this.fechaActualizacionEstado = fechaActualizacionEstado;
    }

    /**
     * Obtiene una representación de los servicios adicionales como String
     */
    public String getServiciosAdicionalesString() {
        if (serviciosAdicionales.isEmpty()) {
            return "Ninguno";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < serviciosAdicionales.size(); i++) {
            sb.append(serviciosAdicionales.get(i).toString());
            if (i < serviciosAdicionales.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * Obtiene el estado de entrega como String PARA LA UI (con emojis)
     */
    public String getEstadoEntregaString() {
        if (estadoEntrega == null) {
            return "Sin asignar";
        }
        return estadoEntrega.toString(); // Con emojis para la UI
    }

    /**
     * Obtiene el estado de entrega SIN EMOJIS para usar en PDFs
     * ✅ MÉTODO NUEVO - Soluciona el error de fuente en PDFs
     */
    public String getEstadoEntregaStringPlain() {
        if (estadoEntrega == null) {
            return "Sin asignar";
        }
        return estadoEntrega.getDisplayName(); // Sin emojis, solo texto
    }

    @Override
    public String toString() {
        return "Envio{" +
                "id='" + id + '\'' +
                ", origen=" + (origen != null ? origen.getAlias() : "null") +
                ", destino=" + (destino != null ? destino.getAlias() : "null") +
                ", nombreDestinatario='" + nombreDestinatario + '\'' +
                ", peso=" + peso +
                ", contenido='" + contenido + '\'' +
                ", costoTotal=" + costoTotal +
                ", estado='" + estado + '\'' +
                ", estadoEntrega=" + (estadoEntrega != null ? estadoEntrega.getDisplayName() : "null") +
                ", repartidorAsignado=" + (repartidorAsignado != null ? repartidorAsignado.getNombre() : "null") +
                '}';
    }
}