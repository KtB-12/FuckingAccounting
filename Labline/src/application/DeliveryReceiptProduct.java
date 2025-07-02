package application;

public class DeliveryReceiptProduct {
    private final Integer qty;
    private final String product;
    private final Double price;
    private final Double discount;
    private final Double amount;

    public DeliveryReceiptProduct(Integer qty, String product, Double price, Double discount, Double amount) {
        this.qty = qty;
        this.product = product;
        this.price = price;
        this.discount = discount;
        this.amount = amount;
    }

    public Integer getQty() { return qty; }
    public String getProduct() { return product; }
    public Double getPrice() { return price; }
    public Double getDiscount() { return discount; }
    public Double getAmount() { return amount; }
}