package com.etrade.dao;

import com.etrade.model.Message;
import com.etrade.model.User;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChatDAOTest {

    private static Connection conn;
    private static ChatDAO chatDAO;
    private static UserDAO userDAO;

    private int user1Id;
    private int user2Id;

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
                "CREATE TABLE chat (" +
                        "chat_id INT AUTO_INCREMENT PRIMARY KEY)"
        );

        conn.createStatement().execute(
                "CREATE TABLE chat_participant (" +
                        "chat_id INT, " +
                        "user_id INT, " +
                        "FOREIGN KEY (chat_id) REFERENCES chat(chat_id), " +
                        "FOREIGN KEY (user_id) REFERENCES \"user\"(user_id))"
        );

        conn.createStatement().execute(
                "CREATE TABLE message (" +
                        "message_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "chat_id INT, " +
                        "sender_id INT, " +
                        "content TEXT, " +
                        "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (chat_id) REFERENCES chat(chat_id), " +
                        "FOREIGN KEY (sender_id) REFERENCES \"user\"(user_id))"
        );

        chatDAO = new ChatDAO(conn);
        userDAO = new UserDAO(conn);
    }

    @BeforeEach
    public void resetTables() throws SQLException {
        conn.createStatement().execute("DELETE FROM message");
        conn.createStatement().execute("DELETE FROM chat_participant");
        conn.createStatement().execute("DELETE FROM chat");
        conn.createStatement().execute("DELETE FROM \"user\"");

        User u1 = new User();
        u1.setUsername("alice");
        u1.setPassword("a");
        u1.setEmail("alice@ex.com");
        u1.setFirstName("Alice");
        u1.setLastName("Liddell");

        User u2 = new User();
        u2.setUsername("bob");
        u2.setPassword("b");
        u2.setEmail("bob@ex.com");
        u2.setFirstName("Bob");
        u2.setLastName("Builder");

        userDAO.addUser(u1);
        userDAO.addUser(u2);

        user1Id = userDAO.getUserByUsername("alice").getUserId();
        user2Id = userDAO.getUserByUsername("bob").getUserId();
    }

    @Test
    public void testCreateAndFetchChat() throws SQLException {
        int chatId = chatDAO.createChatBetweenUsers(user1Id, user2Id);

        assertTrue(chatId > 0);

        chatDAO.insertMessage(chatId, user1Id, "Hello!");
        chatDAO.insertMessage(chatId, user2Id, "Hi!");

        List<Message> messages = chatDAO.getMessagesByChatId(chatId);

        assertEquals(2, messages.size());
        assertEquals("Hello!", messages.get(0).getContent());
    }

    @AfterAll
    public static void close() throws SQLException {
        conn.close();
    }
}
