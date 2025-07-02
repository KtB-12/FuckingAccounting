package application;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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

public class AccountsController {

    @FXML private Pane sidebar;
    @FXML private Pane homepane;
    @FXML private TableView<Account> accountstable;
    @FXML private TableColumn<Account, String> name;
    @FXML private TableColumn<Account, String> municipality;
    @FXML private TableColumn<Account, String> province;
    @FXML private ImageView minbutton;
    @FXML private ImageView maximizebutton;
    @FXML private Text accountsign;
    @FXML private TextField search;
    @FXML private ChoiceBox<String> sort;

    @FXML private Button accbutton, prosign, deliversign, collectionsign, summarixsign, logsign, settingsb1, homesign;
    @FXML private ImageView accicon, prodicon, deliviericon, colloectionicon, summarixicon, logicon1, settingsicon, homeicon;

    private ObservableList<Account> accountList = FXCollections.observableArrayList();

    private static final double SIDEBAR_MIN_WIDTH = 115;
    private static final double SIDEBAR_MAX_WIDTH = 260;
    private static final double HOME_PANE_MIN_WIDTH = 80;
    private static final double HOME_PANE_MAX_WIDTH = 230;
    private static final double ACCOUNTSIGN_MIN_X = 680;
    private static final double ACCOUNTSIGN_MAX_X = 771;
    private static final Duration ANIMATION_DURATION = Duration.millis(300);

    private boolean isMinimized = false;
    private double originalTableX;
    private double originalTableWidth;
    private List<Double> originalColumnWidths = new ArrayList<>();

    @FXML
    public void initialize() {
        boolean shouldMinimize = SidebarState.isMinimized();
        accountstable.setVisible(false);

        Platform.runLater(() -> {
            originalTableX = accountstable.getLayoutX();
            originalTableWidth = accountstable.getPrefWidth();
            originalColumnWidths.clear();
            for (TableColumn<?, ?> col : accountstable.getColumns()) {
                originalColumnWidths.add(col.getPrefWidth());
            }

            if (shouldMinimize) {
                double expandAmount = SIDEBAR_MAX_WIDTH - SIDEBAR_MIN_WIDTH;
                sidebar.setPrefWidth(SIDEBAR_MIN_WIDTH);
                homepane.setPrefWidth(HOME_PANE_MIN_WIDTH);
                accountstable.setLayoutX(originalTableX - expandAmount);
                accountstable.setPrefWidth(originalTableWidth + expandAmount);
                accountsign.setLayoutX(ACCOUNTSIGN_MIN_X);
                accountsign.setVisible(false);
                toggleSidebarText(false);
                minbutton.setVisible(false);
                maximizebutton.setVisible(true);
                isMinimized = true;

                for (int i = 0; i < accountstable.getColumns().size(); i++) {
                    TableColumn<?, ?> col = accountstable.getColumns().get(i);
                    col.setPrefWidth(originalColumnWidths.get(i) * ((originalTableWidth + expandAmount) / originalTableWidth));
                }
            } else {
                sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
                homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
                accountstable.setLayoutX(originalTableX);
                accountstable.setPrefWidth(originalTableWidth);
                accountsign.setLayoutX(ACCOUNTSIGN_MAX_X);
                accountsign.setVisible(true);
                toggleSidebarText(true);
                minbutton.setVisible(true);
                maximizebutton.setVisible(false);
                isMinimized = false;
                for (int i = 0; i < accountstable.getColumns().size(); i++) {
                    accountstable.getColumns().get(i).setPrefWidth(originalColumnWidths.get(i));
                }
            }
            accountstable.setVisible(true);
        });

        name.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        municipality.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMunicipality()));
        province.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProvince()));

        sort.getItems().addAll(
            "Name (A-Z)", "Name (Z-A)",
            "Municipality (A-Z)", "Municipality (Z-A)",
            "Province (A-Z)", "Province (Z-A)"
        );
        sort.getSelectionModel().selectFirst();

        sort.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            accountstable.getSortOrder().clear();

            switch (newVal) {
                case "Name (A-Z)" -> {
                    name.setSortType(TableColumn.SortType.ASCENDING);
                    accountstable.getSortOrder().add(name);
                }
                case "Name (Z-A)" -> {
                    name.setSortType(TableColumn.SortType.DESCENDING);
                    accountstable.getSortOrder().add(name);
                }
                case "Municipality (A-Z)" -> {
                    municipality.setSortType(TableColumn.SortType.ASCENDING);
                    accountstable.getSortOrder().add(municipality);
                }
                case "Municipality (Z-A)" -> {
                    municipality.setSortType(TableColumn.SortType.DESCENDING);
                    accountstable.getSortOrder().add(municipality);
                }
                case "Province (A-Z)" -> {
                    province.setSortType(TableColumn.SortType.ASCENDING);
                    accountstable.getSortOrder().add(province);
                }
                case "Province (Z-A)" -> {
                    province.setSortType(TableColumn.SortType.DESCENDING);
                    accountstable.getSortOrder().add(province);
                }
            }
        });

        loadAccounts();
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

    private void loadAccounts() {
        accountList.clear();
        try (Connection conn = PostgresConnect.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, municipality, prov FROM accounts")) {
            while (rs.next()) {
                String n = rs.getString("name");
                String m = rs.getString("municipality");
                String p = rs.getString("prov");
                accountList.add(new Account(n, m, p));
            }

            FilteredList<Account> filteredData = new FilteredList<>(accountList, b -> true);

            search.textProperty().addListener((obs, oldVal, newVal) -> {
                String lower = newVal.toLowerCase();
                filteredData.setPredicate(account -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    return account.getName().toLowerCase().contains(lower)
                            || account.getMunicipality().toLowerCase().contains(lower)
                            || account.getProvince().toLowerCase().contains(lower);
                });
            });

            SortedList<Account> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(accountstable.comparatorProperty());

            accountstable.setItems(sortedData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML private void min(MouseEvent event) {
        if (isMinimized) return;

        isMinimized = true;
        SidebarState.setMinimized(true);
        minbutton.setVisible(false);
        maximizebutton.setVisible(true);
        toggleSidebarText(false);

        double expandAmount = SIDEBAR_MAX_WIDTH - SIDEBAR_MIN_WIDTH;
        double targetWidth = originalTableWidth + expandAmount;

        Timeline timeline = new Timeline(
                new KeyFrame(ANIMATION_DURATION,
                        new KeyValue(sidebar.prefWidthProperty(), SIDEBAR_MIN_WIDTH),
                        new KeyValue(homepane.prefWidthProperty(), HOME_PANE_MIN_WIDTH),
                        new KeyValue(accountstable.layoutXProperty(), originalTableX - expandAmount),
                        new KeyValue(accountstable.prefWidthProperty(), targetWidth),
                        new KeyValue(accountsign.layoutXProperty(), ACCOUNTSIGN_MIN_X)
                )
        );

        accountstable.prefWidthProperty().addListener((obs, oldVal, newVal) -> {
            double scaleFactor = newVal.doubleValue() / originalTableWidth;
            for (int i = 0; i < accountstable.getColumns().size(); i++) {
                TableColumn<?, ?> col = accountstable.getColumns().get(i);
                col.setPrefWidth(originalColumnWidths.get(i) * scaleFactor);
            }
        });

        timeline.play();
    }

    @FXML private void max(MouseEvent event) {
        if (!isMinimized) return;

        isMinimized = false;
        SidebarState.setMinimized(false);

        Timeline timeline = new Timeline(
                new KeyFrame(ANIMATION_DURATION,
                        new KeyValue(sidebar.prefWidthProperty(), SIDEBAR_MAX_WIDTH),
                        new KeyValue(homepane.prefWidthProperty(), HOME_PANE_MAX_WIDTH),
                        new KeyValue(accountstable.layoutXProperty(), originalTableX),
                        new KeyValue(accountstable.prefWidthProperty(), originalTableWidth),
                        new KeyValue(accountsign.layoutXProperty(), ACCOUNTSIGN_MAX_X)
                )
        );

        timeline.setOnFinished(e -> {
            for (int i = 0; i < accountstable.getColumns().size(); i++) {
                accountstable.getColumns().get(i).setPrefWidth(originalColumnWidths.get(i));
            }
            toggleSidebarText(true);
            maximizebutton.setVisible(false);
            minbutton.setVisible(true);
            accountsign.setVisible(true);
        });

        timeline.play();
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

    @FXML private void homeic(MouseEvent event) { loadFXMLWithState("Homepage.fxml", event); }
    @FXML private void homesi(MouseEvent event) { loadFXMLWithState("Homepage.fxml", event); }
    @FXML private void sum(MouseEvent event) { loadFXMLWithState("executive_summary.fxml", event); }
    @FXML private void openaccount(MouseEvent event) { loadFXMLWithState("MyACCOUNT.fxml", event); }
    @FXML private void col(MouseEvent event) { loadFXMLWithState("collectionreciepts_list.fxml", event); }
    @FXML private void prod(MouseEvent event) { loadFXMLWithState("products_list.fxml", event); }
    @FXML private void del(MouseEvent event) { loadFXMLWithState("deliveryreceipts_list.fxml", event); }
    
    @FXML
    private void delete(MouseEvent event) {
        Account selected = accountstable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Account Selected");
            alert.setContentText("Please select an account to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Account");
        confirmation.setContentText("Are you sure you want to delete this account?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response.getText().equals("OK")) {
                try (Connection conn = PostgresConnect.getConnection();
                     Statement stmt = conn.createStatement()) {
                    
                    // Assuming name is a unique identifier
                    String sql = "DELETE FROM accounts WHERE name = '" + selected.getName().replace("'", "''") + "'";
                    stmt.executeUpdate(sql);

                    accountList.remove(selected);
                    accountstable.refresh();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Account Deleted");
                    alert.setContentText("The selected account has been deleted.");
                    alert.showAndWait();

                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Deletion Failed");
                    errorAlert.setContentText("Could not delete the account due to a database error.");
                    errorAlert.showAndWait();
                }
            }
        });
    }
    
    @FXML
    private void edit(MouseEvent event) {
        Account selected = accountstable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Account Selected");
            alert.setContentText("Please select an account to edit.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add_accounts_edit.fxml"));
            Parent root = loader.load();

            NewAccountControlleredit controller = loader.getController();
            controller.setParentController(this);
            controller.loadAccountData(selected); // 👈 Pass selected account here

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
    private void view_detials(MouseEvent event) {
        Account selected = accountstable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Account Selected");
            alert.setContentText("Please select an account from the table before viewing details.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("accounts_machine.fxml"));
            Parent root = loader.load();
            if (loader.getController() instanceof accounts_machineController controller) {
                controller.applySidebarState(SidebarState.isMinimized());
                controller.setClinicAndAddress(
                    selected.getName(),
                    selected.getMunicipality() + ", " + selected.getProvince()
                );
            }
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML private void exit(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML private void add_account(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add_accounts_form.fxml"));
            Parent root = loader.load();

            NewAccountController controller = loader.getController();
            controller.setParentController(this);

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

    public void refreshAccountsTable() {
        loadAccounts();
    }

    private void loadFXML(String fxml, MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
