package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class dr_viewadd {

    @FXML private ComboBox<String> products;
    @FXML private TextField qty;
    @FXML private TextField price;
    @FXML private TextField discount;
    @FXML private TextField amount;
    @FXML private Button atl;
    
    private DeliveryReceiptsController_view parentController;
    private String currentDrNumber;
    private String productCategoryFilter;
    
    public void setParentController(DeliveryReceiptsController_view controller) {
        this.parentController = controller;
    }
    
    public void setCurrentDrNumber(String drNumber) {
        this.currentDrNumber = drNumber;
    }
    
    public void setProductCategoryFilter(String category) {
        this.productCategoryFilter = category;
        loadProductsFromDatabase();
    }
    
    @FXML 
    private void initialize() {   
        // Set up listeners
        products.setOnAction(event -> {
            String selected = products.getSelectionModel().getSelectedItem();
            if (selected != null) {
                qty.setText("1");
                setProductPrice(selected);
                calculateAmount();
            }
        });

        discount.textProperty().addListener((obs, oldVal, newVal) -> calculateAmount());
        qty.textProperty().addListener((obs, oldVal, newVal) -> calculateAmount());
    }
    
    private void loadProductsFromDatabase() {
        products.getItems().clear();
        
        String query;
        if ("Machine".equals(productCategoryFilter)) {
            query = "SELECT \"Product List\" FROM product WHERE category = 'Machine' ORDER BY \"Product List\"";
        } else {
            // Show both Spare parts and Consumable
            query = "SELECT \"Product List\" FROM product WHERE category IN ('Spare parts', 'Consumable') ORDER BY \"Product List\"";
        }
        
        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                products.getItems().add(rs.getString("Product List"));
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Could not load products", e.getMessage());
        }
    }
    
    private void setProductPrice(String productName) {
        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT \"Price\" FROM product WHERE \"Product List\" = ?")) {
            
            stmt.setString(1, productName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                price.setText(String.format("%.2f", rs.getDouble("Price")));
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Could not get product price", e.getMessage());
        }
    }
    
    @FXML
    private void exit(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void add_to_list() {
        if (!validateInputs()) {
            return;
        }

        try {
            int productId = getProductId(products.getValue());
            if (productId == -1) {
                showAlert("Error", "Invalid Product", "Selected product not found in database");
                return;
            }

            saveToDatabase(productId);

            if (parentController != null) {
                parentController.loadDeliveryReceiptData(currentDrNumber);
            }

            Stage stage = (Stage) atl.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save item", e.getMessage());
        }
    }
    
    private boolean validateInputs() {
        if (products.getValue() == null) {
            showAlert("Validation Error", "Missing Field", "Please select a product");
            return false;
        }
        
        try {
            Integer.parseInt(qty.getText());
            Double.parseDouble(price.getText());
            if (!discount.getText().isEmpty()) {
                Double.parseDouble(discount.getText());
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid Number", "Please enter valid numbers for quantity, price, and discount");
            return false;
        }
        
        return true;
    }
    
    private int getProductId(String productName) throws SQLException {
        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT id FROM product WHERE \"Product List\" = ?")) {
            
            stmt.setString(1, productName);
            ResultSet rs = stmt.executeQuery();
            
            return rs.next() ? rs.getInt("id") : -1;
        }
    }
    
    private void saveToDatabase(int productId) throws SQLException {
        String sql = "INSERT INTO delivery_items (dr_number, product_id, quantity, unit_price, discount) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, currentDrNumber);
            stmt.setInt(2, productId);
            stmt.setInt(3, Integer.parseInt(qty.getText()));
            stmt.setDouble(4, Double.parseDouble(price.getText()));
            stmt.setDouble(5, discount.getText().isEmpty() ? 0 : Double.parseDouble(discount.getText()));
            
            stmt.executeUpdate();
        }
    }
    
    private void calculateAmount() {
        try {
            double priceValue = Double.parseDouble(price.getText());
            double discountValue = discount.getText().isEmpty() ? 0 : Double.parseDouble(discount.getText());
            int quantityValue = qty.getText().isEmpty() ? 0 : Integer.parseInt(qty.getText());

            double result = (priceValue * quantityValue) - discountValue;
            amount.setText(String.format("%.2f", result));
        } catch (NumberFormatException e) {
            amount.setText("0.00");
        }
    }
    
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}