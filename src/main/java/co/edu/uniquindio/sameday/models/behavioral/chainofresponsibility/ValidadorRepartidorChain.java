package co.edu.uniquindio.sameday.models.behavioral.chainofresponsibility;

import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.Envio;

/**
 * PATRÓN COMPORTAMENTAL: CHAIN OF RESPONSIBILITY
 *
 * Clase que construye y administra la cadena de validadores de repartidor.
 * Facilita el uso del patrón al encapsular la creación y configuración
 * de la cadena completa.
 *
 * Orden de la cadena:
 * 1. ValidadorDisponibilidadManual - Verifica que no esté marcado como no disponible
 * 2. ValidadorCargaMaxima - Verifica que no tenga demasiados envíos activos
 * 3. ValidadorZonaCobertura - Verifica que cubra la zona del destino
 */
public class ValidadorRepartidorChain {

    private ValidadorRepartidor primerValidador;

    /**
     * Constructor que inicializa la cadena de validadores
     */
    public ValidadorRepartidorChain() {
        construirCadena();
    }

    /**
     * Construye la cadena de responsabilidad enlazando los validadores
     */
    private void construirCadena() {
        // Crear los validadores
        primerValidador = new ValidadorDisponibilidadManual();
        ValidadorRepartidor validadorCarga = new ValidadorCargaMaxima();
        ValidadorRepartidor validadorZona = new ValidadorZonaCobertura();

        // Enlazar la cadena
        primerValidador
                .linkWith(validadorCarga)
                .linkWith(validadorZona);

        System.out.println("✅ Cadena de validación de repartidor construida:");
        System.out.println("   1. ValidadorDisponibilidadManual");
        System.out.println("   2. ValidadorCargaMaxima");
        System.out.println("   3. ValidadorZonaCobertura");
    }

    /**
     * Ejecuta la cadena de validación completa
     * @param repartidor El repartidor a validar
     * @param envio El envío que se quiere asignar
     * @return ResultadoValidacion con el resultado de toda la cadena
     */
    public ResultadoValidacion validar(Dealer repartidor, Envio envio) {
        System.out.println("\n========================================");
        System.out.println("🚀 INICIANDO CADENA DE VALIDACIÓN");
        System.out.println("   Repartidor: " + repartidor.getNombre());
        System.out.println("   Envío: " + envio.getId());
        System.out.println("========================================");

        ResultadoValidacion resultado = primerValidador.validar(repartidor, envio);

        System.out.println("========================================");
        if (resultado.isValido()) {
            System.out.println("✅ RESULTADO FINAL: VALIDACIÓN EXITOSA");
        } else {
            System.out.println("❌ RESULTADO FINAL: VALIDACIÓN FALLIDA");
            System.out.println("   Motivo: " + resultado.getMensaje());
        }
        System.out.println("========================================\n");

        return resultado;
    }

    /**
     * Permite agregar un validador personalizado al final de la cadena
     * @param validador Nuevo validador a agregar
     */
    public void agregarValidador(ValidadorRepartidor validador) {
        if (primerValidador == null) {
            primerValidador = validador;
        } else {
            // Recorrer hasta el final de la cadena
            ValidadorRepartidor actual = primerValidador;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.setSiguiente(validador);
        }
    }
}