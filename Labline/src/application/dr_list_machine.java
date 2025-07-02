package application;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class dr_list_machine {

    @FXML private TableView<DRItem> tableView;
    @FXML private TableColumn<DRItem, Integer> quantity;
    @FXML private TableColumn<DRItem, String> product;
    @FXML private TableColumn<DRItem, Double> price;
    @FXML private TableColumn<DRItem, Double> discount;
    @FXML private TableColumn<DRItem, Double> amount;

    @FXML
    private void initialize() {
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        product.setCellValueFactory(new PropertyValueFactory<>("product"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        discount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        tableView.setItems(drmachine_popup.drItems);
    }

    @FXML
    private void exit(MouseEvent event) {
        ((javafx.stage.Stage)(((javafx.scene.Node)event.getSource()).getScene().getWindow())).close();
    }
}
