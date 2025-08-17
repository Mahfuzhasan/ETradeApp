package com.etrade.servlet;

import com.etrade.model.Item;
import com.etrade.model.User;
import com.etrade.service.ItemService;
import com.etrade.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.*;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class ItemServletTest {

    private ItemServlet servlet;
    private ItemService itemService;
    private ChatService chatService;

    @BeforeEach
    public void setup() throws Exception {
        servlet = new ItemServlet();
        itemService = mock(ItemService.class);
        chatService = mock(ChatService.class);

        java.lang.reflect.Field itemField = ItemServlet.class.getDeclaredField("itemService");
        itemField.setAccessible(true);
        itemField.set(servlet, itemService);

        java.lang.reflect.Field chatField = ItemServlet.class.getDeclaredField("chatService");
        chatField.setAccessible(true);
        chatField.set(servlet, chatService);
    }

    @Test
    public void testMyItemsWithUser() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        User user = new User();
        user.setUserId(1);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("user"))
                .thenReturn(user);
        when(itemService.getItemsByUser(1)).thenReturn(Collections.emptyList());
        when(chatService.getUsersWhoMessaged(1)).thenReturn(Collections.emptyList());
        when(req.getParameter("action")).thenReturn("myitems");
        when(req.getRequestDispatcher("/views/itemList.jsp")).thenReturn(dispatcher);

        servlet.doGet(req, res);

        verify(req).setAttribute(eq("items"), any());
        verify(req).setAttribute(eq("chatSenders"), any());
        verify(dispatcher).forward(req, res);
    }

    @Test
    public void testPostWithInvalidPrice() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        User user = new User();
        user.setUserId(1);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("user"))
                .thenReturn(user);
        when(req.getParameter("price")).thenReturn("");
        when(req.getParameter("itemId")).thenReturn(null);
        when(req.getRequestDispatcher("/views/item.jsp")).thenReturn(dispatcher);

        servlet.doPost(req, res);

        verify(req).setAttribute(eq("error"), eq("Price is required."));
        verify(dispatcher).forward(req, res);
    }

    @Test
    public void testDoGetRequiresLogin() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("user"))
                .thenReturn(null);
        when(req.getParameter("action")).thenReturn("myitems");

        servlet.doGet(req, res);

        verify(res).sendRedirect("views/login.jsp");
    }
}