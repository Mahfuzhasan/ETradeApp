package com.etrade.servlet;

import com.etrade.dao.ChatDAO;
import com.etrade.model.Message;
import com.etrade.model.User;
import com.etrade.service.ChatService;
import com.etrade.util.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;


public class ChatServlet extends HttpServlet {
    private ChatService chatService;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("org.postgresql.Driver");
            String DB_URL = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://db:5432/etrade_db");
            String DB_USER = System.getenv().getOrDefault("DB_USER", "postgres");
            String DB_PASS = System.getenv().getOrDefault("DB_PASSWORD", "postgres");

            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            chatService = new ChatService(new ChatDAO(conn));
        } catch (Exception e) {
            throw new ServletException("DB Init Error in ChatServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User currentUser = SessionManager.getCurrentUser(req.getSession());
        String recipientParam = req.getParameter("recipientId");

        if (currentUser == null || recipientParam == null || recipientParam.isEmpty()) {
            resp.sendRedirect("views/login.jsp");
            return;
        }

        int recipientId = Integer.parseInt(recipientParam);

        try {
            int chatId = chatService.getOrCreateChat(currentUser.getUserId(), recipientId);
            List<Message> messages = chatService.getMessages(chatId);
            User recipient = chatService.getUserById(recipientId);

            // Optional: fetch all message senders for dropdown in navbar
            List<User> chatSenders = chatService.getUsersWhoMessaged(currentUser.getUserId());

            req.setAttribute("chatId", chatId);
            req.setAttribute("messages", messages);
            req.setAttribute("recipientId", recipientId);
            req.setAttribute("recipientName", recipient.getUsername());
            req.setAttribute("chatSenders", chatSenders);

            req.getRequestDispatcher("/views/chat.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Error loading chat", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User currentUser = SessionManager.getCurrentUser(req.getSession());
        if (currentUser == null) {
            resp.sendRedirect("views/login.jsp");
            return;
        }

        String recipientParam = req.getParameter("recipientId");
        String messageContent = req.getParameter("message");

        if (recipientParam == null || recipientParam.isEmpty()) {
            resp.sendRedirect("ItemServlet");
            return;
        }

        int recipientId = Integer.parseInt(recipientParam);

        try {
            int chatId = chatService.getOrCreateChat(currentUser.getUserId(), recipientId);

            if (messageContent != null && !messageContent.trim().isEmpty()) {
                chatService.sendMessage(chatId, currentUser.getUserId(), messageContent.trim());
            }

            resp.sendRedirect(req.getContextPath() + "/ChatServlet?recipientId=" + recipientId);
        } catch (SQLException e) {
            throw new ServletException("Failed to send message", e);
        }
    }
}
