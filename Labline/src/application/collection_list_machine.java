	package application;
	
	import javafx.fxml.FXML;
	import javafx.scene.control.TableColumn;
	import javafx.scene.control.TableView;
	import javafx.scene.control.cell.PropertyValueFactory;
	import javafx.scene.input.MouseEvent;
	
	public class collection_list_machine {
	
	    @FXML private TableView<PaymentRecord> list;
	    @FXML private TableColumn<PaymentRecord, String> amount;
	    @FXML private TableColumn<PaymentRecord, String> mod;
	
	    @FXML
	    private void initialize() {
	        mod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
	        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
	
	        list.setItems(PaymentSessionData.getInstance().getPaymentRecords());
	        System.out.println("Payment list loaded with " + PaymentSessionData.getInstance().getPaymentRecords().size() + " records.");
	    }
	
	    @FXML
	    private void exit(MouseEvent event) {
	        ((javafx.stage.Stage)(((javafx.scene.Node)event.getSource()).getScene().getWindow())).close();
	    }
	}
