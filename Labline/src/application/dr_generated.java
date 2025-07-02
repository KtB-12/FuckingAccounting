package application;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class dr_generated {

    @FXML private TableView<GeneratedDeliveryItem> tableView;
    @FXML private TableColumn<GeneratedDeliveryItem, String> drnumber;
    @FXML private TableColumn<GeneratedDeliveryItem, LocalDate> date;
    @FXML private TableColumn<GeneratedDeliveryItem, Integer> qty;
    @FXML private TableColumn<GeneratedDeliveryItem, String> product;
    @FXML private TableColumn<GeneratedDeliveryItem, String> deliveredby;
    @FXML private TableColumn<GeneratedDeliveryItem, Double> amount;
    @FXML private TextField totalall;
    @FXML private Button atl;
    @FXML private Button atl1;

    @FXML
    private void initialize() {
        drnumber.setCellValueFactory(data -> data.getValue().drNumberProperty());
        date.setCellValueFactory(data -> data.getValue().dateDeliveredProperty());
        qty.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        product.setCellValueFactory(data -> data.getValue().productProperty());
        deliveredby.setCellValueFactory(data -> data.getValue().deliveredByProperty());
        amount.setCellValueFactory(data -> data.getValue().amountProperty().asObject());

        loadData();
    }

    private void loadData() {
        ObservableList<GeneratedDeliveryItem> items = FXCollections.observableArrayList();
        double total = 0.0;

        String query = """
            SELECT di.dr_number, dr.date_delivered, di.quantity,
                   p."Product List" AS product_name, dr.delivered_by, di.unit_price
            FROM delivery_items di
            JOIN delivery_receipts dr ON di.dr_number = dr.dr_number
            JOIN product p ON di.product_id = p.id
            WHERE dr.date_delivered BETWEEN ? AND ?
            ORDER BY dr.date_delivered DESC
            """;

        try (Connection conn = PostgresConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(dr_generateDate.selectedStartDate));
            stmt.setDate(2, Date.valueOf(dr_generateDate.selectedEndDate));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String drNumber = rs.getString("dr_number");
                LocalDate dateDelivered = rs.getDate("date_delivered").toLocalDate();
                int quantity = rs.getInt("quantity");
                String productName = rs.getString("product_name");
                String deliveredBy = rs.getString("delivered_by");
                double unitPrice = rs.getDouble("unit_price");

                items.add(new GeneratedDeliveryItem(drNumber, dateDelivered, quantity,
                        productName, deliveredBy, unitPrice));

                total += unitPrice;
            }
            tableView.setItems(items);
            totalall.setText(String.format("%.2f", total));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    

    @FXML
    private void print(MouseEvent event) {
        try {
            // Choose your save path
            String home = System.getProperty("user.home");
            String dest = home + "/Downloads/Generated_Delivery_Receipt.pdf";

            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add a title
            document.add(new Paragraph("Generated Delivery Receipt")
                .setFontSize(16)
                .setBold()
               
                .setMarginBottom(20));

            // Create table with 6 columns
            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 1, 3, 2, 2}))
                .useAllAvailableWidth();

            // Add headers
            table.addHeaderCell("DR Number");
            table.addHeaderCell("Date Delivered");
            table.addHeaderCell("Quantity");
            table.addHeaderCell("Product");
            table.addHeaderCell("Delivered By");
            table.addHeaderCell("Amount");

            double totalAmount = 0.0;

            for (Object obj : tableView.getItems()) {
                if (obj instanceof GeneratedDeliveryItem) {
                    GeneratedDeliveryItem row = (GeneratedDeliveryItem) obj;

                    table.addCell(row.getDrNumber());
                    table.addCell(row.getDateDelivered().toString());
                    table.addCell(String.valueOf(row.getQuantity()));
                    table.addCell(row.getProduct());
                    table.addCell(row.getDeliveredBy());
                    table.addCell(String.format("%.2f", row.getAmount()));

                    totalAmount += row.getAmount();
                }
            }

            // Add table to document
            document.add(table);

            // Add total at the bottom
            document.add(new Paragraph("Total Amount: PHP " + String.format("%.2f", totalAmount))
                .setFontSize(12)
                .setBold()
              
                .setMarginTop(10));

            document.close();

            // Optional: Alert user on success
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Complete");
            alert.setHeaderText(null);
            alert.setContentText("PDF exported successfully to your Desktop!");
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Failed");
            alert.setHeaderText(null);
            alert.setContentText("Failed to export PDF: " + e.getMessage());
            alert.showAndWait();
        }
    }


    @FXML
    private void exit(MouseEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void minimize(MouseEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.setIconified(true);
    }
}
