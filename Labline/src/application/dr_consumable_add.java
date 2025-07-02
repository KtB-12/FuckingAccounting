package application;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class dr_consumable_add implements Initializable {

    @FXML private DatePicker date;
    @FXML private ComboBox<String> accountname;
    @FXML private TextField prov;
    @FXML private TextField municipality;
    @FXML private TextField drnumber;
    @FXML private TextField totalamount;
    @FXML private TextField area_manager;
    @FXML private TextField deliveredby;
    @FXML private TextField terms;

    private ObservableList<String> accountNamesList = FXCollections.observableArrayList();
    private Map<String, AccountInfo> accountInfoMap = new HashMap<>();

    private static class AccountInfo {
        String municipality, prov, areaManager;
        AccountInfo(String municipality, String prov, String areaManager) {
            this.municipality = municipality;
            this.prov = prov;
            this.areaManager = areaManager;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAccountNames();
        setupAccountSearch();
        bindAccountAutoFill();
        bindTotalAmountAutoFill();
    }

    private void loadAccountNames() {
        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT name, municipality, prov, area_manager FROM public.accounts ORDER BY name ASC");
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

    private void setupAccountSearch() {
        accountname.setEditable(true);
        TextField editor = accountname.getEditor();
        editor.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            ObservableList<String> filtered = FXCollections.observableArrayList();
            for (String name : accountNamesList) {
                if (name.toLowerCase().contains(newVal.toLowerCase())) {
                    filtered.add(name);
                }
            }
            accountname.setItems(filtered);
            accountname.show();

            if (accountInfoMap.containsKey(newVal)) {
                fillAccountFields(accountInfoMap.get(newVal));
            }
        });
    }

    private void bindAccountAutoFill() {
        accountname.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && accountInfoMap.containsKey(newVal)) {
                fillAccountFields(accountInfoMap.get(newVal));
            } else {
                clearAccountFields();
            }
        });
    }

    private void fillAccountFields(AccountInfo info) {
        municipality.setText(info.municipality != null ? info.municipality : "");
        prov.setText(info.prov != null ? info.prov : "");
        area_manager.setText(info.areaManager != null ? info.areaManager : "");
    }

    private void clearAccountFields() {
        municipality.clear();
        prov.clear();
        area_manager.clear();
    }

    private void bindTotalAmountAutoFill() {
        drconsumable_popup.drItemsconsumable.addListener((javafx.collections.ListChangeListener<DRItemconsumable>) c -> updateTotalAmountFromItems());
        updateTotalAmountFromItems();
    }

    private void updateTotalAmountFromItems() {
        if (!drconsumable_popup.drItemsconsumable.isEmpty()) {
            double total = drconsumable_popup.drItemsconsumable.stream().mapToDouble(DRItemconsumable::getAmount).sum();
            totalamount.setText(String.format("%.2f", total));
        } else {
            totalamount.clear();
        }
    }

    @FXML
    private void addac(MouseEvent event) {
        if (drnumber.getText().isEmpty() || accountname.getValue() == null || date.getValue() == null) {
            showAlert("Please fill in DR Number, Account Name, and Date.");
            return;
        }

        try (Connection conn = PostgresConnect.getConnection()) {
            // Check for duplicate DR number
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

            String insertSQL = """
                INSERT INTO public.delivery_receipts
                (dr_number, date_delivered, receipt_type, account_name, delivered_by, area_manager,
                 date_installed, down_payment, payment_terms_months, monthly_payment,
                 total_amount, status, created_at)
                VALUES (?, ?, 'consumable', ?, ?, ?, NULL, NULL, ?, NULL, ?, NULL, CURRENT_DATE)
                """;

            try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                stmt.setString(1, drnumber.getText());
                stmt.setDate(2, Date.valueOf(date.getValue()));
                stmt.setString(3, accountname.getValue());
                stmt.setString(4, deliveredby.getText());
                stmt.setString(5, area_manager.getText());

                if (terms.getText() != null && !terms.getText().isEmpty()) {
                    stmt.setInt(6, Integer.parseInt(terms.getText()));
                } else {
                    stmt.setNull(6, java.sql.Types.INTEGER);
                }

                if (totalamount.getText() != null && !totalamount.getText().isEmpty()) {
                    stmt.setDouble(7, Double.parseDouble(totalamount.getText()));
                } else {
                    stmt.setNull(7, java.sql.Types.DOUBLE);
                }

                stmt.executeUpdate();
            }

            showInfo("Delivery Receipt successfully saved.");
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            showAlert("Error: " + e.getMessage());
        }
    }


    @FXML
    private void addprod(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("popup_for_consumable.fxml"));
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
    private void list(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dr_list_consumable.fxml"));
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

    private static class Delta {
        double x, y;
    }
}
