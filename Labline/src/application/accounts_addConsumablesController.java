package application;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class accounts_addConsumablesController {

	@FXML
	private void back_to_list(MouseEvent event) {
	    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	    stage.close();
	}
    
    @FXML
    private void add_to_list(MouseEvent event) {
        System.out.print("done");
    }
}