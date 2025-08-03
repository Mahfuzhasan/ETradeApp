package com.etrade.service;

import com.etrade.dao.UserDAO;
import com.etrade.model.User;
import com.etrade.model.UserForm;
import java.sql.SQLException;

public class UserService {
    private UserDAO userDAO;

    public UserService(UserDAO dao) {
        this.userDAO = dao;
    }

    public boolean registerUser(@org.jetbrains.annotations.NotNull UserForm form) throws SQLException {
        User existing = userDAO.getUserByUsername(form.getUsername());
        if (existing != null) return false;

        User newUser = new User();
        newUser.setUsername(form.getUsername());
        newUser.setPassword(form.getPassword());
        newUser.setEmail(form.getEmail());
        newUser.setFirstName(form.getFirstName());
        newUser.setLastName(form.getLastName());

        userDAO.addUser(newUser);
        return true;
    }


    public User loginUser(String username, String password) throws SQLException {
        User user = userDAO.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
