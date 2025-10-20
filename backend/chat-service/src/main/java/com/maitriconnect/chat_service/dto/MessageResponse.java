package com.maitriconnect.chat_service.dto;

import com.maitriconnect.chat_service.model.Message;
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
public class MessageResponse {
    
    private String id;
    private String roomId;
    private String senderId;
    private String senderName;
    private String content;
    private Message.MessageType type;
    private Message.MessageStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime editedAt;
    private Boolean isEdited;
    private Boolean isDeleted;
    private String replyToMessageId;
    private Message.MediaMetadata mediaMetadata;
    private List<Message.Reaction> reactions;
    private List<Message.ReadReceipt> readReceipts;
    private Integer reactionCount;
    private Integer readCount;
}
