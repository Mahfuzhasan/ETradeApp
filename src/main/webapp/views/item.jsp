<%@ page import="com.etrade.model.User, com.etrade.model.Item" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    Item item = (Item) request.getAttribute("item");
    boolean editing = item != null;
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <title><%= editing ? "Edit Item" : "Post Item" %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/item.css" />
</head>
<body>

<div class="container">

    <div class="navbar">
        <a href="<%= request.getContextPath() %>/views/dashboard.jsp">Dashboard</a> |
        <a href="javascript:history.back()">Back</a> |
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </div>

    <h2><%= editing ? "Edit Item" : "Post a New Item" %></h2>

    <% if (error != null) { %>
        <p class="error"><%= error %></p>
    <% } %>

    <form action="<%=request.getContextPath()%>/ItemServlet" method="post" enctype="multipart/form-data">
        <% if (editing) { %>
            <input type="hidden" name="itemId" value="<%= item.getItemId() %>" />
        <% } %>

        <label>Title:</label>
        <input type="text" name="title" value="<%= editing ? item.getTitle() : "" %>" required />

        <label>Description:</label>
        <textarea name="description" required><%= editing ? item.getDescription() : "" %></textarea>

        <label>Price:</label>
        <input type="text" name="price" value="<%= editing ? item.getPrice() : "" %>" required />

        <label>Country:</label>
        <input type="text" name="country" value="<%= editing ? item.getCountry() : "" %>" />

        <label>Province:</label>
        <input type="text" name="province" value="<%= editing ? item.getProvince() : "" %>" />

        <label>City:</label>
        <input type="text" name="city" value="<%= editing ? item.getCity() : "" %>" />

        <label>Postal Code:</label>
        <input type="text" name="postalCode" value="<%= editing ? item.getPostalCode() : "" %>" />

        <label>Image:</label>
        <input type="file" name="imageFile" accept="image/*" />

        <% if (editing && item.getImagePath() != null && !item.getImagePath().isEmpty()) { %>
            <img src="<%= request.getContextPath() + "/" + item.getImagePath() %>" alt="Item Image" class="preview-img" />
        <% } %>

        <input type="submit" value="<%= editing ? "Update Item" : "Post Item" %>" />
    </form>

</div>

</body>
</html>
