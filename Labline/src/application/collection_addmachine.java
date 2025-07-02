package application;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class collection_addmachine implements Initializable {

    @FXML private TextField cr_number;
    @FXML private ComboBox dr_numberr;
    @FXML private TextField name;
    @FXML private TextField address;
    @FXML private DatePicker date;
    @FXML private TextField terms;
    @FXML private TextField machine;
    @FXML private TextField price;
    @FXML private TextField total;
    @FXML private TextField recievedby;
    @FXML private Button atl;
    @FXML private Button list;
    @FXML private Button add;

    private ObservableList<String> drNumbersList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        date.setValue(LocalDate.now());
        loadDRNumbers();

        dr_numberr.setEditable(true);
        dr_numberr.getEditor().addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            String input = dr_numberr.getEditor().getText();
            if (input.isEmpty()) {
                dr_numberr.setItems(drNumbersList);
            } else {
                List<String> filtered = new ArrayList<>();
                for (String dr : drNumbersList) {
                    if (dr.toLowerCase().contains(input.toLowerCase())) {
                        filtered.add(dr);
                    }
                }
                dr_numberr.setItems(FXCollections.observableArrayList(filtered));
                dr_numberr.show();
            }
        });

        dr_numberr.setOnAction(e -> {
            if (dr_numberr.getValue() != null) {
                autofillFields((String) dr_numberr.getValue());
            }
        });

        // ✅ Automatically update total whenever a payment is added
        PaymentSessionData.getInstance().getPaymentRecords().addListener(
            (javafx.collections.ListChangeListener<PaymentRecord>) change -> updateTotal()
        );

        // ✅ Initialize total immediately on open in case previous session data exists
        updateTotal();
    }

    private void updateTotal() {
        double sum = PaymentSessionData.getInstance().getPaymentRecords().stream()
            .mapToDouble(record -> {
                try {
                    return Double.parseDouble(record.getAmount().replaceAll(",", ""));
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            })
            .sum();
        total.setText(String.format("%.2f", sum));
    }

    private void loadDRNumbers() {
        String query = "SELECT dr_number FROM delivery_receipts WHERE receipt_type = 'machine'";
        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                drNumbersList.add(rs.getString("dr_number"));
            }
            dr_numberr.setItems(drNumbersList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void autofillFields(String selectedDR) {
        try (Connection conn = PostgresConnect.getConnection()) {
            String receiptQuery = "SELECT account_name, payment_terms_months FROM delivery_receipts WHERE dr_number = ?";
            try (PreparedStatement stmt = conn.prepareStatement(receiptQuery)) {
                stmt.setString(1, selectedDR);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String accountName = rs.getString("account_name");
                        name.setText(accountName);
                        terms.setText(rs.getString("payment_terms_months"));

                        String addressQuery = "SELECT municipality, prov FROM accounts WHERE name = ?";
                        try (PreparedStatement stmt2 = conn.prepareStatement(addressQuery)) {
                            stmt2.setString(1, accountName);
                            try (ResultSet rs2 = stmt2.executeQuery()) {
                                if (rs2.next()) {
                                    String fullAddress = rs2.getString("municipality") + ", " + rs2.getString("prov");
                                    address.setText(fullAddress);
                                }
                            }
                        }
                    }
                }
            }

            String machineQuery = """
                SELECT p."Product List", di.amount
                FROM delivery_items di
                JOIN product p ON di.product_id = p.id
                WHERE di.dr_number::TEXT = ?
                LIMIT 1
                """;
            try (PreparedStatement stmt = conn.prepareStatement(machineQuery)) {
                stmt.setString(1, selectedDR);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        machine.setText(rs.getString("Product List"));
                        price.setText(rs.getString("amount"));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void add_to_list() {
        try (Connection conn = PostgresConnect.getConnection()) {
            conn.setAutoCommit(false);

            String crNum = cr_number.getText().trim();
            String drNum = dr_numberr.getValue() != null ? dr_numberr.getValue().toString().trim() : null;
            String accName = name.getText().trim();
            String addr = address.getText().trim();
            LocalDate receiptDate = date.getValue();
            String termsText = terms.getText().trim();
            String machineText = machine.getText().trim();
            String priceText = price.getText().trim();
            String totalText = total.getText().trim();
            String receivedBy = recievedby.getText().trim();

            // Validate required fields
            if (crNum.isEmpty() || drNum == null || accName.isEmpty() || addr.isEmpty() || receiptDate == null || 
                termsText.isEmpty() || machineText.isEmpty() || priceText.isEmpty() || totalText.isEmpty() || 
                receivedBy.isEmpty()) {
                showPrompt("Validation Error", "Please fill in all required fields.");
                return;
            }

            // === VALIDATION CHECKS ===
            // Check if dr_number exists in delivery_receipts
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM delivery_receipts WHERE dr_number = ?")) {
                ps.setString(1, drNum);
                ResultSet rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    showPrompt("Validation Error", "DR number does not exist in delivery receipts.");
                    conn.rollback();
                    return;
                }
            }

            // Check if address exists in accounts
            String[] parts = addr.split(",\\s*");
            if (parts.length < 2) {
                showPrompt("Validation Error", "Invalid address format. Please use format: Municipality, Province");
                conn.rollback();
                return;
            }
            String municipality = parts[0];
            String prov = parts[1];
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM accounts WHERE municipality = ? AND prov = ?")) {
                ps.setString(1, municipality);
                ps.setString(2, prov);
                ResultSet rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    showPrompt("Validation Error", "Address not found in our records. Please verify the address.");
                    conn.rollback();
                    return;
                }
            }

            // Check if terms exist in delivery_receipts
            try {
                int termsValue = Integer.parseInt(termsText);
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT COUNT(*) FROM delivery_receipts WHERE payment_terms_months = ?")) {
                    ps.setInt(1, termsValue);
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        showPrompt("Validation Error", "Payment terms not found for this DR number.");
                        conn.rollback();
                        return;
                    }
                }
            } catch (NumberFormatException e) {
                showPrompt("Validation Error", "Invalid payment terms format. Please enter a valid number.");
                conn.rollback();
                return;
            }

            // Check machine and price from delivery_items + product
            try (PreparedStatement ps = conn.prepareStatement("""
            	    SELECT p."Product List", di.amount
            	    FROM delivery_items di
            	    JOIN product p ON di.product_id = p.id
            	    WHERE di.dr_number::TEXT = ? LIMIT 1
            	""")) {
                ps.setString(1, drNum);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String dbMachine = rs.getString(1);
                    String dbPrice = rs.getString(2);
                    if (!dbMachine.equals(machineText) || !dbPrice.equals(priceText)) {
                        showPrompt("Validation Error", "Machine or price does not match the original delivery receipt.");
                        conn.rollback();
                        return;
                    }
                } else {
                    showPrompt("Validation Error", "No product information found for this DR number.");
                    conn.rollback();
                    return;
                }
            }

            // Insert into collection_receipts
            try (PreparedStatement ps = conn.prepareStatement("""
            	    INSERT INTO collection_receipts (
            	        cr_number, dr_number, account_name, date, received_by, total_amount, status, bank_name
            	    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            	""")) {
            	    ps.setString(1, crNum);
            	    ps.setString(2, drNum);
            	    ps.setString(3, accName);
            	    ps.setObject(4, receiptDate);
            	    ps.setString(5, receivedBy);
            	    ps.setBigDecimal(6, new java.math.BigDecimal(totalText.replace(",", "")));
            	    ps.setNull(7, java.sql.Types.VARCHAR);
            	    ps.setNull(8, java.sql.Types.VARCHAR);
            	    ps.executeUpdate();
            	}

            // Insert into collection_dr_links
            int drLinkId = 0;
            try (PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO collection_dr_links (cr_number, dr_number) VALUES (?, ?) RETURNING id
                    """)) {
                ps.setString(1, crNum);
                ps.setString(2, drNum);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    drLinkId = rs.getInt(1);
                }
            }

            // Get next payment_id
            int paymentId = 1;
            try (PreparedStatement ps = conn.prepareStatement("SELECT COALESCE(MAX(payment_id), 0) + 1 FROM payment_details")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    paymentId = rs.getInt(1);
                }
            }
            
            // Validate payment records exist
            if (PaymentSessionData.getInstance().getPaymentRecords().isEmpty()) {
                showPrompt("Validation Error", "No payment records found. Please add at least one payment method.");
                conn.rollback();
                return;
            }
            
            // Insert payment details
            for (PaymentRecord pr : PaymentSessionData.getInstance().getPaymentRecords()) {
                try (PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO payment_details (
                        payment_id, payment_method, amount, cheque_date, bank_name,
                        cheque_number, transfer_date, reference_number, cr_number
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """)) {

                    String method = pr.getPaymentMethod().trim().toLowerCase().replace(" ", "_");

                    if (!method.equals("cheque") && !method.equals("fund_transfer") && !method.equals("cash")) {
                        showPrompt("Validation Error", "Invalid payment method: " + pr.getPaymentMethod());
                        conn.rollback();
                        return;
                    }

                    ps.setInt(1, paymentId);
                    ps.setString(2, method);
                    ps.setBigDecimal(3, new java.math.BigDecimal(pr.getAmount().replace(",", "")));

                    if ("cheque".equals(method)) {
                        if (pr.getChequeDate() == null || pr.getBank() == null || pr.getBank().isEmpty() || 
                            pr.getChequeNumber() == null || pr.getChequeNumber().isEmpty()) {
                            showPrompt("Validation Error", "Missing cheque details. Please provide cheque date, bank, and cheque number.");
                            conn.rollback();
                            return;
                        }
                        ps.setObject(4, pr.getChequeDate());
                        ps.setString(5, pr.getBank());
                        ps.setString(6, pr.getChequeNumber());
                        ps.setNull(7, java.sql.Types.DATE);
                        ps.setNull(8, java.sql.Types.VARCHAR);
                    } else if ("fund_transfer".equals(method)) {
                        if (pr.getBank() == null || pr.getBank().isEmpty() || pr.getTransferDate() == null || 
                            pr.getReferenceNumber() == null || pr.getReferenceNumber().isEmpty()) {
                            showPrompt("Validation Error", "Missing transfer details. Please provide bank, transfer date, and reference number.");
                            conn.rollback();
                            return;
                        }
                        ps.setNull(4, java.sql.Types.DATE);
                        ps.setString(5, pr.getBank());
                        ps.setNull(6, java.sql.Types.VARCHAR);
                        ps.setObject(7, pr.getTransferDate());
                        ps.setString(8, pr.getReferenceNumber());
                    } else if ("cash".equals(method)) {
                        ps.setNull(4, java.sql.Types.DATE);
                        ps.setNull(5, java.sql.Types.VARCHAR);
                        ps.setNull(6, java.sql.Types.VARCHAR);
                        ps.setNull(7, java.sql.Types.DATE);
                        ps.setNull(8, java.sql.Types.VARCHAR);
                    }

                    ps.setString(9, crNum);
                    ps.executeUpdate();
                    paymentId++;
                }
            }

            conn.commit();
            showPrompt("Success", "Collection receipt and payment details saved successfully!");

            // Clear PaymentSessionData after successful save
            PaymentSessionData.getInstance().getPaymentRecords().clear();

        } catch (SQLException e) {
            showPrompt("Database Error", "An error occurred while saving to the database: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showPrompt("Validation Error", "Invalid number format in amount fields. Please check your inputs.");
        } catch (Exception e) {
            showPrompt("Error", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method to show alerts
 

    @FXML
    private void list(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("collection_list_machine.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            Node source = (Node) event.getSource();
            Bounds boundsInScreen = source.localToScreen(source.getBoundsInLocal());
            stage.setX(boundsInScreen.getMinX());
            stage.setY(boundsInScreen.getMaxY() + 5);

            final Delta dragDelta = new Delta();
            root.setOnMousePressed(e -> {
                dragDelta.x = e.getSceneX();
                dragDelta.y = e.getSceneY();
            });
            root.setOnMouseDragged(e -> {
                stage.setX(e.getScreenX() - dragDelta.x);
                stage.setY(e.getScreenY() - dragDelta.y);
            });

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void back_to_list(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void add(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("collectionFORM machine_ADD PAYMENT DETAILS.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            Node source = (Node) event.getSource();
            Bounds boundsInScreen = source.localToScreen(source.getBoundsInLocal());
            stage.setX(boundsInScreen.getMinX());
            stage.setY(boundsInScreen.getMaxY() + 5);

            final Delta dragDelta = new Delta();
            root.setOnMousePressed(e -> {
                dragDelta.x = e.getSceneX();
                dragDelta.y = e.getSceneY();
            });
            root.setOnMouseDragged(e -> {
                stage.setX(e.getScreenX() - dragDelta.x);
                stage.setY(e.getScreenY() - dragDelta.y);
            });

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showPrompt(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    private static class Delta {
        double x, y;
    }
}
