package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class accounts_addMachinesController {

    @FXML
    private void back_to_list(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void add_to_list(ActionEvent event) {
        System.out.print("done");
    }
}