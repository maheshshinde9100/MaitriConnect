package com.chatapp.maitriconnect.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;

    private String userId;
    private NotificationType type;
    private String title;
    private String message;
    private String relatedEntityId; // messageId, roomId, etc.
    private boolean isRead = false;
    private Instant createdAt = Instant.now();
    private Instant expiresAt;
}

enum NotificationType {
    MESSAGE, MENTION, ROOM_INVITE, FRIEND_REQUEST, SYSTEM, MESSAGE_REACTION
}