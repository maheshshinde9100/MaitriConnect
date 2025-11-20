package com.maitriconnect.chat_service.dto;

public class SendMessageRequest {
    private String senderId;
    private String receiverId;
    private String content;
    private String chatRoomId;
    private String type;

    // Getters and Setters
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(String chatRoomId) { this.chatRoomId = chatRoomId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}