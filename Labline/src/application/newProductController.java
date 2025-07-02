package application;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class newProductController {

    @FXML private TextField productNameField;
    @FXML private TextField productCodeField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> category;
    @FXML private Button addToListButton;
    @FXML private Button backToTableButton;
    @FXML private ImageView exitIcon;
    @FXML private ImageView minimizeIcon;

    private ProductViewController productViewController;

    public void setProductViewController(ProductViewController controller) {
        this.productViewController = controller;
    }

    @FXML
    private void initialize() {
        category.setItems(FXCollections.observableArrayList("Consumable", "Machine", "Spare parts"));
    }

    @FXML
    private void exit(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void minimize(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void handleAddToList(MouseEvent event) {
        try {
            validateInputs();
            Product newProduct = createProductFromInput();

            if (productExists(newProduct.getProductCode())) {
                showAlert("Duplicate Product", 
                         "Product with code " + newProduct.getProductCode() + " already exists.");
                return;
            }

            if (addProductToDatabase(newProduct)) {
                if (productViewController != null) {
                    productViewController.addProductToTable(newProduct);
                }

                Stage stage = (Stage) addToListButton.getScene().getWindow();
                stage.close();
                showSuccess("Product added successfully.");
            }

        } catch (IllegalArgumentException e) {
            showAlert("Input Error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add product: " + e.getMessage());
        }
    }

    private void validateInputs() {
        if (productCodeField.getText().trim().isEmpty())
            throw new IllegalArgumentException("Product code cannot be empty");
        if (productNameField.getText().trim().isEmpty())
            throw new IllegalArgumentException("Product name cannot be empty");
        if (priceField.getText().trim().isEmpty())
            throw new IllegalArgumentException("Price cannot be empty");
        if (category.getValue() == null)
            throw new IllegalArgumentException("Category must be selected");

        try {
            BigDecimal price = new BigDecimal(priceField.getText().trim());
            if (price.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Price cannot be negative");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a valid price");
        }
    }

    private Product createProductFromInput() {
        return new Product(
            getNextProductId(),
            productCodeField.getText().trim(),
            productNameField.getText().trim(),
            new BigDecimal(priceField.getText().trim()),
            category.getValue()
        );
    }

    private int getNextProductId() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 AS next_id FROM product";
        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("next_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // fallback
    }

    private boolean productExists(String productCode) throws SQLException {
        String sql = "SELECT 1 FROM product WHERE \"Product Code\" = ?";
        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productCode);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean addProductToDatabase(Product product) throws SQLException {
        String sql = "INSERT INTO product (id, \"Product Code\", \"Product List\", \"Price\", category) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, product.getId());
            stmt.setString(2, product.getProductCode());
            stmt.setString(3, product.getProductList());
            stmt.setBigDecimal(4, product.getPrice());
            stmt.setString(5, product.getCategory());

            return stmt.executeUpdate() > 0;
        }
    }

    private void showAlert(String title, String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }

    private void showSuccess(String message) {
        new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK).showAndWait();
    }

    @FXML
    private void handleBackToTable(MouseEvent event) {
        exit(event);
    }
}
