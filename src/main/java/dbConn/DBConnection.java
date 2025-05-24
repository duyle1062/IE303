package dbConn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/IE303?useSSL=false&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Register MySQL driver
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
       String url = "jdbc:mysql://localhost:3306/IE303?useSSL=false&characterEncoding=UTF-8";
        String user = "root";
        String password = "";
       String sql = "select * from BOOKS;";
        try(Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println(conn.getCatalog());
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            showInfo(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        };

    }

    private static void showInfo(ResultSet resultSet) {
        try {
           while (resultSet.next()) {
               System.out.println(resultSet.getInt(1) + " - " + resultSet.getString(2)
                        + " - " + resultSet.getString(3) + " - " + resultSet.getString(4)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
