package application;

public class CollectionReceiptRow {
    private String crNumber;
    private String name;
    private String address;
    private String receivedBy;
    private String amount;
    private String status;
    private String machine;
    private String remarks; // leave null for now

    public CollectionReceiptRow(String crNumber, String name, String address, String receivedBy,
                                String amount, String status, String machine, String remarks) {
        this.crNumber = crNumber;
        this.name = name;
        this.address = address;
        this.receivedBy = receivedBy;
        this.amount = amount;
        this.status = status;
        this.machine = machine;
        this.remarks = remarks;
    }

    public String getCrNumber() { return crNumber; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getReceivedBy() { return receivedBy; }
    public String getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getMachine() { return machine; }
    public String getRemarks() { return remarks; }
}
