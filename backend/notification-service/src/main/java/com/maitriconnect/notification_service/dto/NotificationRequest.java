package com.maitriconnect.notification_service.dto;

import com.maitriconnect.notification_service.model.Notification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    @NotNull(message = "Type is required")
    private Notification.NotificationType type;
    
    @Builder.Default
    private Notification.NotificationPriority priority = Notification.NotificationPriority.MEDIUM;
    
    private String relatedEntityId;
    private String relatedEntityType;
    private String actionUrl;
    private String imageUrl;
    private String senderName;
    private String senderId;
}
