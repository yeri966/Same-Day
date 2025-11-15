package co.edu.uniquindio.sameday.models.behavioral.strategy;

import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.Envio;
import co.edu.uniquindio.sameday.models.EstadoEntrega;
import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;

import java.util.Comparator;
import java.util.List;

public class AsignacionPorMenorCarga implements EstrategiaAsignacion {

    @Override
    public Dealer seleccionarRepartidor(Envio envio, List<Dealer> repartidoresDisponible) {
        if (envio.getDestino() == null) {
            return null;
        }
        SameDay sameDay = SameDay.getInstance();

        return repartidoresDisponible.stream()
                .filter(dealer -> dealer.getCity() == envio.getDestino().getCity())
                .filter(Dealer::isDisponible)
                .min(Comparator.comparingLong(dealer -> contarEnviosActivos(dealer, sameDay)))
                .orElse(null);
    }

    private long contarEnviosActivos(Dealer dealer, SameDay sameDay) {
        return sameDay.getListEnvios().stream()
                .filter(e -> e.getRepartidorAsignado() != null)
                .filter(e -> e.getRepartidorAsignado().getId().equals(dealer.getId()))
                .filter(e -> e.getEstadoEntrega() != null &&
                        e.getEstadoEntrega() != EstadoEntrega.ENTREGADO)
                .count();
    }
}
