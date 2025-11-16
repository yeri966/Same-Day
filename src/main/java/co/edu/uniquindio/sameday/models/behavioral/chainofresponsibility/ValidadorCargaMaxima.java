package co.edu.uniquindio.sameday.models.behavioral.chainofresponsibility;

import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.Envio;
import co.edu.uniquindio.sameday.models.EstadoEntrega;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;

/**
 * PATRÓN COMPORTAMENTAL: CHAIN OF RESPONSIBILITY
 *
 * Validador concreto que verifica si el repartidor no ha excedido
 * su carga máxima de envíos activos. Un repartidor no puede tener
 * más de CARGA_MAXIMA envíos sin entregar.
 */
public class ValidadorCargaMaxima extends ValidadorRepartidor {

    private static final int CARGA_MAXIMA = 5; // Máximo 5 envíos activos por repartidor

    @Override
    public ResultadoValidacion validar(Dealer repartidor, Envio envio) {
        System.out.println("🔍 Validando carga máxima de: " + repartidor.getNombre());

        SameDay sameDay = SameDay.getInstance();

        // Contar envíos activos (no entregados) del repartidor
        long enviosActivos = sameDay.getListEnvios().stream()
                .filter(e -> e.getRepartidorAsignado() != null)
                .filter(e -> e.getRepartidorAsignado().getId().equals(repartidor.getId()))
                .filter(e -> e.getEstadoEntrega() != null &&
                        e.getEstadoEntrega() != EstadoEntrega.ENTREGADO)
                .count();

        System.out.println("   Envíos activos: " + enviosActivos + "/" + CARGA_MAXIMA);

        if (enviosActivos >= CARGA_MAXIMA) {
            System.out.println("❌ Falló: Carga máxima excedida");
            return ResultadoValidacion.fallo(
                    "El repartidor " + repartidor.getNombre() + " ya tiene " + enviosActivos +
                            " envíos activos (máximo permitido: " + CARGA_MAXIMA + ")",
                    "Validador de Carga Máxima"
            );
        }

        System.out.println("✅ Pasó: Carga dentro del límite");
        return pasarAlSiguiente(repartidor, envio);
    }

    /**
     * Permite configurar una carga máxima diferente si es necesario
     * @return La carga máxima configurada
     */
    public static int getCargaMaxima() {
        return CARGA_MAXIMA;
    }
}