package application;

import java.time.LocalDate;

public class TempPaymentData {

    private static TempPaymentData instance;

    private String paymentType; // "Cash", "Cheque", "Fund Transfer"

    private String amount;
    private LocalDate date;
    private String bank;
    private String chequeOrReferenceNumber;

    private TempPaymentData() {}

    public static TempPaymentData getInstance() {
        if (instance == null) {
            instance = new TempPaymentData();
        }
        return instance;
    }

    public void clear() {
        paymentType = null;
        amount = null;
        date = null;
        bank = null;
        chequeOrReferenceNumber = null;
    }

    // Getters and Setters
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getBank() { return bank; }
    public void setBank(String bank) { this.bank = bank; }

    public String getChequeOrReferenceNumber() { return chequeOrReferenceNumber; }
    public void setChequeOrReferenceNumber(String chequeOrReferenceNumber) { this.chequeOrReferenceNumber = chequeOrReferenceNumber; }
}
