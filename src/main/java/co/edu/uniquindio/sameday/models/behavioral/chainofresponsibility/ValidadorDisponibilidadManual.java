package co.edu.uniquindio.sameday.models.behavioral.chainofresponsibility;

import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.Envio;

/**
 * PATRÓN COMPORTAMENTAL: CHAIN OF RESPONSIBILITY
 *
 * Validador concreto que verifica si el repartidor está disponible manualmente.
 * Este es el primer eslabón de la cadena y verifica que el repartidor
 * no se haya marcado como "No Disponible" desde su dashboard.
 */
public class ValidadorDisponibilidadManual extends ValidadorRepartidor {

    @Override
    public ResultadoValidacion validar(Dealer repartidor, Envio envio) {
        System.out.println("🔍 Validando disponibilidad manual de: " + repartidor.getNombre());

        if (!repartidor.isDisponibleManual()) {
            System.out.println("❌ Falló: Repartidor marcado como no disponible");
            return ResultadoValidacion.fallo(
                    "El repartidor " + repartidor.getNombre() +
                            " no está disponible (marcado como no disponible manualmente)",
                    "Validador de Disponibilidad"
            );
        }

        System.out.println("✅ Pasó: Repartidor disponible manualmente");
        return pasarAlSiguiente(repartidor, envio);
    }
}