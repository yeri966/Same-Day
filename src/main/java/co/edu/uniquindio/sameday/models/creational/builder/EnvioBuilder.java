package co.edu.uniquindio.sameday.models.creational.builder;

import co.edu.uniquindio.sameday.models.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PATRÓN CREACIONAL: BUILDER
 *
 * Builder para construir objetos Envio de forma fluida y legible.
 * Facilita la creación de envíos con múltiples atributos opcionales y validaciones.
 *
 * Ventajas:
 * - Código más legible y mantenible
 * - Validaciones centralizadas en build()
 * - Construcción paso a paso de objetos complejos
 * - Inmutabilidad opcional del objeto construido
 *
 * Uso:
 * Envio envio = new EnvioBuilder("ENV001")
 *     .origen(direccionOrigen)
 *     .destino(direccionDestino)
 *     .peso(5.5)
 *     .contenido("Electrónicos")
 *     .destinatario("Carlos", "123456", "3001234567")
 *     .agregarServicio(ServicioAdicional.SEGURO)
 *     .costoTotal(25000)
 *     .build();
 */
public class EnvioBuilder {

    // Atributo obligatorio
    private final String id;

    // Atributos opcionales con valores por defecto
    private Address origen;
    private Address destino;
    private double peso;
    private String dimensiones = "";
    private double volumen;
    private String contenido = "";

    // Información del destinatario
    private String nombreDestinatario;
    private String telefonoDestinatario;
    private String cedulaDestinatario;

    // Servicios y costos
    private List<ServicioAdicional> serviciosAdicionales = new ArrayList<>();
    private double costoTotal;

    // Metadatos del envío
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    private String estado = "SOLICITADO";

    // Asignación de repartidor y estado de entrega
    private Dealer repartidorAsignado;
    private EstadoEntrega estadoEntrega;
    private String observaciones = "";
    private LocalDateTime fechaActualizacionEstado;

    /**
     * Constructor del Builder - solo requiere el ID (obligatorio)
     *
     * @param id Identificador único del envío
     */
    public EnvioBuilder(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del envío no puede ser nulo o vacío");
        }
        this.id = id;
    }

    // ==================== MÉTODOS DE CONSTRUCCIÓN ====================

    /**
     * Establece la dirección de origen del envío
     */
    public EnvioBuilder origen(Address origen) {
        this.origen = origen;
        return this;
    }

    /**
     * Establece la dirección de destino del envío
     */
    public EnvioBuilder destino(Address destino) {
        this.destino = destino;
        return this;
    }

    /**
     * Establece el peso del paquete en kilogramos
     */
    public EnvioBuilder peso(double peso) {
        this.peso = peso;
        return this;
    }

    /**
     * Establece las dimensiones del paquete (ej: "30x40x50 cm")
     */
    public EnvioBuilder dimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
        return this;
    }

    /**
     * Establece el volumen del paquete en cm³
     */
    public EnvioBuilder volumen(double volumen) {
        this.volumen = volumen;
        return this;
    }

    /**
     * Establece la descripción del contenido del paquete
     */
    public EnvioBuilder contenido(String contenido) {
        this.contenido = contenido;
        return this;
    }

    /**
     * Establece la información completa del destinatario de una vez
     *
     * @param nombre Nombre completo del destinatario
     * @param cedula Número de cédula del destinatario
     * @param telefono Teléfono de contacto del destinatario
     */
    public EnvioBuilder destinatario(String nombre, String cedula, String telefono) {
        this.nombreDestinatario = nombre;
        this.cedulaDestinatario = cedula;
        this.telefonoDestinatario = telefono;
        return this;
    }

    /**
     * Establece solo el nombre del destinatario
     */
    public EnvioBuilder nombreDestinatario(String nombreDestinatario) {
        this.nombreDestinatario = nombreDestinatario;
        return this;
    }

    /**
     * Establece solo la cédula del destinatario
     */
    public EnvioBuilder cedulaDestinatario(String cedulaDestinatario) {
        this.cedulaDestinatario = cedulaDestinatario;
        return this;
    }

    /**
     * Establece solo el teléfono del destinatario
     */
    public EnvioBuilder telefonoDestinatario(String telefonoDestinatario) {
        this.telefonoDestinatario = telefonoDestinatario;
        return this;
    }

    /**
     * Agrega un servicio adicional a la lista de servicios del envío
     *
     * @param servicio Servicio adicional a agregar (SEGURO, FRAGIL, etc.)
     */
    public EnvioBuilder agregarServicio(ServicioAdicional servicio) {
        if (servicio != null && !this.serviciosAdicionales.contains(servicio)) {
            this.serviciosAdicionales.add(servicio);
        }
        return this;
    }

    /**
     * Establece la lista completa de servicios adicionales
     *
     * @param servicios Lista de servicios adicionales
     */
    public EnvioBuilder serviciosAdicionales(List<ServicioAdicional> servicios) {
        if (servicios != null) {
            this.serviciosAdicionales = new ArrayList<>(servicios);
        }
        return this;
    }

    /**
     * Establece el costo total del envío
     */
    public EnvioBuilder costoTotal(double costoTotal) {
        this.costoTotal = costoTotal;
        return this;
    }

    /**
     * Establece el estado del pago del envío
     *
     * @param estado "SOLICITADO" o "PAGADO"
     */
    public EnvioBuilder estado(String estado) {
        this.estado = estado;
        return this;
    }

    /**
     * Establece la fecha de creación del envío
     * (Por defecto es LocalDateTime.now())
     */
    public EnvioBuilder fechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
        return this;
    }

    /**
     * Asigna un repartidor al envío
     * Automáticamente establece el estado de entrega como ASIGNADO
     *
     * @param repartidorAsignado Repartidor que se encargará del envío
     */
    public EnvioBuilder repartidorAsignado(Dealer repartidorAsignado) {
        this.repartidorAsignado = repartidorAsignado;
        if (repartidorAsignado != null && this.estadoEntrega == null) {
            this.estadoEntrega = EstadoEntrega.ASIGNADO;
            this.fechaActualizacionEstado = LocalDateTime.now();
        }
        return this;
    }

    /**
     * Establece el estado de entrega del envío
     * (ASIGNADO, RECOGIDO, EN_RUTA, ENTREGADO, CON_INCIDENCIA)
     */
    public EnvioBuilder estadoEntrega(EstadoEntrega estadoEntrega) {
        this.estadoEntrega = estadoEntrega;
        this.fechaActualizacionEstado = LocalDateTime.now();
        return this;
    }

    /**
     * Establece observaciones o notas sobre el envío
     */
    public EnvioBuilder observaciones(String observaciones) {
        this.observaciones = observaciones;
        return this;
    }

    // ==================== MÉTODO BUILD ====================

    /**
     * Construye y valida el objeto Envio
     *
     * @return Envio construido y validado
     * @throws IllegalStateException si faltan campos obligatorios o hay datos inválidos
     */
    public Envio build() {
        // Validar campos obligatorios
        validar();

        // Crear el objeto Envio
        Envio envio = new Envio();

        // Asignar todos los valores
        envio.setId(this.id);
        envio.setOrigen(this.origen);
        envio.setDestino(this.destino);
        envio.setPeso(this.peso);
        envio.setDimensiones(this.dimensiones);
        envio.setVolumen(this.volumen);
        envio.setContenido(this.contenido);

        envio.setNombreDestinatario(this.nombreDestinatario);
        envio.setCedulaDestinatario(this.cedulaDestinatario);
        envio.setTelefonoDestinatario(this.telefonoDestinatario);

        envio.setServiciosAdicionales(this.serviciosAdicionales);
        envio.setCostoTotal(this.costoTotal);
        envio.setFechaCreacion(this.fechaCreacion);
        envio.setEstado(this.estado);

        envio.setRepartidorAsignado(this.repartidorAsignado);
        envio.setEstadoEntrega(this.estadoEntrega);
        envio.setObservaciones(this.observaciones);
        envio.setFechaActualizacionEstado(this.fechaActualizacionEstado);

        return envio;
    }

    // ==================== VALIDACIONES ====================

    /**
     * Valida que todos los campos obligatorios estén presentes y sean válidos
     *
     * @throws IllegalStateException si hay errores de validación
     */
    private void validar() {
        StringBuilder errores = new StringBuilder();

        // Validar dirección de origen
        if (origen == null) {
            errores.append("• La dirección de origen es obligatoria\n");
        }

        // Validar dirección de destino
        if (destino == null) {
            errores.append("• La dirección de destino es obligatoria\n");
        }

        // Validar que origen y destino no sean iguales
        if (origen != null && destino != null && origen.getId().equals(destino.getId())) {
            errores.append("• La dirección de origen y destino no pueden ser la misma\n");
        }

        // Validar peso
        if (peso <= 0) {
            errores.append("• El peso debe ser mayor a 0 kg\n");
        }
        if (peso > 100) {
            errores.append("• El peso no puede superar los 100 kg\n");
        }

        // Validar volumen
        if (volumen <= 0) {
            errores.append("• El volumen debe ser mayor a 0 cm³\n");
        }

        // Validar contenido
        if (contenido == null || contenido.trim().isEmpty()) {
            errores.append("• La descripción del contenido es obligatoria\n");
        }

        // Validar información del destinatario
        if (nombreDestinatario == null || nombreDestinatario.trim().isEmpty()) {
            errores.append("• El nombre del destinatario es obligatorio\n");
        }

        if (cedulaDestinatario == null || cedulaDestinatario.trim().isEmpty()) {
            errores.append("• La cédula del destinatario es obligatoria\n");
        } else if (!cedulaDestinatario.matches("\\d+")) {
            errores.append("• La cédula debe contener solo números\n");
        }

        if (telefonoDestinatario == null || telefonoDestinatario.trim().isEmpty()) {
            errores.append("• El teléfono del destinatario es obligatorio\n");
        } else if (!telefonoDestinatario.matches("\\d{10}")) {
            errores.append("• El teléfono debe tener 10 dígitos\n");
        }

        // Validar costo total
        if (costoTotal <= 0) {
            errores.append("• El costo total debe ser mayor a 0\n");
        }

        // Si hay errores, lanzar excepción
        if (errores.length() > 0) {
            throw new IllegalStateException("Error al construir el Envio:\n" + errores.toString());
        }
    }

    // ==================== MÉTODOS DE UTILIDAD ====================

    /**
     * Crea un EnvioBuilder a partir de un Envio existente
     * Útil para clonar o modificar envíos existentes
     *
     * @param envio Envio del cual copiar los valores
     * @return Builder con los valores del envío original
     */
    public static EnvioBuilder fromEnvio(Envio envio) {
        if (envio == null) {
            throw new IllegalArgumentException("El envío no puede ser nulo");
        }

        return new EnvioBuilder(envio.getId())
                .origen(envio.getOrigen())
                .destino(envio.getDestino())
                .peso(envio.getPeso())
                .dimensiones(envio.getDimensiones())
                .volumen(envio.getVolumen())
                .contenido(envio.getContenido())
                .nombreDestinatario(envio.getNombreDestinatario())
                .cedulaDestinatario(envio.getCedulaDestinatario())
                .telefonoDestinatario(envio.getTelefonoDestinatario())
                .serviciosAdicionales(envio.getServiciosAdicionales())
                .costoTotal(envio.getCostoTotal())
                .fechaCreacion(envio.getFechaCreacion())
                .estado(envio.getEstado())
                .repartidorAsignado(envio.getRepartidorAsignado())
                .estadoEntrega(envio.getEstadoEntrega())
                .observaciones(envio.getObservaciones());
    }

    @Override
    public String toString() {
        return "EnvioBuilder{" +
                "id='" + id + '\'' +
                ", origen=" + (origen != null ? origen.getAlias() : "null") +
                ", destino=" + (destino != null ? destino.getAlias() : "null") +
                ", nombreDestinatario='" + nombreDestinatario + '\'' +
                ", peso=" + peso +
                ", contenido='" + contenido + '\'' +
                ", costoTotal=" + costoTotal +
                ", servicios=" + serviciosAdicionales.size() +
                '}';
    }
}