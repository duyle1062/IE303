package service;

import dbConn.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ReservationService {
    public Map<String, Object> createReservation(int customerId, String bookName) {
        Map<String, Object> response = new HashMap<>();

        // Validate customer
        String customerSql = "SELECT customer_id FROM CUSTOMER WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(customerSql)) {
            stmt.setInt(1, customerId);
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

        // Find book
        String bookSql = "SELECT book_id, copies_available FROM BOOKS WHERE title = ?";
        int bookId = -1;
        int copiesAvailable = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(bookSql)) {
            stmt.setString(1, bookName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    bookId = rs.getInt("book_id");
                    copiesAvailable = rs.getInt("copies_available");
                } else {
                    response.put("error", "Book not found");
                    return response;
                }
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
            return response;
        }

        // Check if book is available
        if (copiesAvailable <= 0) {
            response.put("error", "Book is not available");
            return response;
        }

        // Create reservation
        String insertSql = "INSERT INTO Reservations (customer_id, book_id, reservation_date, status) VALUES (?, ?, NOW(), 'active')";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, customerId);
                    stmt.setInt(2, bookId);
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            response.put("message", "Reservation created successfully");
                            response.put("reservationId", rs.getInt(1));
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                response.put("error", "Failed to create reservation");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
        }

        return response;
    }

    public Map<String, Object> cancelReservation(int customerId, int reservationId) {
        Map<String, Object> response = new HashMap<>();

        // Validate customer
        String customerSql = "SELECT customer_id FROM CUSTOMER WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(customerSql)) {
            stmt.setInt(1, customerId);
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

        // Validate reservation and ownership
        String reservationSql = "SELECT reservation_id, status FROM Reservations WHERE reservation_id = ? AND customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(reservationSql)) {
            stmt.setInt(1, reservationId);
            stmt.setInt(2, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    response.put("error", "Reservation not found or not owned by customer");
                    return response;
                }
                String status = rs.getString("status");
                if ("cancelled".equals(status)) {
                    response.put("error", "Reservation is already cancelled");
                    return response;
                }
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
            return response;
        }

        // Cancel reservation
        String updateSql = "UPDATE Reservations SET status = 'cancelled' WHERE reservation_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setInt(1, reservationId);
                    stmt.executeUpdate();
                }
                conn.commit();
                response.put("message", "Reservation cancelled successfully");
                response.put("reservationId", reservationId);
            } catch (SQLException e) {
                conn.rollback();
                response.put("error", "Failed to cancel reservation");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
        }

        return response;
    }
}