package com.maitriconnect.notification_service.service;

import com.maitriconnect.notification_service.dto.NotificationRequest;
import com.maitriconnect.notification_service.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NotificationService notificationService;

    @KafkaListener(topics = "chat.events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeChatEvents(Map<String, Object> event) {
        try {
            String eventType = (String) event.get("eventType");
            log.info("Consumed chat event: {}", eventType);
            
            switch (eventType) {
                case "MESSAGE_SENT":
                    handleMessageSent(event);
                    break;
                case "MEMBER_JOINED":
                    handleMemberJoined(event);
                    break;
                case "MEMBER_LEFT":
                    handleMemberLeft(event);
                    break;
                case "ROOM_CREATED":
                    handleRoomCreated(event);
                    break;
                default:
                    log.debug("Unhandled chat event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing chat event", e);
        }
    }

    @KafkaListener(topics = "user.registered", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserRegistered(Map<String, Object> event) {
        try {
            log.info("Consumed user.registered event: {}", event);
            String userId = (String) event.get("userId");
            String username = (String) event.get("username");
            
            // Send welcome notification
            NotificationRequest notification = NotificationRequest.builder()
                    .userId(userId)
                    .title("Welcome to MaitriConnect!")
                    .message("Welcome " + username + "! Start connecting with friends.")
                    .type(Notification.NotificationType.SYSTEM)
                    .priority(Notification.NotificationPriority.HIGH)
                    .build();
            
            notificationService.createNotification(notification);
        } catch (Exception e) {
            log.error("Error processing user.registered event", e);
        }
    }

    @KafkaListener(topics = "user.logged_in", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserLoggedIn(Map<String, Object> event) {
        try {
            log.debug("Consumed user.logged_in event: {}", event);
            // Can be used for login notifications if needed
        } catch (Exception e) {
            log.error("Error processing user.logged_in event", e);
        }
    }

    private void handleMessageSent(Map<String, Object> event) {
        String roomId = (String) event.get("roomId");
        String messageId = (String) event.get("messageId");
        String senderId = (String) event.get("userId");
        
        // Create notifications for room members
        // Note: In a real implementation, you'd fetch room members and create notifications for each
        log.debug("Message sent in room: {}, messageId: {}", roomId, messageId);
    }

    private void handleMemberJoined(Map<String, Object> event) {
        String roomId = (String) event.get("roomId");
        String userId = (String) event.get("userId");
        
        log.debug("Member joined room: {}, userId: {}", roomId, userId);
        
        // Notify existing room members
        // Implementation would fetch room members and send notifications
    }

    private void handleMemberLeft(Map<String, Object> event) {
        String roomId = (String) event.get("roomId");
        String userId = (String) event.get("userId");
        
        log.debug("Member left room: {}, userId: {}", roomId, userId);
    }

    private void handleRoomCreated(Map<String, Object> event) {
        String roomId = (String) event.get("roomId");
        String creatorId = (String) event.get("userId");
        
        log.debug("Room created: {}, creator: {}", roomId, creatorId);
    }
}
