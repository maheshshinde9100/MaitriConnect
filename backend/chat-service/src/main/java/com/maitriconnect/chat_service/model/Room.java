package com.maitriconnect.chat_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rooms")
public class Room {
    
    @Id
    private String id;
    
    @Indexed
    private String name;
    
    private String description;
    
    private RoomType type; // DIRECT, GROUP, CHANNEL
    
    private String createdBy;
    
    @Builder.Default
    private List<String> memberIds = new ArrayList<>();
    
    @Builder.Default
    private List<String> adminIds = new ArrayList<>();
    
    private String avatarUrl;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    private LocalDateTime lastMessageAt;
    
    @Builder.Default
    private Boolean isActive = true;
    
    // For direct messages, store both user IDs for easy lookup
    private String user1Id;
    private String user2Id;
    
    // Room settings
    @Builder.Default
    private RoomSettings settings = new RoomSettings();
    
    public enum RoomType {
        DIRECT,    // One-on-one chat
        GROUP,     // Group chat
        CHANNEL    // Public channel
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomSettings {
        @Builder.Default
        private Boolean allowMemberInvites = true;
        
        @Builder.Default
        private Boolean allowFileSharing = true;
        
        @Builder.Default
        private Boolean muteNotifications = false;
        
        @Builder.Default
        private Integer maxMembers = 100;
    }
}
