package application;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class collection_choice {
	@FXML
	private void machine(MouseEvent event) {
	    try {
	        // Load the new window
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("collection_addmachine.fxml"));
	        Parent root = loader.load();

	        Scene scene = new Scene(root);
	        scene.setFill(Color.TRANSPARENT);

	        Stage newStage = new Stage();
	        newStage.setScene(scene);
	        newStage.initStyle(StageStyle.TRANSPARENT);
	        newStage.initModality(Modality.APPLICATION_MODAL);
	        newStage.setResizable(false);

	        final Delta dragDelta = new Delta();
	        root.setOnMousePressed(e -> {
	            dragDelta.x = e.getSceneX();
	            dragDelta.y = e.getSceneY();
	        });
	        root.setOnMouseDragged(e -> {
	            newStage.setX(e.getScreenX() - dragDelta.x);
	            newStage.setY(e.getScreenY() - dragDelta.y);
	        });

	        newStage.show();

	        // Close the current window (the one containing the button clicked)
	        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	        currentStage.close();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}


    public static class Delta {
        double x, y;
    }

    @FXML
	private void consumable(MouseEvent event) {
	    try {
	        // Load the new window
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("collectionreciepts_add_consumable.fxml"));
	        Parent root = loader.load();

	        Scene scene = new Scene(root);
	        scene.setFill(Color.TRANSPARENT);

	        Stage newStage = new Stage();
	        newStage.setScene(scene);
	        newStage.initStyle(StageStyle.TRANSPARENT);
	        newStage.initModality(Modality.APPLICATION_MODAL);
	        newStage.setResizable(false);

	        final Delta dragDelta = new Delta();
	        root.setOnMousePressed(e -> {
	            dragDelta.x = e.getSceneX();
	            dragDelta.y = e.getSceneY();
	        });
	        root.setOnMouseDragged(e -> {
	            newStage.setX(e.getScreenX() - dragDelta.x);
	            newStage.setY(e.getScreenY() - dragDelta.y);
	        });

	        newStage.show();

	        // Close the current window (the one containing the button clicked)
	        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	        currentStage.close();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Button Clicked");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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