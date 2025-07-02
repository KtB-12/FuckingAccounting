package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

public class CollectionReceiptsControllerconsumable implements SidebarStateAware {

	@FXML private Button homesign, accbutton, deliversign, prosign, summarixsign, logsign, settingsb1, collectionsign;
	@FXML private Button machinebutton, consumablebutton;

	@FXML private ImageView minbutton, maximizebutton;
	@FXML private ImageView homeicon, accicon, deliviericon, prodicon, summarixicon, settingsicon, logicon1, colloectionicon;
	@FXML private ImageView edit, delete, view; // Added 'view'
	@FXML private ImageView exit, minimize;    // Already correct

	@FXML private ChoiceBox<?> sort;           // FIX: was 'choiceBox', should be 'sort' per FXML

	@FXML private TableView<CollectionReceiptRowconsumable> collectiontable;
	
	@FXML private TableColumn<CollectionReceiptRowconsumable, String> cr_nunmber;
	@FXML private TableColumn<CollectionReceiptRowconsumable, String> date;
	@FXML private TableColumn<CollectionReceiptRowconsumable, String> name;
	@FXML private TableColumn<CollectionReceiptRowconsumable, String> address;
	@FXML private TableColumn<CollectionReceiptRowconsumable, String> amount;
	@FXML private TableColumn<CollectionReceiptRowconsumable, String> recieved_by;
	@FXML private TableColumn<CollectionReceiptRowconsumable, String> status;

	@FXML private Pane sidebar, homepane;

	@FXML private Text colsign;

    private static final double SIDEBAR_MIN_WIDTH = 115;
    private static final double SIDEBAR_MAX_WIDTH = 260;
    private static final double HOME_PANE_MIN_WIDTH = 80;
    private static final double HOME_PANE_MAX_WIDTH = 230;
    private static final double TABLE_EXPANSION = 110;
    private static final double TEXT_SHIFT = 100;
    private static final double MOVE_DISTANCE = (SIDEBAR_MAX_WIDTH - SIDEBAR_MIN_WIDTH) / 2;
    private static final Duration ANIMATION_DURATION = Duration.millis(300);

    private double originalTableWidth, originalTableX, originalTextX;
    private double originalMachineX, originalConsumableX;
    private boolean isMinimized = false;

    @FXML
    private void initialize() {
        maximizebutton.setVisible(false);
        homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);

        originalTableWidth = collectiontable.getPrefWidth();
        originalTableX = collectiontable.getLayoutX();
        originalTextX = colsign.getLayoutX();

        originalMachineX = machinebutton.getLayoutX();
        originalConsumableX = consumablebutton.getLayoutX();

        collectiontable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Apply saved sidebar state
        applySidebarState(SidebarState.isMinimized());
        
        loadCollectionReceiptsConsumable();
    }

    @Override
    public void applySidebarState(boolean minimized) {
        if (minimized) {
            sidebar.setPrefWidth(SIDEBAR_MIN_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MIN_WIDTH);
            collectiontable.setPrefWidth(originalTableWidth + TABLE_EXPANSION);
            collectiontable.setLayoutX(originalTableX - TABLE_EXPANSION);
            colsign.setLayoutX(originalTextX - TEXT_SHIFT);
            machinebutton.setLayoutX(originalMachineX - MOVE_DISTANCE - 35);
            consumablebutton.setLayoutX(originalConsumableX - MOVE_DISTANCE - 35);
            toggleSidebarText(false);
            minbutton.setVisible(false);
            maximizebutton.setVisible(true);
            isMinimized = true;
        } else {
            sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
            collectiontable.setPrefWidth(originalTableWidth);
            collectiontable.setLayoutX(originalTableX);
            colsign.setLayoutX(originalTextX);
            machinebutton.setLayoutX(originalMachineX);
            consumablebutton.setLayoutX(originalConsumableX);
            toggleSidebarText(true);
            maximizebutton.setVisible(false);
            minbutton.setVisible(true);
            isMinimized = false;
        }
    }

    @FXML
    private void min(MouseEvent event) {
        if (isMinimized) return;

        toggleSidebarText(false);
        minbutton.setVisible(false);

        Timeline timeline = new Timeline(
            new KeyFrame(ANIMATION_DURATION,
                new KeyValue(sidebar.prefWidthProperty(), SIDEBAR_MIN_WIDTH),
                new KeyValue(homepane.prefWidthProperty(), HOME_PANE_MIN_WIDTH),
                new KeyValue(collectiontable.prefWidthProperty(), originalTableWidth + TABLE_EXPANSION),
                new KeyValue(collectiontable.layoutXProperty(), originalTableX - TABLE_EXPANSION),
                new KeyValue(colsign.layoutXProperty(), originalTextX - TEXT_SHIFT),
                new KeyValue(machinebutton.layoutXProperty(), originalMachineX - MOVE_DISTANCE - 35),
                new KeyValue(consumablebutton.layoutXProperty(), originalConsumableX - MOVE_DISTANCE - 35)
            )
        );

        timeline.setOnFinished(e -> {
            maximizebutton.setVisible(true);
            SidebarState.setMinimized(true);
        });

        timeline.play();
        isMinimized = true;
    }

    @FXML
    private void max(MouseEvent event) {
        if (!isMinimized) return;

        Timeline timeline = new Timeline(
            new KeyFrame(ANIMATION_DURATION,
                new KeyValue(sidebar.prefWidthProperty(), SIDEBAR_MAX_WIDTH),
                new KeyValue(homepane.prefWidthProperty(), HOME_PANE_MAX_WIDTH),
                new KeyValue(collectiontable.prefWidthProperty(), originalTableWidth),
                new KeyValue(collectiontable.layoutXProperty(), originalTableX),
                new KeyValue(colsign.layoutXProperty(), originalTextX),
                new KeyValue(machinebutton.layoutXProperty(), originalMachineX),
                new KeyValue(consumablebutton.layoutXProperty(), originalConsumableX)
            )
        );

        timeline.setOnFinished(e -> {
            toggleSidebarText(true);
            maximizebutton.setVisible(false);
            minbutton.setVisible(true);
            SidebarState.setMinimized(false);
        });

        timeline.play();
        isMinimized = false;
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

    @FXML
    private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
    
    private void loadCollectionReceiptsConsumable() {
        ObservableList<CollectionReceiptRowconsumable> data = FXCollections.observableArrayList();

        String query = """
            SELECT
                cr.cr_number,
                dr.date_delivered::text,
                acc.name,
                acc.municipality || ', ' || acc.prov AS address,
                cr.total_amount::text,
                cr.received_by,
                cr.status
            FROM
                public.collection_receipts cr
            JOIN
                public.accounts acc ON cr.account_name = acc.name
            LEFT JOIN
                public.delivery_receipts dr ON cr.dr_number = dr.dr_number
            LEFT JOIN
                public.delivery_items di ON dr.dr_number = di.dr_number
            LEFT JOIN
                public.product p ON di.product_id = p.id
            WHERE
                (dr.receipt_type = 'consumable')
            GROUP BY
                cr.cr_number, dr.date_delivered, acc.name, acc.municipality, acc.prov,
                cr.total_amount, cr.dr_number, cr.received_by, cr.status
            ORDER BY
                cr.cr_number ASC;
            """;

        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                data.add(new CollectionReceiptRowconsumable(
                    rs.getString("cr_number"),
                    rs.getString("date_delivered"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("total_amount"),
                    rs.getString("received_by"),
                    rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set up column bindings
        ((TableColumn<CollectionReceiptRowconsumable, String>) cr_nunmber).setCellValueFactory(new PropertyValueFactory<>("crNumber"));
        ((TableColumn<CollectionReceiptRowconsumable, String>) date).setCellValueFactory(new PropertyValueFactory<>("dateDelivered"));
        ((TableColumn<CollectionReceiptRowconsumable, String>) name).setCellValueFactory(new PropertyValueFactory<>("name"));
        ((TableColumn<CollectionReceiptRowconsumable, String>) address).setCellValueFactory(new PropertyValueFactory<>("address"));
        ((TableColumn<CollectionReceiptRowconsumable, String>) amount).setCellValueFactory(new PropertyValueFactory<>("amount"));
        ((TableColumn<CollectionReceiptRowconsumable, String>) recieved_by).setCellValueFactory(new PropertyValueFactory<>("receivedBy"));
        ((TableColumn<CollectionReceiptRowconsumable, String>) status).setCellValueFactory(new PropertyValueFactory<>("status"));

        collectiontable.setItems(data);
    }

    @FXML
    private void exit(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void edit(MouseEvent event) {
        loadFXMLWithState("collectionreciepts_viewedit_consumable.fxml", event);
    }

    @FXML private void prod(MouseEvent event) { loadFXMLWithState("products_list.fxml", event); }
    @FXML private void machine(MouseEvent event) { loadFXMLWithState("collectionreciepts_list.fxml", event); }
    @FXML private void homeic(MouseEvent event) { loadFXMLWithState("Homepage.fxml", event); }
    @FXML private void homesi(MouseEvent event) { loadFXMLWithState("Homepage.fxml", event); }
    @FXML private void consumable(MouseEvent event) { loadFXMLWithState("collectionreciepts_list_consumable.fxml", event); }
    @FXML private void sum(MouseEvent event) { loadFXMLWithState("executive_summary.fxml", event); }
    @FXML private void openaccount(MouseEvent event) { loadFXMLWithState("MyACCOUNT.fxml", event); }
    @FXML private void del(MouseEvent event) { loadFXMLWithState("deliveryreceipts_list.fxml", event); }
    @FXML private void account1(MouseEvent event) { loadFXMLWithState("List_of_accounts.fxml", event); }
    @FXML private void view_detials(MouseEvent event) {
    	System.out.print("qwertyu");
    }

    @FXML
    private void addac(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("collectionreciepts_add.fxml"));
            Parent root = loader.load();

            // Round window shape
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT); // Make background transparent

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT); // Remove window border
            stage.initModality(Modality.APPLICATION_MODAL); // Block background
            stage.setResizable(false);

            // Optional: Make it draggable
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

    private static class Delta {
        double x, y;
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
        openModalAtButton("collection_add.fxml", event);
    }
    @FXML
    private void view(MouseEvent event) {
    	loadFXMLWithState("collectionreciepts_viewedit_consumable.fxml", event);
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

    private void loadFXMLWithState(String fxmlFile, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            if (loader.getController() instanceof SidebarStateAware) {
                ((SidebarStateAware) loader.getController()).applySidebarState(SidebarState.isMinimized());
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading " + fxmlFile);
        }
    }
}
