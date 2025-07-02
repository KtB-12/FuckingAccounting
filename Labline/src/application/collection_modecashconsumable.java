package application;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class collection_modecashconsumable {

    @FXML
    private TextField totalamount;

    @FXML
    private Button atl;

    // Triggered when "Confirm" button is clicked
    @FXML
    private void add_to_list(MouseEvent event) {
        String amount = totalamount.getText();
        if (amount == null || amount.isEmpty()) {
            System.out.println("Amount is empty.");
        } else {
            System.out.println("Confirmed amount: " + amount);
            // You can replace this with logic to add the amount to a list or database
        }

        // Optional: Close window after confirming
        closeWindow(event);
    }

    // Triggered when the close icon (ImageView) is clicked
    @FXML
    private void exit(MouseEvent event) {
        closeWindow(event);
    }

    private void closeWindow(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
