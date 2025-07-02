package application;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class collection_modecash {

    @FXML private TextField totalamount;
    @FXML private Button atl;

    @FXML
    private void add_to_list(MouseEvent event) {
        String amount = totalamount.getText();
        if (amount == null || amount.isEmpty()) {
            System.out.println("Amount is empty.");
        } else {
            PaymentRecord record = new PaymentRecord("Cash", amount);
            PaymentSessionData.getInstance().addPaymentRecord(record);
            System.out.println("Cash payment saved: " + amount);
            showPrompt("Payment Added", "Cash payment saved successfully.");
        }
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
