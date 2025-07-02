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

public class CollectionReceiptsController implements SidebarStateAware {

	@FXML private Button homesign, accbutton, deliversign, prosign, summarixsign, logsign, settingsb1, collectionsign;
	@FXML private ImageView minbutton, maximizebutton;
	@FXML private ImageView homeicon, accicon, deliviericon, prodicon, summarixicon, settingsicon, logicon1, colloectionicon;
	@FXML private ImageView exit, minimize;
	@FXML private Pane sidebar, homepane;
	@FXML private Text colsign;
	@FXML private ImageView edit, delete, view;
	@FXML private ChoiceBox<?> sort;
	@FXML private Button machinebutton, consumablebutton;
	
	@FXML private TableView<CollectionReceiptRow> collectiontable;
	@FXML private TableColumn<CollectionReceiptRow, String> cr_number, name, address, machine, recieved_by, amount, status, remarks;


    private static final double SIDEBAR_MIN_WIDTH = 115;
    private static final double SIDEBAR_MAX_WIDTH = 260;
    private static final double HOME_PANE_MIN_WIDTH = 80;
    private static final double HOME_PANE_MAX_WIDTH = 230;
    private static final double TABLE_EXPANSION = 110;
    private static final double TEXT_SHIFT = 100;
    private static final Duration ANIMATION_DURATION = Duration.millis(300);
    private static final double MOVE_DISTANCE = (SIDEBAR_MAX_WIDTH - SIDEBAR_MIN_WIDTH) / 2;

    private double originalTableWidth;
    private double originalTableX;
    private double originalTextX;
    private double originalMachineX;
    private double originalConsumableX;

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

        applySidebarState(SidebarState.isMinimized());
        
        setupTableColumns();
        loadCollectionReceiptsData();
    }

    @Override
    public void applySidebarState(boolean minimized) {
        if (minimized) {
            sidebar.setPrefWidth(SIDEBAR_MIN_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MIN_WIDTH);
            toggleSidebarText(false);
            minbutton.setVisible(false);
            maximizebutton.setVisible(true);

            collectiontable.setLayoutX(originalTableX - TABLE_EXPANSION);
            collectiontable.setPrefWidth(originalTableWidth + TABLE_EXPANSION);
            colsign.setLayoutX(originalTextX - TEXT_SHIFT);
            machinebutton.setLayoutX(originalMachineX - MOVE_DISTANCE - 35);
            consumablebutton.setLayoutX(originalConsumableX - MOVE_DISTANCE - 35);
        } else {
            sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
            toggleSidebarText(true);
            minbutton.setVisible(true);
            maximizebutton.setVisible(false);

            collectiontable.setLayoutX(originalTableX);
            collectiontable.setPrefWidth(originalTableWidth);
            colsign.setLayoutX(originalTextX);
            machinebutton.setLayoutX(originalMachineX);
            consumablebutton.setLayoutX(originalConsumableX);
        }
    }
    
    private void loadCollectionReceiptsData() {
        ObservableList<CollectionReceiptRow> data = FXCollections.observableArrayList();
        String query = """
                SELECT
                    cr.cr_number,
                    acc.name,
                    acc.municipality || ', ' || acc.prov AS address,
                    cr.received_by,
                    cr.total_amount::text,
                    cr.status,
                    p."Product List" AS machine
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
                    (dr.receipt_type = 'machine')
                    AND (p.category = 'Machine' OR p.category IS NULL)
                ORDER BY
                    cr.cr_number ASC;
            """;


        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String crNumber = rs.getString("cr_number");
                String accName = rs.getString("name");
                String address = rs.getString("address");
                String receivedBy = rs.getString("received_by");
                String totalAmount = rs.getString("total_amount");
                String status = rs.getString("status");
                String machine = rs.getString("machine"); // will be null if not machine

                data.add(new CollectionReceiptRow(
                    crNumber,
                    accName,
                    address,
                    receivedBy,
                    totalAmount,
                    status,
                    machine,
                    null // remarks left null
                ));
            }
            
            System.out.println("Fetched rows: " + data.size());
            for (CollectionReceiptRow row : data) {
                System.out.println(row.getCrNumber() + " | " + row.getName() + " | " + row.getAddress() +
                    " | " + row.getReceivedBy() + " | " + row.getAmount() + " | " + row.getStatus() +
                    " | " + row.getMachine() + " | " + row.getRemarks());
            }

            collectiontable.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
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
    }

    @FXML
    private void max(MouseEvent event) {
        if (!SidebarState.isMinimized()) return;

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
    
    private void setupTableColumns() {
        cr_number.setCellValueFactory(new PropertyValueFactory<>("crNumber"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));
        recieved_by.setCellValueFactory(new PropertyValueFactory<>("receivedBy"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        machine.setCellValueFactory(new PropertyValueFactory<>("machine"));
        remarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));
    }


    @FXML
    private void exit(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    public void refreshTable() {
        loadCollectionReceiptsData();
    }


    @FXML
    private void edit(MouseEvent event) {
        loadFXMLWithState("COLLECTION_RECEIPT VIEW AND EDIT.fxml", event);
    }

    @FXML private void prod(MouseEvent event) { loadFXMLWithState("products_list.fxml", event); }
    @FXML private void homeic(MouseEvent event) { loadFXMLWithState("Homepage.fxml", event); }
    @FXML private void consumable(MouseEvent event) { loadFXMLWithState("collectionreciepts_list_consumable.fxml", event); }
    @FXML private void homesi(MouseEvent event) { loadFXMLWithState("Homepage.fxml", event); }
    @FXML private void sum(MouseEvent event) { loadFXMLWithState("executive_summary.fxml", event); }
    @FXML private void openaccount(MouseEvent event) { loadFXMLWithState("MyACCOUNT.fxml", event); }
    @FXML private void del(MouseEvent event) { loadFXMLWithState("deliveryreceipts_list.fxml", event); }
    @FXML private void view(MouseEvent event) { loadFXMLWithState("collection_receipt(view and edit)machine.fxml", event); }
    @FXML private void account1(MouseEvent event) { loadFXMLWithState("List_of_accounts.fxml", event); }
    @FXML private void view_detials(MouseEvent event) {
    	System.out.print("qwertyu");
    }
    
    @FXML
    private void addac(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("collectionreciepts_add.fxml"));
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

            // ✅ Refresh table when modal closes
            stage.setOnHidden(e -> refreshTable());

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Delta {
        double x, y;
    }

    @FXML
    private void add(MouseEvent event) {
        openModalAtButton("collection_add.fxml", event);
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

    
    private void loadFXMLWithState(String fxmlFile, MouseEvent event) {
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