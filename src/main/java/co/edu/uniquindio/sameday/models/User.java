package co.edu.uniquindio.sameday.models;

import java.util.UUID;

public class User {
  private String id = UUID.randomUUID().toString();
  private String name;
  private String mail;
  private String phone;
  private String address;

    public User(Builder builder){
        this.id = (builder.id!=null) ? builder.id: UUID.randomUUID().toString();
        this.name = builder.name;
        this.mail = builder.mail;
        this.phone = builder.phone;
        this.address = builder.address;
    }

    public static class Builder {
        private String id;
        private String name;
        private String mail;
        private String phone;
        private String address;

        public Builder id(String id) {
            this.id = id;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder mail(String mail) {
            this.mail = mail;
            return this;
        }
        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public User build(){
            return new User(this);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMail() {
            return mail;
        }

        public void setMail(String mail) {
            this.mail = mail;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @Override
        public String toString() {
            return id+" "+name+" "+mail+" "+phone+" "+address;
        }
    }
}

