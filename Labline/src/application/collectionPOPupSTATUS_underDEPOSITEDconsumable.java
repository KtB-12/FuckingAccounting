package application;

import java.util.ResourceBundle;

import java.net.URL;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class collectionPOPupSTATUS_underDEPOSITEDconsumable implements Initializable {

    @FXML
    private Button add;

    @FXML
    private DatePicker date;

    @FXML
    private ComboBox<String> bank;

    @FXML
    private ImageView exit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bank.getItems().addAll("BDO", "BPI", "Metrobank", "Landbank");
        date.setValue(LocalDate.now());
    }

    @FXML
    private void add_to_list(MouseEvent event) {
        LocalDate selectedDate = date.getValue();
        String selectedBank = bank.getValue();

        if (selectedDate != null && selectedBank != null) {
            System.out.println("Confirmed with Date: " + selectedDate + " and Bank: " + selectedBank);
            // You can now forward this data to your database or another list
            Stage stage = (Stage) add.getScene().getWindow();
            stage.close();
        } else {
            System.out.println("Please select both a date and a bank.");
        }
    }

    @FXML
    private void exit(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
