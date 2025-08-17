package com.etrade.dao;

import com.etrade.model.User;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    private static Connection conn;
    private static UserDAO userDAO;

    @BeforeAll
    public static void setup() throws SQLException {
        conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        conn.createStatement().execute(
                "CREATE TABLE \"user\" (" +
                        "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(100) UNIQUE, " +
                        "password VARCHAR(100), " +
                        "email VARCHAR(100), " +
                        "first_name VARCHAR(100), " +
                        "last_name VARCHAR(100))"
        );
        userDAO = new UserDAO(conn);
    }

    @BeforeEach
    public void cleanUp() throws SQLException {
        conn.createStatement().execute("DELETE FROM \"user\"");
    }

    @Test
    public void testAddAndGetUser() throws SQLException {
        User user = new User();
        user.setUsername("john");
        user.setPassword("secret");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        userDAO.addUser(user);

        User fetched = userDAO.getUserByUsername("john");

        assertNotNull(fetched);
        assertEquals("john", fetched.getUsername());
        assertEquals("Doe", fetched.getLastName());
    }

    @AfterAll
    public static void close() throws SQLException {
        conn.close();
    }
}
