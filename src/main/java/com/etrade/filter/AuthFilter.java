package com.etrade.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        HttpSession session = req.getSession(); // ✅ Always get session

        boolean loggedIn = (session != null && session.getAttribute("user") != null);
        boolean isLoginOrRegister = uri.endsWith("login.jsp") || uri.endsWith("register.jsp")
                || uri.contains("/login") || uri.contains("/register") || uri.contains("/UserServlet");
        boolean isStaticAsset = uri.contains("/css/") || uri.contains("/js/") || uri.contains("/images/");

        // ✅ Logging for debugging
        System.out.println("🔍 FILTER URI: " + uri);
        System.out.println("🔍 Logged In: " + loggedIn);

        if (loggedIn || isLoginOrRegister || isStaticAsset) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(req.getContextPath() + "/views/login.jsp");
        }
    }
}
