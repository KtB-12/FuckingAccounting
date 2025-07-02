package application;

public class DRItemconsumable {
    private final String product;
    private final int quantity;
    private final double price;
    private final double discount;
    private final double amount;

    public DRItemconsumable(String product, int quantity, double price, double discount, double amount) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.amount = amount;
    }

    // Getters - these must exactly match the PropertyValueFactory names
    public String getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getDiscount() { return discount; }
    public double getAmount() { return amount; }
}