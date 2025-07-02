package application;

public class DRItem {
    private String product;
    private int quantity;
    private double price;
    private double discount;
    private double amount;

    public DRItem(String product, int quantity, double price, double discount, double amount) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.amount = amount;
    }

    public String getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getDiscount() { return discount; }
    public double getAmount() { return amount; }
}
