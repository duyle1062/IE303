package model;

public class BorrowingAddRequest {
    private int customerId;
    private String borrowDate;
    private String dueDate;
    private double borrowingFee;
    private double overdueFee;

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public String getBorrowDate() { return borrowDate; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public double getBorrowingFee() { return borrowingFee; }
    public void setBorrowingFee(double borrowingFee) { this.borrowingFee = borrowingFee; }
    public double getOverdueFee() { return overdueFee; }
    public void setOverdueFee(double overdueFee) { this.overdueFee = overdueFee; }
}