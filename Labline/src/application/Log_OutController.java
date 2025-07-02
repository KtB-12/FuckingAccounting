package application;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Log_OutController {

    @FXML
    private AnchorPane mainAnchor;


    private void applyRoundedClip(AnchorPane pane, double radius) {
        Rectangle clip = new Rectangle();
        clip.setArcWidth(radius);
        clip.setArcHeight(radius);
        clip.widthProperty().bind(pane.widthProperty());
        clip.heightProperty().bind(pane.heightProperty());
        pane.setClip(clip);
    }
    @FXML 
    private void homesi(MouseEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void yes(MouseEvent event) {
        MainApp.fullRestart();
    }

}