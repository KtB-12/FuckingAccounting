package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class DeliveryReceiptsController_view implements SidebarStateAware {

	@FXML private Pane sidebar;
    @FXML private Pane homepane;
    @FXML private Pane contenth;

    @FXML private ImageView minbutton, maximizebutton;
    @FXML private ImageView edit, delete, archive;
    @FXML private Button homesign, accbutton, prosign, collectionsign, summarixsign, logsign, settingsb1, deliversign;
    @FXML private ImageView homeicon, accicon, prodicon, colloectionicon, summarixicon, logicon1, settingsicon, deliviericon;
	
	@FXML private javafx.scene.control.ChoiceBox<String> status;
	@FXML private Button back;

	@FXML private javafx.scene.control.TextField account_name;
	@FXML private javafx.scene.control.TextField address;
	@FXML private javafx.scene.control.TextField date;
	@FXML private javafx.scene.control.TextField terms;
	@FXML private javafx.scene.control.TextField duedate;
	@FXML private javafx.scene.control.TextField area_manager;
	@FXML private javafx.scene.control.TextField total;
	@FXML private javafx.scene.control.TextField delivered_by;
	@FXML private javafx.scene.control.TextField drnumber;

	@FXML private javafx.scene.control.TableView<DeliveryItemView> deltable;
	@FXML private TableColumn<DeliveryItemView, Integer> qty;
	@FXML private TableColumn<DeliveryItemView, String> product;
	@FXML private TableColumn<DeliveryItemView, Double> price;
	@FXML private TableColumn<DeliveryItemView, Double> discount;
	@FXML private TableColumn<DeliveryItemView, Double> amount;

    private static final double SIDEBAR_MIN_WIDTH = 115;
    private static final double SIDEBAR_MAX_WIDTH = 260;
    private static final double HOME_PANE_MIN_WIDTH = 80;
    private static final double HOME_PANE_MAX_WIDTH = 230;
    private static final double CONTENT_SHIFT = -70;
    private static final Duration ANIMATION_DURATION = Duration.millis(300);

    private double originalContentX;

    @FXML
    private void initialize() {
        maximizebutton.setVisible(false);
        sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
        homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
        originalContentX = contenth.getLayoutX();

        // Update to allowed statuses only
        status.getItems().addAll("Uncollected", "Partial", "Collected", "Cancelled");

        status.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                updateStatus(newVal);
            }
        });

        initializeStatusChoiceBox();
        applySidebarState(SidebarState.isMinimized());
        
        
    }

    @Override
    public void applySidebarState(boolean minimized) {
        if (minimized) {
            sidebar.setPrefWidth(SIDEBAR_MIN_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MIN_WIDTH);
            toggleSidebarText(false);
            minbutton.setVisible(false);
            maximizebutton.setVisible(true);
            contenth.setLayoutX(originalContentX + CONTENT_SHIFT);
        } else {
            sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
            toggleSidebarText(true);
            minbutton.setVisible(true);
            maximizebutton.setVisible(false);
            contenth.setLayoutX(originalContentX);
        }
    }

    @FXML
    private void min(MouseEvent event) {
        if (SidebarState.isMinimized()) return;

        toggleSidebarText(false);
        minbutton.setVisible(false);

        Timeline timeline = new Timeline(
            new KeyFrame(ANIMATION_DURATION,
                new KeyValue(sidebar.prefWidthProperty(), SIDEBAR_MIN_WIDTH),
                new KeyValue(homepane.prefWidthProperty(), HOME_PANE_MIN_WIDTH),
                new KeyValue(contenth.layoutXProperty(), originalContentX + CONTENT_SHIFT - 35)
            )
        );

        timeline.setOnFinished(e -> {
            maximizebutton.setVisible(true);
            SidebarState.setMinimized(true);
        });

        timeline.play();
    }
    
    public void loadDeliveryReceiptData(String drNumber) {
        try (Connection conn = PostgresConnect.getConnection()) {
            // Load header
            String headerQuery = """
                SELECT dr_number, date_delivered, account_name, delivered_by, area_manager,
                       payment_terms_months, total_amount, status
                FROM public.delivery_receipts
                WHERE dr_number = ?
                """;
            String accountName = null;

            try (PreparedStatement stmt = conn.prepareStatement(headerQuery)) {
                stmt.setString(1, drNumber);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    drnumber.setText(rs.getString("dr_number"));

                    LocalDate dateDelivered = rs.getDate("date_delivered") != null
                            ? rs.getDate("date_delivered").toLocalDate()
                            : null;
                    date.setText(dateDelivered != null ? dateDelivered.toString() : "");

                    accountName = rs.getString("account_name");
                    account_name.setText(accountName);

                    delivered_by.setText(rs.getString("delivered_by"));
                    area_manager.setText(rs.getString("area_manager"));
                    terms.setText(String.valueOf(rs.getInt("payment_terms_months")));
                    total.setText(String.format("%.2f", rs.getDouble("total_amount")));
                    status.setValue(capitalizeStatus(rs.getString("status")));
                }
            }

            // Load and display address from accounts
            if (accountName != null) {
                String addressQuery = "SELECT municipality, prov FROM public.accounts WHERE name = ?";
                try (PreparedStatement stmt = conn.prepareStatement(addressQuery)) {
                    stmt.setString(1, accountName);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String municipality = rs.getString("municipality");
                        String prov = rs.getString("prov");

                        StringBuilder combinedAddress = new StringBuilder();
                        if (municipality != null && !municipality.isEmpty()) {
                            combinedAddress.append(municipality);
                        }
                        if (prov != null && !prov.isEmpty()) {
                            if (combinedAddress.length() > 0) {
                                combinedAddress.append(", ");
                            }
                            combinedAddress.append(prov);
                        }
                        address.setText(combinedAddress.toString());
                    } else {
                        address.setText("");
                    }
                }
            } else {
                address.setText("");
            }

            // Load items
            ObservableList<DeliveryItemView> items = FXCollections.observableArrayList();
            String itemQuery = """
                SELECT di.quantity, di.unit_price, di.discount, p."Product List" AS product_name
                FROM public.delivery_items di
                JOIN public.product p ON di.product_id = p.id
                WHERE di.dr_number = ?
                """;
            try (PreparedStatement stmt = conn.prepareStatement(itemQuery)) {
                stmt.setString(1, drNumber);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    double unitPrice = rs.getDouble("unit_price");
                    int quantity = rs.getInt("quantity");
                    double discountValue = rs.getDouble("discount");
                    double computedAmount = unitPrice * quantity - discountValue;

                    items.add(new DeliveryItemView(
                            quantity,
                            quantity, rs.getString("product_name"),
                            unitPrice,
                            discountValue,
                            computedAmount
                    ));
                }
            }

            // Bind to table
            qty.setCellValueFactory(cell -> cell.getValue().quantityProperty().asObject());
            product.setCellValueFactory(cell -> cell.getValue().productProperty());
            price.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());
            discount.setCellValueFactory(cell -> cell.getValue().discountProperty().asObject());
            amount.setCellValueFactory(cell -> cell.getValue().amountProperty().asObject());
            deltable.setItems(items);
            deltable.refresh();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Could not load delivery receipt data.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    private String capitalizeStatus(String status) {
        if (status == null) return "Uncollected";
        switch (status.toLowerCase()) {
            case "collected": return "Collected";
            case "partial": return "Partial";
            case "cancelled": return "Cancelled";
            case "uncollected":
            default: return "Uncollected";
        }
    }

    @FXML
    private void updateStatus(String newVal) {
        String newStatus = status.getValue();

        // Validate against allowed statuses
        if (newStatus == null ||
            !(newStatus.equals("Uncollected") ||
              newStatus.equals("Partial") ||
              newStatus.equals("Collected") ||
              newStatus.equals("Cancelled"))) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Status");
            alert.setHeaderText("Invalid status selected.");
            alert.setContentText("Please select a valid status: Uncollected, Partial, Collected, or Cancelled.");
            alert.showAndWait();
            return;
        }

        newStatus = newStatus.toLowerCase();

        String drNum = drnumber.getText();
        if (drNum == null || drNum.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No DR Number");
            alert.setHeaderText(null);
            alert.setContentText("No delivery receipt is loaded to update.");
            alert.showAndWait();
            return;
        }

        String updateQuery = "UPDATE public.delivery_receipts SET status = ? WHERE dr_number = ?";
        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, newStatus);
            stmt.setString(2, drNum);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Status updated successfully.");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Update Failed");
                alert.setHeaderText(null);
                alert.setContentText("Could not find delivery receipt to update.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Could not update status.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    private void max(MouseEvent event) {
        if (!SidebarState.isMinimized()) return;

        Timeline timeline = new Timeline(
            new KeyFrame(ANIMATION_DURATION,
                new KeyValue(sidebar.prefWidthProperty(), SIDEBAR_MAX_WIDTH),
                new KeyValue(homepane.prefWidthProperty(), HOME_PANE_MAX_WIDTH),
                new KeyValue(contenth.layoutXProperty(), originalContentX)
            )
        );

        timeline.setOnFinished(e -> {
            toggleSidebarText(true);
            maximizebutton.setVisible(false);
            minbutton.setVisible(true);
            SidebarState.setMinimized(false);
        });

        timeline.play();
    }

    private void toggleSidebarText(boolean visible) {
        homesign.setVisible(visible);
        accbutton.setVisible(visible);
        prosign.setVisible(visible);
        deliversign.setVisible(visible);
        collectionsign.setVisible(visible);
        summarixsign.setVisible(visible);
        settingsb1.setVisible(visible);
        logsign.setVisible(visible);
    }

    // Navigation & Utility Methods
    @FXML private void col(MouseEvent event) { loadFXML("collectionreciepts_list.fxml", event); }
    @FXML private void openMyAccount(MouseEvent event) { loadFXML("MyACCOUNT.fxml", event); }
    @FXML private void minimize(MouseEvent event) { ((Stage) ((Node) event.getSource()).getScene().getWindow()).setIconified(true); }
    @FXML private void exit(MouseEvent event) { ((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); }
    @FXML private void prod(MouseEvent event) { loadFXML("products_list.fxml", event); }
    @FXML private void sum(MouseEvent event) { loadFXML("executive_summary.fxml", event); }
    @FXML private void account1(MouseEvent event) { loadFXML("List_of_accounts.fxml", event); }
    @FXML private void homeic(MouseEvent event) { loadFXML("Homepage.fxml", event); }
    @FXML private void homesi(MouseEvent event) { loadFXML("Homepage.fxml", event); }
    @FXML private void back(MouseEvent event) { loadFXML("deliveryreceipts_list.fxml", event); }
    @FXML private void delete(MouseEvent event) { System.out.println("delete clicked"); }
    

    @FXML
    private void out(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("log_out.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

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

    public static class Delta {
        double x, y;
    }
    
    @FXML
    private void edit(MouseEvent event) {
        DeliveryItemView selectedItem = deltable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select an item in the table to edit.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dr_edit_delivery.fxml"));
            Parent root = loader.load();

            dr_viewedit controller = loader.getController();
            controller.setParentController(this);
            controller.setCurrentDrNumber(drnumber.getText());
            controller.loadDataForEdit(selectedItem); // pass selected item data

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

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
    private void ad(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dr_add_delivery.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            
            dr_viewadd controller = loader.getController();
            controller.setParentController(this);
            controller.setCurrentDrNumber(drnumber.getText());
            controller.setProductCategoryFilter("Machine"); 

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

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
    private void addacc(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("deliveryreciepts_add.fxml"));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.setResizable(false);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.setScene(new Scene(root));
            popupStage.centerOnScreen();
            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to open deliveryreciepts_add.fxml");
        }
    }
    
    private void initializeStatusChoiceBox() {
        // Allowed statuses only
        status.setItems(FXCollections.observableArrayList("Uncollected", "Partial", "Collected", "Cancelled"));
    }
    private void loadFXML(String fxmlFile, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            if (loader.getController() instanceof SidebarStateAware) {
                ((SidebarStateAware) loader.getController()).applySidebarState(SidebarState.isMinimized());
            }

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading " + fxmlFile);
        }
    }
}