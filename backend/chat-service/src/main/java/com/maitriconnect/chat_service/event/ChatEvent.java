package com.maitriconnect.chat_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatEvent {
    
    private String eventId;
    private ChatEventType eventType;
    private String userId;
    private String roomId;
    private String messageId;
    private Map<String, Object> data;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    public enum ChatEventType {
        MESSAGE_SENT,
        MESSAGE_EDITED,
        MESSAGE_DELETED,
        MESSAGE_REACTION_ADDED,
        MESSAGE_REACTION_REMOVED,
        MESSAGE_READ,
        ROOM_CREATED,
        ROOM_UPDATED,
        ROOM_DELETED,
        MEMBER_JOINED,
        MEMBER_LEFT,
        MEMBER_ADDED,
        MEMBER_REMOVED,
        USER_TYPING,
        USER_ONLINE,
        USER_OFFLINE
    }
}
