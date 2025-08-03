package com.etrade.model;

import java.sql.Timestamp;

public class Message {
    private int messageId;
    private int chatId;
    private int userId;
    private String content;
    private Timestamp timestamp;

    public Message() {}

    public Message(int chatId, int userId, String content, Timestamp timestamp) {
        this.chatId = chatId;
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getMessageId() { return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }

    public int getChatId() { return chatId; }
    public void setChatId(int chatId) { this.chatId = chatId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}