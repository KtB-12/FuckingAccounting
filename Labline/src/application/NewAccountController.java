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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class NewAccountController {

    @FXML private TextField name;
    @FXML private TextField municipality;
    @FXML private ComboBox<String> prov;
    @FXML private TextField region;
    @FXML private TextField area_manager;

    private ObservableList<String> allProvinces = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        prov.setEditable(true);
        loadProvinces();
    }
    
    private AccountsController parentController;

    public void setParentController(AccountsController controller) {
        this.parentController = controller;
    }

    private void loadProvinces() {
        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT province FROM public.location ORDER BY province");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                allProvinces.add(rs.getString("province"));
            }

            prov.setItems(allProvinces);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void filterProvinces(KeyEvent event) {
        String typed = prov.getEditor().getText().toLowerCase();

        ObservableList<String> filtered = allProvinces.filtered(p -> p.toLowerCase().contains(typed));
        prov.setItems(filtered);
        prov.show();

        autofillFieldsFromProvince(typed);
    }

    @FXML
    private void onProvinceSelected() {
        String typed = prov.getEditor().getText();
        autofillFieldsFromProvince(typed);
    }

    private void autofillFieldsFromProvince(String provinceInput) {
        if (provinceInput == null || provinceInput.trim().isEmpty()) return;

        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT region, area_manager FROM public.location WHERE LOWER(province) = LOWER(?) LIMIT 1")) {

            stmt.setString(1, provinceInput.trim());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                region.setText(rs.getString("region"));
                area_manager.setText(rs.getString("area_manager"));
            } else {
                region.clear();
                area_manager.clear();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void add_to_list(MouseEvent event) {
        String clinicName = name.getText().trim();
        String mun = municipality.getText().trim();
        String province = prov.getEditor().getText().trim();
        String reg = region.getText().trim();
        String manager = area_manager.getText().trim();

        if (!allProvinces.contains(province)) {
            showAlert("Invalid Province", "Please select a valid province from the list.");
            return;
        }

        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO public.accounts (name, municipality, prov, region, area_manager) VALUES (?, ?, ?, ?, ?)")) {

            stmt.setString(1, clinicName);
            stmt.setString(2, mun);
            stmt.setString(3, province);
            stmt.setString(4, reg);
            stmt.setString(5, manager);
            stmt.executeUpdate();

            // ✅ Refresh the parent table
            if (parentController != null) {
                parentController.refreshAccountsTable();
            }

            showAlert("Success", "Account successfully added.");
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not add account.");
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void back_to_list(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
