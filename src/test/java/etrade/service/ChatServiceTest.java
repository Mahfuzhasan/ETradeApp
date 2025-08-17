package com.etrade.service;

import com.etrade.dao.ChatDAO;
import com.etrade.model.Message;
import com.etrade.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChatServiceTest {

    private ChatDAO chatDAO;
    private ChatService chatService;

    @BeforeEach
    public void setup() {
        chatDAO = mock(ChatDAO.class);
        chatService = new ChatService(chatDAO);
    }

    @Test
    public void testSendMessage() throws SQLException {
        doNothing().when(chatDAO).insertMessage(1, 2, "Hi!");
        chatService.sendMessage(1, 2, "Hi!");
        verify(chatDAO).insertMessage(1, 2, "Hi!");
    }

    @Test
    public void testGetMessagesReturnsList() throws SQLException {
        when(chatDAO.getMessagesByChatId(1)).thenReturn(Collections.singletonList(new Message()));
        List<Message> messages = chatService.getMessages(1);
        assertEquals(1, messages.size());
    }

    @Test
    public void testGetUserByIdReturnsUser() throws SQLException {
        User user = new User();
        when(chatDAO.getUserById(99)).thenReturn(user);
        assertEquals(user, chatService.getUserById(99));
    }
}
