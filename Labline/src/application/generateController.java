package application;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class generateController {

    @FXML private Button accbutton;
    @FXML private ImageView accicon;
    @FXML private Button collectionsign;
    @FXML private ImageView colloectionicon;
    @FXML private ImageView deliviericon;
    @FXML private Button deliversign;
    @FXML private Button generatesoa;
    @FXML private Pane homepane;
    @FXML private ImageView homeicon;
    @FXML private Button homesign;
    @FXML private ImageView logicon1;
    @FXML private Button logsign;
    @FXML private ImageView maximizebutton;
    @FXML private ImageView minbutton;
    @FXML private ImageView prodicon;
    @FXML private Button prosign;
    @FXML private ImageView settingsicon;
    @FXML private Button settingsb1;
    @FXML private Pane sidebar;
    @FXML private ImageView summarixicon;
    @FXML private Button summarixsign;
    @FXML private TextField dateField;
    @FXML private TextField accountNameField;
    @FXML private TextField addressField;
    @FXML private TextField totalAmountField;
    @FXML private Pane content;

    private static final double SIDEBAR_MIN_WIDTH = 115;
    private static final double SIDEBAR_MAX_WIDTH = 260;
    private static final double HOME_PANE_MIN_WIDTH = 80;
    private static final double HOME_PANE_MAX_WIDTH = 230;
    private static final Duration ANIMATION_DURATION = Duration.millis(300);
    private boolean isMinimized = false;

    @FXML
    public void initialize() {
        maximizebutton.setVisible(false);

        if (SidebarState.isMinimized()) {
            applyMinimizedState();
        } else {
            applyMaximizedState();
        }
    }

    private void applyMinimizedState() {
        sidebar.setPrefWidth(SIDEBAR_MIN_WIDTH);
        homepane.setPrefWidth(HOME_PANE_MIN_WIDTH);
        content.setLayoutX(200);
        toggleSidebarText(false);
        minbutton.setVisible(false);
        maximizebutton.setVisible(true);
        isMinimized = true;
    }

    private void applyMaximizedState() {
        sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
        homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
        content.setLayoutX(280);
        toggleSidebarText(true);
        minbutton.setVisible(true);
        maximizebutton.setVisible(false);
        isMinimized = false;
    }

    @FXML
    void min(MouseEvent event) {
        if (isMinimized) return;

        toggleSidebarText(false);
        minbutton.setVisible(false);

        Timeline timeline = new Timeline(
            new KeyFrame(ANIMATION_DURATION,
                new KeyValue(sidebar.prefWidthProperty(), SIDEBAR_MIN_WIDTH),
                new KeyValue(homepane.prefWidthProperty(), HOME_PANE_MIN_WIDTH)
            )
        );

        timeline.setOnFinished(e -> {
            maximizebutton.setVisible(true);
            SidebarState.setMinimized(true);
            isMinimized = true;
        });

        timeline.play();
        animateContentX(200);
    }

    @FXML
    void max(MouseEvent event) {
        if (!isMinimized) return;

        Timeline timeline = new Timeline(
            new KeyFrame(ANIMATION_DURATION,
                new KeyValue(sidebar.prefWidthProperty(), SIDEBAR_MAX_WIDTH),
                new KeyValue(homepane.prefWidthProperty(), HOME_PANE_MAX_WIDTH)
            )
        );

        timeline.setOnFinished(e -> {
            toggleSidebarText(true);
            maximizebutton.setVisible(false);
            minbutton.setVisible(true);
            SidebarState.setMinimized(false);
            isMinimized = false;
        });

        timeline.play();
        animateContentX(280);
    }

    private void animateContentX(double targetX) {
        TranslateTransition transition = new TranslateTransition(ANIMATION_DURATION, content);
        transition.setToX(targetX - content.getLayoutX());
        transition.play();
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

    @FXML
    private void exit(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    private void generatesoa(javafx.event.ActionEvent event) {
        System.out.println("Generate SOA button clicked!");
        System.out.println("Date: " + dateField.getText());
        System.out.println("Account Name: " + accountNameField.getText());
        System.out.println("Address: " + addressField.getText());
        System.out.println("Total Amount: " + totalAmountField.getText());
    }

    // Navigation methods
    @FXML private void homeic(MouseEvent event) { loadFXML("Homepage.fxml", event); }
    @FXML private void homesi(MouseEvent event) { loadFXML("Homepage.fxml", event); }
    @FXML private void prod(MouseEvent event) { loadFXML("products_list.fxml", event); }
    @FXML private void del(MouseEvent event) { loadFXML("deliveryreceipts_list.fxml", event); }
    @FXML private void col(MouseEvent event) { loadFXML("collectionreciepts_list.fxml", event); }
    @FXML private void sum(MouseEvent event) { loadFXML("executive_summary.fxml", event); }
    @FXML private void openaccount(MouseEvent event) { loadFXML("MyACCOUNT.fxml", event); }
    @FXML private void back(MouseEvent event) { loadFXML("accounts_consumable.fxml", event); }

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
