package application;

import java.util.ResourceBundle;

import java.net.URL;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class collectionPOPupSTATUS_underREMITTEDconsumable implements Initializable {

    @FXML
    private Button atl;

    @FXML
    private DatePicker date;

    @FXML
    private TextField recievedby;

    @FXML
    private ImageView exit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Optionally set defaults if needed
        date.setValue(LocalDate.now());
    }

    @FXML
    private void exit(MouseEvent event) {
        Stage stage = (Stage) exit.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void add_to_list(MouseEvent event) {

        Stage stage = (Stage) atl.getScene().getWindow();
        stage.close();
    }
}
