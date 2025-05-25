package model;

public class BorrowStatusRequest {
    private int billId;
    private String status;
    private String returnDate;

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
}