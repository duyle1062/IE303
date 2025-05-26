package DAO;

import dbConn.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BillDAO {
    public Map<String, Object> updateAllBillTotals() {
        Map<String, Object> response = new HashMap<>();
        int updatedCount = 0;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Fetch all bills with borrowing fees
                String selectSql = "SELECT b.bill_id, b.borrowing_id, br.borrowing_fee, br.overdue_fee " +
                        "FROM BILL b JOIN BORROWING br ON b.borrowing_id = br.borrowing_id";
                try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                     ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        int billId = rs.getInt("bill_id");
                        double borrowingFee = rs.getDouble("borrowing_fee");
                        double overdueFee = rs.getDouble("overdue_fee");
                        double totalAmount = borrowingFee + overdueFee;

                        // Update total_amount
                        String updateSql = "UPDATE BILL SET total_amount = ? WHERE bill_id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setDouble(1, totalAmount);
                            updateStmt.setInt(2, billId);
                            updateStmt.executeUpdate();
                            updatedCount++;
                        }
                    }
                }
                conn.commit();
                response.put("message", "Total amounts updated successfully");
                response.put("updatedCount", updatedCount);
            } catch (SQLException e) {
                conn.rollback();
                response.put("error", "Failed to update total amounts");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            response.put("error", "Database error");
        }

        return response;
    }
}