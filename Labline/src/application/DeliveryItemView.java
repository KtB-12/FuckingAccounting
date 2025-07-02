package application;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DeliveryItemView {
    private final IntegerProperty id;
    private final IntegerProperty quantity;
    private final StringProperty product;
    private final DoubleProperty price;
    private final DoubleProperty discount;
    private final DoubleProperty amount;
    

   

    public DeliveryItemView(int id, int quantity, String product, 
                          double price, double discount, double amount) {
        this.id = new SimpleIntegerProperty(id);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.product = new SimpleStringProperty(product);
        this.price = new SimpleDoubleProperty(price);
        this.discount = new SimpleDoubleProperty(discount);
        this.amount = new SimpleDoubleProperty(amount);
    }

    // Property getters
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty quantityProperty() { return quantity; }
    public StringProperty productProperty() { return product; }
    public DoubleProperty priceProperty() { return price; }
    public DoubleProperty discountProperty() { return discount; }
    public DoubleProperty amountProperty() { return amount; }

    // Regular getters
    public int getId() { return id.get(); }
    public int getQuantity() { return quantity.get(); }
    public String getProduct() { return product.get(); }
    public double getPrice() { return price.get(); }
    public double getDiscount() { return discount.get(); }
    public double getAmount() { return amount.get(); }
}