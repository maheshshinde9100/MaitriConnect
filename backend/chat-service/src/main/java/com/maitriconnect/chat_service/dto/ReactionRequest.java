package com.maitriconnect.chat_service.dto;

public class ReactionRequest {
    private String messageId;
    private String userId;
    private String username;
    private String emoji;
    private String action; // "add" or "remove"

    public ReactionRequest() {}

    public ReactionRequest(String messageId, String userId, String username, String emoji, String action) {
        this.messageId = messageId;
        this.userId = userId;
        this.username = username;
        this.emoji = emoji;
        this.action = action;
    }

    // Getters and Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
