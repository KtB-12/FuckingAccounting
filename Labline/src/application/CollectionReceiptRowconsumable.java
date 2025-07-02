package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CollectionReceiptRowconsumable {

    private final StringProperty crNumber;
    private final StringProperty dateDelivered;
    private final StringProperty name;
    private final StringProperty address;
    private final StringProperty amount;
    private final StringProperty receivedBy;
    private final StringProperty status;

    public CollectionReceiptRowconsumable(String crNumber, String dateDelivered, String name, String address,
                                          String amount, String receivedBy, String status) {
        this.crNumber = new SimpleStringProperty(crNumber);
        this.dateDelivered = new SimpleStringProperty(dateDelivered);
        this.name = new SimpleStringProperty(name);
        this.address = new SimpleStringProperty(address);
        this.amount = new SimpleStringProperty(amount);
        this.receivedBy = new SimpleStringProperty(receivedBy);
        this.status = new SimpleStringProperty(status);
    }

    public StringProperty crNumberProperty() { return crNumber; }
    public StringProperty dateDeliveredProperty() { return dateDelivered; }
    public StringProperty nameProperty() { return name; }
    public StringProperty addressProperty() { return address; }
    public StringProperty amountProperty() { return amount; }
    public StringProperty receivedByProperty() { return receivedBy; }
    public StringProperty statusProperty() { return status; }
}
