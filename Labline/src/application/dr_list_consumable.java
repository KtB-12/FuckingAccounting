package application;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class dr_list_consumable {

    @FXML private TableView<DRItemconsumable> tableView;
    @FXML private TableColumn<DRItemconsumable, String> product;
    @FXML private TableColumn<DRItemconsumable, Integer> quantity;
    @FXML private TableColumn<DRItemconsumable, Double> price;
    @FXML private TableColumn<DRItemconsumable, Double> discount;
    @FXML private TableColumn<DRItemconsumable, Double> amount;

    @FXML
    private void initialize() {
        // Initialize columns
        product.setCellValueFactory(new PropertyValueFactory<>("product"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        discount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Verify the list exists before connecting
        if (drconsumable_popup.drItemsconsumable != null) {
            tableView.setItems(drconsumable_popup.drItemsconsumable);
            System.out.println("Successfully connected to shared list");
        } else {
            System.err.println("Error: Shared list is null!");
        }

        // Enhanced debug listener
        drconsumable_popup.drItemsconsumable.addListener((ListChangeListener.Change<? extends DRItemconsumable> change) -> {
            System.out.println("\n--- List Change Detected ---");
            System.out.println("Current items in list: " + change.getList().size());
            
            while (change.next()) {
                if (change.wasAdded()) {
                    System.out.println("Added " + change.getAddedSize() + " items");
                    change.getAddedSubList().forEach(item -> 
                        System.out.println(" - " + item.getProduct() + ", Qty: " + item.getQuantity()));
                }
                if (change.wasRemoved()) {
                    System.out.println("Removed " + change.getRemovedSize() + " items");
                }
            }
            
            // Verify table view is updated
            System.out.println("Table view now shows: " + tableView.getItems().size() + " items");
        });
    }

    @FXML
    private void exit(MouseEvent event) {
        ((javafx.stage.Stage)(((javafx.scene.Node)event.getSource()).getScene().getWindow())).close();
    }
}