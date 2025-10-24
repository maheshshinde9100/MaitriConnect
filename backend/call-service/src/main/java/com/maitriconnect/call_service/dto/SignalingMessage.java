package com.maitriconnect.call_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignalingMessage {
    
    private String callId;
    private SignalType type;
    private String senderId;
    private String receiverId;
    private Object data;
    
    public enum SignalType {
        OFFER,
        ANSWER,
        ICE_CANDIDATE,
        CALL_END,
        CALL_ACCEPT,
        CALL_REJECT,
        CALL_BUSY
    }
}
