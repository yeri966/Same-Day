package co.edu.uniquindio.sameday.models.creational.builder;

import co.edu.uniquindio.sameday.models.UserAccount;

public class Client extends Person {
    private String direccion;

    private Client(Builder builder) {
        super(builder.id, builder.nombre, builder.correo, builder.telefono, builder.userAccount);
        this.direccion = builder.direccion;
    }

    public static class Builder {
        private String id;
        private String nombre;
        private String correo;
        private String telefono;
        private UserAccount userAccount;
        private String direccion;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder nombre(String nombre){
            this.nombre=nombre;
            return this;
        }
        public Builder correo(String correo){
            this.correo=correo;
            return this;
        }
        public Builder telefono(String telefono){
            this.telefono=telefono;
            return this;
        }
        public Builder userAcconunt(UserAccount userAccount){
            this.userAccount=userAccount;
            return this;
        }
        public Builder direccion(String direccion){
            this.direccion=direccion;
            return this;
        }
        public Client build(){
            return new Client(this);
        }
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
