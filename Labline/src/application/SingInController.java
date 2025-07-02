package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SingInController {

    @FXML private TextField username;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private CheckBox remeberpass;
    @FXML private ImageView seepass;
    @FXML private ImageView exit;
    @FXML private ImageView minimize;
    @FXML private Button log1;
    @FXML private Button sign1;

    private boolean isPasswordVisible = false;

    private Connection connect() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/Labline";
        String user = "postgres";
        String dbPassword = "password"; // replace with your actual DB password
        return DriverManager.getConnection(url, user, dbPassword);
    }

    @FXML
    private void togglePasswordVisibility(MouseEvent event) {
        if (isPasswordVisible) {
            passwordField.setText(passwordVisibleField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
        } else {
            passwordVisibleField.setText(passwordField.getText());
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        }
        isPasswordVisible = !isPasswordVisible;
    }

    @FXML
    private void log1(MouseEvent event) throws IOException {
        String uname = username.getText().trim();
        String pwd = isPasswordVisible ? passwordVisibleField.getText().trim() : passwordField.getText().trim();

        if (uname.isEmpty() || pwd.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter username and password.");
            return;
        }

        try (Connection conn = connect()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, uname);
            stmt.setString(2, pwd);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // ✅ Save session data from correct columns
                UserSession.id = rs.getInt("user_id");
                UserSession.username = rs.getString("username");
                UserSession.password = rs.getString("password");
                UserSession.name = rs.getString("name");         // Only if "name" column exists
                UserSession.birthday = rs.getString("birthday"); // Only if "birthday" column exists

                // ✅ Determine account type based on null values
                boolean isSuperadmin = rs.getString("admin") == null;
                boolean isAdmin = rs.getString("superadmin") == null;
                UserSession.acctype = isSuperadmin ? "superadmin" : (isAdmin ? "admin" : "unknown");

                switchScene(event, "Homepage.fxml");

            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect username or password.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void switchScene(MouseEvent event, String fxmlFile) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
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

    @FXML
    private void forg(MouseEvent event) throws IOException {
        switchScene(event, "Reset_Pass.fxml");
    }

    @FXML
    private void sign1(MouseEvent event) throws IOException {
        switchScene(event, "test2.fxml");
    }

    @FXML
    private void mission(MouseEvent event) throws IOException {
        // Get current stage and scene
        Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene ownerScene = ownerStage.getScene();
        Parent ownerRoot = ownerScene.getRoot();

        // Create background dim layer
        AnchorPane dimLayer = new AnchorPane();
        dimLayer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        dimLayer.setPrefSize(ownerScene.getWidth(), ownerScene.getHeight());

        if (ownerRoot instanceof Pane) {
            ((Pane) ownerRoot).getChildren().add(dimLayer);
        }

        // Load the Mission popup
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Mission.fxml"));
        Parent popupRoot = fxmlLoader.load();

        // Clip the popup root to a rounded rectangle
        Rectangle clip = new Rectangle(popupRoot.prefWidth(-1), popupRoot.prefHeight(-1));
        clip.setArcWidth(60);  // 30px corner radius horizontally
        clip.setArcHeight(60); // 30px corner radius vertically
        popupRoot.setClip(clip);

        // Scene and stage setup
        Scene popupScene = new Scene(popupRoot);
        popupScene.setFill(Color.TRANSPARENT);

        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.TRANSPARENT);
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initOwner(ownerStage);
        popupStage.setScene(popupScene);

        // Set initial opacity and scale for opening animation
        popupRoot.setOpacity(0);
        popupRoot.setScaleX(0.9);
        popupRoot.setScaleY(0.9);

        popupStage.show();

        // Fade and scale in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), popupRoot);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(250), popupRoot);
        scaleIn.setFromX(0.9);
        scaleIn.setFromY(0.9);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        new ParallelTransition(fadeIn, scaleIn).play();

        // Auto-close popup when mouse exits
        popupRoot.setOnMouseExited(e -> {
            // Fade and scale out animation
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), popupRoot);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), popupRoot);
            scaleOut.setFromX(1.0);
            scaleOut.setFromY(1.0);
            scaleOut.setToX(0.9);
            scaleOut.setToY(0.9);

            ParallelTransition closeAnim = new ParallelTransition(fadeOut, scaleOut);
            closeAnim.setOnFinished(ev -> {
                popupStage.close();
                if (ownerRoot instanceof Pane) {
                    ((Pane) ownerRoot).getChildren().remove(dimLayer);
                }
            });
            closeAnim.play();
        });
    }
    @FXML
    private void vision(MouseEvent event) throws IOException {
        // Get the current window (main stage)
        Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene ownerScene = ownerStage.getScene();
        Parent ownerRoot = ownerScene.getRoot();

        AnchorPane dimLayer = new AnchorPane();
        dimLayer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        dimLayer.setPrefSize(ownerScene.getWidth(), ownerScene.getHeight());

        if (ownerRoot instanceof Pane) {
            ((Pane) ownerRoot).getChildren().add(dimLayer);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Vision.fxml"));
        Parent popupRoot = fxmlLoader.load();

        Scene popupScene = new Scene(popupRoot);
        popupScene.setFill(Color.TRANSPARENT);

        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.TRANSPARENT);
        popupStage.initOwner(ownerStage); 
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.setScene(popupScene);

        popupRoot.setStyle("-fx-background-color: white; -fx-background-radius: 30;");
        popupRoot.setOpacity(0);
        popupRoot.setScaleX(0.9);
        popupRoot.setScaleY(0.9);

        popupStage.show();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), popupRoot);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(250), popupRoot);
        scaleIn.setFromX(0.9);
        scaleIn.setFromY(0.9);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        new ParallelTransition(fadeIn, scaleIn).play();

        popupRoot.setOnMouseExited(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), popupRoot);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), popupRoot);
            scaleOut.setFromX(1.0);
            scaleOut.setFromY(1.0);
            scaleOut.setToX(0.9);
            scaleOut.setToY(0.9);

            ParallelTransition closeAnim = new ParallelTransition(fadeOut, scaleOut);
            closeAnim.setOnFinished(ev -> {
                popupStage.close();

                if (ownerRoot instanceof Pane) {
                    ((Pane) ownerRoot).getChildren().remove(dimLayer);
                }
            });
            closeAnim.play();
        });
    }


    @FXML
    private void report(MouseEvent event) throws IOException {
        switchScene(event, "Report_Prob.fxml");
    }
}
