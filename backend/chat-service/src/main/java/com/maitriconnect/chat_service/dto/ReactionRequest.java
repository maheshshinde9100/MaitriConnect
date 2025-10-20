package com.maitriconnect.chat_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionRequest {
    
    @NotBlank(message = "Message ID is required")
    private String messageId;
    
    @NotBlank(message = "Emoji is required")
    private String emoji;
}
