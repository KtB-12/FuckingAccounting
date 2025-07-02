package application;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class dr_machine_add {

    @FXML private DatePicker date;
    @FXML private ComboBox<String> accountname;
    @FXML private TextField municipality;
    @FXML private TextField province;
    @FXML private DatePicker dateinstalled;
    @FXML private TextField drnumber;
    @FXML private TextField downpayment;
    @FXML private TextField terms;
    @FXML private TextField monthlypayment;
    @FXML private TextField firstpaymentdate;
    @FXML private TextField totalamount;
    @FXML private TextField area_manager;
    @FXML private TextField deliveredby;
    @FXML private Button atl;
    @FXML private ImageView back_to_list;

    private ObservableList<String> accountNamesList = FXCollections.observableArrayList();
    private Map<String, AccountInfo> accountInfoMap = new HashMap<>();

    private static class AccountInfo {
        String municipality;
        String province;
        String areaManager;
        AccountInfo(String municipality, String province, String areaManager) {
            this.municipality = municipality;
            this.province = province;
            this.areaManager = areaManager;
        }
    }

    @FXML
    private void initialize() {
        loadAccountNames();
        setupAccountNameSearch();
        bindAutoFillFields();

        downpayment.textProperty().addListener((obs, oldVal, newVal) -> calculateAndFillMonthlyPaymentAndFirstPaymentDate());
        terms.textProperty().addListener((obs, oldVal, newVal) -> calculateAndFillMonthlyPaymentAndFirstPaymentDate());
        dateinstalled.valueProperty().addListener((obs, oldVal, newVal) -> calculateAndFillMonthlyPaymentAndFirstPaymentDate());

        // ✅ Add this to auto-update when DRItems change
        drmachine_popup.drItems.addListener((javafx.collections.ListChangeListener<DRItem>) c -> {
            updateTotalAmountFromDRItems();
        });

        updateTotalAmountFromDRItems();
        calculateAndFillMonthlyPaymentAndFirstPaymentDate();
    }


    private void loadAccountNames() {
        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT name, municipality, prov, area_manager FROM public.accounts ORDER BY name ASC");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                accountNamesList.add(name);
                accountInfoMap.put(name, new AccountInfo(
                        rs.getString("municipality"),
                        rs.getString("prov"),
                        rs.getString("area_manager")
                ));
            }
            accountname.setItems(accountNamesList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupAccountNameSearch() {
        accountname.setEditable(true);
        TextField editor = accountname.getEditor();
        editor.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null) return;
            ObservableList<String> filtered = FXCollections.observableArrayList();
            for (String name : accountNamesList) {
                if (name.toLowerCase().contains(newText.toLowerCase())) {
                    filtered.add(name);
                }
            }
            accountname.setItems(filtered);
            accountname.show();

            // Autofill immediately if exact match while typing
            if (accountInfoMap.containsKey(newText)) {
                fillFields(accountInfoMap.get(newText));
            }
        });
    }

    private void bindAutoFillFields() {
        accountname.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && accountInfoMap.containsKey(newVal)) {
                fillFields(accountInfoMap.get(newVal));
            } else {
                clearFields();
            }
        });
    }

    private void fillFields(AccountInfo info) {
        municipality.setText(info.municipality != null ? info.municipality : "");
        province.setText(info.province != null ? info.province : "");
        area_manager.setText(info.areaManager != null ? info.areaManager : "");
    }

    private void clearFields() {
        municipality.clear();
        province.clear();
        area_manager.clear();
    }

    private void updateTotalAmountFromDRItems() {
        if (!drmachine_popup.drItems.isEmpty()) {
            double total = drmachine_popup.drItems.stream().mapToDouble(DRItem::getAmount).sum();
            totalamount.setText(String.format("%.2f", total));
        } else {
            totalamount.clear();
        }
        calculateAndFillMonthlyPaymentAndFirstPaymentDate();
    }


    @FXML
    private void add_to_list(MouseEvent event) {
        if (drnumber.getText().isEmpty() || accountname.getValue() == null || dateinstalled.getValue() == null) {
            showAlert("Please fill in DR Number, Account Name, and Date Installed.");
            return;
        }

        try (Connection conn = PostgresConnect.getConnection()) {

            // ✅ Check if dr_number already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT 1 FROM public.delivery_receipts WHERE dr_number = ?")) {
                checkStmt.setString(1, drnumber.getText());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        showAlert("DR Number already exists. Please use a unique DR Number.");
                        return;
                    }
                }
            }

            conn.setAutoCommit(false);

            // ✅ Insert into delivery_receipts
            String insertReceipt = """
                INSERT INTO public.delivery_receipts
                (dr_number, date_delivered, receipt_type, account_name, delivered_by, area_manager,
                 date_installed, down_payment, payment_terms_months, monthly_payment,
                 total_amount, status, created_at)
                VALUES (?, ?, 'machine', ?, ?, ?, ?, ?, ?, ?, ?, NULL, CURRENT_DATE)
                """;
            try (PreparedStatement stmt = conn.prepareStatement(insertReceipt)) {
                stmt.setString(1, drnumber.getText());
                stmt.setDate(2, Date.valueOf(dateinstalled.getValue()));
                stmt.setString(3, accountname.getValue());
                stmt.setString(4, deliveredby.getText());
                stmt.setString(5, area_manager.getText());
                stmt.setDate(6, Date.valueOf(dateinstalled.getValue()));

                double dp = Double.parseDouble(downpayment.getText());
                int term = Integer.parseInt(terms.getText());
                double totalAmt = Double.parseDouble(totalamount.getText());
                double monthly = (totalAmt - dp) / term;
                monthlypayment.setText(String.format("%.2f", monthly));

                stmt.setDouble(7, dp);
                stmt.setInt(8, term);
                stmt.setDouble(9, monthly);
                stmt.setDouble(10, totalAmt);

                stmt.executeUpdate();
            }

            // ✅ Get next available item_id
            int nextItemId = 1;
            try (PreparedStatement getMaxIdStmt = conn.prepareStatement("SELECT COALESCE(MAX(item_id), 0) FROM public.delivery_items");
                 ResultSet rs = getMaxIdStmt.executeQuery()) {
                if (rs.next()) {
                    nextItemId = rs.getInt(1) + 1;
                }
            }

            // ✅ Insert into delivery_items
            String insertItem = """
                INSERT INTO public.delivery_items
                (item_id, dr_number, product_id, quantity, unit_price, discount)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
            try (PreparedStatement stmt = conn.prepareStatement(insertItem)) {
                for (DRItem item : drmachine_popup.drItems) {
                    int productId = fetchProductId(conn, item.getProduct());
                    if (productId == -1) {
                        conn.rollback();
                        showAlert("Product not found: " + item.getProduct());
                        return;
                    }

                    stmt.setInt(1, nextItemId++);
                    stmt.setString(2, drnumber.getText());
                    stmt.setInt(3, productId);
                    stmt.setInt(4, item.getQuantity());
                    stmt.setDouble(5, item.getPrice());
                    stmt.setDouble(6, item.getDiscount());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();
            showInfo("Delivery Receipt and Items successfully saved.");
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            showAlert("Error: " + e.getMessage());
        }
    }



    private int fetchProductId(Connection conn, String productName) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM public.product WHERE \"Product List\" = ?")) {
            stmt.setString(1, productName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void addprod(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("popup_for_machine.fxml"));
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
    private void calculateAndFillMonthlyPaymentAndFirstPaymentDate() {
        try {
            if (totalamount.getText().isEmpty() || downpayment.getText().isEmpty() || terms.getText().isEmpty()) {
                monthlypayment.clear();
                firstpaymentdate.clear();
                return;
            }

            double dp = Double.parseDouble(downpayment.getText());
            int term = Integer.parseInt(terms.getText());
            double totalAmt = Double.parseDouble(totalamount.getText());

            if (term <= 0) {
                monthlypayment.clear();
                firstpaymentdate.clear();
                return;
            }

            double monthly = (totalAmt - dp) / term;
            monthlypayment.setText(String.format("%.2f", monthly));

            LocalDate baseDate = dateinstalled.getValue() != null ? dateinstalled.getValue() : LocalDate.now();
            LocalDate nextPaymentDate = baseDate.plusDays(term);
            firstpaymentdate.setText(nextPaymentDate.toString());

        } catch (NumberFormatException e) {
            monthlypayment.clear();
            firstpaymentdate.clear();
        }
    }


    @FXML
    private void list(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dr_list_machine.fxml"));
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

    private static class Delta {
        double x, y;
    }
}
