package application;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class CollectionReceiptsadddetailsmachine {

    @FXML private DatePicker datePicker;
    @FXML private TextField checkNumber;
    @FXML private TextField amount;
    @FXML private TextField bankName;
    @FXML private TextField remarks;
    @FXML private ImageView exit;

    @FXML
    private void exit(MouseEvent event) {
        Stage stage = (Stage) exit.getScene().getWindow();
        stage.close();
    }

    public void printFormData() {
        System.out.println("Date: " + datePicker.getValue());
        System.out.println("Check #: " + checkNumber.getText());
        System.out.println("Amount: " + amount.getText());
        System.out.println("Bank: " + bankName.getText());
        System.out.println("Remarks: " + remarks.getText());
    }
}
