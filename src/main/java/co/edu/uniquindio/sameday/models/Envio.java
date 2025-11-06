package co.edu.uniquindio.sameday.models;

import co.edu.uniquindio.sameday.models.ServicioAdicional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un envío en el sistema de logística
 */
public class Envio {
    private String id;
    private Address origen;
    private Address destino;
    private double peso;
    private String dimensiones;
    private double volumen;
    private List<ServicioAdicional> serviciosAdicionales;
    private double costoTotal;
    private LocalDateTime fechaCreacion;
    private String estado;

    public Envio() {
        this.serviciosAdicionales = new ArrayList<>();
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "SOLICITADO";
    }

    public Envio(String id, Address origen, Address destino, double peso,
                 String dimensiones, double volumen) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
        this.dimensiones = dimensiones;
        this.volumen = volumen;
        this.serviciosAdicionales = new ArrayList<>();
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "SOLICITADO";
    }

    // Getters y Setters
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

    @Override
    public String toString() {
        return "Envio{" +
                "id='" + id + '\'' +
                ", origen=" + (origen != null ? origen.getAlias() : "null") +
                ", destino=" + (destino != null ? destino.getAlias() : "null") +
                ", peso=" + peso +
                ", costoTotal=" + costoTotal +
                ", estado='" + estado + '\'' +
                '}';
    }
}