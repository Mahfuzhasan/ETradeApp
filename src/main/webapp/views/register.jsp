<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/register.css">
</head>
<body>

<div class="register-container">
    <form action="${pageContext.request.contextPath}/UserServlet" method="post">
        <h2>Register</h2>
        <input type="hidden" name="action" value="register" />

        <label>Username:</label>
        <input type="text" name="username" required />

        <label>Password:</label>
        <input type="password" name="password" required />

        <label>Email:</label>
        <input type="email" name="email" required />

        <label>First Name:</label>
        <input type="text" name="firstName" required />

        <label>Last Name:</label>
        <input type="text" name="lastName" required />

        <input type="submit" value="Register" />

        <% String error = (String) request.getAttribute("error");
           if (error != null) { %>
            <p class="error"><%= error %></p>
        <% } %>
    </form>

    <p class="login-link">Already have an account?
        <a href="${pageContext.request.contextPath}/views/login.jsp">Login here</a>
    </p>
</div>

</body>
</html>
