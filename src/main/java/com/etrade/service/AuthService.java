package com.etrade.service;

import com.etrade.model.User;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public User authenticate(String username, String password) throws SQLException {
        return userService.loginUser(username, password);
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}
