package com.maitriconnect.auth_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String username;
    
    @Indexed(unique = true)
    private String email;
    
    private String password;
    
    private String displayName;
    
    private String avatarUrl;
    
    @Builder.Default
    private String status = "offline"; // online, offline, away, busy
    
    @Builder.Default
    private Set<String> roles = new HashSet<>(); // USER, ADMIN, MODERATOR
    
    @Builder.Default
    private Map<String, Object> settings = Map.of();
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime lastSeen;
    
    @Builder.Default
    private boolean emailVerified = false;
    
    @Builder.Default
    private boolean accountLocked = false;
    
    @Builder.Default
    private int failedLoginAttempts = 0;
    
    private LocalDateTime lockoutEndTime;
}
