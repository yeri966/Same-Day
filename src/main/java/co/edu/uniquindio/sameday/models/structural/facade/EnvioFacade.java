package co.edu.uniquindio.sameday.models.structural.facade;

import co.edu.uniquindio.sameday.models.*;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
import co.edu.uniquindio.sameday.models.creational.builder.EnvioBuilder;
import co.edu.uniquindio.sameday.models.structural.decorator.*;

import java.util.ArrayList;
import java.util.List;

/**
 * PATRÓN ESTRUCTURAL: FACADE
 *
 * Proporciona una interfaz simplificada para la gestión completa de envíos.
 * Coordina las operaciones entre múltiples subsistemas:
 * - Validación de datos
 * - Cálculo de costos (usando Decorator)
 * - Gestión de envíos (usando Singleton)
 * - Generación de IDs
 * - Construcción de envíos (usando Builder) ✨ NUEVO
 *
 * Simplifica la complejidad para los controladores de la interfaz.
 */
public class EnvioFacade {

    private SameDay sameDay;

    public EnvioFacade() {
        this.sameDay = SameDay.getInstance();
    }

    /**
     * Valida todos los datos necesarios para crear un envío
     */
    public ResultadoOperacion validarDatosEnvio(Address origen, Address destino,
                                                String contenido, double peso,
                                                String dimensiones, double volumen) {

        // Validar dirección de origen
        if (origen == null) {
            return ResultadoOperacion.error("Debe seleccionar una dirección de origen");
        }

        // Validar dirección de destino
        if (destino == null) {
            return ResultadoOperacion.error("Debe seleccionar una dirección de destino");
        }

        // Validar que origen y destino no sean iguales
        if (origen.getId().equals(destino.getId())) {
            return ResultadoOperacion.error("La dirección de origen y destino no pueden ser la misma");
        }

        // Validar contenido
        if (contenido == null || contenido.trim().isEmpty()) {
            return ResultadoOperacion.error("Debe ingresar el contenido del paquete");
        }

        // Validar peso
        if (peso <= 0) {
            return ResultadoOperacion.error("El peso debe ser mayor a 0 kg");
        }

        if (peso > 100) {
            return ResultadoOperacion.error("El peso no puede superar los 100 kg");
        }

        // Validar dimensiones
        if (dimensiones == null || dimensiones.trim().isEmpty()) {
            return ResultadoOperacion.error("Debe ingresar las dimensiones del paquete");
        }

        // Validar volumen
        if (volumen <= 0) {
            return ResultadoOperacion.error("El volumen debe ser mayor a 0 cm³");
        }

        return ResultadoOperacion.exito("Datos validados correctamente");
    }

    /**
     * Calcula el costo total del envío aplicando servicios adicionales
     * Utiliza el patrón Decorator para agregar costos incrementales
     */
    public ResultadoOperacion calcularCostoEnvio(double peso,
                                                 List<ServicioAdicional> serviciosAdicionales) {

        try {
            // Crear el envío básico
            EnvioComponent envio = new EnvioBasico(peso);

            // Aplicar decoradores para cada servicio adicional
            if (serviciosAdicionales != null) {
                for (ServicioAdicional servicio : serviciosAdicionales) {
                    envio = aplicarServicioAdicional(envio, servicio);
                }
            }

            // Calcular el costo total
            double costoTotal = envio.calcularCosto();
            String descripcion = envio.getDescripcion();

            // Crear objeto de respuesta con el costo y descripción
            CotizacionResult resultado = new CotizacionResult(costoTotal, descripcion);

            return ResultadoOperacion.exitoConDato(
                    String.format("Cotización calculada: $%,.0f", costoTotal),
                    resultado
            );

        } catch (Exception e) {
            return ResultadoOperacion.error("Error al calcular el costo: " + e.getMessage());
        }
    }

    /**
     * Crea un nuevo envío completo en el sistema
     * AHORA USA BUILDER para construcción fluida y validada ✨
     *
     * @param origen Dirección de origen
     * @param destino Dirección de destino
     * @param contenido Descripción del contenido
     * @param peso Peso en kg
     * @param dimensiones Dimensiones del paquete
     * @param volumen Volumen en cm³
     * @param serviciosAdicionales Lista de servicios adicionales
     * @param costoTotal Costo total pre-calculado
     * @param nombreDestinatario Nombre del destinatario ✨ NUEVO
     * @param cedulaDestinatario Cédula del destinatario ✨ NUEVO
     * @param telefonoDestinatario Teléfono del destinatario ✨ NUEVO
     * @return ResultadoOperacion indicando el resultado de la operación
     */
    public ResultadoOperacion crearEnvio(Address origen, Address destino,
                                         String contenido, double peso,
                                         String dimensiones, double volumen,
                                         List<ServicioAdicional> serviciosAdicionales,
                                         double costoTotal,
                                         String nombreDestinatario,
                                         String cedulaDestinatario,
                                         String telefonoDestinatario) {

        // 1. Validar que el costo fue calculado
        if (costoTotal <= 0) {
            return ResultadoOperacion.error("Debe cotizar el envío antes de crearlo");
        }

        try {
            // 2. Generar ID único
            String idEnvio = generarIdEnvio();

            // 3. USAR BUILDER para crear el envío ✨
            //    El Builder se encarga de todas las validaciones INCLUYENDO destinatario
            Envio nuevoEnvio = new EnvioBuilder(idEnvio)
                    .origen(origen)
                    .destino(destino)
                    .contenido(contenido)
                    .peso(peso)
                    .dimensiones(dimensiones)
                    .volumen(volumen)
                    .serviciosAdicionales(serviciosAdicionales != null ? serviciosAdicionales : new ArrayList<>())
                    .costoTotal(costoTotal)
                    .destinatario(nombreDestinatario, cedulaDestinatario, telefonoDestinatario) // ✨ AGREGADO
                    .build(); // build() valida automáticamente TODO

            // 4. Registrar en el sistema
            sameDay.addEnvio(nuevoEnvio);

            return ResultadoOperacion.exitoConDato(
                    String.format("Envío %s creado exitosamente", idEnvio),
                    nuevoEnvio
            );

        } catch (IllegalStateException e) {
            // Errores de validación del Builder
            return ResultadoOperacion.error(e.getMessage());
        } catch (Exception e) {
            return ResultadoOperacion.error("Error al crear el envío: " + e.getMessage());
        }
    }

    /**
     * Actualiza un envío existente
     * AHORA USA BUILDER para construcción fluida y validada ✨
     */
    public ResultadoOperacion actualizarEnvio(String envioId, Address origen, Address destino,
                                              String contenido, double peso,
                                              String dimensiones, double volumen,
                                              List<ServicioAdicional> serviciosAdicionales,
                                              double costoTotal,
                                              String nombreDestinatario,
                                              String cedulaDestinatario,
                                              String telefonoDestinatario) {

        // 1. Buscar el envío
        Envio envioExistente = buscarEnvioPorId(envioId);
        if (envioExistente == null) {
            return ResultadoOperacion.error("Envío no encontrado: " + envioId);
        }

        // 2. Verificar costo
        if (costoTotal <= 0) {
            return ResultadoOperacion.error("Debe cotizar el envío antes de actualizarlo");
        }

        try {
            // 3. USAR BUILDER para reconstruir el envío con las validaciones ✨
            Envio envioActualizado = new EnvioBuilder(envioId)
                    .origen(origen)
                    .destino(destino)
                    .contenido(contenido)
                    .peso(peso)
                    .dimensiones(dimensiones)
                    .volumen(volumen)
                    .serviciosAdicionales(serviciosAdicionales != null ? serviciosAdicionales : new ArrayList<>())
                    .costoTotal(costoTotal)
                    .destinatario(nombreDestinatario, cedulaDestinatario, telefonoDestinatario) // ✨ AGREGADO
                    .fechaCreacion(envioExistente.getFechaCreacion())
                    .estado(envioExistente.getEstado())
                    .repartidorAsignado(envioExistente.getRepartidorAsignado())
                    .estadoEntrega(envioExistente.getEstadoEntrega())
                    .observaciones(envioExistente.getObservaciones())
                    .build();

            // 4. Copiar datos
            copiarDatosEnvio(envioActualizado, envioExistente);

            // 5. Guardar cambios
            sameDay.updateEnvio(envioExistente);

            return ResultadoOperacion.exitoConDato(
                    String.format("Envío %s actualizado exitosamente", envioId),
                    envioExistente
            );

        } catch (IllegalStateException e) {
            return ResultadoOperacion.error(e.getMessage());
        } catch (Exception e) {
            return ResultadoOperacion.error("Error al actualizar el envío: " + e.getMessage());
        }
    }

    /**
     * Elimina un envío del sistema
     */
    public ResultadoOperacion eliminarEnvio(String envioId) {

        Envio envio = buscarEnvioPorId(envioId);
        if (envio == null) {
            return ResultadoOperacion.error("Envío no encontrado: " + envioId);
        }

        // No permitir eliminar envíos ya pagados
        if ("PAGADO".equals(envio.getEstado())) {
            return ResultadoOperacion.error("No se puede eliminar un envío que ya fue pagado");
        }

        try {
            sameDay.deleteEnvio(envioId);
            return ResultadoOperacion.exito("Envío eliminado exitosamente");
        } catch (Exception e) {
            return ResultadoOperacion.error("Error al eliminar el envío: " + e.getMessage());
        }
    }

    /**
     * Obtiene la lista completa de envíos
     */
    public List<Envio> obtenerTodosLosEnvios() {
        return sameDay.getListEnvios();
    }

    /**
     * Genera un ID único para un nuevo envío
     */
    public String generarIdEnvio() {
        int count = sameDay.getListEnvios().size();
        return String.format("ENV%04d", count + 1);
    }

    // ==================== MÉTODOS PRIVADOS AUXILIARES ====================

    /**
     * Aplica un servicio adicional usando el patrón Decorator
     */
    private EnvioComponent aplicarServicioAdicional(EnvioComponent envio,
                                                    ServicioAdicional servicio) {
        switch (servicio) {
            case SEGURO:
                return new SeguroDecorator(envio);
            case FRAGIL:
                return new FragilDecorator(envio);
            case FIRMA_REQUERIDA:
                return new FirmaRequeridaDecorator(envio);
            case PRIORIDAD:
                return new PrioridadDecorator(envio);
            default:
                return envio;
        }
    }

    /**
     * Busca un envío por su ID
     */
    private Envio buscarEnvioPorId(String envioId) {
        for (Envio envio : sameDay.getListEnvios()) {
            if (envio.getId().equals(envioId)) {
                return envio;
            }
        }
        return null;
    }

    /**
     * Copia los datos de un envío a otro (mantiene la referencia del objeto original)
     * Útil para actualizar envíos sin perder la referencia en la lista
     */
    private void copiarDatosEnvio(Envio origen, Envio destino) {
        destino.setOrigen(origen.getOrigen());
        destino.setDestino(origen.getDestino());
        destino.setPeso(origen.getPeso());
        destino.setDimensiones(origen.getDimensiones());
        destino.setVolumen(origen.getVolumen());
        destino.setContenido(origen.getContenido());
        destino.setNombreDestinatario(origen.getNombreDestinatario());
        destino.setCedulaDestinatario(origen.getCedulaDestinatario());
        destino.setTelefonoDestinatario(origen.getTelefonoDestinatario());
        destino.setServiciosAdicionales(origen.getServiciosAdicionales());
        destino.setCostoTotal(origen.getCostoTotal());
        // No copiar fechas, estado de pago, repartidor ni estado de entrega
        // ya que se mantienen del envío original
    }

    // ==================== CLASE INTERNA AUXILIAR ====================

    /**
     * Clase auxiliar para retornar el resultado de una cotización
     */
    public static class CotizacionResult {
        private double costoTotal;
        private String descripcion;

        public CotizacionResult(double costoTotal, String descripcion) {
            this.costoTotal = costoTotal;
            this.descripcion = descripcion;
        }

        public double getCostoTotal() {
            return costoTotal;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }
}