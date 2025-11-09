package co.edu.uniquindio.sameday.models.creational.builder;

import co.edu.uniquindio.sameday.models.UserAccount;

public class Dealer extends Person {
    private boolean disponible;
    private String zonaCobertura;

    private Dealer(Builder builder) {
        super(builder.id, builder.nombre, builder.correo, builder.telefono, builder.userAccount);
        this.disponible = builder.disponible;
        this.zonaCobertura = builder.zonaCobertura;
    }

    public static class Builder {
        private String id;
        private String nombre;
        private String correo;
        private String telefono;
        private UserAccount userAccount;
        private boolean disponible;
        private String zonaCobertura;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public Builder correo(String correo) {
            this.correo = correo;
            return this;
        }

        public Builder telefono(String telefono) {
            this.telefono = telefono;
            return this;
        }

        public Builder userAccount(UserAccount userAccount) {
            this.userAccount = userAccount;
            return this;
        }

        public Builder disponible(boolean disponible) {
            this.disponible = disponible;
            return this;
        }

        public Builder zonaCobertura(String zonaCobertura) {
            this.zonaCobertura = zonaCobertura;
            return this;
        }

        public Dealer build() {
            return new Dealer(this);
        }

    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getZonaCobertura() {
        return zonaCobertura;
    }

    public void setZonaCobertura(String zonaCobertura) {
        this.zonaCobertura = zonaCobertura;
    }
}
