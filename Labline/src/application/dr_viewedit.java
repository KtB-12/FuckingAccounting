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

public class dr_viewedit {

    @FXML private ComboBox<String> products;
    @FXML private TextField qty;
    @FXML private TextField price;
    @FXML private TextField discount;
    @FXML private TextField amount;
    @FXML private Button atl; 

    private DeliveryReceiptsController_view parentController;
    private String currentDrNumber;
    private int deliveryItemId; // Needed to identify which item to update in DB

    public void setParentController(DeliveryReceiptsController_view controller) {
        this.parentController = controller;
    }

    public void setCurrentDrNumber(String drNumber) {
        this.currentDrNumber = drNumber;
    }

    public void loadDataForEdit(DeliveryItemView item) {
        this.deliveryItemId = item.getId(); // Ensure DeliveryItemView has getId()

        qty.setText(String.valueOf(item.getQuantity()));
        price.setText(String.format("%.2f", item.getPrice()));
        discount.setText(String.format("%.2f", item.getDiscount()));
        amount.setText(String.format("%.2f", item.getAmount()));

        loadProductsFromDatabase();
        products.setValue(item.getProduct());
    }

    @FXML 
    private void initialize() {   
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
        price.textProperty().addListener((obs, oldVal, newVal) -> calculateAmount());
    }

    private void loadProductsFromDatabase() {
        products.getItems().clear();

        String query = "SELECT \"Product List\" FROM product ORDER BY \"Product List\"";
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
             PreparedStatement stmt = conn.prepareStatement("SELECT \"Price\" FROM product WHERE \"Product List\" = ?")) {
            stmt.setString(1, productName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                price.setText(String.format("%.2f", rs.getDouble("Price")));
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Could not fetch product price", e.getMessage());
        }
    }

    @FXML
    private void exit(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void add_to_list() {
        if (!validateInputs()) return;

        try {
            int productId = getProductId(products.getValue());
            if (productId == -1) {
                showAlert("Error", "Product Not Found", "Selected product does not exist in database.");
                return;
            }

            updateDatabase(productId);

            if (parentController != null) {
                parentController.loadDeliveryReceiptData(currentDrNumber);
            }

            Stage stage = (Stage) atl.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            showAlert("Database Error", "Could not update item", e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (products.getValue() == null) {
            showAlert("Validation Error", "Missing Product", "Please select a product.");
            return false;
        }
        try {
            Integer.parseInt(qty.getText());
            Double.parseDouble(price.getText());
            if (!discount.getText().isEmpty()) Double.parseDouble(discount.getText());
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid Input", "Please enter valid numbers for quantity, price, and discount.");
            return false;
        }
        return true;
    }

    private int getProductId(String productName) throws SQLException {
        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM product WHERE \"Product List\" = ?")) {
            stmt.setString(1, productName);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        }
    }

    private void updateDatabase(int productId) throws SQLException {
        String sql = "UPDATE delivery_items SET product_id = ?, quantity = ?, unit_price = ?, discount = ? WHERE id = ?";
        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, Integer.parseInt(qty.getText()));
            stmt.setDouble(3, Double.parseDouble(price.getText()));
            stmt.setDouble(4, discount.getText().isEmpty() ? 0 : Double.parseDouble(discount.getText()));
            stmt.setInt(5, deliveryItemId);
            stmt.executeUpdate();
        }
    }

    private void calculateAmount() {
        try {
            double p = Double.parseDouble(price.getText());
            double d = discount.getText().isEmpty() ? 0 : Double.parseDouble(discount.getText());
            int q = qty.getText().isEmpty() ? 0 : Integer.parseInt(qty.getText());
            amount.setText(String.format("%.2f", (p * q) - d));
        } catch (NumberFormatException e) {
            amount.setText("0.00");
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }
}
