package co.edu.uniquindio.sameday.models.creational.builder;

import co.edu.uniquindio.sameday.models.UserAccount;

//Patron Builder
public class Admin extends Person {
    private String cargo;

    private Admin(Builder builder) {
        super(builder.id, builder.nombre, builder.correo, builder.telefono, builder.userAccount);
        this.cargo = builder.cargo;
    }

    public static class Builder {
        private String id;
        private String nombre;
        private String correo;
        private String telefono;
        private UserAccount userAccount;
        private String cargo;

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

        public Builder telefono(String telefono){
            this.telefono=telefono;
            return this;
        }

        public Builder userAccount(UserAccount userAccount){
            this.userAccount=userAccount;
            return this;
        }

        public Builder cargo(String cargo){
            this.cargo=cargo;
            return this;
        }

        public Admin build(){
            return new Admin(this);
        }
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
