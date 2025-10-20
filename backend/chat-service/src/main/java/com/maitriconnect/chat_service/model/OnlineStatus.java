package com.maitriconnect.chat_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnlineStatus implements Serializable {
    
    private String userId;
    private String userName;
    private Boolean isOnline;
    
    @Builder.Default
    private LocalDateTime lastSeen = LocalDateTime.now();
    
    private String status; // ONLINE, OFFLINE, AWAY, BUSY
}
