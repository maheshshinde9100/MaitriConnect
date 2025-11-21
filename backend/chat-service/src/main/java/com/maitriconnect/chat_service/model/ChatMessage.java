package com.maitriconnect.chat_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "chat_messages")
public class ChatMessage {
    @Id
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private String chatRoomId;
    private LocalDateTime timestamp;
    private MessageType type;
    private MessageStatus status = MessageStatus.SENT;
    
    // New fields for enhanced features
    private List<String> fileAttachments = new ArrayList<>(); // File IDs
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private Map<String, Integer> reactions = new HashMap<>(); // emoji -> count

    public enum MessageType {
        CHAT, JOIN, LEAVE, TYPING, STOP_TYPING, SEEN, FILE, DELIVERED, READ, REACTION
    }

    public enum MessageStatus {
        SENT, DELIVERED, READ
    }

    // Constructors
    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
    }

    public ChatMessage(String senderId, String receiverId, String content, String chatRoomId, MessageType type) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.chatRoomId = chatRoomId;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(String chatRoomId) { this.chatRoomId = chatRoomId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public MessageStatus getStatus() { return status; }
    public void setStatus(MessageStatus status) { this.status = status; }

    public List<String> getFileAttachments() { return fileAttachments; }
    public void setFileAttachments(List<String> fileAttachments) { this.fileAttachments = fileAttachments; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    public Map<String, Integer> getReactions() { return reactions; }
    public void setReactions(Map<String, Integer> reactions) { this.reactions = reactions; }
}