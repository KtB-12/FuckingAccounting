package application;

import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class collection_modecheque {

    @FXML private TextField totalamount;
    @FXML private DatePicker date;
    @FXML private ComboBox<String> bank;
    @FXML private TextField chequenum;
    @FXML private Button atl;

    @FXML
    private void add_to_list(MouseEvent event) {
        String amount = totalamount.getText();
        LocalDate chequeDate = date.getValue();
        String selectedBank = bank.getValue();
        String chequeNumber = chequenum.getText();

        if (amount == null || amount.isEmpty()) {
            System.out.println("Amount is required.");
        } else if (chequeDate == null) {
            System.out.println("Cheque date is required.");
        } else if (selectedBank == null || selectedBank.isEmpty()) {
            System.out.println("Bank is required.");
        } else if (chequeNumber == null || chequeNumber.isEmpty()) {
            System.out.println("Cheque number is required.");
        } else {
            PaymentRecord record = PaymentRecord.createCheque("Cheque", amount, chequeDate, selectedBank, chequeNumber);
            PaymentSessionData.getInstance().addPaymentRecord(record);
            System.out.println("Cheque payment saved: " + amount);
            showPrompt("Payment Added", "Cheque payment saved successfully.");
            closeWindow(event);
        }
    }

    @FXML
    private void exit(MouseEvent event) {
        closeWindow(event);
    }

    private void closeWindow(MouseEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void initialize() {
        bank.getItems().addAll("SECURITY BANK", "BDO UNIBANK", "AUB", "EASTWEST UNIBANK");
    }
    private void showPrompt(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
