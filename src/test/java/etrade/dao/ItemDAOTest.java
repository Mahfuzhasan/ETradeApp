package com.etrade.dao;

import com.etrade.model.Item;
import com.etrade.model.User;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemDAOTest {

    private static Connection conn;
    private static ItemDAO itemDAO;
    private static UserDAO userDAO;

    private int testUserId;

    @BeforeAll
    public static void setup() throws SQLException {
        conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");

        conn.createStatement().execute(
                "CREATE TABLE \"user\" (" +
                        "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(100), " +
                        "password VARCHAR(100), " +
                        "email VARCHAR(100), " +
                        "first_name VARCHAR(100), " +
                        "last_name VARCHAR(100))"
        );

        conn.createStatement().execute(
                "CREATE TABLE item (" +
                        "item_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "title VARCHAR(255), " +
                        "description TEXT, " +
                        "price DOUBLE, " +
                        "country VARCHAR(100), " +
                        "province VARCHAR(100), " +
                        "city VARCHAR(100), " +
                        "postal_code VARCHAR(20), " +
                        "image_path VARCHAR(255), " +
                        "sold BOOLEAN, " +
                        "user_id INT, " +
                        "FOREIGN KEY (user_id) REFERENCES \"user\"(user_id))"
        );

        userDAO = new UserDAO(conn);
        itemDAO = new ItemDAO(conn);
    }

    @BeforeEach
    public void reset() throws SQLException {
        conn.createStatement().execute("DELETE FROM item");
        conn.createStatement().execute("DELETE FROM \"user\"");

        User user = new User();
        user.setUsername("itemuser");
        user.setPassword("pass");
        user.setEmail("item@example.com");
        user.setFirstName("Item");
        user.setLastName("User");
        userDAO.addUser(user);

        testUserId = userDAO.getUserByUsername("itemuser").getUserId();
    }

    @Test
    public void testAddAndGetItem() throws SQLException {
        Item item = new Item();
        item.setTitle("Laptop");
        item.setDescription("Gaming laptop");
        item.setPrice(999.99);
        item.setCountry("Canada");
        item.setProvince("Ontario");
        item.setCity("Toronto");
        item.setPostalCode("M5G1Z8");
        item.setImagePath("laptop.jpg");
        item.setSold(false);
        item.setUserId(testUserId);

        itemDAO.addItem(item);

        List<Item> items = itemDAO.getAllItems();

        assertFalse(items.isEmpty());
        assertEquals("Laptop", items.get(0).getTitle());
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        conn.close();
    }
}
