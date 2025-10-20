package com.maitriconnect.chat_service.dto;

import com.maitriconnect.chat_service.model.Room;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {
    
    @NotBlank(message = "Room name is required")
    @Size(min = 1, max = 100, message = "Room name must be between 1 and 100 characters")
    private String name;
    
    private String description;
    
    @NotNull(message = "Room type is required")
    private Room.RoomType type;
    
    private List<String> memberIds;
    
    private String avatarUrl;
    
    private RoomSettingsDto settings;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomSettingsDto {
        private Boolean allowMemberInvites;
        private Boolean allowFileSharing;
        private Boolean muteNotifications;
        private Integer maxMembers;
    }
}
