package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        launchApp(primaryStage);
    }

    public static void launchApp(Stage stage) {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource("Sing_in.fxml"));
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

            // Center the stage
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Closes all open windows and relaunches the app cleanly
    public static void fullRestart() {
        Platform.runLater(() -> {
            // Close all open windows
            for (Stage stage : Stage.getWindows().filtered(w -> w instanceof Stage).toArray(Stage[]::new)) {
                stage.close();
            }

            // Start fresh app window
            Stage newStage = new Stage();
            launchApp(newStage);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
