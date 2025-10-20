package com.maitriconnect.chat_service.service;

import com.maitriconnect.chat_service.event.ChatEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    
    private final KafkaTemplate<String, ChatEvent> kafkaTemplate;
    
    private static final String CHAT_EVENTS_TOPIC = "chat.events";
    
    public void publishChatEvent(ChatEvent event) {
        try {
            if (event.getEventId() == null) {
                event.setEventId(UUID.randomUUID().toString());
            }
            
            CompletableFuture<SendResult<String, ChatEvent>> future = 
                    kafkaTemplate.send(CHAT_EVENTS_TOPIC, event.getEventId(), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Published chat event: {} with id: {}", 
                            event.getEventType(), event.getEventId());
                } else {
                    log.error("Failed to publish chat event: {} with id: {}", 
                            event.getEventType(), event.getEventId(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error publishing chat event: {}", event.getEventType(), e);
        }
    }
    
    public void publishMessageSent(String userId, String roomId, String messageId) {
        ChatEvent event = ChatEvent.builder()
                .eventType(ChatEvent.ChatEventType.MESSAGE_SENT)
                .userId(userId)
                .roomId(roomId)
                .messageId(messageId)
                .build();
        publishChatEvent(event);
    }
    
    public void publishMessageEdited(String userId, String roomId, String messageId) {
        ChatEvent event = ChatEvent.builder()
                .eventType(ChatEvent.ChatEventType.MESSAGE_EDITED)
                .userId(userId)
                .roomId(roomId)
                .messageId(messageId)
                .build();
        publishChatEvent(event);
    }
    
    public void publishMessageDeleted(String userId, String roomId, String messageId) {
        ChatEvent event = ChatEvent.builder()
                .eventType(ChatEvent.ChatEventType.MESSAGE_DELETED)
                .userId(userId)
                .roomId(roomId)
                .messageId(messageId)
                .build();
        publishChatEvent(event);
    }
    
    public void publishRoomCreated(String userId, String roomId) {
        ChatEvent event = ChatEvent.builder()
                .eventType(ChatEvent.ChatEventType.ROOM_CREATED)
                .userId(userId)
                .roomId(roomId)
                .build();
        publishChatEvent(event);
    }
    
    public void publishMemberJoined(String userId, String roomId) {
        ChatEvent event = ChatEvent.builder()
                .eventType(ChatEvent.ChatEventType.MEMBER_JOINED)
                .userId(userId)
                .roomId(roomId)
                .build();
        publishChatEvent(event);
    }
    
    public void publishMemberLeft(String userId, String roomId) {
        ChatEvent event = ChatEvent.builder()
                .eventType(ChatEvent.ChatEventType.MEMBER_LEFT)
                .userId(userId)
                .roomId(roomId)
                .build();
        publishChatEvent(event);
    }
}
