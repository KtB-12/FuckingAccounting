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
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class CollectionReceiptsController_view {

    @FXML private Button homesign, accbutton, deliversign, prosign, summarixsign, logsign, settingsb1, collectionsign;

    @FXML private ImageView minbutton, maximizebutton;
    @FXML private ImageView homeicon, accicon, deliviericon, prodicon, summarixicon, settingsicon, logicon1, colloectionicon;
    @FXML private ImageView edit, delete;

    @FXML private ImageView exit, minimize;
    @FXML private ChoiceBox<?> choiceBox;
    @FXML private Pane content;
    @FXML private Pane sidebar, homepane;

    private static final double SIDEBAR_MIN_WIDTH = 115;
    private static final double SIDEBAR_MAX_WIDTH = 260;
    private static final double HOME_PANE_MIN_WIDTH = 80;
    private static final double HOME_PANE_MAX_WIDTH = 230;
    private static final double CONTENT_SHIFT = 110;
    private static final Duration ANIMATION_DURATION = Duration.millis(300);

    private double originalContentX;
    private boolean isMinimized = false;

    @FXML
    private void initialize() {
        boolean shouldMinimize = SidebarState.isMinimized();
        maximizebutton.setVisible(false);
        homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
        originalContentX = content.getLayoutX();

        // Apply sidebar state instantly (no animation)
        if (shouldMinimize) {
            sidebar.setPrefWidth(SIDEBAR_MIN_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MIN_WIDTH);
            content.setLayoutX(originalContentX - CONTENT_SHIFT);
            toggleSidebarText(false);
            minbutton.setVisible(false);
            maximizebutton.setVisible(true);
            isMinimized = true;
        } else {
            sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
            content.setLayoutX(originalContentX);
            toggleSidebarText(true);
            minbutton.setVisible(true);
            maximizebutton.setVisible(false);
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
                new KeyValue(content.layoutXProperty(), originalContentX - CONTENT_SHIFT)
            )
        );

        timeline.setOnFinished(e -> {
            maximizebutton.setVisible(true);
            SidebarState.setMinimized(true); // save state
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
                new KeyValue(content.layoutXProperty(), originalContentX)
            )
        );

        timeline.setOnFinished(e -> {
            toggleSidebarText(true);
            maximizebutton.setVisible(false);
            minbutton.setVisible(true);
            SidebarState.setMinimized(false); // save state
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

    @FXML
    private void exit(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML private void prod(MouseEvent event) { loadFXML("products_list.fxml", event); }
    @FXML private void homeic(MouseEvent event) { loadFXML("Homepage.fxml", event); }
    @FXML private void homesi(MouseEvent event) { loadFXML("Homepage.fxml", event); }
    @FXML private void back(MouseEvent event) { loadFXML("collectionreciepts_list.fxml", event); }

    
    @FXML
    private void deposit(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("collectionPOPup STATUS_under DEPOSITED.fxml"));
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
    private void remit(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("collectionPOPup STATUS_under REMITTED.fxml"));
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
    private void addac(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/collectionreciepts_add.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Product");
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading collectionreciepts_add.fxml");
        }
    }

    @FXML private void sum(MouseEvent event) { loadFXML("executive_summary.fxml", event); }
    @FXML private void openaccount(MouseEvent event) { loadFXML("MyACCOUNT.fxml", event); }
    @FXML private void del(MouseEvent event) { loadFXML("deliveryreceipts_list.fxml", event); }
    @FXML private void account1(MouseEvent event) { loadFXML("List_of_accounts.fxml", event); }

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
