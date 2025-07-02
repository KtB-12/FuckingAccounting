package application;

import java.time.LocalDate;

public class DRGeneratedRow {
    private String drNumber;
    private LocalDate dateDelivered;
    private int quantity;
    private String product;
    private String deliveredBy;
    private double amount;

    public DRGeneratedRow(String drNumber, LocalDate dateDelivered, int quantity,
                          String product, String deliveredBy, double amount) {
        this.drNumber = drNumber;
        this.dateDelivered = dateDelivered;
        this.quantity = quantity;
        this.product = product;
        this.deliveredBy = deliveredBy;
        this.amount = amount;
    }

    public String getDrNumber() {
        return drNumber;
    }

    public LocalDate getDateDelivered() {
        return dateDelivered;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProduct() {
        return product;
    }

    public String getDeliveredBy() {
        return deliveredBy;
    }

    public double getAmount() {
        return amount;
    }
}
