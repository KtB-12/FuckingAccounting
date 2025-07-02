package application;

import java.time.LocalDate;

public class DeliveryReceipt {
    private String drNumber;
    private LocalDate dateDelivered;
    private String accountName;
    private String deliveredBy;
    private String address;
    private double totalAmount;
    private String status;

    public DeliveryReceipt(String drNumber, LocalDate dateDelivered, String accountName,
                           String deliveredBy, String address, double totalAmount, String status) {
        this.drNumber = drNumber;
        this.dateDelivered = dateDelivered;
        this.accountName = accountName;
        this.deliveredBy = deliveredBy;
        this.address = address;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getDrNumber() { return drNumber; }
    public LocalDate getDateDelivered() { return dateDelivered; }
    public String getAccountName() { return accountName; }
    public String getDeliveredBy() { return deliveredBy; }
    public String getAddress() { return address; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
}
