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
public class TypingIndicator implements Serializable {
    
    private String roomId;
    private String userId;
    private String userName;
    private Boolean isTyping;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
