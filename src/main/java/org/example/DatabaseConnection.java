package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        }

        String url = "jdbc:postgresql://localhost:5432/rose_db";
        String user = "postgres";
        String password = "password";

        return DriverManager.getConnection(url, user, password);
    }
}