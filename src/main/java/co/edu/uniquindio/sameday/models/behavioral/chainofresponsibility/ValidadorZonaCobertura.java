package co.edu.uniquindio.sameday.models.behavioral.chainofresponsibility;

import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.Envio;

/**
 * PATRÓN COMPORTAMENTAL: CHAIN OF RESPONSIBILITY
 *
 * Validador concreto que verifica si el repartidor cubre la zona
 * de destino del envío. El repartidor debe estar asignado a la misma
 * ciudad donde se encuentra el destino del envío.
 */
public class ValidadorZonaCobertura extends ValidadorRepartidor {

    @Override
    public ResultadoValidacion validar(Dealer repartidor, Envio envio) {
        System.out.println("🔍 Validando zona de cobertura para: " + repartidor.getNombre());

        // Verificar que el envío tenga destino
        if (envio.getDestino() == null) {
            System.out.println("❌ Falló: Envío sin destino definido");
            return ResultadoValidacion.fallo(
                    "El envío no tiene una dirección de destino definida",
                    "Validador de Zona de Cobertura"
            );
        }

        // Verificar que el repartidor tenga ciudad asignada
        if (repartidor.getCity() == null) {
            System.out.println("❌ Falló: Repartidor sin ciudad asignada");
            return ResultadoValidacion.fallo(
                    "El repartidor " + repartidor.getNombre() + " no tiene una ciudad asignada",
                    "Validador de Zona de Cobertura"
            );
        }

        System.out.println("   Ciudad repartidor: " + repartidor.getCity());
        System.out.println("   Ciudad destino: " + envio.getDestino().getCity());

        // Verificar que las ciudades coincidan
        if (repartidor.getCity() != envio.getDestino().getCity()) {
            System.out.println("❌ Falló: Ciudades no coinciden");
            return ResultadoValidacion.fallo(
                    "El repartidor " + repartidor.getNombre() + " está asignado a " +
                            repartidor.getCity() + ", pero el envío debe entregarse en " +
                            envio.getDestino().getCity(),
                    "Validador de Zona de Cobertura"
            );
        }

        System.out.println("✅ Pasó: Zona de cobertura correcta");
        return pasarAlSiguiente(repartidor, envio);
    }
}