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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SummaryReportController implements SidebarStateAware {

    @FXML private Pane sidebar;
    @FXML private ImageView minbutton;
    @FXML private ImageView maximizebutton;
    @FXML private Pane homepane;
    @FXML private Pane maincontent;

    @FXML private Button summarixsign;
    @FXML private ImageView summarixicon;
    @FXML private Button prosign;
    @FXML private ImageView prodicon;
    @FXML private Button logsign;
    @FXML private ImageView settingsicon;
    @FXML private ImageView logicon1;
    @FXML private Button settingsb1;
    @FXML private Button homesign;
    @FXML private ImageView homeicon;
    @FXML private Button accbutton;
    @FXML private ImageView accicon;
    @FXML private Button deliversign;
    @FXML private ImageView deliviericon;
    @FXML private Button collectionsign;
    @FXML private ImageView colloectionicon;

    @FXML private ImageView closeButton;
    @FXML private ImageView minimizeButton;

    private static final double SIDEBAR_MIN_WIDTH = 115;
    private static final double SIDEBAR_MAX_WIDTH = 260;
    private static final double HOME_PANE_MIN_WIDTH = 80;
    private static final double HOME_PANE_MAX_WIDTH = 230;
    private static final Duration ANIMATION_DURATION = Duration.millis(300);
    private static final double MAINCONTENT_SHIFT_DISTANCE = 85;

    private boolean isMinimized = false;
    private double originalMainX;

    @FXML
    public void initialize() {
        maximizebutton.setVisible(false);
        sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
        homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
        originalMainX = maincontent.getLayoutX();
    }

    @Override
    public void applySidebarState(boolean minimized) {
        double shiftedX = originalMainX - MAINCONTENT_SHIFT_DISTANCE;
        if (minimized) {
            sidebar.setPrefWidth(SIDEBAR_MIN_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MIN_WIDTH);
            toggleSidebarText(false);
            minbutton.setVisible(false);
            maximizebutton.setVisible(true);
            maincontent.setLayoutX(shiftedX);
            isMinimized = true;
        } else {
            sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
            toggleSidebarText(true);
            minbutton.setVisible(true);
            maximizebutton.setVisible(false);
            maincontent.setLayoutX(originalMainX);
            isMinimized = false;
        }
    }

    @FXML
    private void min(MouseEvent event) {
        if (isMinimized) return;
        toggleSidebarText(false);
        minbutton.setVisible(false);
        double shiftedX = originalMainX - MAINCONTENT_SHIFT_DISTANCE;
        Timeline timeline = new Timeline(
            new KeyFrame(ANIMATION_DURATION,
                new KeyValue(sidebar.prefWidthProperty(), SIDEBAR_MIN_WIDTH),
                new KeyValue(homepane.prefWidthProperty(), HOME_PANE_MIN_WIDTH),
                new KeyValue(maincontent.layoutXProperty(), shiftedX)
            )
        );
        timeline.setOnFinished(e -> maximizebutton.setVisible(true));
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
                new KeyValue(maincontent.layoutXProperty(), originalMainX)
            )
        );
        timeline.setOnFinished(e -> {
            toggleSidebarText(true);
            maximizebutton.setVisible(false);
            minbutton.setVisible(true);
        });
        timeline.play();
        isMinimized = false;
    }

    private void toggleSidebarText(boolean visible) {
        summarixsign.setVisible(visible);
        prosign.setVisible(visible);
        logsign.setVisible(visible);
        settingsb1.setVisible(visible);
        homesign.setVisible(visible);
        accbutton.setVisible(visible);
        deliversign.setVisible(visible);
        collectionsign.setVisible(visible);
    }

    private void loadFXMLWithState(String fxmlFile, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            if (loader.getController() instanceof SidebarStateAware) {
                ((SidebarStateAware) loader.getController()).applySidebarState(isMinimized);
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
    @FXML private void prod(MouseEvent event) { loadFXMLWithState("products_list.fxml", event); }
    @FXML private void openaccount(MouseEvent event) { loadFXMLWithState("MyACCOUNT.fxml", event); }
    @FXML private void account1(MouseEvent event) { loadFXMLWithState("List_of_accounts.fxml", event); }
    @FXML private void del(MouseEvent event) { loadFXMLWithState("deliveryreceipts_list.fxml", event); }
    @FXML private void col(MouseEvent event) { loadFXMLWithState("collectionreciepts_list.fxml", event); }

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

    @FXML private void exit(MouseEvent event) {
        System.exit(0);
    }
    
    @FXML
    private void generate(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OVERVIEW for AREA MANAGER.fxml"));
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

    @FXML private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
}
