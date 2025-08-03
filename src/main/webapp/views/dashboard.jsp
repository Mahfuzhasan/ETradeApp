<%@ page import="com.etrade.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/views/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/dashboard.css" />
</head>
<body>

<div class="container">

    <div class="navbar">
        <a href="<%= request.getContextPath() %>/views/dashboard.jsp">Dashboard</a> |
        <a href="javascript:history.back()">Back</a> |
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </div>

    <h2>Welcome, <%= user.getFirstName() %> <%= user.getLastName() %>!</h2>

    <ul class="dashboard-links">
        <li><a href="<%= request.getContextPath() %>/views/item.jsp"> Post a New Item</a></li>
        <li><a href="<%= request.getContextPath() %>/ItemServlet?action=myitems">My Listings and Messages</a></li>
        <li><a href="<%= request.getContextPath() %>/ItemServlet?action=list">View All Items</a></li>
    </ul>

</div>

</body>
</html>
