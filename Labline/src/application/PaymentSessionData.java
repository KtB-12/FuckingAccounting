package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PaymentSessionData {

    private static PaymentSessionData instance;
    private final ObservableList<PaymentRecord> paymentRecords = FXCollections.observableArrayList();

    private PaymentSessionData() {}

    public static PaymentSessionData getInstance() {
        if (instance == null) {
            instance = new PaymentSessionData();
        }
        return instance;
    }

    public ObservableList<PaymentRecord> getPaymentRecords() {
        return paymentRecords;
    }

    public void addPaymentRecord(PaymentRecord record) {
        paymentRecords.add(record);
    }

    public void clear() {
        paymentRecords.clear();
    }
}
