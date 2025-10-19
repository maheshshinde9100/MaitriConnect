package com.maitriconnect.auth_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserEvent {
    
    private String userId;
    private String status;
    private LocalDateTime timestamp;
    private String eventType; // JOINED, LEFT
}
