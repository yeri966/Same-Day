package co.edu.uniquindio.sameday.models.behavioral.strategy;

import co.edu.uniquindio.sameday.models.Dealer;
import co.edu.uniquindio.sameday.models.Envio;

import java.util.List;

public class AsignadorEnvios {
    private EstrategiaAsignacion estrategia;

    public AsignadorEnvios(EstrategiaAsignacion estrategia){
        this.estrategia=estrategia;
    }

    public EstrategiaAsignacion getEstrategia() {
        return estrategia;
    }

    public void setEstrategia(EstrategiaAsignacion estrategia) {
        this.estrategia = estrategia;
    }

    public Dealer asignar(Envio envio, List<Dealer>repartidorDisponible){
        return estrategia.seleccionarRepartidor(envio,repartidorDisponible);
    }
}
