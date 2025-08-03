package com.etrade.util;



import com.etrade.model.User;

import javax.servlet.http.HttpSession;

public class SessionManager {

    public static boolean isAuthenticated(HttpSession session) {
        return session != null && session.getAttribute("user") != null;
    }

    public static User getCurrentUser(HttpSession session) {
        if (session == null) return null;
        return (User) session.getAttribute("user");
    }

    public static void setCurrentUser(HttpSession session, User user) {
        if (session != null) {
            session.setAttribute("user", user);
        }
    }

    public static void clearSession(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}
