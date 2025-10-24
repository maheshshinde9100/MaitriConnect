package com.maitriconnect.notification_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    private String title;
    
    private String message;
    
    private NotificationType type;
    
    private NotificationPriority priority;
    
    @Builder.Default
    private Boolean isRead = false;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime readAt;
    
    // Related entity information
    private String relatedEntityId;
    private String relatedEntityType; // MESSAGE, ROOM, USER, CALL, etc.
    
    // Action URL for click handling
    private String actionUrl;
    
    // Additional metadata
    private String imageUrl;
    private String senderName;
    private String senderId;
    
    public enum NotificationType {
        MESSAGE,           // New message
        MENTION,           // User mentioned
        REACTION,          // Message reaction
        ROOM_INVITE,       // Room invitation
        MEMBER_JOINED,     // Member joined room
        MEMBER_LEFT,       // Member left room
        CALL_INCOMING,     // Incoming call
        CALL_MISSED,       // Missed call
        SYSTEM,            // System notification
        FRIEND_REQUEST,    // Friend request
        USER_REGISTERED    // New user registered
    }
    
    public enum NotificationPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
}
