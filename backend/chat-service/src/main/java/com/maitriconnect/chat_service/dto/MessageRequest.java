package com.maitriconnect.chat_service.dto;

import com.maitriconnect.chat_service.model.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    
    @NotBlank(message = "Room ID is required")
    private String roomId;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "Message type is required")
    private Message.MessageType type;
    
    private String replyToMessageId;
    
    private MediaMetadataDto mediaMetadata;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaMetadataDto {
        private String fileName;
        private String fileUrl;
        private String fileType;
        private Long fileSize;
        private Integer width;
        private Integer height;
        private Integer duration;
        private String thumbnailUrl;
    }
}
