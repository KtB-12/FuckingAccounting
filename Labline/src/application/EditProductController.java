package application;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class EditProductController {

    @FXML private TextField codeField;
    @FXML private TextField listField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> category;

    private Product selectedProduct;
    private Runnable onUpdateCallback;
    private String originalCode;

    public void setProduct(Product product) {
        this.selectedProduct = product;
        this.originalCode = product.getProductCode();
        populateFields();
    }

    @FXML
    private void initialize() {
        category.setItems(FXCollections.observableArrayList("Consumable", "Machine", "Spare parts"));
    }

    private void populateFields() {
        if (selectedProduct != null) {
            codeField.setText(selectedProduct.getProductCode());
            listField.setText(selectedProduct.getProductList());
            priceField.setText(
                selectedProduct.getPrice() != null ? selectedProduct.getPrice().toPlainString() : ""
            );
            category.setValue(selectedProduct.getCategory()); // Populate category
        }
    }

    public void setOnUpdateCallback(Runnable callback) {
        this.onUpdateCallback = callback;
    }

    @FXML
    private void exit(MouseEvent event) {
        closeWindow(event);
    }

    @FXML
    private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    private boolean validateInputs() {
        if (selectedProduct == null) {
            showAlert("Error", "No product selected.");
            return false;
        }

        if (codeField.getText().trim().isEmpty() ||
            listField.getText().trim().isEmpty() ||
            category.getValue() == null) {
            showAlert("Missing Fields", "Please fill in all fields and select a category.");
            return false;
        }

        try {
            if (!priceField.getText().trim().isEmpty()) {
                new BigDecimal(priceField.getText().trim());
            }
            return true;
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Price must be a valid number.");
            return false;
        }
    }

    private boolean updateProductInDatabase() {
        String sql = "UPDATE product SET \"Product Code\" = ?, \"Product List\" = ?, \"Price\" = ?, category = ? " +
                     "WHERE \"Product Code\" = ?";

        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String newCode = codeField.getText().trim();
            String newList = listField.getText().trim();
            String newCategory = category.getValue();
            String priceText = priceField.getText().trim();
            BigDecimal newPrice = priceText.isEmpty() ? null : new BigDecimal(priceText);

            stmt.setString(1, newCode.isEmpty() ? null : newCode);
            stmt.setString(2, newList.isEmpty() ? null : newList);
            if (newPrice == null) {
                stmt.setNull(3, java.sql.Types.NUMERIC);
            } else {
                stmt.setBigDecimal(3, newPrice);
            }
            stmt.setString(4, newCategory);
            stmt.setString(5, originalCode);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                showAlert("Update Failed", "No product was updated.");
                return false;
            }

            selectedProduct.setProductCode(newCode);
            selectedProduct.setProductList(newList);
            selectedProduct.setPrice(newPrice);
            selectedProduct.setCategory(newCategory);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update product: " + e.getMessage());
            return false;
        }
    }

    @FXML
    private void saveChanges() {
        if (!validateInputs()) return;

        try {
            if (updateProductInDatabase()) {
                showSuccessAlert("Update Successful", "Product updated successfully!");
                if (onUpdateCallback != null) {
                    try {
                        onUpdateCallback.run();
                    } catch (Exception e) {
                        showWarningAlert("Update Notification Failed",
                            "Product was updated but there was an issue refreshing the view: " + e.getMessage());
                    }
                }
                closeWindow();
            }
        } catch (Exception e) {
            showErrorAlert("Update Failed", "Failed to update product: " + e.getMessage());
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) codeField.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private void closeWindow(MouseEvent event) {
        if (event != null && event.getSource() != null) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarningAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
