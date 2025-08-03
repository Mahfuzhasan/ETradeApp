package com.etrade.dao;

import java.sql.*;
import com.etrade.model.User;

public class UserDAO {
    private final Connection conn;

    public UserDAO(Connection conn) throws SQLException {
        this.conn = conn;
        ensureTableExists();
    }

    // Automatically create the "user" table if it doesn't exist
    private void ensureTableExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS \"user\" (" +
                "userid SERIAL PRIMARY KEY, " +
                "username VARCHAR(255) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) NOT NULL, " +
                "first_name VARCHAR(100), " +
                "last_name VARCHAR(100))";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }


    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM \"user\" WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("userid"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                return user;
            }
            return null;
        }
    }


    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO \"user\" (username, password, email, first_name, last_name) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.executeUpdate();
        }
    }

}
