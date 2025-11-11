package co.edu.uniquindio.sameday.models.structural.facade;

import co.edu.uniquindio.sameday.models.*;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;
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
     *
     * @param origen Dirección de origen
     * @param destino Dirección de destino
     * @param contenido Descripción del contenido
     * @param peso Peso del paquete en kg
     * @param dimensiones Dimensiones del paquete
     * @param volumen Volumen del paquete en cm³
     * @return ResultadoOperacion indicando si la validación fue exitosa
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
     *
     * @param peso Peso del paquete
     * @param serviciosAdicionales Lista de servicios adicionales a aplicar
     * @return ResultadoOperacion con el costo calculado
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
     * Coordina la validación, cálculo de costo y registro
     *
     * @param origen Dirección de origen
     * @param destino Dirección de destino
     * @param contenido Descripción del contenido
     * @param peso Peso en kg
     * @param dimensiones Dimensiones del paquete
     * @param volumen Volumen en cm³
     * @param serviciosAdicionales Lista de servicios adicionales
     * @param costoTotal Costo total pre-calculado
     * @return ResultadoOperacion indicando el resultado de la operación
     */
    public ResultadoOperacion crearEnvio(Address origen, Address destino,
                                         String contenido, double peso,
                                         String dimensiones, double volumen,
                                         List<ServicioAdicional> serviciosAdicionales,
                                         double costoTotal) {

        // 1. Validar datos
        ResultadoOperacion validacion = validarDatosEnvio(origen, destino, contenido,
                peso, dimensiones, volumen);
        if (!validacion.isExitoso()) {
            return validacion;
        }

        // 2. Verificar que el costo fue calculado
        if (costoTotal <= 0) {
            return ResultadoOperacion.error("Debe cotizar el envío antes de crearlo");
        }

        try {
            // 3. Generar ID único
            String idEnvio = generarIdEnvio();

            // 4. Crear el objeto Envio
            Envio nuevoEnvio = new Envio();
            nuevoEnvio.setId(idEnvio);
            nuevoEnvio.setOrigen(origen);
            nuevoEnvio.setDestino(destino);
            nuevoEnvio.setContenido(contenido);
            nuevoEnvio.setPeso(peso);
            nuevoEnvio.setDimensiones(dimensiones);
            nuevoEnvio.setVolumen(volumen);
            nuevoEnvio.setServiciosAdicionales(
                    serviciosAdicionales != null ? new ArrayList<>(serviciosAdicionales) : new ArrayList<>()
            );
            nuevoEnvio.setCostoTotal(costoTotal);

            // 5. Registrar en el sistema
            sameDay.addEnvio(nuevoEnvio);

            return ResultadoOperacion.exitoConDato(
                    String.format("Envío %s creado exitosamente", idEnvio),
                    nuevoEnvio
            );

        } catch (Exception e) {
            return ResultadoOperacion.error("Error al crear el envío: " + e.getMessage());
        }
    }

    /**
     * Actualiza un envío existente
     *
     * @param envioId ID del envío a actualizar
     * @param origen Nueva dirección de origen
     * @param destino Nueva dirección de destino
     * @param contenido Nuevo contenido
     * @param peso Nuevo peso
     * @param dimensiones Nuevas dimensiones
     * @param volumen Nuevo volumen
     * @param serviciosAdicionales Nuevos servicios
     * @param costoTotal Nuevo costo total
     * @return ResultadoOperacion indicando el resultado
     */
    public ResultadoOperacion actualizarEnvio(String envioId, Address origen, Address destino,
                                              String contenido, double peso,
                                              String dimensiones, double volumen,
                                              List<ServicioAdicional> serviciosAdicionales,
                                              double costoTotal) {

        // 1. Buscar el envío
        Envio envioExistente = buscarEnvioPorId(envioId);
        if (envioExistente == null) {
            return ResultadoOperacion.error("Envío no encontrado: " + envioId);
        }

        // 2. Validar datos
        ResultadoOperacion validacion = validarDatosEnvio(origen, destino, contenido,
                peso, dimensiones, volumen);
        if (!validacion.isExitoso()) {
            return validacion;
        }

        // 3. Verificar costo
        if (costoTotal <= 0) {
            return ResultadoOperacion.error("Debe cotizar el envío antes de actualizarlo");
        }

        try {
            // 4. Actualizar campos
            envioExistente.setOrigen(origen);
            envioExistente.setDestino(destino);
            envioExistente.setContenido(contenido);
            envioExistente.setPeso(peso);
            envioExistente.setDimensiones(dimensiones);
            envioExistente.setVolumen(volumen);
            envioExistente.setServiciosAdicionales(
                    serviciosAdicionales != null ? new ArrayList<>(serviciosAdicionales) : new ArrayList<>()
            );
            envioExistente.setCostoTotal(costoTotal);

            // 5. Guardar cambios
            sameDay.updateEnvio(envioExistente);

            return ResultadoOperacion.exitoConDato(
                    String.format("Envío %s actualizado exitosamente", envioId),
                    envioExistente
            );

        } catch (Exception e) {
            return ResultadoOperacion.error("Error al actualizar el envío: " + e.getMessage());
        }
    }

    /**
     * Elimina un envío del sistema
     *
     * @param envioId ID del envío a eliminar
     * @return ResultadoOperacion indicando el resultado
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
     *
     * @return Lista de envíos
     */
    public List<Envio> obtenerTodosLosEnvios() {
        return sameDay.getListEnvios();
    }

    /**
     * Genera un ID único para un nuevo envío
     *
     * @return ID generado con formato ENV0001, ENV0002, etc.
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