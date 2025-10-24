package com.maitriconnect.call_service.model;

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
@Document(collection = "calls")
public class Call {
    
    @Id
    private String id;
    
    @Indexed
    private String callerId;
    
    private String callerName;
    
    @Indexed
    private String receiverId;
    
    private String receiverName;
    
    @Indexed
    private String roomId;  // For group calls
    
    private CallType type;  // AUDIO, VIDEO
    
    private CallStatus status;  // INITIATED, RINGING, ACCEPTED, REJECTED, ENDED, MISSED, BUSY
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime startedAt;
    
    private LocalDateTime endedAt;
    
    private Long duration;  // Duration in seconds
    
    private String endReason;  // USER_ENDED, TIMEOUT, ERROR, NETWORK_ISSUE
    
    // WebRTC signaling data
    private String offer;
    private String answer;
    
    @Builder.Default
    private List<IceCandidate> iceCandidates = new ArrayList<>();
    
    // Call quality metrics
    private CallMetrics metrics;
    
    public enum CallType {
        AUDIO,
        VIDEO
    }
    
    public enum CallStatus {
        INITIATED,
        RINGING,
        ACCEPTED,
        REJECTED,
        ENDED,
        MISSED,
        BUSY,
        FAILED
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IceCandidate {
        private String userId;
        private String candidate;
        private String sdpMid;
        private Integer sdpMLineIndex;
        private LocalDateTime timestamp;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CallMetrics {
        private Integer avgBitrate;
        private Integer avgPacketLoss;
        private Integer avgLatency;
        private String quality;  // EXCELLENT, GOOD, FAIR, POOR
    }
}
