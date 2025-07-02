package application;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class v_m_reportController {
	
	 
	 @FXML
		private void minimize(MouseEvent event) throws IOException {
		    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		    stage.setIconified(true);
		}

		@FXML
		private void exit(MouseEvent event) throws IOException {
		    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		    stage.close();
		}

}