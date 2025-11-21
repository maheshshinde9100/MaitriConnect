package com.maitriconnect.chat_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "message_reactions")
public class MessageReaction {
    @Id
    private String id;
    private String messageId;
    private String userId;
    private String username;
    private String emoji;
    private LocalDateTime timestamp;

    public MessageReaction() {
        this.timestamp = LocalDateTime.now();
    }

    public MessageReaction(String messageId, String userId, String username, String emoji) {
        this.messageId = messageId;
        this.userId = userId;
        this.username = username;
        this.emoji = emoji;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
