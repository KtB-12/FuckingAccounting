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

public class collection_modefundtransferconsumable {

    @FXML
    private TextField totalamount;

    @FXML
    private DatePicker date;

    @FXML
    private ComboBox<String> bank;

    @FXML
    private TextField referencenum;

    @FXML
    private Button atl;

    // Handle confirm button click
    @FXML
    private void add_to_list(MouseEvent event) {
        String amount = totalamount.getText();
        LocalDate transferDate = date.getValue();
        String selectedBank = bank.getValue();
        String referenceNumber = referencenum.getText();

        if (amount == null || amount.isEmpty()) {
            System.out.println("Amount is required.");
        } else if (transferDate == null) {
            System.out.println("Transfer date is required.");
        } else if (selectedBank == null || selectedBank.isEmpty()) {
            System.out.println("Bank selection is required.");
        } else if (referenceNumber == null || referenceNumber.isEmpty()) {
            System.out.println("Reference number is required.");
        } else {
            System.out.println("Bank Transfer Details:");
            System.out.println("Amount: " + amount);
            System.out.println("Date: " + transferDate);
            System.out.println("Bank: " + selectedBank);
            System.out.println("Reference No.: " + referenceNumber);

            // Optional: Add logic here to store/send the data

            closeWindow(event);
        }
    }

    // Handle close icon click
    @FXML
    private void exit(MouseEvent event) {
        closeWindow(event);
    }

    private void closeWindow(MouseEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    // Optional: Populate bank choices on load
    @FXML
    private void initialize() {
        bank.getItems().addAll("BDO", "BPI", "Metrobank", "PNB", "Landbank", "RCBC");
    }
}
