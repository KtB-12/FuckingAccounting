package application;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

public class collection_consumable_addr_list {

    @FXML private TableView<?> tableView;
    @FXML private TableColumn<?, ?> dr_number;
    @FXML private TableColumn<?, ?> amount;

    

    @FXML
    private void exit(MouseEvent event) {
        ((javafx.stage.Stage)(((javafx.scene.Node)event.getSource()).getScene().getWindow())).close();
    }
}
