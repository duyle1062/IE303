package model;

public class BorrowHistoryResponse {
    private String borrowDate;
    private String dueDate;
    private String returnDate;
    private double borrowingFee;
    private double overdueFee;
    private String title;
    private int quantity;

    public String getBorrowDate() { return borrowDate; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    public double getBorrowingFee() { return borrowingFee; }
    public void setBorrowingFee(double borrowingFee) { this.borrowingFee = borrowingFee; }
    public double getOverdueFee() { return overdueFee; }
    public void setOverdueFee(double overdueFee) { this.overdueFee = overdueFee; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getQuantity() { return  quantity;}
    public void setQuantity(int quantity) { this.quantity = quantity; }
}