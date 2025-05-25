package service;

import dbConn.DBConnection;
import model.BorrowHistoryResponse;
import model.BorrowStatusRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BorrowingService {

    public List<BorrowHistoryResponse> getBorrowingHistory(int customerId) {
        List<BorrowHistoryResponse> history = new ArrayList<>();

        // Validate customer
        String customerSql = "SELECT customer_id FROM Customer WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(customerSql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return history; // Return empty list if customer not found
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }

        // Fetch borrowing history
        String sql = "SELECT b.borrow_date, b.due_date, b.return_date, b.borrowing_fee, b.overdue_fee, bk.title, bb.quantity " +
                "FROM BORROWING b " +
                "JOIN BOOK_BORROW bb ON b.borrowing_id = bb.borrowing_id " +
                "JOIN BOOKS bk ON bb.book_id = bk.book_id " +
                "WHERE b.customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BorrowHistoryResponse record = new BorrowHistoryResponse();
                    record.setBorrowDate(rs.getDate("borrow_date").toString());
                    record.setDueDate(rs.getDate("due_date").toString());
                    record.setReturnDate(rs.getDate("return_date") != null ? rs.getDate("return_date").toString() : null);
                    record.setBorrowingFee(rs.getDouble("borrowing_fee"));
                    record.setOverdueFee(rs.getDouble("overdue_fee"));
                    record.setTitle(rs.getString("title"));
                    record.setQuantity(rs.getInt("quantity"));
                    history.add(record);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }

        return history;
    }

    public Map<String, String> updateBorrowingStatus(BorrowStatusRequest request) {
        Map<String, String> response = new HashMap<>();

        // Validate bill
        String checkSql = "SELECT borrowing_id FROM BORROWING WHERE bill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setInt(1, request.getBillId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    response.put("error", "Bill not found");
                    return response;
                }
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
            return response;
        }

        // Update borrowing status
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String updateSql;
                if ("returned".equals(request.getStatus())) {
                    updateSql = "UPDATE BORROWING SET status = ?, return_date = ? WHERE bill_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                        stmt.setString(1, request.getStatus());
                        stmt.setString(2, request.getReturnDate());
                        stmt.setInt(3, request.getBillId());
                        stmt.executeUpdate();
                    }
                } else {
                    updateSql = "UPDATE BORROWING SET status = ?, return_date = NULL WHERE bill_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                        stmt.setString(1, request.getStatus());
                        stmt.setInt(2, request.getBillId());
                        stmt.executeUpdate();
                    }
                }
                conn.commit();
                response.put("message", "Borrowing status updated successfully");
            } catch (SQLException e) {
                conn.rollback();
                response.put("error", "Failed to update status");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
        }

        return response;
    }
}