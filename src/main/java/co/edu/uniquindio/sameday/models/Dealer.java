package co.edu.uniquindio.sameday.models;

import co.edu.uniquindio.sameday.models.creational.singleton.SameDay;

public class Dealer extends Person {

    private boolean disponibleManual = true;
    private City city;


    public Dealer(String id, String documento, String nombre, String correo, String telefono,
                  UserAccount userAccount, boolean disponibleManual, City city) {
        super(id, documento, nombre, correo, telefono, userAccount);
        this.disponibleManual = disponibleManual;
        this.city = city;
    }


    public boolean isDisponibleManual() {
        return disponibleManual;
    }


    public void setDisponibleManual(boolean disponibleManual) {
        this.disponibleManual = disponibleManual;
    }

    public boolean isDisponible() {
        // Si el repartidor se marcó como no disponible manualmente, no está disponible
        if (!disponibleManual) {
            return false;
        }

        // Verificar si tiene envíos activos (sin entregar)
        SameDay sameDay = SameDay.getInstance();
        boolean tieneEnviosActivos = sameDay.getListEnvios().stream()
                .filter(envio -> envio.getRepartidorAsignado() != null)
                .filter(envio -> envio.getRepartidorAsignado().getId().equals(this.getId()))
                .anyMatch(envio -> envio.getEstadoEntrega() != null &&
                        envio.getEstadoEntrega() != EstadoEntrega.ENTREGADO);

        // Disponible solo si NO tiene envíos activos
        return !tieneEnviosActivos;
    }

    @Deprecated
    public void setDisponible(boolean disponible) {
        this.disponibleManual = disponible;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Dealer{" +
                "id='" + getId() + '\'' +
                ", nombre='" + getNombre() + '\'' +
                ", ciudad=" + city +
                ", disponibleManual=" + disponibleManual +
                ", disponible=" + isDisponible() +
                '}';
    }
}