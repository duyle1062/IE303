package service;

import dbConn.DBConnection;
import model.CustomerModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerService {
    public void registerCustomer(String username, String password, String email,
                                 String firstName, String lastName, String phone, String address) {
        // Tạo đối tượng Customer
        CustomerModel customerModel = new CustomerModel();
        customerModel.setUsername(username);
        customerModel.setPassword(password);
        customerModel.setEmail(email);
        customerModel.setFirstName(firstName);
        customerModel.setLastName(lastName);
        customerModel.setPhone(phone);
        customerModel.setAddress(address);


        // Lưu Customer vào cơ sở dữ liệu
        String sql = "INSERT INTO CUSTOMER (username, password, email, first_name, last_name, phone, address) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerModel.getUsername());
            stmt.setString(2, customerModel.getPassword());
            stmt.setString(3, customerModel.getEmail());
            stmt.setString(4, customerModel.getFirstName());
            stmt.setString(5, customerModel.getLastName());
            stmt.setString(6, customerModel.getPhone());
            stmt.setString(7, customerModel.getAddress());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register customer: " + e.getMessage(), e);
        }
    }

    public List<CustomerModel> getAllCustomers() {
        List<CustomerModel> customers = new ArrayList<>();
        String sql = "SELECT * FROM CUSTOMER";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                CustomerModel customer = new CustomerModel();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setUsername(rs.getString("username"));
                customer.setPassword(rs.getString("password"));
                customer.setEmail(rs.getString("email"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddress(rs.getString("address"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve customers: " + e.getMessage(), e);
        }
        return customers;
    }
}
