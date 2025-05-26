package DAO;

import dbConn.DBConnection;
import model.CustomerModel;
import model.AdminModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UpdateProfileDAO {


    public boolean updateCustomerProfile(int customerId, CustomerModel customer) throws SQLException {
        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        // Kiểm tra và thêm các trường không null vào câu lệnh SQL
        if (customer.getEmail() != null) {
            fields.add("email = ?");
            values.add(customer.getEmail());
        }
        if (customer.getFirstName() != null) {
            fields.add("first_name = ?");
            values.add(customer.getFirstName());
        }
        if (customer.getLastName() != null) {
            fields.add("last_name = ?");
            values.add(customer.getLastName());
        }
        if (customer.getPhone() != null) {
            fields.add("phone = ?");
            values.add(customer.getPhone());
        }
        if (customer.getAddress() != null) {
            fields.add("address = ?");
            values.add(customer.getAddress());
        }

        // Nếu không có trường nào để cập nhật, trả về false
        if (fields.isEmpty()) {
            return false;
        }

        // Xây dựng câu lệnh SQL
        String sql = "UPDATE CUSTOMER SET " + String.join(", ", fields) + " WHERE customer_id = ?";
        values.add(customerId);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Gán giá trị cho các tham số
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }

            // Thực thi cập nhật
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }


    public boolean updateAdminProfile(int adminId, AdminModel admin) throws SQLException {
        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        // Kiểm tra và thêm các trường không null vào câu lệnh SQL
        if (admin.getEmail() != null) {
            fields.add("email = ?");
            values.add(admin.getEmail());
        }
        if (admin.getFirstName() != null) {
            fields.add("first_name = ?");
            values.add(admin.getFirstName());
        }
        if (admin.getLastName() != null) {
            fields.add("last_name = ?");
            values.add(admin.getLastName());
        }
        if (admin.getPhone() != null) {
            fields.add("phone = ?");
            values.add(admin.getPhone());
        }
        if (admin.getAddress() != null) {
            fields.add("address = ?");
            values.add(admin.getAddress());
        }
        if (admin.getDepartment() != null) {
            fields.add("department = ?");
            values.add(admin.getDepartment());
        }

        // Nếu không có trường nào để cập nhật, trả về false
        if (fields.isEmpty()) {
            return false;
        }

        // Xây dựng câu lệnh SQL
        String sql = "UPDATE ADMIN SET " + String.join(", ", fields) + " WHERE admin_id = ?";
        values.add(adminId);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Gán giá trị cho các tham số
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }

            // Thực thi cập nhật
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}