package com.maitriconnect.auth_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    
    private String userId;
    private String username;
    private String email;
    private String displayName;
    private String avatarUrl;
    private String status;
    private Set<String> roles;
    private LocalDateTime timestamp;
    private String eventType; // REGISTERED, LOGGED_IN, UPDATED, DELETED
}
