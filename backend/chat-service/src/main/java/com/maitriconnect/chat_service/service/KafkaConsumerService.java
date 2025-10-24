package com.maitriconnect.chat_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "user.registered", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserRegistered(Map<String, Object> event) {
        try {
            log.info("Consumed user.registered event: {}", event);
            String userId = (String) event.get("userId");
            String username = (String) event.get("username");
            
            // Notify all connected clients about new user
            messagingTemplate.convertAndSend("/topic/users/new", Map.of(
                "userId", userId,
                "username", username,
                "eventType", "USER_REGISTERED"
            ));
        } catch (Exception e) {
            log.error("Error processing user.registered event", e);
        }
    }

    @KafkaListener(topics = "user.logged_in", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserLoggedIn(Map<String, Object> event) {
        try {
            log.info("Consumed user.logged_in event: {}", event);
            String userId = (String) event.get("userId");
            
            // Broadcast user online status
            messagingTemplate.convertAndSend("/topic/users/status", Map.of(
                "userId", userId,
                "status", "online",
                "eventType", "USER_ONLINE"
            ));
        } catch (Exception e) {
            log.error("Error processing user.logged_in event", e);
        }
    }

    @KafkaListener(topics = "user.updated", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserUpdated(Map<String, Object> event) {
        try {
            log.info("Consumed user.updated event: {}", event);
            String userId = (String) event.get("userId");
            
            // Notify about user profile update
            messagingTemplate.convertAndSend("/topic/users/updated", Map.of(
                "userId", userId,
                "eventType", "USER_UPDATED"
            ));
        } catch (Exception e) {
            log.error("Error processing user.updated event", e);
        }
    }

    @KafkaListener(topics = "user.deleted", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserDeleted(Map<String, Object> event) {
        try {
            log.info("Consumed user.deleted event: {}", event);
            String userId = (String) event.get("userId");
            
            // Notify about user deletion
            messagingTemplate.convertAndSend("/topic/users/deleted", Map.of(
                "userId", userId,
                "eventType", "USER_DELETED"
            ));
        } catch (Exception e) {
            log.error("Error processing user.deleted event", e);
        }
    }
}
