package com.maitriconnect.auth_service.kafka;

import com.maitriconnect.auth_service.event.ChatUserEvent;
import com.maitriconnect.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    
    private final UserService userService;
    
    @KafkaListener(topics = "chat.user.joined", groupId = "auth-service-group")
    public void consumeUserJoinedEvent(ChatUserEvent event) {
        log.info("Received user joined event for user: {}", event.getUserId());
        userService.updateUserStatus(event.getUserId(), "online");
        userService.updateLastSeen(event.getUserId());
    }
    
    @KafkaListener(topics = "chat.user.left", groupId = "auth-service-group")
    public void consumeUserLeftEvent(ChatUserEvent event) {
        log.info("Received user left event for user: {}", event.getUserId());
        userService.updateUserStatus(event.getUserId(), "offline");
        userService.updateLastSeen(event.getUserId());
    }
}
