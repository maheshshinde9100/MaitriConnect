package com.maitriconnect.auth_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sessions")
public class Session {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    private Map<String, String> deviceInfo; // browser, os, device
    
    private String ipAddress;
    
    @Indexed(unique = true)
    private String jwtToken;
    
    @Indexed(unique = true)
    private String refreshToken;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime expiresAt;
    
    @Builder.Default
    private boolean isActive = true;
    
    private LocalDateTime lastAccessedAt;
}
