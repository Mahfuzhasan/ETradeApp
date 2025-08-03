package com.etrade.service;

import com.etrade.dao.ChatDAO;
import com.etrade.model.Chat;
import com.etrade.model.Message;
import com.etrade.model.User;

import java.sql.SQLException;
import java.util.List;

public class ChatService {
    private final ChatDAO chatDAO;

    public ChatService(ChatDAO chatDAO) {
        this.chatDAO = chatDAO;
    }

    public int getOrCreateChat(int user1Id, int user2Id) throws SQLException {
        Integer chatId = chatDAO.findExistingChat(user1Id, user2Id);
        if (chatId != null) return chatId;
        return chatDAO.createChatBetweenUsers(user1Id, user2Id);
    }

    public void sendMessage(int chatId, int userId, String content) throws SQLException {
        chatDAO.insertMessage(chatId, userId, content);
    }

    public List<Message> getMessages(int chatId) throws SQLException {
        return chatDAO.getMessagesByChatId(chatId);
    }

    public List<Chat> getChatsForUser(int userId) throws SQLException {
        return chatDAO.getChatsByUserId(userId);
    }
    public User getUserById(int userId) throws SQLException {
        return chatDAO.getUserById(userId);
    }

    public List<User> getUsersWhoMessaged(int sellerId) throws SQLException {
        return chatDAO.getUsersWhoMessaged(sellerId);
    }

}
