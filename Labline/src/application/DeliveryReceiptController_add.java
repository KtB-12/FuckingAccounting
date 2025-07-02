package application;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DeliveryReceiptController_add {

    @FXML private DatePicker datePicker;
    @FXML private TextField drNumberField;
    @FXML private TextField registeredNameField;
    @FXML private TextField addressField;
    @FXML private TextField termsField;
    @FXML private TextField quantityField;
    @FXML private TextField itemDescriptionField;
    @FXML private TextField unitPriceField;
    @FXML private TextField discountField;
    @FXML private TextField amountField;
    @FXML private TextField areaManagerField;
    @FXML private TextField districtSalesManagerField;

    @FXML private Button addToListButton;
    @FXML private Button backToListButton;

    @FXML
    private void btl(MouseEvent event) {
    	System.out.println("Back to List clicked!");
        
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
    
    @FXML
    private void list(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pop-up_for_addingreceipt.fxml"));
            Parent root = loader.load();

            // Round window shape
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT); // Make background transparent

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT); // Remove window border
            stage.initModality(Modality.APPLICATION_MODAL); // Block background
            stage.setResizable(false);

            // Optional: Make it draggable
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

    private static class Delta {
        double x, y;
    }
    
    
}
