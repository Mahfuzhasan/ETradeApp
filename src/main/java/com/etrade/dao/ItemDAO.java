package com.etrade.dao;

import com.etrade.model.Item;

import java.sql.*;
import java.util.*;

public class ItemDAO {
    private final Connection conn;

    public ItemDAO(Connection conn) throws SQLException {
        this.conn = conn;
        ensureTableExists();
    }

    private void ensureTableExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS item (" +
                "itemid SERIAL PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL, " +
                "description TEXT, " +
                "price DOUBLE PRECISION, " +
                "country VARCHAR(100), " +
                "province VARCHAR(100), " +
                "city VARCHAR(100), " +
                "postalCode VARCHAR(20), " +
                "isSold BOOLEAN DEFAULT FALSE, " +
                "imagePath TEXT, " +
                "user_id INTEGER REFERENCES \"user\"(userid))";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public void addItem(Item item) throws SQLException {
        String sql = "INSERT INTO item (title, description, price, country, province, city, postalCode, isSold, imagePath, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getTitle());
            stmt.setString(2, item.getDescription());
            stmt.setDouble(3, item.getPrice());
            stmt.setString(4, item.getCountry());
            stmt.setString(5, item.getProvince());
            stmt.setString(6, item.getCity());
            stmt.setString(7, item.getPostalCode());
            stmt.setBoolean(8, item.isSold());
            stmt.setString(9, item.getImagePath());
            stmt.setInt(10, item.getUserId()); // ✅ Set user ID
            stmt.executeUpdate();
        }
    }

    public List<Item> getAllItems() throws SQLException {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM item ORDER BY itemid DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractItem(rs));
            }
        }
        return list;
    }

    public List<Item> getItemsByUser(int userId) throws SQLException {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM item WHERE user_id = ? ORDER BY itemid DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(extractItem(rs));
            }
        }
        return list;
    }

    public Item getItemById(int id) throws SQLException {
        String sql = "SELECT * FROM item WHERE itemid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractItem(rs);
            }
        }
        return null;
    }

    public void updateItem(Item item) throws SQLException {
        String sql = "UPDATE item SET title = ?, description = ?, price = ?, country = ?, province = ?, city = ?, postalCode = ?, imagePath = ?, isSold = ? WHERE itemid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getTitle());
            stmt.setString(2, item.getDescription());
            stmt.setDouble(3, item.getPrice());
            stmt.setString(4, item.getCountry());
            stmt.setString(5, item.getProvince());
            stmt.setString(6, item.getCity());
            stmt.setString(7, item.getPostalCode());
            stmt.setString(8, item.getImagePath());
            stmt.setBoolean(9, item.isSold());
            stmt.setInt(10, item.getItemId());
            stmt.executeUpdate();
        }
    }

    public void deleteItem(int itemId) throws SQLException {
        String sql = "DELETE FROM item WHERE itemid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            stmt.executeUpdate();
        }
    }

    private Item extractItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setItemId(rs.getInt("itemid"));
        item.setTitle(rs.getString("title"));
        item.setDescription(rs.getString("description"));
        item.setPrice(rs.getDouble("price"));
        item.setCountry(rs.getString("country"));
        item.setProvince(rs.getString("province"));
        item.setCity(rs.getString("city"));
        item.setPostalCode(rs.getString("postalCode"));
        item.setSold(rs.getBoolean("isSold"));
        item.setImagePath(rs.getString("imagePath"));
        item.setUserId(rs.getInt("user_id")); // ✅ Include user ID
        return item;
    }

    public List<Item> searchItems(String keyword) throws SQLException {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM item " +
                "WHERE LOWER(title) LIKE ? OR LOWER(description) LIKE ? OR LOWER(city) LIKE ? " +
                "OR LOWER(country) LIKE ? OR LOWER(province) LIKE ?";

        // Try parsing the keyword as price (double)
        boolean isPrice = false;
        double priceValue = 0.0;
        try {
            priceValue = Double.parseDouble(keyword);
            sql += " OR price = ?";
            isPrice = true;
        } catch (NumberFormatException ignored) {}

        sql += " ORDER BY itemid DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            stmt.setString(5, searchPattern);

            if (isPrice) {
                stmt.setDouble(6, priceValue);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(extractItem(rs));
            }
        }

        return list;
    }


}
