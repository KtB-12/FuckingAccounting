package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class drmachine_popup {

    @FXML private ComboBox<String> products;
    @FXML private TextField qty;
    @FXML private TextField price;
    @FXML private TextField discount;
    @FXML private TextField amount;
    @FXML private Button atl;

    // This list can be accessed by dr_list_machine
    public static ObservableList<DRItem> drItems = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadMachineProducts();  // Changed to load only machine products
        setupComboBoxSearch();

        products.setOnAction(event -> {
            String selected = products.getSelectionModel().getSelectedItem();
            if (selected != null) {
                fetchProductPrice(selected);
                calculateAmount();
            }
        });

        discount.textProperty().addListener((obs, oldVal, newVal) -> calculateAmount());
        qty.textProperty().addListener((obs, oldVal, newVal) -> calculateAmount());
    }

    private void loadMachineProducts() {
        try (Connection conn = PostgresConnect.getConnection()) {
            // Try different variations of the category name
            String sql = "SELECT \"Product List\" FROM public.product WHERE LOWER(\"category\") = LOWER(?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "Machine"); // Try with exact case first
            
            ResultSet rs = ps.executeQuery();
            
            // Clear existing items first
            ObservableList<String> machineProducts = FXCollections.observableArrayList();
            
            while (rs.next()) {
                machineProducts.add(rs.getString("Product List"));
            }
            
            // If no results, try with trimmed and case-insensitive
            if (machineProducts.isEmpty()) {
                sql = "SELECT \"Product List\" FROM public.product WHERE TRIM(LOWER(\"category\")) = LOWER(?)";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "machine"); // Try with lowercase
                rs = ps.executeQuery();
                
                while (rs.next()) {
                    machineProducts.add(rs.getString("Product List"));
                }
            }
            
            products.setItems(machineProducts);
            
            // Debug output
            System.out.println("Loaded " + machineProducts.size() + " machine products");
            if (!machineProducts.isEmpty()) {
                System.out.println("Sample product: " + machineProducts.get(0));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Show error to user
            showAlert("Database Error", "Could not load machine products: " + e.getMessage());
        }
    }

    private void setupComboBoxSearch() {
        products.setEditable(true);
        // You can expand this with a custom filter if needed
    }

    private void fetchProductPrice(String productName) {
        try (Connection conn = PostgresConnect.getConnection()) {
            String sql = "SELECT \"Price\" FROM public.product WHERE \"Product List\" = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, productName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                price.setText(String.valueOf(rs.getDouble("Price")));
                qty.setText("1");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void calculateAmount() {
        try {
            double priceValue = Double.parseDouble(price.getText());
            double discountValue = discount.getText().isEmpty() ? 0 : Double.parseDouble(discount.getText());
            int quantityValue = qty.getText().isEmpty() ? 1 : Integer.parseInt(qty.getText());

            double discountedPrice = priceValue - discountValue;
            double result = discountedPrice * quantityValue;
            amount.setText(String.format("%.2f", result));
        } catch (NumberFormatException e) {
            amount.setText("0.00");
        }
    }

    @FXML
    private void add_to_list() {
        String product = products.getValue();
        String quantity = qty.getText();
        String unitPrice = price.getText();
        String discountValue = discount.getText();
        String totalAmount = amount.getText();

        if (product == null || product.isEmpty() ||
            quantity == null || quantity.isEmpty() ||
            unitPrice == null || unitPrice.isEmpty() ||
            totalAmount == null || totalAmount.isEmpty()) {

            showAlert("Missing Data", "Please fill in all required fields");
            return;
        }

        // Add to the shared list
        drItems.add(new DRItem(
            product, 
            Integer.parseInt(quantity),
            Double.parseDouble(unitPrice),
            Double.parseDouble(discountValue.isEmpty() ? "0" : discountValue),
            Double.parseDouble(totalAmount)));

        // Show success notification
        showSuccessNotification("Item Added", "The item has been successfully added to the list");

        // Clear fields
        products.getSelectionModel().clearSelection();
        products.setValue("");
        qty.clear();
        price.clear();
        discount.clear();
        amount.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void exit(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}