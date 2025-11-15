package co.edu.uniquindio.sameday.models.behavioral.strategy;

import co.edu.uniquindio.sameday.models.City;
import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.Envio;

import java.util.List;

public class AsignacionPorCiudad implements EstrategiaAsignacion{
    @Override
    public Dealer seleccionarRepartidor(Envio envio, List<Dealer> repartidoresDisponible) {
        if(envio.getDestino()==null){
            return null;
        }

        City ciudadDestino=envio.getDestino().getCity();
        return repartidoresDisponible.stream()
                .filter(dealer -> dealer.getCity() == ciudadDestino)
                .filter(Dealer::isDisponible)
                .findFirst()
                .orElse(null);
    }
}
