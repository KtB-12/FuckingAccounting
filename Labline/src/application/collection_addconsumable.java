package application;

import java.util.ResourceBundle;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class collection_addconsumable implements Initializable {

    @FXML private TextField crnumber;
    @FXML private TextField name;
    @FXML private TextField address;
    @FXML private DatePicker date;
    @FXML private TextField price;
    @FXML private TextField total;
    @FXML private TextField recievedby;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Optional: set default date to today
        date.setValue(LocalDate.now());
    }
    
    @FXML
    private void listdr(MouseEvent event) {
    	System.out.print("asdfasfda");
    }
    
    @FXML
    private void listpd(MouseEvent event) {
    	System.out.print("asdfasfda");
    }
    
    @FXML
    private void add_to_list(MouseEvent event) {
    	System.out.print("asdfasfda");
    }

    @FXML
    private void adddr(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("collectionPOPup_ADD DR NUMBER_CR-consumables.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT); 

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            // Get screen coordinates of the button
            Node source = (Node) event.getSource();
            Bounds boundsInScreen = source.localToScreen(source.getBoundsInLocal());

            // Set stage position slightly below the button
            stage.setX(boundsInScreen.getMinX());
            stage.setY(boundsInScreen.getMaxY() + 5); // +5 pixels below

            // Dragging support
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
    private void back_to_list(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
    //collectionFORM machine_ADD PAYMENT DETAILS.fxml
    @FXML
    private void addpd(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("collectionFORM consumable_ADD PAYMENT DETAILS.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT); 

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            // Get screen coordinates of the button
            Node source = (Node) event.getSource();
            Bounds boundsInScreen = source.localToScreen(source.getBoundsInLocal());

            // Set stage position slightly below the button
            stage.setX(boundsInScreen.getMinX());
            stage.setY(boundsInScreen.getMaxY() + 5); // +5 pixels below

            // Dragging support
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
