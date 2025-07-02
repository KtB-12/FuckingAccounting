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

public class collection_modechequeconsumable {

    @FXML
    private TextField totalamount;

    @FXML
    private DatePicker date;

    @FXML
    private ComboBox<String> bank;

    @FXML
    private TextField chequenum;

    @FXML
    private Button atl;

    // Called when "Confirm" button is clicked
    @FXML
    private void add_to_list(MouseEvent event) {
        String amount = totalamount.getText();
        LocalDate chequeDate = date.getValue();
        String selectedBank = bank.getValue();
        String chequeNumber = chequenum.getText();

        // Simple validation or logging
        if (amount == null || amount.isEmpty()) {
            System.out.println("Amount is required.");
        } else if (chequeDate == null) {
            System.out.println("Date of cheque is required.");
        } else if (selectedBank == null || selectedBank.isEmpty()) {
            System.out.println("Please choose a bank.");
        } else if (chequeNumber == null || chequeNumber.isEmpty()) {
            System.out.println("Cheque number is required.");
        } else {
            System.out.println("Cheque Payment Details:");
            System.out.println("Amount: " + amount);
            System.out.println("Date: " + chequeDate);
            System.out.println("Bank: " + selectedBank);
            System.out.println("Cheque Number: " + chequeNumber);

            // You can call another method here to pass this data to a table or database

            closeWindow(event);
        }
    }

    // Called when the close icon is clicked
    @FXML
    private void exit(MouseEvent event) {
        closeWindow(event);
    }

    private void closeWindow(MouseEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    // Optional: initialize default bank options
    @FXML
    private void initialize() {
        bank.getItems().addAll("BDO", "BPI", "Metrobank", "PNB", "Landbank", "RCBC");
    }
}
