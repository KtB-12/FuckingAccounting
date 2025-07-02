package application;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class collection_consumable_adddr {

    @FXML private TextField dr_number;
    @FXML private TextField amount;

    @FXML
    private void add_to_list(MouseEvent event) {
       

        System.out.println("Added DR: ");

    }

    @FXML
    private void exit(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
