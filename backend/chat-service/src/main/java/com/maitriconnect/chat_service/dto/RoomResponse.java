package com.maitriconnect.chat_service.dto;

import com.maitriconnect.chat_service.model.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    
    private String id;
    private String name;
    private String description;
    private Room.RoomType type;
    private String createdBy;
    private List<String> memberIds;
    private List<String> adminIds;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastMessageAt;
    private Boolean isActive;
    private Room.RoomSettings settings;
    private Integer memberCount;
    private Integer unreadCount;
    private MessageResponse lastMessage;
}
