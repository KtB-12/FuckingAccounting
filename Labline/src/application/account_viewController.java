package application;

import java.util.ResourceBundle;

import java.io.IOException;
import java.net.URL;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

public class account_viewController implements Initializable, SidebarStateAware {

    @FXML private Pane sidebar;
    @FXML private Pane homepane;
    @FXML private ImageView minbutton;
    @FXML private ImageView maximizebutton;

    @FXML private Button homesign, accbutton, prosign, deliversign, collectionsign, summarixsign, logsign, settingsb1;
    @FXML private ImageView homeicon, accicon, prodicon, deliviericon, colloectionicon, summarixicon, logicon1, settingsicon;

    @FXML private TableView<?> detailstable;
    @FXML private TextField totalAmountField;
    @FXML private Text clinicnname;
    @FXML private Text clinicaddress;

    private static final double SIDEBAR_MIN_WIDTH = 115;
    private static final double SIDEBAR_MAX_WIDTH = 260;
    private static final double HOME_PANE_MIN_WIDTH = 80;
    private static final double HOME_PANE_MAX_WIDTH = 230;
    private static final Duration ANIMATION_DURATION = Duration.millis(300);
    private static final double CONTENT_EXPANSION = 170;
    private static final double CLINIC_LABEL_MIN_X = 602;

    private double originalTableWidth;
    private double originalTableX;
    private double originalClinicNameX;
    private double originalClinicAddressX;
    private boolean isMinimized = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        originalTableWidth = detailstable.getPrefWidth();
        originalTableX = detailstable.getLayoutX();
        originalClinicNameX = clinicnname.getLayoutX();
        originalClinicAddressX = clinicaddress.getLayoutX();
        applySidebarState(SidebarState.isMinimized());
    }

    @Override
    public void applySidebarState(boolean minimized) {
        if (minimized) {
            sidebar.setPrefWidth(SIDEBAR_MIN_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MIN_WIDTH);
            toggleSidebarText(false);
            minbutton.setVisible(false);
            maximizebutton.setVisible(true);

            detailstable.setPrefWidth(originalTableWidth + CONTENT_EXPANSION);
            detailstable.setLayoutX(originalTableX - CONTENT_EXPANSION);
            clinicnname.setLayoutX(CLINIC_LABEL_MIN_X);
            clinicaddress.setLayoutX(CLINIC_LABEL_MIN_X);

            isMinimized = true;
        } else {
            sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
            toggleSidebarText(true);
            minbutton.setVisible(true);
            maximizebutton.setVisible(false);

            detailstable.setPrefWidth(originalTableWidth);
            detailstable.setLayoutX(originalTableX);
            clinicnname.setLayoutX(originalClinicNameX);
            clinicaddress.setLayoutX(originalClinicAddressX);

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
                new KeyValue(detailstable.prefWidthProperty(), originalTableWidth + CONTENT_EXPANSION),
                new KeyValue(detailstable.layoutXProperty(), originalTableX - CONTENT_EXPANSION),
                new KeyValue(clinicnname.layoutXProperty(), CLINIC_LABEL_MIN_X),
                new KeyValue(clinicaddress.layoutXProperty(), CLINIC_LABEL_MIN_X)
            )
        );

        timeline.setOnFinished(e -> {
            maximizebutton.setVisible(true);
            SidebarState.setMinimized(true);
            isMinimized = true;
        });

        timeline.play();
    }

    @FXML
    private void max(MouseEvent event) {
        if (!isMinimized) return;

        Timeline timeline = new Timeline(
            new KeyFrame(ANIMATION_DURATION,
                new KeyValue(sidebar.prefWidthProperty(), SIDEBAR_MAX_WIDTH),
                new KeyValue(homepane.prefWidthProperty(), HOME_PANE_MAX_WIDTH),
                new KeyValue(detailstable.prefWidthProperty(), originalTableWidth),
                new KeyValue(detailstable.layoutXProperty(), originalTableX),
                new KeyValue(clinicnname.layoutXProperty(), originalClinicNameX),
                new KeyValue(clinicaddress.layoutXProperty(), originalClinicAddressX)
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

    private void loadFXMLWithState(String fxmlFile, MouseEvent event) {
        try {
            Node source = (Node) event.getSource();
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), source.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                    Parent root = loader.load();

                    Object controller = loader.getController();
                    if (controller instanceof SidebarStateAware) {
                        ((SidebarStateAware) controller).applySidebarState(SidebarState.isMinimized());
                    }

                    Stage stage = (Stage) source.getScene().getWindow();
                    Scene scene = new Scene(root);
              
                    stage.setScene(scene);

                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            fadeOut.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void homeic(MouseEvent event) { loadFXMLWithState("Homepage.fxml", event); }
    @FXML private void homesi(MouseEvent event) { loadFXMLWithState("Homepage.fxml", event); }
    @FXML private void openaccount(MouseEvent event) { loadFXMLWithState("MyACCOUNT.fxml", event); }
    @FXML private void generate(MouseEvent event) { loadFXMLWithState("generate_SOA.fxml", event); }
    @FXML private void back(MouseEvent event) { loadFXMLWithState("list_of_accounts.fxml", event); }
    @FXML private void prod(MouseEvent event) { loadFXMLWithState("products_list.fxml", event); }
    @FXML private void sum(MouseEvent event) { loadFXMLWithState("executive_summary.fxml", event); }
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

    @FXML
    private void exit(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
}
