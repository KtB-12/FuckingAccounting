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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class HomeController {

    @FXML private Pane sidebar;
    @FXML private ImageView minbutton;
    @FXML private ImageView maximizebutton;

    @FXML private Pane homepane;
    @FXML private Button homesign;
    @FXML private ImageView homeicon;
    @FXML private Button account1;
    @FXML private ImageView account;
    @FXML private Button prosign;
    @FXML private ImageView prodicon;
    @FXML private Button deliversign;
    @FXML private ImageView deliviericon;
    @FXML private Button collectionsign;
    @FXML private ImageView colloectionicon;
    @FXML private Button summarixsign;
    @FXML private ImageView summarixicon;
    @FXML private Button settingsb1;
    @FXML private ImageView settingsicon;
    @FXML private Button logsign;
    @FXML private ImageView logicon1;

    @FXML private Pane maincontent;
    @FXML private Pane maincontent4;
    @FXML private Pane maincontent5;
    @FXML private Pane maincontent6;
    @FXML private Pane maincontent7;
    @FXML private LineChart<String, Number> linec;
    @FXML private PieChart machineschart;

    @FXML private ChoiceBox<String> calendarbar;
    @FXML private TextField TotalAccounts;
    @FXML private TextField TotalOrders;

    private boolean isMinimized = false;

    private static final double SIDEBAR_MIN_WIDTH = 115;
    private static final double SIDEBAR_MAX_WIDTH = 260;
    private static final double HOME_PANE_MIN_WIDTH = 80;
    private static final double HOME_PANE_MAX_WIDTH = 230;
    private static final Duration ANIMATION_DURATION = Duration.millis(300);

    private double[] originalXPositions = new double[5];
    private double[] originalWidths = new double[5];
    private double[] originalHeights = new double[5];
    private double shiftAmount;

    @FXML
    public void initialize() {
        maximizebutton.setVisible(false);
        homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);

        shiftAmount = SIDEBAR_MAX_WIDTH - SIDEBAR_MIN_WIDTH;

        originalXPositions[0] = maincontent.getLayoutX();
        originalXPositions[1] = maincontent4.getLayoutX();
        originalXPositions[2] = maincontent5.getLayoutX();
        originalXPositions[3] = maincontent6.getLayoutX();
        originalXPositions[4] = maincontent7.getLayoutX();

        originalWidths[0] = maincontent.getPrefWidth();
        originalWidths[1] = maincontent4.getPrefWidth();
        originalWidths[2] = maincontent5.getPrefWidth();
        originalWidths[3] = maincontent6.getPrefWidth();
        originalWidths[4] = maincontent7.getPrefWidth();

        originalHeights[0] = maincontent.getPrefHeight();
        originalHeights[1] = maincontent4.getPrefHeight();
        originalHeights[2] = maincontent5.getPrefHeight();
        originalHeights[3] = maincontent6.getPrefHeight();
        originalHeights[4] = maincontent7.getPrefHeight();

        settingsicon.setOnMouseClicked(this::openMyAccount);
        settingsb1.setOnMouseClicked(this::openMyAccount);
        account.setOnMouseClicked(this::account1);
        account1.setOnMouseClicked(this::account1);

        if (SidebarState.isMinimized()) {
            sidebar.setPrefWidth(SIDEBAR_MIN_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MIN_WIDTH);
            toggleSidebarText(false);
            minbutton.setVisible(false);
            maximizebutton.setVisible(true);

            for (int i = 0; i < 5; i++) {
                getContentPane(i).setLayoutX(originalXPositions[i] - shiftAmount);
            }

            maincontent5.setPrefWidth(originalWidths[2] + shiftAmount);
            linec.setPrefWidth(originalWidths[2] + shiftAmount - 16);
            maincontent7.setPrefWidth(originalWidths[4] + shiftAmount);
            machineschart.setPrefWidth(originalWidths[4] + shiftAmount - 6);
            maincontent7.setPrefHeight(originalHeights[4] + 90);
            machineschart.setPrefHeight(originalHeights[4] + 90 - 6);

            isMinimized = true;
        } else {
            sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
            toggleSidebarText(true);
            minbutton.setVisible(true);
            maximizebutton.setVisible(false);
            isMinimized = false;
        }
    }

    private Pane getContentPane(int index) {
        switch(index) {
            case 0: return maincontent;
            case 1: return maincontent4;
            case 2: return maincontent5;
            case 3: return maincontent6;
            case 4: return maincontent7;
            default: return maincontent;
        }
    }

    private void animateSidebar(double fromWidth, double toWidth, double fromHomeWidth, double toHomeWidth, Runnable onFinished) {
        Timeline timeline = new Timeline();
        KeyValue kvSidebar = new KeyValue(sidebar.prefWidthProperty(), toWidth);
        KeyValue kvHomePane = new KeyValue(homepane.prefWidthProperty(), toHomeWidth);
        KeyFrame kf = new KeyFrame(ANIMATION_DURATION, kvSidebar, kvHomePane);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(e -> onFinished.run());
        timeline.play();
    }

    private void toggleSidebarText(boolean visible) {
        homesign.setVisible(visible);
        account1.setVisible(visible);
        prosign.setVisible(visible);
        deliversign.setVisible(visible);
        collectionsign.setVisible(visible);
        summarixsign.setVisible(visible);
        settingsb1.setVisible(visible);
        logsign.setVisible(visible);
    }

    @FXML private void openMyAccount(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MyACCOUNT.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
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

    @FXML private void min(MouseEvent event) {
        toggleSidebarText(false);
        minbutton.setVisible(false);

        animateSidebar(SIDEBAR_MAX_WIDTH, SIDEBAR_MIN_WIDTH, HOME_PANE_MAX_WIDTH, HOME_PANE_MIN_WIDTH, () -> {
            maximizebutton.setVisible(true);

            Timeline contentTimeline = new Timeline();

            for (int i = 0; i < 5; i++) {
                contentTimeline.getKeyFrames().add(
                    new KeyFrame(ANIMATION_DURATION,
                        new KeyValue(getContentPane(i).layoutXProperty(), originalXPositions[i] - shiftAmount)
                    )
                );
            }

            contentTimeline.getKeyFrames().addAll(
                new KeyFrame(ANIMATION_DURATION,
                    new KeyValue(maincontent5.prefWidthProperty(), originalWidths[2] + shiftAmount),
                    new KeyValue(linec.prefWidthProperty(), originalWidths[2] + shiftAmount - 16),
                    new KeyValue(maincontent7.prefWidthProperty(), originalWidths[4] + shiftAmount),
                    new KeyValue(machineschart.prefWidthProperty(), originalWidths[4] + shiftAmount - 6),
                    new KeyValue(maincontent7.prefHeightProperty(), originalHeights[4] + 90),
                    new KeyValue(machineschart.prefHeightProperty(), originalHeights[4] + 90 - 6)
                )
            );

            contentTimeline.play();

            SidebarState.setMinimized(true);
            isMinimized = true;
        });
    }

    @FXML private void max(MouseEvent event) {
        Timeline contentTimeline = new Timeline();

        for (int i = 0; i < 5; i++) {
            contentTimeline.getKeyFrames().add(
                new KeyFrame(ANIMATION_DURATION,
                    new KeyValue(getContentPane(i).layoutXProperty(), originalXPositions[i])
                )
            );
        }

        contentTimeline.getKeyFrames().addAll(
            new KeyFrame(ANIMATION_DURATION,
                new KeyValue(maincontent5.prefWidthProperty(), originalWidths[2]),
                new KeyValue(linec.prefWidthProperty(), originalWidths[2] - 16),
                new KeyValue(maincontent7.prefWidthProperty(), originalWidths[4]),
                new KeyValue(machineschart.prefWidthProperty(), originalWidths[4] - 6),
                new KeyValue(maincontent7.prefHeightProperty(), originalHeights[4]),
                new KeyValue(machineschart.prefHeightProperty(), originalHeights[4] - 6)
            )
        );

        contentTimeline.setOnFinished(e -> {
            animateSidebar(SIDEBAR_MIN_WIDTH, SIDEBAR_MAX_WIDTH, HOME_PANE_MIN_WIDTH, HOME_PANE_MAX_WIDTH, () -> {
                toggleSidebarText(true);
                maximizebutton.setVisible(false);
                minbutton.setVisible(true);

                SidebarState.setMinimized(false);
                isMinimized = false;
            });
        });

        contentTimeline.play();
    }

    @FXML private void homeic(MouseEvent event) {
        System.out.println("Home icon clicked");
    }

    @FXML private void homesi(MouseEvent event) {
        System.out.println("Home label clicked");
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

    @FXML private void sum(MouseEvent event) {
        loadFXML("executive_summary.fxml", event);
    }
    
    @FXML
    private void prod(MouseEvent event) {
        loadFXML("products_list.fxml", event);
    }

    @FXML private void del(MouseEvent event) {
        loadFXML("deliveryreceipts_list.fxml", event);
    }

    @FXML private void col(MouseEvent event) {
        loadFXML("collectionreciepts_list.fxml", event);
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
        }
    }

    @FXML private void account1(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("List_of_accounts.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
