package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnect {

    private static final String URL = "jdbc:postgresql://localhost:5432/Labline";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password"; // <-- update if needed

    // ✅ This is the static method you're missing
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Connected to PostgreSQL database!");
        } catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
        }
    }
}