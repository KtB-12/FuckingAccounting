package application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class myaccount implements SidebarStateAware {

    @FXML private Pane sidebar, homepane, settingscontent;
    @FXML private ImageView minbutton, maximizebutton;

    @FXML private Button homesign, accbutton, prosign, deliversign, collectionsign, summarixsign, settingsb1, logsign;
    @FXML private ImageView homeicon, accicon, prodicon, deliviericon, colloectionicon, summarixicon, settingsicon, logicon1;

    @FXML private TextField username, fname, keyTextField, acctype;
    @FXML private PasswordField password, keyPasswordField;
    @FXML private DatePicker bday;
    @FXML private Circle avatar;
    @FXML private Button change, delete, update;

    private byte[] selectedImageBytes = null;
    private byte[] avatarBytes = null;
    public static byte[] avatarImageData;

    private static final double SIDEBAR_MIN_WIDTH = 115;
    private static final double SIDEBAR_MAX_WIDTH = 260;
    private static final double HOME_PANE_MIN_WIDTH = 80;
    private static final double HOME_PANE_MAX_WIDTH = 230;
    private static final Duration ANIMATION_DURATION = Duration.millis(300);
    private static final double SCENE_WIDTH = 1500;

    private double originalSettingsX;

    @FXML
    public void initialize() {
        maximizebutton.setVisible(false);
        homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
        originalSettingsX = settingscontent.getLayoutX();

        applySidebarState(SidebarState.isMinimized());

        username.setText(UserSession.username);
        fname.setText(UserSession.name);
        password.setText(UserSession.password);
        if (UserSession.birthday != null) {
            bday.setValue(LocalDate.parse(UserSession.birthday));
        }
        acctype.setText(UserSession.acctype);

        try (Connection conn = PostgresConnect.getConnection()) {
            String query = "SELECT keys FROM adminkeys LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String keyValue = rs.getString("keys");
                if ("superadmin".equalsIgnoreCase(UserSession.acctype)) {
                    keyTextField.setText(keyValue);
                    keyTextField.setVisible(true);
                    keyPasswordField.setVisible(false);
                } else {
                    keyPasswordField.setText(keyValue);
                    keyPasswordField.setVisible(true);
                    keyTextField.setVisible(false);
                }
            }

            stmt = conn.prepareStatement("SELECT pictures FROM users WHERE user_id = ?");
            stmt.setInt(1, UserSession.id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                avatarImageData = rs.getBytes("pictures");
                if (avatarImageData != null) {
                    avatar.setFill(new ImagePattern(new Image(new ByteArrayInputStream(avatarImageData))));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void applySidebarState(boolean minimized) {
        if (minimized) {
            sidebar.setPrefWidth(SIDEBAR_MIN_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MIN_WIDTH);
            toggleSidebarText(false);
            minbutton.setVisible(false);
            maximizebutton.setVisible(true);
            double newContentX = ((SCENE_WIDTH - SIDEBAR_MIN_WIDTH - settingscontent.getPrefWidth()) / 2) + 110;
            settingscontent.setLayoutX(newContentX);
        } else {
            sidebar.setPrefWidth(SIDEBAR_MAX_WIDTH);
            homepane.setPrefWidth(HOME_PANE_MAX_WIDTH);
            toggleSidebarText(true);
            minbutton.setVisible(true);
            maximizebutton.setVisible(false);
            settingscontent.setLayoutX(originalSettingsX);
        }
    }

    @FXML
    private void min(MouseEvent event) {
        if (SidebarState.isMinimized()) return;

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
            double newContentX = ((SCENE_WIDTH - SIDEBAR_MIN_WIDTH - settingscontent.getPrefWidth()) / 2) + 110;
            new Timeline(new KeyFrame(ANIMATION_DURATION,
                new KeyValue(settingscontent.layoutXProperty(), newContentX))).play();
        });
        timeline.play();
    }

    @FXML
    private void max(MouseEvent event) {
        if (!SidebarState.isMinimized()) return;

        // Animate settingscontent first
        Timeline layoutTimeline = new Timeline(
            new KeyFrame(ANIMATION_DURATION,
                new KeyValue(settingscontent.layoutXProperty(), originalSettingsX)
            )
        );

        layoutTimeline.setOnFinished(e -> {
            // Animate the sidebar and homepane width
            Timeline sidebarTimeline = new Timeline(
                new KeyFrame(ANIMATION_DURATION,
                    new KeyValue(sidebar.prefWidthProperty(), SIDEBAR_MAX_WIDTH),
                    new KeyValue(homepane.prefWidthProperty(), HOME_PANE_MAX_WIDTH)
                )
            );

            sidebarTimeline.setOnFinished(ev -> {
                // Only show labels AFTER the sidebar is fully expanded
                toggleSidebarText(true);
                maximizebutton.setVisible(false);
                minbutton.setVisible(true);
                SidebarState.setMinimized(false);
            });

            sidebarTimeline.play();
        });

        layoutTimeline.play();
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

    @FXML private void update(MouseEvent event) {
        try (Connection conn = PostgresConnect.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE users SET username = ?, name = ?, password = ?, birthday = ?, pictures = ? WHERE user_id = ?");

            stmt.setString(1, username.getText());
            stmt.setString(2, fname.getText());
            stmt.setString(3, password.getText());
            LocalDate localDate = bday.getValue();
            if (localDate != null) {
                stmt.setDate(4, java.sql.Date.valueOf(localDate));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }

            if (selectedImageBytes != null) {
                stmt.setBytes(5, selectedImageBytes);
                avatarImageData = selectedImageBytes;
            } else if (avatarImageData != null) {
                stmt.setBytes(5, avatarImageData);
            } else {
                stmt.setNull(5, java.sql.Types.BINARY);
            }

            stmt.setInt(6, UserSession.id);
            int result = stmt.executeUpdate();
            showAlert("Success", result > 0 ? "Account updated successfully!" : "No changes were made.",
                Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML private void change(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Avatar");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            try {
                selectedImageBytes = Files.readAllBytes(file.toPath());
                avatar.setFill(new ImagePattern(new Image(file.toURI().toString())));
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to load the image.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML private void delete(MouseEvent event) {
        try (Connection conn = PostgresConnect.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE users SET pictures = NULL WHERE user_id = ?");
            stmt.setInt(1, UserSession.id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                avatar.setFill(null);
                avatarBytes = null;
                showAlert("Deleted", "Avatar removed successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Info", "No avatar to delete.", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadPage(String fxmlFile, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            if (loader.getController() instanceof SidebarStateAware) {
                ((SidebarStateAware) loader.getController()).applySidebarState(SidebarState.isMinimized());
            }

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void homeic(MouseEvent event) { loadPage("Homepage.fxml", event); }
    @FXML private void homesi(MouseEvent event) { loadPage("Homepage.fxml", event); }
    @FXML private void account1(MouseEvent event) { loadPage("List_of_accounts.fxml", event); }
    @FXML private void prod(MouseEvent event) { loadPage("products_list.fxml", event); }
    @FXML private void del(MouseEvent event) { loadPage("deliveryreceipts_list.fxml", event); }
    @FXML private void col(MouseEvent event) { loadPage("collectionreciepts_list.fxml", event); }
    @FXML private void sum(MouseEvent event) { loadPage("executive_summary.fxml", event); }
    @FXML private void settingsClicked(MouseEvent event) { loadPage("Settings.fxml", event); }
    @FXML private void logoutClicked(MouseEvent event) { loadPage("Login.fxml", event); }
    @FXML private void exit(MouseEvent event) { ((Stage)((Node)event.getSource()).getScene().getWindow()).close(); }
    @FXML private void minimize(MouseEvent event) { ((Stage)((Node)event.getSource()).getScene().getWindow()).setIconified(true); }

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
}
