package com.maitriconnect.chat_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
@CompoundIndex(name = "room_timestamp_idx", def = "{'roomId': 1, 'createdAt': -1}")
public class Message {
    
    @Id
    private String id;
    
    @Indexed
    private String roomId;
    
    @Indexed
    private String senderId;
    
    private String senderName;
    
    private String content;
    
    private MessageType type; // TEXT, IMAGE, FILE, VIDEO, AUDIO, SYSTEM
    
    private MessageStatus status; // SENT, DELIVERED, READ
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    private LocalDateTime editedAt;
    
    @Builder.Default
    private Boolean isEdited = false;
    
    @Builder.Default
    private Boolean isDeleted = false;
    
    // For reply functionality
    private String replyToMessageId;
    
    // For file/media messages
    private MediaMetadata mediaMetadata;
    
    // Reactions to this message
    @Builder.Default
    private List<Reaction> reactions = new ArrayList<>();
    
    // Read receipts
    @Builder.Default
    private List<ReadReceipt> readReceipts = new ArrayList<>();
    
    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        VIDEO,
        AUDIO,
        SYSTEM
    }
    
    public enum MessageStatus {
        SENT,
        DELIVERED,
        READ
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaMetadata {
        private String fileName;
        private String fileUrl;
        private String fileType;
        private Long fileSize;
        private Integer width;
        private Integer height;
        private Integer duration; // For audio/video in seconds
        private String thumbnailUrl;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reaction {
        private String userId;
        private String userName;
        private String emoji;
        @Builder.Default
        private LocalDateTime createdAt = LocalDateTime.now();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadReceipt {
        private String userId;
        private String userName;
        @Builder.Default
        private LocalDateTime readAt = LocalDateTime.now();
    }
}
