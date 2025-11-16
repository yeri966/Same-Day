package co.edu.uniquindio.sameday.models.behavioral.chainofresponsibility;

import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.Envio;

/**
 * PATRÓN COMPORTAMENTAL: CHAIN OF RESPONSIBILITY
 *
 * Handler abstracto para validar si un repartidor puede recibir un envío.
 * Cada validador en la cadena verifica un aspecto específico y pasa
 * la solicitud al siguiente si la validación es exitosa.
 *
 * Cadena: DisponibilidadManual → CargaMaxima → ZonaCobertura
 */
public abstract class ValidadorRepartidor {

    protected ValidadorRepartidor siguiente;

    /**
     * Establece el siguiente validador en la cadena
     * @param siguiente El siguiente handler
     */
    public void setSiguiente(ValidadorRepartidor siguiente) {
        this.siguiente = siguiente;
    }

    /**
     * Encadena el siguiente validador y lo retorna para permitir encadenamiento fluido
     * @param siguiente El siguiente handler
     * @return El siguiente handler para continuar encadenando
     */
    public ValidadorRepartidor linkWith(ValidadorRepartidor siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    /**
     * Método abstracto que cada validador concreto debe implementar
     * @param repartidor El repartidor a validar
     * @param envio El envío que se quiere asignar
     * @return ResultadoValidacion indicando si pasó o falló la validación
     */
    public abstract ResultadoValidacion validar(Dealer repartidor, Envio envio);

    /**
     * Pasa la validación al siguiente handler en la cadena
     * @param repartidor El repartidor a validar
     * @param envio El envío que se quiere asignar
     * @return Resultado del siguiente validador o éxito si no hay más
     */
    protected ResultadoValidacion pasarAlSiguiente(Dealer repartidor, Envio envio) {
        if (siguiente != null) {
            return siguiente.validar(repartidor, envio);
        }
        return ResultadoValidacion.exito("Todas las validaciones pasaron correctamente");
    }
}