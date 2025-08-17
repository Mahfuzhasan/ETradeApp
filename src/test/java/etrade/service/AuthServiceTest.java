package com.etrade.service;

import com.etrade.model.User;
import org.junit.jupiter.api.Test;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Test
    public void testAuthenticateDelegatesToUserService() throws Exception {
        UserService mockUserService = mock(UserService.class);
        AuthService authService = new AuthService(mockUserService);

        User expected = new User();
        when(mockUserService.loginUser("test", "123")).thenReturn(expected);

        User result = authService.authenticate("test", "123");

        assertEquals(expected, result);
    }

    @Test
    public void testLogoutInvalidatesSession() {
        HttpSession session = mock(HttpSession.class);
        AuthService authService = new AuthService(mock(UserService.class));

        authService.logout(session);

        verify(session).invalidate();
    }
}
