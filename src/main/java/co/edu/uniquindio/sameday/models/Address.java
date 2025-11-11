package co.edu.uniquindio.sameday.models;


public class Address {
    private String id;
    private String alias;
    private String street;
    private City city;
    private AddressType type;
    private String placeDescription;
    private String additionalInfo;

    public Address() {
    }

    public Address(String id, String alias, String street, City city,
                   AddressType type, String placeDescription) {
        this.id = id;
        this.alias = alias;
        this.street = street;
        this.city = city;
        this.type = type;
        this.placeDescription = placeDescription;
    }

    public Address(String id, String alias, String street, City city,
                   AddressType type, String placeDescription, String additionalInfo) {
        this(id, alias, street, city, type, placeDescription);
        this.additionalInfo = additionalInfo;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }

    public City getCity() {
        return city;
    }
    public void setCity(City city) {
        this.city = city;
    }

    public AddressType getType() {
        return type;
    }
    public void setType(AddressType type) {
        this.type = type;
    }

    public String getPlaceDescription() {
        return placeDescription;
    }
    public void setPlaceDescription(String placeDescription) {
        this.placeDescription = placeDescription;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        fullAddress.append(street);
        if (additionalInfo != null && !additionalInfo.isEmpty()) {
            fullAddress.append(", ").append(additionalInfo);
        }
        fullAddress.append(", ").append(city);
        fullAddress.append(", Quindío, Colombia");
        return fullAddress.toString();
    }

    @Override
    public String toString() {
        return alias + " - " + getFullAddress();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Address address = (Address) obj;
        return id != null && id.equals(address.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}