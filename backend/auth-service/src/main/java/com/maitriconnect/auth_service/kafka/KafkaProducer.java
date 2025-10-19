package com.maitriconnect.auth_service.kafka;

import com.maitriconnect.auth_service.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    
    public void sendUserRegisteredEvent(UserEvent event) {
        log.info("Sending user registered event for user: {}", event.getUserId());
        kafkaTemplate.send("user.registered", event.getUserId(), event);
    }
    
    public void sendUserLoggedInEvent(UserEvent event) {
        log.info("Sending user logged in event for user: {}", event.getUserId());
        kafkaTemplate.send("user.logged_in", event.getUserId(), event);
    }
    
    public void sendUserUpdatedEvent(UserEvent event) {
        log.info("Sending user updated event for user: {}", event.getUserId());
        kafkaTemplate.send("user.updated", event.getUserId(), event);
    }
    
    public void sendUserDeletedEvent(UserEvent event) {
        log.info("Sending user deleted event for user: {}", event.getUserId());
        kafkaTemplate.send("user.deleted", event.getUserId(), event);
    }
}
