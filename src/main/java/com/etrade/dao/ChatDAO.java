package com.etrade.dao;

import com.etrade.model.Chat;
import com.etrade.model.Message;
import com.etrade.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO {
    private final Connection conn;

    public ChatDAO(Connection conn) throws SQLException {
        this.conn = conn;
        ensureTablesExist();
    }

    private void ensureTablesExist() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS chat (" +
                    "chatid SERIAL PRIMARY KEY, " +
                    "chatcontent TEXT)");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS message (" +
                    "messageid SERIAL PRIMARY KEY, " +
                    "chatid INTEGER REFERENCES chat(chatid), " +
                    "userid INTEGER REFERENCES \"user\"(userid), " +
                    "content TEXT, " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS chat_participant (" +
                    "chatid INTEGER REFERENCES chat(chatid), " +
                    "userid INTEGER REFERENCES \"user\"(userid), " +
                    "PRIMARY KEY (chatid, userid))");
        }
    }

    public Integer findExistingChat(int user1, int user2) throws SQLException {
        String sql = "SELECT c.chatid FROM chat c " +
                "JOIN chat_participant cp1 ON c.chatid = cp1.chatid " +
                "JOIN chat_participant cp2 ON c.chatid = cp2.chatid " +
                "WHERE cp1.userid = ? AND cp2.userid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user1);
            stmt.setInt(2, user2);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("chatid");
        }
        return null;
    }

    public int createChatBetweenUsers(int user1Id, int user2Id) throws SQLException {
        String sql = "INSERT INTO chat (chatcontent) VALUES ('') RETURNING chatid";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int newChatId = rs.getInt("chatid");
                addChatParticipant(newChatId, user1Id);
                addChatParticipant(newChatId, user2Id);
                return newChatId;
            }
        }
        throw new SQLException("Failed to create chat between users");
    }

    private void addChatParticipant(int chatId, int userId) throws SQLException {
        String sql = "INSERT INTO chat_participant (chatid, userid) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chatId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public void insertMessage(int chatId, int userId, String content) throws SQLException {
        String sql = "INSERT INTO message (chatid, userid, content) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chatId);
            stmt.setInt(2, userId);
            stmt.setString(3, content);
            stmt.executeUpdate();
        }
    }

    public List<Message> getMessagesByChatId(int chatId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE chatid = ? ORDER BY timestamp ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chatId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message msg = new Message();
                msg.setMessageId(rs.getInt("messageid"));
                msg.setChatId(rs.getInt("chatid"));
                msg.setUserId(rs.getInt("userid"));
                msg.setContent(rs.getString("content"));
                msg.setTimestamp(rs.getTimestamp("timestamp"));
                messages.add(msg);
            }
        }
        return messages;
    }

    public List<Chat> getChatsByUserId(int userId) throws SQLException {
        List<Chat> chats = new ArrayList<>();
        String sql = "SELECT c.chatid, c.chatcontent FROM chat c " +
                "JOIN chat_participant cp ON c.chatid = cp.chatid WHERE cp.userid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Chat chat = new Chat();
                chat.setChatId(rs.getInt("chatid"));
                chat.setChatContent(rs.getString("chatcontent"));
                chats.add(chat);
            }
        }
        return chats;
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT userid, username, email FROM \"user\" WHERE userid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("userid"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                return user;
            } else {
                throw new SQLException("User not found");
            }
        }
    }

    public List<User> getUsersWhoMessaged(int sellerId) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT DISTINCT u.userid, u.username, u.first_name, u.last_name, u.email " +
                "FROM message m " +
                "JOIN chat_participant cp ON m.chatid = cp.chatid " +
                "JOIN \"user\" u ON m.userid = u.userid " +
                "WHERE cp.userid = ? AND m.userid <> ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sellerId);
            stmt.setInt(2, sellerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("userid"));
                user.setUsername(rs.getString("username"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                users.add(user);
            }
        }
        return users;
    }
}
