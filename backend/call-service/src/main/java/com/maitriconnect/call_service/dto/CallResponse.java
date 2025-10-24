package com.maitriconnect.call_service.dto;

import com.maitriconnect.call_service.model.Call;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallResponse {
    
    private String id;
    private String callerId;
    private String callerName;
    private String receiverId;
    private String receiverName;
    private String roomId;
    private Call.CallType type;
    private Call.CallStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Long duration;
    private String endReason;
}
