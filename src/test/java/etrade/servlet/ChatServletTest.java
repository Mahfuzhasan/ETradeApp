package com.etrade.servlet;

import com.etrade.model.Message;
import com.etrade.model.User;
import com.etrade.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.*;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class ChatServletTest {

    private ChatServlet servlet;
    private ChatService chatService;

    @BeforeEach
    public void setup() {
        servlet = new ChatServlet();
        chatService = mock(ChatService.class);

        // Inject mock chatService using reflection
        try {
            java.lang.reflect.Field field = ChatServlet.class.getDeclaredField("chatService");
            field.setAccessible(true);
            field.set(servlet, chatService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject chatService", e);
        }
    }

    @Test
    public void testDoGet_NoUserRedirectsToLogin() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(req.getParameter("recipientId")).thenReturn("2");

        servlet.doGet(req, res);
        verify(res).sendRedirect("views/login.jsp");
    }

    @Test
    public void testDoGet_WithUserAndValidRecipient() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        User currentUser = new User();
        currentUser.setUserId(1);
        User recipient = new User();
        recipient.setUsername("bob");

        List<Message> messages = Collections.emptyList();
        List<User> senders = Collections.singletonList(recipient);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(currentUser);
        when(req.getParameter("recipientId")).thenReturn("2");
        when(chatService.getOrCreateChat(1, 2)).thenReturn(123);
        when(chatService.getMessages(123)).thenReturn(messages);
        when(chatService.getUserById(2)).thenReturn(recipient);
        when(chatService.getUsersWhoMessaged(1)).thenReturn(senders);
        when(req.getRequestDispatcher("/views/chat.jsp")).thenReturn(dispatcher);

        servlet.doGet(req, res);

        verify(req).setAttribute("chatId", 123);
        verify(req).setAttribute("messages", messages);
        verify(req).setAttribute("recipientId", 2);
        verify(req).setAttribute("recipientName", "bob");
        verify(req).setAttribute("chatSenders", senders);
        verify(dispatcher).forward(req, res);
    }

    @Test
    public void testDoPost_UnauthenticatedRedirectsToLogin() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);

        servlet.doPost(req, res);
        verify(res).sendRedirect("views/login.jsp");
    }

    @Test
    public void testDoPost_SendsMessageAndRedirects() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        User user = new User();
        user.setUserId(1);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(req.getParameter("recipientId")).thenReturn("2");
        when(req.getParameter("message")).thenReturn("Hi there!");
        when(chatService.getOrCreateChat(1, 2)).thenReturn(10);
        when(req.getContextPath()).thenReturn("");

        servlet.doPost(req, res);

        verify(chatService).sendMessage(10, 1, "Hi there!");
        verify(res).sendRedirect("/ChatServlet?recipientId=2");
    }

    @Test
    public void testDoPost_BlankMessageSkipsSend() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        User user = new User();
        user.setUserId(1);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(req.getParameter("recipientId")).thenReturn("2");
        when(req.getParameter("message")).thenReturn("   ");
        when(chatService.getOrCreateChat(1, 2)).thenReturn(15);
        when(req.getContextPath()).thenReturn("");

        servlet.doPost(req, res);

        verify(chatService, never()).sendMessage(anyInt(), anyInt(), anyString());
        verify(res).sendRedirect("/ChatServlet?recipientId=2");
    }
}
