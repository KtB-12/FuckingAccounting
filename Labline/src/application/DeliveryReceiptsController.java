package application;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class DeliveryReceiptsController implements SidebarStateAware {

    @FXML private Pane sidebar, homepane;
    @FXML private ImageView minbutton, maximizebutton;
    @FXML private ImageView edit, delete, archive;
    @FXML private Button homesign, accbutton, prosign, collectionsign, summarixsign, logsign, settingsb1, deliversign;
    @FXML private ImageView homeicon, accicon, prodicon, colloectionicon, summarixicon, logicon1, settingsicon, deliviericon;

    @FXML private TableView<DeliveryReceipt> deltable;
    @FXML private Text delsign;

    @FXML private ImageView view;
    @FXML private ImageView view1;
    @FXML private ChoiceBox<?> choiceBox;
    @FXML private Button generate;
    @FXML private ImageView exit;
    @FXML private ImageView minimize;
    @FXML private ChoiceBox sort;

    @FXML private TableColumn<DeliveryReceipt, String> dr_number;
    @FXML private TableColumn<DeliveryReceipt, LocalDate> date_delivered;
    @FXML private TableColumn<DeliveryReceipt, String> account_name;
    @FXML private TableColumn<DeliveryReceipt, String> address;
    @FXML private TableColumn<DeliveryReceipt, String> delivered_by;
    @FXML private TableColumn<DeliveryReceipt, String> total_amount;
    @FXML private TableColumn<DeliveryReceipt, String> status;
    
    @FXML private TextField searchField; 

    private ObservableList<DeliveryReceipt> masterReceipts = FXCollections.observableArrayList();
    private FilteredList<DeliveryReceipt> filteredReceipts;


    private static final double SIDEBAR_MIN_WIDTH = 115;
    private static final double SIDEBAR_MAX_WIDTH = 260;
    private static final double HOME_PANE_MIN_WIDTH = 80;
    private static final double HOME_PANE_MAX_WIDTH = 230;
    private static final double DELTABLE_EXPAND_AMOUNT = 150;
    private static final double DELSIGN_MOVE_AMOUNT = 100;
    private static final Duration ANIMATION_DURATION = Duration.millis(300);

    private double originalTableX;
    private double originalTableWidth;
    private double originalDelsignX;

    private final List<Double> originalColumnWidths = new ArrayList<>();
    private final List<TableColumn<?, ?>> columns = new ArrayList<>();

    @FXML
    private void initialize() {
        maximizebutton.setVisible(false);
        sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
        homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);

        originalTableX = deltable.getLayoutX();
        originalTableWidth = deltable.getPrefWidth();
        originalDelsignX = delsign.getLayoutX();

        for (Object obj : deltable.getColumns()) {
            TableColumn<?, ?> col = (TableColumn<?, ?>) obj;
            columns.add(col);
            originalColumnWidths.add(col.getPrefWidth());
        }
        
        Platform.runLater(() -> {
            Stage stage = (Stage) deltable.getScene().getWindow();
            stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (isNowFocused) {
                    System.out.println("Window focused, refreshing delivery receipts.");
                    loadDeliveryReceipts();
                }
            });
        });
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase();
            filteredReceipts.setPredicate(dr -> {
                if (filter.isEmpty()) return true;
                return (dr.getDrNumber() != null && dr.getDrNumber().toLowerCase().contains(filter)) ||
                       (dr.getAccountName() != null && dr.getAccountName().toLowerCase().contains(filter)) ||
                       (dr.getStatus() != null && dr.getStatus().toLowerCase().contains(filter)) ||
                       (dr.getAddress() != null && dr.getAddress().toLowerCase().contains(filter)) ||
                       (dr.getDeliveredBy() != null && dr.getDeliveredBy().toLowerCase().contains(filter));
            });
        });

        applySidebarState(SidebarState.isMinimized());
        setupTableColumns();
        loadDeliveryReceipts();
        
        sort.setItems(FXCollections.observableArrayList(
        	    "Collected",
        	    "Cancelled",
        	    "Partial",
        	    "Uncollected",
        	    "Clear Sort"
        	));

        	sort.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
        	    if (newVal != null) {
        	        applySortFilter(newVal.toString());
        	    }
        	});

    }

    @Override
    public void applySidebarState(boolean minimized) {
        if (minimized) {
            sidebar.setPrefWidth(SIDEBAR_MIN_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MIN_WIDTH);
            toggleSidebarText(false);
            minbutton.setVisible(false);
            maximizebutton.setVisible(true);
            deltable.setLayoutX(originalTableX - DELTABLE_EXPAND_AMOUNT);
            deltable.setPrefWidth(originalTableWidth + DELTABLE_EXPAND_AMOUNT);
            delsign.setLayoutX(originalDelsignX - DELSIGN_MOVE_AMOUNT);

            for (int i = 0; i < columns.size(); i++) {
                double originalWidth = originalColumnWidths.get(i);
                double ratio = originalWidth / originalTableWidth;
                double expandedWidth = originalWidth + (DELTABLE_EXPAND_AMOUNT * ratio);
                columns.get(i).setPrefWidth(expandedWidth);
            }
        } else {
            sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
            toggleSidebarText(true);
            minbutton.setVisible(true);
            maximizebutton.setVisible(false);
            deltable.setLayoutX(originalTableX);
            deltable.setPrefWidth(originalTableWidth);
            delsign.setLayoutX(originalDelsignX);

            for (int i = 0; i < columns.size(); i++) {
                columns.get(i).setPrefWidth(originalColumnWidths.get(i));
            }
        }
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

    private void setupTableColumns() {
        // Use PropertyValueFactory for simpler binding if column names match
        dr_number.setCellValueFactory(new PropertyValueFactory<>("drNumber"));
        date_delivered.setCellValueFactory(new PropertyValueFactory<>("dateDelivered"));
        account_name.setCellValueFactory(new PropertyValueFactory<>("accountName"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));
        delivered_by.setCellValueFactory(new PropertyValueFactory<>("deliveredBy"));
        total_amount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadDeliveryReceipts() {
        ObservableList<DeliveryReceipt> receipts = FXCollections.observableArrayList();

        String query = """
            SELECT dr.dr_number, dr.date_delivered, dr.account_name, dr.delivered_by,
                   CASE 
                       WHEN a.municipality IS NULL AND a.prov IS NULL THEN ''
                       WHEN a.municipality IS NULL THEN a.prov
                       WHEN a.prov IS NULL THEN a.municipality
                       ELSE a.municipality || ', ' || a.prov
                   END AS address,
                   dr.total_amount, dr.status
            FROM delivery_receipts dr
            LEFT JOIN accounts a ON dr.account_name = a.name
            ORDER BY dr.date_delivered DESC
        """;

        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("Executing query: " + query); // Debug log

            while (rs.next()) {
                // Debug logging for each record
                System.out.println("saddasd" );

                // Handle potential null values
                LocalDate deliveryDate = null;
                java.sql.Date sqlDate = rs.getDate("date_delivered");
                if (sqlDate != null) {
                    deliveryDate = sqlDate.toLocalDate();
                }

                String address = rs.getString("address");
                if (address == null) {
                    address = "";
                }

                receipts.add(new DeliveryReceipt(
                    rs.getString("dr_number"),
                    deliveryDate,
                    rs.getString("account_name"),
                    rs.getString("delivered_by"),
                    address,
                    rs.getDouble("total_amount"),
                    rs.getString("status")
                ));
            }

            System.out.println("Successfully loaded " + receipts.size() + " delivery receipts"); // Debug log
            
            // Update UI on JavaFX Application Thread
            Platform.runLater(() -> {
            	masterReceipts = receipts;
            	filteredReceipts = new FilteredList<>(masterReceipts, p -> true);
            	deltable.setItems(filteredReceipts);
            	deltable.refresh();
            });

        } catch (SQLException e) {
            System.err.println("Database error loading delivery receipts:");
            e.printStackTrace();
            
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText("Could not load delivery receipts");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            });
        }
    }
    
    @FXML
    private void delete(MouseEvent event) {
        DeliveryReceipt selected = deltable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Delivery Receipt Selected");
            alert.setContentText("Please select a delivery receipt to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Delivery Receipt");
        confirm.setContentText("Are you sure you want to delete DR Number: " + selected.getDrNumber() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = PostgresConnect.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                             "DELETE FROM public.delivery_receipts WHERE dr_number = ?")) {
                    stmt.setString(1, selected.getDrNumber());
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        masterReceipts.remove(selected);
                        deltable.refresh();
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setTitle("Deleted");
                        info.setHeaderText(null);
                        info.setContentText("Delivery Receipt deleted successfully.");
                        info.showAndWait();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Error");
                        error.setHeaderText("Deletion Failed");
                        error.setContentText("No matching record found to delete.");
                        error.showAndWait();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Database Error");
                    error.setHeaderText("Could not delete delivery receipt.");
                    error.setContentText(e.getMessage());
                    error.showAndWait();
                }
            }
        });
    }
    
    private void applySortFilter(String criteria) {
        if (criteria.equals("Clear Sort")) {
            deltable.setItems(filteredReceipts);
            deltable.getSortOrder().clear();
            filteredReceipts.setPredicate(p -> true);
            return;
        }

        if (criteria.equals("DR Number Ascending")) {
            dr_number.setSortType(TableColumn.SortType.ASCENDING);
            deltable.getSortOrder().setAll(dr_number);
        } else if (criteria.equals("DR Number Descending")) {
            dr_number.setSortType(TableColumn.SortType.DESCENDING);
            deltable.getSortOrder().setAll(dr_number);
        } else {
            // Status filtering
            filteredReceipts.setPredicate(dr -> {
                if (criteria.equals("Collected")) {
                    return "collected".equalsIgnoreCase(dr.getStatus());
                } else if (criteria.equals("Cancelled")) {
                    return "cancelled".equalsIgnoreCase(dr.getStatus());
                } else if (criteria.equals("Partial")) {
                    return "partial".equalsIgnoreCase(dr.getStatus());
                } else if (criteria.equals("Uncollected")) {
                    return "uncollected".equalsIgnoreCase(dr.getStatus());
                }
                return true;
            });
            deltable.setItems(filteredReceipts);
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
                new KeyValue(deltable.layoutXProperty(), originalTableX - DELTABLE_EXPAND_AMOUNT),
                new KeyValue(deltable.prefWidthProperty(), originalTableWidth + DELTABLE_EXPAND_AMOUNT),
                new KeyValue(delsign.layoutXProperty(), originalDelsignX - DELSIGN_MOVE_AMOUNT)
            )
        );

        for (int i = 0; i < columns.size(); i++) {
            TableColumn<?, ?> col = columns.get(i);
            double originalWidth = originalColumnWidths.get(i);
            double ratio = originalWidth / originalTableWidth;
            double expandedWidth = originalWidth + (DELTABLE_EXPAND_AMOUNT * ratio);
            timeline.getKeyFrames().add(
                new KeyFrame(ANIMATION_DURATION, new KeyValue(col.prefWidthProperty(), expandedWidth))
            );
        }

        timeline.setOnFinished(e -> {
            maximizebutton.setVisible(true);
            SidebarState.setMinimized(true);
        });

        timeline.play();
    }

    @FXML
    private void max(MouseEvent event) {
        if (!SidebarState.isMinimized()) return;

        Timeline timeline = new Timeline(
            new KeyFrame(ANIMATION_DURATION,
                new KeyValue(sidebar.prefWidthProperty(), SIDEBAR_MAX_WIDTH),
                new KeyValue(homepane.prefWidthProperty(), HOME_PANE_MAX_WIDTH),
                new KeyValue(deltable.layoutXProperty(), originalTableX),
                new KeyValue(deltable.prefWidthProperty(), originalTableWidth),
                new KeyValue(delsign.layoutXProperty(), originalDelsignX)
            )
        );

        for (int i = 0; i < columns.size(); i++) {
            TableColumn<?, ?> col = columns.get(i);
            double originalWidth = originalColumnWidths.get(i);
            timeline.getKeyFrames().add(
                new KeyFrame(ANIMATION_DURATION, new KeyValue(col.prefWidthProperty(), originalWidth))
            );
        }

        timeline.setOnFinished(e -> {
            toggleSidebarText(true);
            maximizebutton.setVisible(false);
            minbutton.setVisible(true);
            SidebarState.setMinimized(false);
        });

        timeline.play();
    }
 
    @FXML private void openMyAccount(MouseEvent event) { loadFXML("MyACCOUNT.fxml", event); }
    @FXML private void minimize(MouseEvent event) { ((Stage) ((Node) event.getSource()).getScene().getWindow()).setIconified(true); }
    @FXML private void exit(MouseEvent event) { ((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); }
    @FXML private void prod(MouseEvent event) { loadFXML("products_list.fxml", event); }
    @FXML private void sum(MouseEvent event) { loadFXML("executive_summary.fxml", event); }
    @FXML private void account1(MouseEvent event) { loadFXML("List_of_accounts.fxml", event); }
    @FXML private void homeic(MouseEvent event) { loadFXML("Homepage.fxml", event); }
    @FXML private void homesi(MouseEvent event) { loadFXML("Homepage.fxml", event); }
    @FXML private void arch(MouseEvent event) { loadFXML("LIST-CancelledDeliveryReceipts.fxml", event); }
    @FXML private void col(MouseEvent event) { loadFXML("collectionreciepts_list.fxml", event); }

    @FXML
    private void view(MouseEvent event) {
        DeliveryReceipt selected = deltable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Delivery Receipt Selected");
            alert.setContentText("Please select a delivery receipt to view.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("deliveryreciepts_view.fxml"));
            Parent root = loader.load();

            DeliveryReceiptsController_view controller = loader.getController();

            // Apply sidebar minimized state
            if (controller instanceof SidebarStateAware) {
                controller.applySidebarState(SidebarState.isMinimized());
            }

            // Pass selected DR to load full data
            controller.loadDeliveryReceiptData(selected.getDrNumber());

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("Loaded deliveryreciepts_view.fxml for DR Number: " + selected.getDrNumber());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading deliveryreciepts_view.fxml");
        }
    }

    
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

    @FXML
    private void add(MouseEvent event) {
        openModalAtButton("drchoice.fxml", event);
    }

    @FXML
    private void generate(MouseEvent event) {
        openModalAtButton("generate(Date).fxml", event);
    }
    
    @FXML
    private void clearsearch(MouseEvent event) {
        searchField.clear();
        searchTable(); // reapply to show all items after clearing
    }
    
    @FXML
    private void searchTable() {
        String filter = searchField.getText().toLowerCase().trim();

        ObservableList<DeliveryReceipt> filteredList = FXCollections.observableArrayList();

        for (DeliveryReceipt dr : deltable.getItems()) {
            boolean matches = false;

            if (dr.getDrNumber() != null && dr.getDrNumber().toLowerCase().contains(filter)) {
                matches = true;
            }
            if (dr.getAccountName() != null && dr.getAccountName().toLowerCase().contains(filter)) {
                matches = true;
            }
            if (dr.getDeliveredBy() != null && dr.getDeliveredBy().toLowerCase().contains(filter)) {
                matches = true;
            }
            if (dr.getStatus() != null && dr.getStatus().toLowerCase().contains(filter)) {
                matches = true;
            }
            if (dr.getAddress() != null && dr.getAddress().toLowerCase().contains(filter)) {
                matches = true;
            }
            // Handle LocalDate field
            if (dr.getDateDelivered() != null && dr.getDateDelivered().toString().contains(filter)) {
                matches = true;
            }
            // Handle totalAmount field
            if (String.format("%.2f", dr.getTotalAmount()).contains(filter)) {
                matches = true;
            }

            if (matches) {
                filteredList.add(dr);
            }
        }

        deltable.setItems(filteredList);
    }

    private void openModalAtButton(String fxml, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
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

    public static class Delta {
        double x, y;
    }
}



