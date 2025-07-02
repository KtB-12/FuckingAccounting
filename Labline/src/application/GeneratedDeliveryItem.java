package application;

import java.time.LocalDate;
import javafx.beans.property.*;

public class GeneratedDeliveryItem {
    private final StringProperty drNumber;
    private final ObjectProperty<LocalDate> dateDelivered;
    private final IntegerProperty quantity;
    private final StringProperty product;
    private final StringProperty deliveredBy;
    private final DoubleProperty amount;

    public GeneratedDeliveryItem(String drNumber, LocalDate dateDelivered, int quantity,
                                 String product, String deliveredBy, double amount) {
        this.drNumber = new SimpleStringProperty(drNumber);
        this.dateDelivered = new SimpleObjectProperty<>(dateDelivered);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.product = new SimpleStringProperty(product);
        this.deliveredBy = new SimpleStringProperty(deliveredBy);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public String getDrNumber() { return drNumber.get(); }
    public LocalDate getDateDelivered() { return dateDelivered.get(); }
    public int getQuantity() { return quantity.get(); }
    public String getProduct() { return product.get(); }
    public String getDeliveredBy() { return deliveredBy.get(); }
    public double getAmount() { return amount.get(); }

    public StringProperty drNumberProperty() { return drNumber; }
    public ObjectProperty<LocalDate> dateDeliveredProperty() { return dateDelivered; }
    public IntegerProperty quantityProperty() { return quantity; }
    public StringProperty productProperty() { return product; }
    public StringProperty deliveredByProperty() { return deliveredBy; }
    public DoubleProperty amountProperty() { return amount; }
}
