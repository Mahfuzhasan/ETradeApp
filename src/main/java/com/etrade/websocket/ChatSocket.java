package com.etrade.websocket;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chat/{userId}")
public class ChatSocket {
    private static final ConcurrentHashMap<Integer, Session> userSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") int userId) {
        userSessions.put(userId, session);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("userId") int senderId) {
        try {
            String[] parts = message.split(":", 2);
            int recipientId = Integer.parseInt(parts[0]);
            String content = parts[1];

            Session recipientSession = userSessions.get(recipientId);
            if (recipientSession != null && recipientSession.isOpen()) {
                recipientSession.getBasicRemote().sendText("User " + senderId + ": " + content);
            }

            Session senderSession = userSessions.get(senderId);
            if (senderSession != null && senderSession.isOpen()) {
                senderSession.getBasicRemote().sendText("You: " + content);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(@PathParam("userId") int userId) {
        userSessions.remove(userId);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }
}
