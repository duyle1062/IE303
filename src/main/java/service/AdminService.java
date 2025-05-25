package service;

import dbConn.DBConnection;
import model.AdminModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminService {
    public List<AdminModel> getAllAdmins() {
        List<AdminModel> admins = new ArrayList<>();
        String sql = "SELECT * FROM ADMIN";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                AdminModel admin = new AdminModel();
                admin.setAdminId(rs.getInt("admin_id"));
                admin.setUsername(rs.getString("username"));
                admin.setPassword(rs.getString("password"));
                admin.setEmail(rs.getString("email"));
                admin.setFirstName(rs.getString("first_name"));
                admin.setLastName(rs.getString("last_name"));
                admin.setAddress(rs.getString("address"));
                admin.setPhone(rs.getString("Phone"));
                admin.setHireDate(rs.getDate("hire_date"));
                admin.setDepartment(rs.getString("department"));
                admins.add(admin);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve admins: " + e.getMessage(), e);
        }
        return admins;
    }
}
