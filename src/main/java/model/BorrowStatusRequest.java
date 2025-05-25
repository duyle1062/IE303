package model;

public class BorrowStatusRequest {
    private int borrowId;
    private String status;
    private String returnDate;

    public int getBorrowId() { return borrowId; }
    public void setBorrowId(int borrowId) { this.borrowId = borrowId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
}