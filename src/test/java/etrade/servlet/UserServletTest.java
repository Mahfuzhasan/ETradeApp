package com.etrade.servlet;

import com.etrade.model.User;
import com.etrade.model.UserForm;
import com.etrade.service.AuthService;
import com.etrade.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServletTest {

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private RequestDispatcher dispatcher;

    @Mock private UserService userService;
    @Mock private AuthService authService;

    @InjectMocks private UserServlet servlet;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new UserServlet();

        // Inject mocks manually because init() creates new instances
        java.lang.reflect.Field userField = servlet.getClass().getDeclaredField("userService");
        userField.setAccessible(true);
        userField.set(servlet, userService);

        java.lang.reflect.Field authField = servlet.getClass().getDeclaredField("authService");
        authField.setAccessible(true);
        authField.set(servlet, authService);
    }

    @Test
    public void testHandleLoginSuccess() throws Exception {
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("username")).thenReturn("alice");
        when(request.getParameter("password")).thenReturn("pass123");
        when(request.getSession()).thenReturn(session);

        User user = new User();
        user.setUserId(1);
        user.setUsername("alice");

        when(authService.authenticate("alice", "pass123")).thenReturn(user);

        servlet.doPost(request, response);

        verify(session).setAttribute("user", user);
        verify(response).sendRedirect(contains("/views/dashboard.jsp"));
    }

    @Test
    public void testHandleLoginFailure() throws Exception {
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("username")).thenReturn("alice");
        when(request.getParameter("password")).thenReturn("wrong");
        when(request.getRequestDispatcher("/views/login.jsp")).thenReturn(dispatcher);

        when(authService.authenticate("alice", "wrong")).thenReturn(null);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("error"), eq("Invalid credentials"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testHandleRegisterSuccess() throws Exception {
        when(request.getParameter("action")).thenReturn("register");
        when(request.getParameter("username")).thenReturn("bob");
        when(request.getParameter("password")).thenReturn("secret");
        when(request.getParameter("email")).thenReturn("bob@ex.com");
        when(request.getParameter("firstName")).thenReturn("Bob");
        when(request.getParameter("lastName")).thenReturn("Builder");

        when(userService.registerUser(any(UserForm.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(response).sendRedirect(contains("/views/login.jsp"));
    }

    @Test
    public void testHandleRegisterFail_UsernameExists() throws Exception {
        when(request.getParameter("action")).thenReturn("register");
        when(request.getParameter("username")).thenReturn("bob");
        when(request.getParameter("password")).thenReturn("secret");
        when(request.getParameter("email")).thenReturn("bob@ex.com");
        when(request.getParameter("firstName")).thenReturn("Bob");
        when(request.getParameter("lastName")).thenReturn("Builder");

        when(userService.registerUser(any(UserForm.class))).thenReturn(false);
        when(request.getRequestDispatcher("/views/register.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("error"), eq("Username already taken"));
        verify(dispatcher).forward(request, response);
    }
}
