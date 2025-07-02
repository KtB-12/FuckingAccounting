package application;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AreaManagerController {

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ProgressIndicator collectionProgress;

    @FXML
    private TextField totalCollectionField;

    @FXML
    private TextField totalSalesField;

    @FXML
    private TextField totalMachineCollectedBlue;

    @FXML
    private TextField totalMachineCollectedRed;

    @FXML
    private TextField totalCommissionField;

    @FXML
    private ImageView exit;


    @FXML
    private void exit(MouseEvent event) {
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
