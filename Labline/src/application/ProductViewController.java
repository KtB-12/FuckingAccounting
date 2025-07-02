package application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class ProductViewController {

    @FXML private Pane sidebar;
    @FXML private Pane homepane;

    @FXML private ImageView minbutton;
    @FXML private ImageView maximizebutton;

    @FXML private ImageView homeicon, accicon, deliviericon, colloectionicon, summarixicon, settingsicon, logicon1;
    @FXML private ImageView delete, edit, prodicon, homeiconTopRight, exitIcon, minimizeIcon;

    @FXML private Button homesign;
    @FXML private Button accbutton;
    @FXML private Button prosign;
    @FXML private Button deliversign;
    @FXML private Button collectionsign;
    @FXML private Button summarixsign;
    @FXML private Button settingsb1;
    @FXML private Button logsign;
    @FXML private TextField search;
    @FXML private ImageView clearsearch;
    @FXML private ChoiceBox sortby;

    @FXML private TableView<Product> prodtable;
    @FXML private TableColumn<Product, String> Product_Code;
    @FXML private TableColumn<Product, String> Product_List;
    @FXML private TableColumn<Product, BigDecimal> Price;
    @FXML private TableColumn<Product, String> category;

    @FXML private Text prodsign;
    private ObservableList<Product> originalProductList = FXCollections.observableArrayList();

    private static final double SIDEBAR_MIN_WIDTH = 115;
    private static final double SIDEBAR_MAX_WIDTH = 260;
    private static final double HOME_PANE_MIN_WIDTH = 80;
    private static final double HOME_PANE_MAX_WIDTH = 230;
    private static final double PRODSIGN_MIN_X = 680;
    private static final double PRODSIGN_MAX_X = 771; 
    private static final Duration ANIMATION_DURATION = Duration.millis(300);

    private boolean isMinimized = false;
    private double originalTableX;
    private double originalTableWidth;
    private List<Double> originalColumnWidths = new ArrayList<>();
    
    private enum SortOption {
        CODE_ASC("Product Code (A-Z)"),
        CODE_DESC("Product Code (Z-A)"),
        NAME_ASC("Product Name (A-Z)"),
        NAME_DESC("Product Name (Z-A)"),
        PRICE_ASC("Price (Low-High)"),
        PRICE_DESC("Price (High-Low)");

        private final String displayText;

        SortOption(String displayText) {
            this.displayText = displayText;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    @FXML
    void initialize() {
        boolean shouldMinimize = SidebarState.isMinimized();

        // Temporarily hide the table until layout is stable to avoid flicker
        prodtable.setVisible(false);

        Platform.runLater(() -> {
            // Store original layout after scene is shown
            originalTableX = prodtable.getLayoutX();
            originalTableWidth = prodtable.getPrefWidth();
            originalColumnWidths.clear();
            for (TableColumn<?, ?> column : prodtable.getColumns()) {
                originalColumnWidths.add(column.getPrefWidth());
            }

            if (shouldMinimize) {
                double expandAmount = SIDEBAR_MAX_WIDTH - SIDEBAR_MIN_WIDTH;

                sidebar.setPrefWidth(SIDEBAR_MIN_WIDTH);
                homepane.setPrefWidth(HOME_PANE_MIN_WIDTH);
                prodtable.setLayoutX(originalTableX - expandAmount);
                prodtable.setPrefWidth(originalTableWidth + expandAmount);
                prodsign.setLayoutX(PRODSIGN_MIN_X);
                toggleSidebarText(false);
                minbutton.setVisible(false);
                maximizebutton.setVisible(true);
                isMinimized = true;

                for (int i = 0; i < prodtable.getColumns().size(); i++) {
                    TableColumn<?, ?> column = prodtable.getColumns().get(i);
                    column.setPrefWidth(originalColumnWidths.get(i) * 
                        ((originalTableWidth + expandAmount) / originalTableWidth));
                }
            } else {
                sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
                homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
                prodtable.setLayoutX(originalTableX);
                prodtable.setPrefWidth(originalTableWidth);
                prodsign.setLayoutX(PRODSIGN_MAX_X);
                toggleSidebarText(true);
                minbutton.setVisible(true);
                maximizebutton.setVisible(false);
                isMinimized = false;

                for (int i = 0; i < prodtable.getColumns().size(); i++) {
                    TableColumn<?, ?> column = prodtable.getColumns().get(i);
                    column.setPrefWidth(originalColumnWidths.get(i));
                }
            }

            prodtable.setVisible(true); // Now show the table after layout is applied
        });

        setupSortOptions();
        loadProducts();
        setupSearchListener();
    }

    
    private void setupSortOptions() {

        sortby.getItems().addAll(SortOption.values());
        
        sortby.setValue(SortOption.CODE_ASC);
        
        sortby.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            sortProducts((SortOption) newVal);
        });
    }

    private void sortProducts(SortOption sortOption) {
        if (sortOption == null || prodtable.getItems().isEmpty()) return;
        
        Comparator<Product> comparator = switch (sortOption) {
            case CODE_ASC -> Comparator.comparing(Product::getProductCode, String.CASE_INSENSITIVE_ORDER);
            case CODE_DESC -> Comparator.comparing(Product::getProductCode, String.CASE_INSENSITIVE_ORDER).reversed();
            case NAME_ASC -> Comparator.comparing(Product::getProductList, String.CASE_INSENSITIVE_ORDER);
            case NAME_DESC -> Comparator.comparing(Product::getProductList, String.CASE_INSENSITIVE_ORDER).reversed();
            case PRICE_ASC -> Comparator.comparing(Product::getPrice, Comparator.nullsFirst(Comparator.naturalOrder()));
            case PRICE_DESC -> Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.reverseOrder()));
        };
        
        FXCollections.sort(prodtable.getItems(), comparator);
    }
    
    private void setupSearchListener() {
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProducts(newValue);
        });
        
        clearsearch.setOnMouseClicked(event -> {
            search.clear();
            filterProducts("");
        });
    }

    private void filterProducts(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            prodtable.setItems(originalProductList);
            sortProducts((SortOption) sortby.getValue());
            return;
        }
        
        String lowerCaseFilter = searchText.toLowerCase();
        
        ObservableList<Product> filteredList = FXCollections.observableArrayList();
        
        for (Product product : originalProductList) {
            if ((product.getProductCode() != null && product.getProductCode().toLowerCase().contains(lowerCaseFilter))) {
                filteredList.add(product);
            } else if ((product.getProductList() != null && product.getProductList().toLowerCase().contains(lowerCaseFilter))) {
                filteredList.add(product);
            } else if ((product.getPrice() != null && product.getPrice().toString().contains(searchText))) {
                filteredList.add(product);
            } else if ((product.getCategory() != null && product.getCategory().toLowerCase().contains(lowerCaseFilter))) {
                filteredList.add(product);
            }
        }
        
        prodtable.setItems(filteredList);
        sortProducts((SortOption) sortby.getValue());
    }    

    private void loadProducts() {
        try (Connection conn = PostgresConnect.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT \"Product Code\", \"Product List\", \"Price\", \"category\" FROM product")) {

            List<Product> productList = new ArrayList<>();
            
            while (rs.next()) {
                String code = rs.getString("Product Code");
                String list = rs.getString("Product List");
                BigDecimal price = rs.getBigDecimal("Price");
                String category = rs.getString("category"); // Add this line
                productList.add(new Product(0, code, list, price, category)); // Update this line
            }

            Product_Code.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getProductCode()));
            Product_List.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getProductList()));
            Price.setCellValueFactory(cellData -> 
                new SimpleObjectProperty<>(cellData.getValue().getPrice()));
            category.setCellValueFactory(cellData -> // Add this block
                new SimpleStringProperty(cellData.getValue().getCategory()));

            originalProductList.setAll(productList);
            prodtable.setItems(originalProductList);
            
            sortProducts((SortOption) sortby.getValue());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load products");
        }
    }

    @FXML
    private void min(MouseEvent event) {
        if (isMinimized) return; // 🚫 Already minimized — skip

        isMinimized = true;
        SidebarState.setMinimized(true); // sync state globally
        toggleSidebarText(false);
        minbutton.setVisible(false);

        double expandAmount = SIDEBAR_MAX_WIDTH - SIDEBAR_MIN_WIDTH;
        double targetTableWidth = originalTableWidth + expandAmount;

        Timeline timeline = new Timeline(
            new KeyFrame(ANIMATION_DURATION,
                new KeyValue(sidebar.prefWidthProperty(), SIDEBAR_MIN_WIDTH),
                new KeyValue(homepane.prefWidthProperty(), HOME_PANE_MIN_WIDTH),
                new KeyValue(prodtable.layoutXProperty(), originalTableX - expandAmount),
                new KeyValue(prodtable.prefWidthProperty(), targetTableWidth),
                new KeyValue(prodsign.layoutXProperty(), PRODSIGN_MIN_X)
            )
        );

        prodtable.prefWidthProperty().addListener((obs, oldVal, newVal) -> {
            double scaleFactor = newVal.doubleValue() / originalTableWidth;
            for (int i = 0; i < prodtable.getColumns().size(); i++) {
                TableColumn<?, ?> column = prodtable.getColumns().get(i);
                column.setPrefWidth(originalColumnWidths.get(i) * scaleFactor);
            }
        });

        timeline.setOnFinished(e -> maximizebutton.setVisible(true));
        timeline.play();
    }
    
    @FXML
    private void max(MouseEvent event) {
        if (!isMinimized) return; // 🚫 Already maximized — skip

        isMinimized = false;
        SidebarState.setMinimized(false); // sync state globally

        Timeline timeline = new Timeline(
            new KeyFrame(ANIMATION_DURATION,
                new KeyValue(sidebar.prefWidthProperty(), SIDEBAR_MAX_WIDTH),
                new KeyValue(homepane.prefWidthProperty(), HOME_PANE_MAX_WIDTH),
                new KeyValue(prodtable.layoutXProperty(), originalTableX),
                new KeyValue(prodtable.prefWidthProperty(), originalTableWidth),
                new KeyValue(prodsign.layoutXProperty(), PRODSIGN_MAX_X)
            )
        );

        timeline.setOnFinished(e -> {
            toggleSidebarText(true);
            maximizebutton.setVisible(false);
            minbutton.setVisible(true);

            for (int i = 0; i < prodtable.getColumns().size(); i++) {
                TableColumn<?, ?> column = prodtable.getColumns().get(i);
                column.setPrefWidth(originalColumnWidths.get(i));
            }
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
    
    @FXML private void clear(MouseEvent event) {search.clear();}
    @FXML private void homeic(MouseEvent event) { loadFXML("Homepage.fxml", event); }
    @FXML private void homesi(MouseEvent event) { loadFXML("Homepage.fxml", event); }
    @FXML private void sum(MouseEvent event) { loadFXML("executive_summary.fxml", event); }
    @FXML private void col(MouseEvent event) { loadFXML("collectionreciepts_list.fxml", event); }
    @FXML private void del(MouseEvent event) { loadFXML("deliveryreceipts_list.fxml", event); }
    @FXML private void openaccount(MouseEvent event) { loadFXML("MyACCOUNT.fxml", event); }
    @FXML private void account1(MouseEvent event) { loadFXML("List_of_accounts.fxml", event); }

    public void addProductToTable(Product product) {
        // Add to the original list
        originalProductList.add(product);
        
        sortProducts((SortOption) sortby.getValue());
        
        if (!search.getText().isEmpty()) {
            filterProducts(search.getText());
        }
    }

    @FXML
    private void addprod(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("prodadd.fxml"));
            Parent root = loader.load();

            // ✅ Set controller reference
            newProductController controller = loader.getController();
            controller.setProductViewController(this);

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
    private void delete(MouseEvent event) {
        Product selectedProduct = prodtable.getSelectionModel().getSelectedItem();
        
        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Product");
        confirmation.setContentText("Are you sure you want to delete:\n" + 
                                  selectedProduct.getProductList() + " (" + 
                                  selectedProduct.getProductCode() + ")?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteProductFromDatabase(selectedProduct);
            }
        });
    }

    private void deleteProductFromDatabase(Product product) {
        String sql = "DELETE FROM product WHERE \"Product Code\" = ?";
        
        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getProductCode());
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                Platform.runLater(() -> {
                    originalProductList.remove(product);
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                             "Product deleted successfully.");
                });
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", 
                         "Product not found in database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", 
                     "Failed to delete product: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void exit(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void edit() {
        Product selectedProduct = prodtable.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("No product selected.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("prodedit.fxml"));
            Parent root = loader.load();

            EditProductController controller = loader.getController();
            controller.setProduct(selectedProduct);
            controller.setOnUpdateCallback(this::refreshTable);

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

    
    public void refreshTable() {
        try (Connection conn = PostgresConnect.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT \"Product Code\", \"Product List\", \"Price\", \"category\" FROM product")) {

            List<Product> productList = new ArrayList<>();

            while (rs.next()) {
                String code = rs.getString("Product Code");
                String list = rs.getString("Product List");
                BigDecimal price = rs.getBigDecimal("Price");
                String category = rs.getString("category"); // Add this line
                productList.add(new Product(0, code, list, price, category)); // Update this line
            }

            originalProductList.setAll(productList);
            prodtable.setItems(originalProductList);
            
            sortProducts((SortOption) sortby.getValue());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to refresh product data.");
        }
    }

    private void loadFXML(String fxmlFile, MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading " + fxmlFile);
        }
    }
}