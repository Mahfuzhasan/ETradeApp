package com.etrade.service;

import com.etrade.dao.UserDAO;
import com.etrade.model.User;
import com.etrade.model.UserForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserDAO userDAO;
    private UserService userService;

    @BeforeEach
    public void setup() {
        userDAO = mock(UserDAO.class);
        userService = new UserService(userDAO);
    }

    @Test
    public void testRegisterUserSuccess() throws SQLException {
        UserForm form = new UserForm();
        form.setUsername("newuser");
        form.setPassword("pass123");
        form.setEmail("new@example.com");
        form.setFirstName("New");
        form.setLastName("User");

        when(userDAO.getUserByUsername("newuser")).thenReturn(null);

        boolean result = userService.registerUser(form);

        assertTrue(result);
        verify(userDAO).addUser(any(User.class));
    }

    @Test
    public void testRegisterUserFailsIfExists() throws SQLException {
        UserForm form = new UserForm();
        form.setUsername("existing");
        form.setPassword("pass");
        form.setEmail("e@e.com");
        form.setFirstName("A");
        form.setLastName("B");

        when(userDAO.getUserByUsername("existing")).thenReturn(new User());

        boolean result = userService.registerUser(form);

        assertFalse(result);
        verify(userDAO, never()).addUser(any(User.class));
    }

    @Test
    public void testLoginUserSuccess() throws SQLException {
        User user = new User();
        user.setUsername("test");
        user.setPassword("pass");

        when(userDAO.getUserByUsername("test")).thenReturn(user);

        User result = userService.loginUser("test", "pass");

        assertNotNull(result);
    }

    @Test
    public void testLoginFailsWrongPassword() throws SQLException {
        User user = new User();
        user.setUsername("test");
        user.setPassword("correct");

        when(userDAO.getUserByUsername("test")).thenReturn(user);

        User result = userService.loginUser("test", "wrong");

        assertNull(result);
    }
}
