package com.maitriconnect.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private String id;
    private String username;
    private String email;
    private String displayName;
    private String avatarUrl;
    private String status;
    private Set<String> roles;
    private Map<String, Object> settings;
    private LocalDateTime createdAt;
    private LocalDateTime lastSeen;
    private boolean emailVerified;
}
