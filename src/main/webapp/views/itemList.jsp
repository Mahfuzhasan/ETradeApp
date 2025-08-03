<%@ page import="java.util.*, com.etrade.model.Item, com.etrade.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    List<Item> items = (List<Item>) request.getAttribute("items");
    List<User> chatSenders = (List<User>) request.getAttribute("chatSenders");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Item Listings</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/itemList.css">
</head>
<body>

<div class="container">

    <!-- ✅ NAVIGATION BAR -->
    <div class="navbar">
        <a href="<%= request.getContextPath() %>/views/dashboard.jsp">Dashboard</a> |
        <a href="javascript:history.back()">Back</a> |
        <a href="<%= request.getContextPath() %>/logout">Logout</a>

        <% if (chatSenders != null && !chatSenders.isEmpty()) { %>
            <select onchange="if(this.value) window.location.href=this.value">
                <option disabled selected> Messages</option>
                <% for (User sender : chatSenders) { %>
                    <option value="<%= request.getContextPath() %>/ChatServlet?recipientId=<%= sender.getUserId() %>">
                        From <%= sender.getUsername() %>
                    </option>
                <% } %>
            </select>
        <% } %>
    </div>

    <h2>Available Items</h2>

    <!-- ✅ Search -->
    <form action="<%= request.getContextPath() %>/ItemServlet" method="get" class="search-bar">
        <input type="hidden" name="action" value="search" />
        <input type="text" name="q" placeholder="Search by title, city, price..." required />
        <input type="submit" value="Search" />
    </form>

    <% if (items != null && !items.isEmpty()) { %>
        <div class="item-list">
            <% for (Item item : items) {
                boolean isOwner = (user != null && user.getUserId() == item.getUserId());
            %>
                <div class="item">
                    <h3><%= item.getTitle() %> - $<%= item.getPrice() %></h3>
                    <p><%= item.getDescription() %></p>
                    <p><strong>Location:</strong> <%= item.getCity() %>, <%= item.getProvince() %>, <%= item.getCountry() %></p>

                    <% if (item.getImagePath() != null && !item.getImagePath().isEmpty()) { %>
                        <img src="<%= request.getContextPath() + "/" + item.getImagePath() %>" class="item-img" />
                    <% } %>

                    <div class="actions">
                        <% if (isOwner) { %>
                            <form action="<%=request.getContextPath()%>/ItemServlet" method="get">
                                <input type="hidden" name="action" value="edit" />
                                <input type="hidden" name="id" value="<%= item.getItemId() %>" />
                                <input type="submit" value="Edit" />
                            </form>

                            <form action="<%=request.getContextPath()%>/ItemServlet" method="get" onsubmit="return confirm('Delete this item?');">
                                <input type="hidden" name="action" value="delete" />
                                <input type="hidden" name="id" value="<%= item.getItemId() %>" />
                                <input type="submit" value="🗑 Delete" />
                            </form>

                            <% if (!item.isSold()) { %>
                                <form action="<%=request.getContextPath()%>/ItemServlet" method="get">
                                    <input type="hidden" name="action" value="markSold" />
                                    <input type="hidden" name="id" value="<%= item.getItemId() %>" />
                                    <input type="submit" value=" Mark as Sold" />
                                </form>
                            <% } else { %>
                                <span class="sold-label">[SOLD]</span>
                            <% } %>

                        <% } else if (item.isSold()) { %>
                            <span class="sold-label">[SOLD]</span>
                        <% } else { %>
                            <form action="<%=request.getContextPath()%>/ChatServlet" method="post">
                                <input type="hidden" name="recipientId" value="<%= item.getUserId() %>" />
                                <input type="hidden" name="itemId" value="<%= item.getItemId() %>" />
                                <input type="submit" value=" Chat with Seller" />
                            </form>
                        <% } %>
                    </div>
                </div>
            <% } %>
        </div>
    <% } else { %>
        <p>No items listed yet.</p>
    <% } %>
</div>

</body>
</html>
