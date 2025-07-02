package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SignInC2 {

    @FXML private TextField username;
    @FXML private TextField sadmin;
    @FXML private PasswordField pass;
    @FXML private PasswordField cpass;
    @FXML private DatePicker bday;
    @FXML private Button Ca1;
    @FXML private Button Li1;

    private Connection connect() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/Labline";
        String user = "postgres";
        String dbPassword = "password"; // Replace with your actual PostgreSQL password
        return DriverManager.getConnection(url, user, dbPassword);
    }

    @FXML
    private void Ca1(MouseEvent event) throws IOException {
        String uname = username.getText().trim();
        String staffKey = sadmin.getText().trim();
        String createPass = pass.getText().trim();
        String confirmPass = cpass.getText().trim();
        LocalDate birthdate = bday.getValue();

        if (uname.isEmpty() || createPass.isEmpty() || confirmPass.isEmpty() || birthdate == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill in all required fields.");
            return;
        }

        if (!createPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
            return;
        }

        try (Connection conn = connect()) {
            boolean isSuperAdmin = false;

            if (!staffKey.isEmpty()) {
                PreparedStatement checkKeyStmt = conn.prepareStatement("SELECT * FROM adminkeys WHERE keys = ?");
                checkKeyStmt.setString(1, staffKey);
                ResultSet rs = checkKeyStmt.executeQuery();

                if (!rs.next()) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Key", "Superadmin key is invalid.");
                    return;
                }

                isSuperAdmin = true;
            }

            String sql = "INSERT INTO users (username, name, birthday, password, admin, superadmin) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, uname); 
            stmt.setString(2, uname); 
            stmt.setDate(3, java.sql.Date.valueOf(birthdate)); 
            stmt.setString(4, createPass); // password

            if (isSuperAdmin) {
                stmt.setString(5, null);       // admin
                stmt.setString(6, staffKey);   // superadmin
            } else {
                stmt.setString(5, "admin");    // admin
                stmt.setString(6, null);       // superadmin
            }

            int inserted = stmt.executeUpdate();
            if (inserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Failed to create account.");
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

    @FXML
    private void minimize(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void exit(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
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
    private void Li1(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Sing_in.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
