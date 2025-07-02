package application;

import javafx.beans.property.SimpleStringProperty;

public class Account {
    private final SimpleStringProperty name;
    private final SimpleStringProperty municipality;
    private final SimpleStringProperty province;

    public Account(String name, String municipality, String province) {
        this.name = new SimpleStringProperty(name);
        this.municipality = new SimpleStringProperty(municipality);
        this.province = new SimpleStringProperty(province);
    }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }

    public String getMunicipality() { return municipality.get(); }
    public void setMunicipality(String value) { municipality.set(value); }

    public String getProvince() { return province.get(); }
    public void setProvince(String value) { province.set(value); }
}
