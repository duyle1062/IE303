package DAO;

import dbConn.DBConnection;
import model.BorrowHistoryResponse;
import model.BorrowStatusRequest;
import model.BorrowingAddRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BorrowingDAO {

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

        // Validate borrowing
        String checkSql = "SELECT borrowing_id FROM BORROWING WHERE borrowing_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setInt(1, request.getBorrowId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    response.put("error", "Borrowing not found");
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
                    updateSql = "UPDATE BORROWING SET status = ?, return_date = ? WHERE borrowing_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                        stmt.setString(1, request.getStatus());
                        stmt.setString(2, request.getReturnDate());
                        stmt.setInt(3, request.getBorrowId());
                        stmt.executeUpdate();
                    }
                } else {
                    updateSql = "UPDATE BORROWING SET status = ?, return_date = NULL WHERE borrowing_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                        stmt.setString(1, request.getStatus());
                        stmt.setInt(2, request.getBorrowId());
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

    public Map<String, Object> addBorrowing(int adminId, BorrowingAddRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Validate customer
        String customerSql = "SELECT customer_id FROM CUSTOMER WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(customerSql)) {
            stmt.setInt(1, request.getCustomerId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    response.put("error", "Customer not found");
                    return response;
                }
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
            return response;
        }

        // Validate admin
        String adminSql = "SELECT admin_id FROM ADMIN WHERE admin_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(adminSql)) {
            stmt.setInt(1, adminId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    response.put("error", "Admin not found");
                    return response;
                }
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
            return response;
        }

        // Insert borrowing
        String insertSql = "INSERT INTO BORROWING (customer_id, admin_id, borrow_date, due_date, status, borrowing_fee, overdue_fee) " +
                "VALUES (?, ?, ?, ?, 'borrowed', ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, request.getCustomerId());
                    stmt.setInt(2, adminId);
                    stmt.setString(3, request.getBorrowDate());
                    stmt.setString(4, request.getDueDate());
                    stmt.setDouble(5, request.getBorrowingFee());
                    stmt.setDouble(6, request.getOverdueFee());
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            response.put("message", "Borrowing added successfully");
                            response.put("borrowingId", rs.getInt(1));
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                response.put("error", "Failed to add borrowing");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
        }

        return response;
    }

    public Map<String, Object> calculateOverdueFees(int adminId) {
        Map<String, Object> response = new HashMap<>();
        int updatedCount = 0;

        // Validate admin
        String adminSql = "SELECT admin_id FROM ADMIN WHERE admin_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(adminSql)) {
            stmt.setInt(1, adminId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    response.put("error", "Admin not found");
                    return response;
                }
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
            return response;
        }

        // Fetch all borrowings
        String selectSql = "SELECT borrowing_id, due_date, return_date, status FROM BORROWING";
        String updateSql = "UPDATE BORROWING SET overdue_fee = ? WHERE borrowing_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                     ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        int borrowingId = rs.getInt("borrowing_id");
                        java.sql.Date dueDate = rs.getDate("due_date");
                        java.sql.Date returnDate = rs.getDate("return_date");
                        String status = rs.getString("status");
                        double overdueFee = 0.00;

                        if ("overdue".equals(status) ||
                                (returnDate != null && returnDate.after(dueDate))) {
                            overdueFee = 50000.00;
                        }

                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setDouble(1, overdueFee);
                            updateStmt.setInt(2, borrowingId);
                            updateStmt.executeUpdate();
                            updatedCount++;
                        }
                    }
                }
                conn.commit();
                response.put("message", "Overdue fees calculated successfully");
                response.put("updatedCount", updatedCount);
            } catch (SQLException e) {
                conn.rollback();
                response.put("error", "Failed to calculate overdue fees");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
        }

        return response;
    }
}