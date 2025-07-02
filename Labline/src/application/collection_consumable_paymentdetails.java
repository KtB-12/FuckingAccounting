package application;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class collection_consumable_paymentdetails {

    @FXML private CheckBox cashCheckBox;
    @FXML private CheckBox chequeCheckBox;
    @FXML private CheckBox fundTransferCheckBox;
    @FXML private Button atl;
    @FXML private ImageView exit;

    // For window dragging
    private static class Delta { double x, y; }

    @FXML
    public void initialize() {
        atl.setDisable(true);

        cashCheckBox.setOnAction(e -> handleSelection(cashCheckBox));
        chequeCheckBox.setOnAction(e -> handleSelection(chequeCheckBox));
        fundTransferCheckBox.setOnAction(e -> handleSelection(fundTransferCheckBox));
    }

    private void handleSelection(CheckBox selected) {
        if (selected.isSelected()) {
            if (selected != cashCheckBox) cashCheckBox.setSelected(false);
            if (selected != chequeCheckBox) chequeCheckBox.setSelected(false);
            if (selected != fundTransferCheckBox) fundTransferCheckBox.setSelected(false);
            atl.setDisable(false);
        } else {
            if (!cashCheckBox.isSelected() && !chequeCheckBox.isSelected() && !fundTransferCheckBox.isSelected()) {
                atl.setDisable(true);
            }
        }
    }

    @FXML
    private void add_to_list(MouseEvent event) {
        String fxmlToOpen = "";

        if (cashCheckBox.isSelected()) {
            fxmlToOpen = "collectionPOPupconsumable_under CASH2.fxml";
        } else if (chequeCheckBox.isSelected()) {
            fxmlToOpen = "collectionPOPup consumable_under CHEQUE2.fxml";
        } else if (fundTransferCheckBox.isSelected()) {
            fxmlToOpen = "collectionPOPup consumable_under FUND TRANSFER2.fxml";
        }

        if (!fxmlToOpen.isEmpty()) {
            try {
            	FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlToOpen));
                Parent root = loader.load();

                Scene scene = new Scene(root);
                scene.setFill(Color.TRANSPARENT);

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);

                Node source = (Node) event.getSource();
                Bounds boundsInScreen = source.localToScreen(source.getBoundsInLocal());
                stage.setX(boundsInScreen.getMinX());
                stage.setY(boundsInScreen.getMaxY() + 5);

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
    }

    @FXML
    private void exit(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
