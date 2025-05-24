package service;

import dbConn.DBConnection;
import model.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerService {
    public void registerCustomer(String username, String password, String email,
                                 String firstName, String lastName, String phone, String address) {
        // Tạo đối tượng Customer
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword(password);
        customer.setEmail(email);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setPhone(phone);
        customer.setAddress(address);


        // Lưu Customer vào cơ sở dữ liệu
        String sql = "INSERT INTO CUSTOMER (username, password, email, first_name, last_name, phone, address) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getUsername());
            stmt.setString(2, customer.getPassword());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getFirstName());
            stmt.setString(5, customer.getLastName());
            stmt.setString(6, customer.getPhone());
            stmt.setString(7, customer.getAddress());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register customer: " + e.getMessage(), e);
        }
    }
}
