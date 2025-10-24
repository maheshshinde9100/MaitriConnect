package com.maitriconnect.notification_service.dto;

import com.maitriconnect.notification_service.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    
    private String id;
    private String userId;
    private String title;
    private String message;
    private Notification.NotificationType type;
    private Notification.NotificationPriority priority;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private String relatedEntityId;
    private String relatedEntityType;
    private String actionUrl;
    private String imageUrl;
    private String senderName;
    private String senderId;
}
