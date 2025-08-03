package com.etrade.servlet;

import com.etrade.dao.ChatDAO;
import com.etrade.dao.ItemDAO;
import com.etrade.model.Item;
import com.etrade.model.User;
import com.etrade.service.ChatService;
import com.etrade.service.ItemService;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class ItemServlet extends HttpServlet {
    private Connection conn;
    private ItemService itemService;
    private ChatService chatService;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("org.postgresql.Driver");
            String DB_URL = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://db:5432/etrade_db");
            String DB_USER = System.getenv().getOrDefault("DB_USER", "postgres");
            String DB_PASS = System.getenv().getOrDefault("DB_PASSWORD", "postgres");

            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            itemService = new ItemService(new ItemDAO(conn));
            chatService = new ChatService(new ChatDAO(conn));
        } catch (Exception e) {
            throw new ServletException("ItemServlet DB Init Error", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("edit".equals(action)) {
                int itemId = Integer.parseInt(request.getParameter("id"));
                Item item = itemService.getItemDetails(itemId);
                User user = (User) request.getSession().getAttribute("user");

                if (user == null || item == null || item.getUserId() != user.getUserId()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized access.");
                    return;
                }

                request.setAttribute("item", item);
                request.getRequestDispatcher("/views/item.jsp").forward(request, response);

            } else if ("delete".equals(action)) {
                int itemId = Integer.parseInt(request.getParameter("id"));
                Item item = itemService.getItemDetails(itemId);
                User user = (User) request.getSession().getAttribute("user");

                if (user == null || item == null || item.getUserId() != user.getUserId()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized delete.");
                    return;
                }

                itemService.deleteItem(itemId);
                response.sendRedirect("ItemServlet?action=myitems");

            } else if ("myitems".equals(action)) {
                User user = (User) request.getSession().getAttribute("user");
                if (user == null) {
                    response.sendRedirect("views/login.jsp");
                    return;
                }

                List<Item> myItems = itemService.getItemsByUser(user.getUserId());
                List<User> chatSenders = chatService.getUsersWhoMessaged(user.getUserId());
                request.setAttribute("items", myItems);
                request.setAttribute("chatSenders", chatSenders);
                request.getRequestDispatcher("/views/itemList.jsp").forward(request, response);

            } else if ("markSold".equals(action)) {
                int itemId = Integer.parseInt(request.getParameter("id"));
                Item item = itemService.getItemDetails(itemId);
                User user = (User) request.getSession().getAttribute("user");

                if (user == null || item == null || item.getUserId() != user.getUserId()) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not allowed to mark as sold.");
                    return;
                }

                item.setSold(true);
                itemService.updateItem(item);
                response.sendRedirect("ItemServlet?action=myitems");

            } else if ("search".equals(action)) {
                String keyword = request.getParameter("q");
                List<Item> results = itemService.searchItems(keyword);
                request.setAttribute("items", results);
                request.getRequestDispatcher("/views/itemList.jsp").forward(request, response);

            } else {
                List<Item> itemList = itemService.getAllItems();
                request.setAttribute("items", itemList);
                request.getRequestDispatcher("/views/itemList.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            throw new ServletException("ItemServlet error in GET", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("views/login.jsp");
            return;
        }

        String itemIdStr = request.getParameter("itemId");
        int itemId = (itemIdStr != null && !itemIdStr.isEmpty()) ? Integer.parseInt(itemIdStr) : -1;

        Item item = new Item();
        item.setTitle(request.getParameter("title"));
        item.setDescription(request.getParameter("description"));
        item.setCountry(request.getParameter("country"));
        item.setProvince(request.getParameter("province"));
        item.setCity(request.getParameter("city"));
        item.setPostalCode(request.getParameter("postalCode"));
        item.setSold(false);
        item.setUserId(user.getUserId());

        Part filePart = request.getPart("imageFile");
        String imageFileName = null;

        if (filePart != null && filePart.getSize() > 0) {
            imageFileName = System.currentTimeMillis() + "_" + Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String uploadPath = getServletContext().getRealPath("/") + "images";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            filePart.write(uploadPath + File.separator + imageFileName);
            item.setImagePath("images/" + imageFileName);
        } else if (itemId > 0) {
            Item old = null;
            try {
                old = itemService.getItemDetails(itemId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (old != null) {
                item.setImagePath(old.getImagePath());
            }
        }

        try {
            String priceStr = request.getParameter("price");
            if (priceStr == null || priceStr.trim().isEmpty()) {
                request.setAttribute("error", "Price is required.");
                request.setAttribute("item", item);
                request.getRequestDispatcher("/views/item.jsp").forward(request, response);
                return;
            }
            item.setPrice(Double.parseDouble(priceStr.trim()));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid price format.");
            request.setAttribute("item", item);
            request.getRequestDispatcher("/views/item.jsp").forward(request, response);
            return;
        }

        try {
            if (itemId > 0) {
                Item original = itemService.getItemDetails(itemId);
                if (original == null || original.getUserId() != user.getUserId()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized update.");
                    return;
                }

                item.setItemId(itemId);
                itemService.updateItem(item);
            } else {
                itemService.postItem(item);
            }

            response.sendRedirect("ItemServlet?action=myitems");

        } catch (SQLException e) {
            throw new ServletException("ItemServlet error in POST", e);
        }
    }
}