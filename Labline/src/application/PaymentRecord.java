package application;

import java.time.LocalDate;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PaymentRecord {
    private final StringProperty paymentMethod;
    private final StringProperty amount;
    private LocalDate chequeDate;
    private String bank;
    private String chequeNumber;
    private LocalDate transferDate;
    private String referenceNumber;

    public PaymentRecord(String paymentMethod, String amount) {
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
        this.amount = new SimpleStringProperty(amount);
    }

    // === Factory for cheque ===
    public static PaymentRecord createCheque(String paymentMethod, String amount, LocalDate chequeDate, String bank, String chequeNumber) {
        PaymentRecord pr = new PaymentRecord(paymentMethod, amount);
        pr.chequeDate = chequeDate;
        pr.bank = bank;
        pr.chequeNumber = chequeNumber;
        return pr;
    }

    // === Factory for fund transfer ===
    public static PaymentRecord createFundTransfer(String paymentMethod, String amount, LocalDate transferDate, String bank, String referenceNumber) {
        PaymentRecord pr = new PaymentRecord(paymentMethod, amount);
        pr.transferDate = transferDate;
        pr.bank = bank;
        pr.referenceNumber = referenceNumber;
        return pr;
    }

    // === GETTERS ===
    public String getPaymentMethod() { return paymentMethod.get(); }
    public StringProperty paymentMethodProperty() { return paymentMethod; }

    public String getAmount() { return amount.get(); }
    public StringProperty amountProperty() { return amount; }

    public LocalDate getChequeDate() { return chequeDate; }
    public String getBank() { return bank; }
    public String getChequeNumber() { return chequeNumber; }
    public LocalDate getTransferDate() { return transferDate; }
    public String getReferenceNumber() { return referenceNumber; }

    // Optionally: Setters if modifying data post-construction
}
