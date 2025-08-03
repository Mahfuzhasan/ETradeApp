package com.etrade.servlet;

import com.etrade.dao.UserDAO;
import com.etrade.service.UserService;
import com.etrade.service.AuthService;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

public class LogoutServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() throws ServletException {
        try {
            // Load PostgreSQL driver
            Class.forName("org.postgresql.Driver");

            // Load DB connection details
            String DB_URL = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://db:5432/etrade_db");
            String DB_USER = System.getenv().getOrDefault("DB_USER", "postgres");
            String DB_PASS = System.getenv().getOrDefault("DB_PASSWORD", "postgres");

            // Establish DB connection and service layer
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            UserDAO userDAO = new UserDAO(conn);
            UserService userService = new UserService(userDAO);
            authService = new AuthService(userService);

        } catch (Exception e) {
            throw new ServletException("Logout init error", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        authService.logout(request.getSession(false)); // ✅ Modular logout
        response.sendRedirect(request.getContextPath() + "/views/login.jsp");
    }
}
