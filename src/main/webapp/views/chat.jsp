<%@ page import="java.util.*, java.text.SimpleDateFormat, com.etrade.model.Message, com.etrade.model.User" %>
<%
    User currentUser = (User) session.getAttribute("user");
    List<Message> messages = (List<Message>) request.getAttribute("messages");
    int recipientId = (Integer) request.getAttribute("recipientId");
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Chat</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/chat.css">
    <script>
        let ws;
        const userId = <%= currentUser.getUserId() %>;
        const recipientId = <%= recipientId %>;

        function connectWebSocket() {
            const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
            const contextPath = '<%= request.getContextPath() %>';
            const url = protocol + "://" + window.location.host + contextPath + "/chat/" + userId;
            ws = new WebSocket(url);

            ws.onmessage = function (event) {
                const chatBox = document.getElementById('chatBox');
                const message = document.createElement('p');
                message.textContent = event.data;
                chatBox.appendChild(message);
                chatBox.scrollTop = chatBox.scrollHeight;
            };

            ws.onclose = () => console.log('WebSocket disconnected');
            ws.onerror = (err) => console.error('WebSocket error:', err);
        }

        function sendMessage(event) {
            event.preventDefault();
            const textarea = document.getElementById('messageInput');
            const content = textarea.value.trim();
            if (!content) return;

            if (ws && ws.readyState === WebSocket.OPEN) {
                ws.send(recipientId + ":" + content);
            }

            const formData = "recipientId=" + recipientId + "&message=" + encodeURIComponent(content);

            fetch('<%= request.getContextPath() %>/ChatServlet', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData
            });

            textarea.value = '';
        }

        window.onload = connectWebSocket;
    </script>
</head>
<body>

<div class="container">

    <!-- ✅ NAVIGATION BAR -->
    <div class="navbar">
        <a href="<%= request.getContextPath() %>/views/dashboard.jsp">Dashboard</a> |
        <a href="javascript:history.back()">Back</a> |
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </div>

    <h2>Chat with <%= request.getAttribute("recipientName") %></h2>

    <div id="chatBox" class="chat-box">
        <% for (Message msg : messages) { %>
            <div class="chat-message <%= msg.getUserId() == currentUser.getUserId() ? "sent" : "received" %>">
                <p><strong><%= msg.getUserId() == currentUser.getUserId() ? "You" : "User " + msg.getUserId() %>:</strong>
                    <%= msg.getContent() %></p>
                <span class="timestamp"><%= sdf.format(msg.getTimestamp()) %></span>
            </div>
        <% } %>
    </div>

    <form onsubmit="sendMessage(event)" class="chat-form">
        <textarea id="messageInput" placeholder="Type your message..." required></textarea>
        <button type="submit">Send</button>
    </form>

</div>
</body>
</html>
