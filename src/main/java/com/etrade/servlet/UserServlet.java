package com.etrade.servlet;

import com.etrade.model.User;
import com.etrade.model.UserForm;
import com.etrade.dao.UserDAO;
import com.etrade.service.UserService;
import com.etrade.service.AuthService;
import com.etrade.util.SessionManager;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class UserServlet extends HttpServlet {
    private Connection conn;
    private UserService userService;
    private AuthService authService;

    @Override
    public void init() throws ServletException {
        try {
            // Load PostgreSQL driver
            Class.forName("org.postgresql.Driver");

            // Load DB connection details from environment or use defaults
            String DB_URL = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://db:5432/etrade_db");
            String DB_USER = System.getenv().getOrDefault("DB_USER", "postgres");
            String DB_PASS = System.getenv().getOrDefault("DB_PASSWORD", "postgres");

            // Establish DB connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            // Initialize services
            UserDAO userDAO = new UserDAO(conn);
            userService = new UserService(userDAO);
            authService = new AuthService(userService); // ✅ use modular AuthService

        } catch (Exception e) {
            throw new ServletException("DB Init Error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("register".equals(action)) {
                handleRegister(request, response);
            } else if ("login".equals(action)) {
                handleLogin(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        UserForm form = new UserForm();
        form.setUsername(request.getParameter("username"));
        form.setPassword(request.getParameter("password"));
        form.setEmail(request.getParameter("email"));
        form.setFirstName(request.getParameter("firstName"));
        form.setLastName(request.getParameter("lastName"));

        boolean success = userService.registerUser(form);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
        } else {
            request.setAttribute("error", "Username already taken");
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = authService.authenticate(username, password);
        if (user != null) {
            SessionManager.setCurrentUser(request.getSession(), user);
            response.sendRedirect(request.getContextPath() + "/views/dashboard.jsp");
        } else {
            request.setAttribute("error", "Invalid credentials");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }
}
