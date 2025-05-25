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
    public Map<String, Object> createReservation(int custId, String bookName) {
        Map<String, Object> response = new HashMap<>();

        // Validate customer
        String custSql = "SELECT customer_id FROM Customer WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(custSql)) {
            stmt.setInt(1, custId);
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

        // Find book and check availability
        String bookSql = "SELECT book_id, copies_available FROM Books WHERE title = ?";
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

        if (copiesAvailable <= 0) {
            response.put("error", "No copies available");
            return response;
        }

        // Create reservation and update copies
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insert reservation
                String reservationSql = "INSERT INTO Reservations (customer_id, book_id, reservation_date, status) VALUES (?, ?, ?, ?)";
                int reservationId;
                try (PreparedStatement stmt = conn.prepareStatement(reservationSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, custId);
                    stmt.setInt(2, bookId);
                    stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    stmt.setString(4, "active");
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        rs.next();
                        reservationId = rs.getInt(1);
                    }
                }

                // Update copies_available
                String updateSql = "UPDATE Books SET copies_available = copies_available - 1 WHERE book_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setInt(1, bookId);
                    stmt.executeUpdate();
                }

                conn.commit();
                response.put("message", "Reservation successful");
                response.put("reservationId", reservationId);
            } catch (SQLException e) {
                conn.rollback();
                response.put("error", "Reservation failed");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
        }

        return response;
    }

    public Map<String, String> cancelReservation(int customerId, int reservationId) {
        Map<String, String> response = new HashMap<>();

        // Validate customer
        String customerSql = "SELECT customer_id FROM Customer WHERE customer_id = ?";
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

        // Check reservation and status
        String reservationSql = "SELECT book_id, status FROM Reservations WHERE reservation_id = ? AND customer_id = ?";
        int bookId = -1;
        String status = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(reservationSql)) {
            stmt.setInt(1, reservationId);
            stmt.setInt(2, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    bookId = rs.getInt("book_id");
                    status = rs.getString("status");
                } else {
                    response.put("error", "Reservation not found or not owned by customer");
                    return response;
                }
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
            return response;
        }

        if (!"active".equals(status)) {
            response.put("error", "Reservation already cancelled or not active");
            return response;
        }

        // Cancel reservation and update copies
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Update reservation status
                String updateReservationSql = "UPDATE Reservations SET status = 'cancelled' WHERE reservation_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateReservationSql)) {
                    stmt.setInt(1, reservationId);
                    stmt.executeUpdate();
                }

                // Increment copies_available
                String updateBookSql = "UPDATE Books SET copies_available = copies_available + 1 WHERE book_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateBookSql)) {
                    stmt.setInt(1, bookId);
                    stmt.executeUpdate();
                }

                conn.commit();
                response.put("message", "Reservation cancelled");
            } catch (SQLException e) {
                conn.rollback();
                response.put("error", "Cancellation failed");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
        }

        return response;
    }
}