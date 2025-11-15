package co.edu.uniquindio.sameday.models.behavioral.strategy;

import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.Envio;

import java.util.List;

public interface EstrategiaAsignacion {
    Dealer seleccionarRepartidor(Envio envio, List<Dealer>repartidoresDisponible);
}
