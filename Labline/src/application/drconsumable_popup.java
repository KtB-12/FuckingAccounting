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

public class drconsumable_popup {

    @FXML private ComboBox<String> products;
    @FXML private TextField qty;
    @FXML private TextField price;
    @FXML private TextField discount;
    @FXML private TextField amount;
    @FXML private Button atl;

    public static ObservableList<DRItemconsumable> drItemsconsumable = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        debugPrintAllCategories(); // Debug: Show all available categories
        loadConsumableProducts();
        setupComboBoxSearch();

        // Set default quantity to 1
        qty.setText("1");

        products.setOnAction(event -> {
            String selected = products.getSelectionModel().getSelectedItem();
            if (selected != null) {
                fetchProductDetails(selected);
                calculateAmount();
            }
        });

        // Add input validation
        qty.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                qty.setText(newVal.replaceAll("[^\\d]", ""));
            }
            calculateAmount();
        });

        discount.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                discount.setText(oldVal);
            }
            calculateAmount();
        });
    }

    private void loadConsumableProducts() {
        try (Connection conn = PostgresConnect.getConnection()) {
            // Flexible query for spare parts and consumables
            String sql = "SELECT DISTINCT \"Product List\" FROM public.product " +
                         "WHERE \"category\" ILIKE ANY(ARRAY['%spare part%', '%consumable%', '%supply%', '%material%']) " +
                         "ORDER BY \"Product List\"";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            ObservableList<String> consumableProducts = FXCollections.observableArrayList();
            
            while (rs.next()) {
                consumableProducts.add(rs.getString("Product List"));
            }
            
            products.setItems(consumableProducts);
            
            if (consumableProducts.isEmpty()) {
                showAlert("No Products Found", "No spare parts or consumables found in database. Please check your inventory.");
            } else {
                System.out.println("Successfully loaded " + consumableProducts.size() + " consumable products");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not load products: " + e.getMessage());
        }
    }

    // Debug method to check all available categories
    private void debugPrintAllCategories() {
        try (Connection conn = PostgresConnect.getConnection()) {
            String sql = "SELECT DISTINCT \"category\" FROM public.product ORDER BY \"category\"";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            System.out.println("Available categories in database:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("category"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch categories: " + e.getMessage());
        }
    }

    // Enhanced to fetch all product details at once
    private void fetchProductDetails(String productName) {
        try (Connection conn = PostgresConnect.getConnection()) {
            String sql = "SELECT \"Price\", \"Stock\" FROM public.product WHERE \"Product List\" = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, productName);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                price.setText(String.format("%.2f", rs.getDouble("Price")));
                // You could also display stock information if needed
                System.out.println("Stock available: " + rs.getInt("Stock"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not fetch product details");
        }
    }

    private void setupComboBoxSearch() {
        products.setEditable(true);
        products.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                products.show();
            }
        });
    }

    private void calculateAmount() {
        try {
            double priceValue = price.getText().isEmpty() ? 0 : Double.parseDouble(price.getText());
            double discountValue = discount.getText().isEmpty() ? 0 : Double.parseDouble(discount.getText());
            int quantityValue = qty.getText().isEmpty() ? 1 : Integer.parseInt(qty.getText());

            // Validate discount isn't greater than price
            if (discountValue > priceValue) {
                discount.setText(String.format("%.2f", priceValue));
                discountValue = priceValue;
            }

            double total = (priceValue - discountValue) * quantityValue;
            amount.setText(String.format("%.2f", total));
            
        } catch (NumberFormatException e) {
            amount.setText("0.00");
        }
    }

    @FXML
    private void add_to_list() {
        if (!validateInputs()) return;

        DRItemconsumable newItem = new DRItemconsumable(
            products.getValue(),
            Integer.parseInt(qty.getText()),
            Double.parseDouble(price.getText()),
            Double.parseDouble(discount.getText().isEmpty() ? "0" : discount.getText()),
            Double.parseDouble(amount.getText())
        );

        drItemsconsumable.add(newItem);
        showSuccessNotification("Success", newItem.getProduct() + " added to list");
        clearFields();
    }

    private boolean validateInputs() {
        if (products.getValue() == null || products.getValue().isEmpty()) {
            showAlert("Error", "Please select a product");
            return false;
        }
        if (qty.getText().isEmpty() || Integer.parseInt(qty.getText()) <= 0) {
            showAlert("Error", "Please enter a valid quantity");
            return false;
        }
        if (price.getText().isEmpty() || Double.parseDouble(price.getText()) <= 0) {
            showAlert("Error", "Invalid price value");
            return false;
        }
        return true;
    }

    private void clearFields() {
        products.getSelectionModel().clearSelection();
        products.setValue("");
        qty.setText("1");
        price.clear();
        discount.clear();
        amount.clear();
        products.requestFocus();
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