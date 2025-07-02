package application;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class accounts_machineController implements SidebarStateAware {

    @FXML private Pane sidebar, homepane, contentpane;
    @FXML private AnchorPane mainPane;
    @FXML private ImageView minbutton, maximizebutton;
    @FXML private Button homesign, accbutton, prosign, deliversign, collectionsign, summarixsign, logsign, settingsb1;
    @FXML private ImageView homeicon, accicon, prodicon, deliviericon, colloectionicon, summarixicon, logicon1, settingsicon;
    @FXML private Button machine, consumable, generate;
    @FXML private ImageView edit, delete, exit, minimize, menudrop;
    @FXML private TableView<?> unpaidMonthsTable;
    @FXML private TextField machineField, dateInstalledField, termsField, totalField;
    @FXML private Label clinic, address;

    private static final double SIDEBAR_MIN_WIDTH = 115;
    private static final double SIDEBAR_MAX_WIDTH = 260;
    private static final double HOME_PANE_MIN_WIDTH = 80;
    private static final double HOME_PANE_MAX_WIDTH = 230;
    private static final Duration ANIMATION_DURATION = Duration.millis(300);

    private double originalContentX, originalMachineX, originalConsumableX, originalAddAcX;
    private double originalGenerateX, originalClinicX, originalAddressX;

    @FXML
    private void initialize() {
        maximizebutton.setVisible(false);
        homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);

        originalContentX = contentpane.getLayoutX();
        originalMachineX = machine.getLayoutX();
        originalConsumableX = consumable.getLayoutX();
        originalGenerateX = generate.getLayoutX();
        originalClinicX = clinic.getLayoutX();
        originalAddressX = address.getLayoutX();

        if (exit != null) exit.setOnMouseClicked(this::exit);
        if (minimize != null) minimize.setOnMouseClicked(this::minimize);
        if (menudrop != null) menudrop.setOnMouseClicked(this::toggleSidebar);

        clinic.setText("No Clinic Selected");
        address.setText("No Address Available");

        applySidebarState(SidebarState.isMinimized());
        switchToMachineView();
    }

    @Override
    public void applySidebarState(boolean minimized) {
        double moveDistance = (SIDEBAR_MAX_WIDTH - SIDEBAR_MIN_WIDTH) / 2;

        if (minimized) {
            sidebar.setPrefWidth(SIDEBAR_MIN_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MIN_WIDTH);
            toggleSidebarText(false);
            minbutton.setVisible(false);
            maximizebutton.setVisible(true);

            contentpane.setLayoutX(originalContentX - moveDistance);
            machine.setLayoutX(originalMachineX - moveDistance);
            consumable.setLayoutX(originalConsumableX - moveDistance);
            generate.setLayoutX(originalGenerateX - moveDistance);
            clinic.setLayoutX(originalClinicX - moveDistance);
            address.setLayoutX(originalAddressX - moveDistance);
        } else {
            sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
            toggleSidebarText(true);
            minbutton.setVisible(true);
            maximizebutton.setVisible(false);

            contentpane.setLayoutX(originalContentX);
            machine.setLayoutX(originalMachineX);
            consumable.setLayoutX(originalConsumableX);
            generate.setLayoutX(originalGenerateX);
            clinic.setLayoutX(originalClinicX);
            address.setLayoutX(originalAddressX);
        }
    }

    @FXML
    private void min(MouseEvent event) {
        if (SidebarState.isMinimized()) return;

        toggleSidebarText(false);
        minbutton.setVisible(false);

        double moveDistance = (SIDEBAR_MAX_WIDTH - SIDEBAR_MIN_WIDTH) / 2;

        Timeline timeline = new Timeline(
            new KeyFrame(ANIMATION_DURATION,
                new KeyValue(sidebar.prefWidthProperty(), SIDEBAR_MIN_WIDTH),
                new KeyValue(homepane.prefWidthProperty(), HOME_PANE_MIN_WIDTH),
                new KeyValue(contentpane.layoutXProperty(), originalContentX - moveDistance),
                new KeyValue(machine.layoutXProperty(), originalMachineX - moveDistance),
                new KeyValue(consumable.layoutXProperty(), originalConsumableX - moveDistance),
                new KeyValue(generate.layoutXProperty(), originalGenerateX - moveDistance),
                new KeyValue(clinic.layoutXProperty(), originalClinicX - moveDistance),
                new KeyValue(address.layoutXProperty(), originalAddressX - moveDistance)
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
                new KeyValue(contentpane.layoutXProperty(), originalContentX),
                new KeyValue(machine.layoutXProperty(), originalMachineX),
                new KeyValue(consumable.layoutXProperty(), originalConsumableX),
                new KeyValue(generate.layoutXProperty(), originalGenerateX),
                new KeyValue(clinic.layoutXProperty(), originalClinicX),
                new KeyValue(address.layoutXProperty(), originalAddressX)
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

    @FXML private void toggleSidebar(MouseEvent event) {
        if (SidebarState.isMinimized()) {
            max(event);
        } else {
            min(event);
        }
    }

    private void switchToMachineView() {
        machine.setStyle("-fx-background-color: #47919f; -fx-text-fill: white;");
        consumable.setStyle("-fx-background-color: white; -fx-text-fill: black;");
    }

    @FXML
    private void switchToConsumableView() {
        consumable.setStyle("-fx-background-color: #47919f; -fx-text-fill: white;");
        machine.setStyle("-fx-background-color: white; -fx-text-fill: black;");
    }

    @FXML
    private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void exit(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
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

    @FXML private void prod(MouseEvent event) { loadFXMLWithState("products_list.fxml", event); }
    @FXML private void homeic(MouseEvent event) { loadFXMLWithState("Homepage.fxml", event); }
    @FXML private void acc(MouseEvent event) { loadFXMLWithState("List_of_accounts.fxml", event); }
    @FXML private void homesi(MouseEvent event) { loadFXMLWithState("Homepage.fxml", event); }
    @FXML private void sum(MouseEvent event) { loadFXMLWithState("executive_summary.fxml", event); }
    @FXML private void openaccount(MouseEvent event) { loadFXMLWithState("MyACCOUNT.fxml", event); }
    @FXML private void del(MouseEvent event) { loadFXMLWithState("deliveryreceipts_list.fxml", event); }
    @FXML private void col(MouseEvent event) { loadFXMLWithState("collectionreceipts_list.fxml", event); }
    @FXML private void account1(MouseEvent event) { loadFXMLWithState("List_of_accounts.fxml", event); }
    @FXML private void generateSOA(MouseEvent event) { loadFXMLWithState("generateSOAMachine.fxml", event); }
    
    @FXML
    private void consumableTable(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("accounts_consumable.fxml"));
            Parent root = loader.load();

            // Assuming you get clinic and address from your existing fields
            String selectedClinic = clinic.getText(); 
            String selectedAddress = address.getText(); 

            accounts_consumableController controller = loader.getController();
            controller.applySidebarState(SidebarState.isMinimized());
            controller.setClinicAndAddress(selectedClinic, selectedAddress);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading accounts_consumable.fxml");
        }
    }


    @FXML
    private void addmachines(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add_accounts_for_machine.fxml"));
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
    private void addAccount(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("account_add.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Account");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading account_add.fxml");
        }
    }

    public void setClinicAndAddress(String clinicName, String fullAddress) {
        if (clinic != null) clinic.setText(clinicName);
        if (address != null) address.setText(fullAddress);
    }

    public void setMachineData(String machineName, String installDate, String terms) {
        machineField.setText(machineName);
        dateInstalledField.setText(installDate);
        termsField.setText(terms);
    }

    public void setTotalAmount(String amount) {
        totalField.setText(amount);
    }
}
