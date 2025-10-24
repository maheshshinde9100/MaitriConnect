package com.maitriconnect.call_service.service;

import com.maitriconnect.call_service.dto.CallRequest;
import com.maitriconnect.call_service.dto.CallResponse;
import com.maitriconnect.call_service.dto.SignalingMessage;
import com.maitriconnect.call_service.model.Call;
import com.maitriconnect.call_service.repository.CallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallService {
    
    private final CallRepository callRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Transactional
    public CallResponse initiateCall(CallRequest request, String callerId, String callerName) {
        log.info("User {} initiating {} call to {}", callerId, request.getType(), request.getReceiverId());
        
        // Check if receiver is already in a call
        List<Call> activeReceiverCalls = callRepository.findByReceiverIdAndStatusIn(
                request.getReceiverId(), 
                List.of(Call.CallStatus.INITIATED, Call.CallStatus.RINGING, Call.CallStatus.ACCEPTED)
        );
        
        if (!activeReceiverCalls.isEmpty()) {
            log.warn("Receiver {} is already in a call", request.getReceiverId());
            throw new RuntimeException("User is already in a call");
        }
        
        Call call = Call.builder()
                .callerId(callerId)
                .callerName(callerName)
                .receiverId(request.getReceiverId())
                .roomId(request.getRoomId())
                .type(request.getType())
                .status(Call.CallStatus.INITIATED)
                .build();
        
        Call savedCall = callRepository.save(call);
        
        // Notify receiver via WebSocket
        CallResponse response = mapToResponse(savedCall);
        notifyUser(request.getReceiverId(), "call.incoming", response);
        
        log.info("Call initiated: {}", savedCall.getId());
        return response;
    }
    
    @Transactional
    public CallResponse acceptCall(String callId, String userId) {
        log.info("User {} accepting call: {}", userId, callId);
        
        Call call = callRepository.findById(callId)
                .orElseThrow(() -> new RuntimeException("Call not found"));
        
        if (!call.getReceiverId().equals(userId)) {
            throw new RuntimeException("Unauthorized to accept this call");
        }
        
        if (call.getStatus() != Call.CallStatus.INITIATED && call.getStatus() != Call.CallStatus.RINGING) {
            throw new RuntimeException("Call cannot be accepted in current state");
        }
        
        call.setStatus(Call.CallStatus.ACCEPTED);
        call.setStartedAt(LocalDateTime.now());
        
        Call updated = callRepository.save(call);
        
        // Notify caller
        CallResponse response = mapToResponse(updated);
        notifyUser(call.getCallerId(), "call.accepted", response);
        
        return response;
    }
    
    @Transactional
    public CallResponse rejectCall(String callId, String userId, String reason) {
        log.info("User {} rejecting call: {}", userId, callId);
        
        Call call = callRepository.findById(callId)
                .orElseThrow(() -> new RuntimeException("Call not found"));
        
        if (!call.getReceiverId().equals(userId)) {
            throw new RuntimeException("Unauthorized to reject this call");
        }
        
        call.setStatus(Call.CallStatus.REJECTED);
        call.setEndedAt(LocalDateTime.now());
        call.setEndReason(reason != null ? reason : "REJECTED");
        
        Call updated = callRepository.save(call);
        
        // Notify caller
        CallResponse response = mapToResponse(updated);
        notifyUser(call.getCallerId(), "call.rejected", response);
        
        return response;
    }
    
    @Transactional
    public CallResponse endCall(String callId, String userId) {
        log.info("User {} ending call: {}", userId, callId);
        
        Call call = callRepository.findById(callId)
                .orElseThrow(() -> new RuntimeException("Call not found"));
        
        if (!call.getCallerId().equals(userId) && !call.getReceiverId().equals(userId)) {
            throw new RuntimeException("Unauthorized to end this call");
        }
        
        call.setStatus(Call.CallStatus.ENDED);
        call.setEndedAt(LocalDateTime.now());
        call.setEndReason("USER_ENDED");
        
        // Calculate duration if call was accepted
        if (call.getStartedAt() != null) {
            Duration duration = Duration.between(call.getStartedAt(), call.getEndedAt());
            call.setDuration(duration.getSeconds());
        }
        
        Call updated = callRepository.save(call);
        
        // Notify other participant
        String otherUserId = call.getCallerId().equals(userId) ? call.getReceiverId() : call.getCallerId();
        CallResponse response = mapToResponse(updated);
        notifyUser(otherUserId, "call.ended", response);
        
        return response;
    }
    
    @Transactional
    public void handleSignaling(SignalingMessage message) {
        log.debug("Handling signaling message: {} for call: {}", message.getType(), message.getCallId());
        
        Call call = callRepository.findById(message.getCallId())
                .orElseThrow(() -> new RuntimeException("Call not found"));
        
        switch (message.getType()) {
            case OFFER:
                call.setOffer(message.getData().toString());
                call.setStatus(Call.CallStatus.RINGING);
                callRepository.save(call);
                break;
            case ANSWER:
                call.setAnswer(message.getData().toString());
                callRepository.save(call);
                break;
            case ICE_CANDIDATE:
                // Store ICE candidate
                break;
            default:
                log.debug("Unhandled signaling type: {}", message.getType());
        }
        
        // Forward signaling message to other participant
        notifyUser(message.getReceiverId(), "call.signaling", message);
    }
    
    public List<CallResponse> getCallHistory(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Call> calls = callRepository.findByCallerIdOrReceiverIdOrderByCreatedAtDesc(
                userId, userId, pageable);
        
        return calls.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<CallResponse> getActiveCalls(String userId) {
        List<Call> activeCalls = callRepository.findByReceiverIdAndStatusIn(
                userId,
                List.of(Call.CallStatus.INITIATED, Call.CallStatus.RINGING, Call.CallStatus.ACCEPTED)
        );
        
        return activeCalls.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private void notifyUser(String userId, String event, Object data) {
        try {
            String destination = "/topic/user/" + userId + "/calls";
            messagingTemplate.convertAndSend(destination, data);
            log.debug("Sent {} notification to user: {}", event, userId);
        } catch (Exception e) {
            log.error("Failed to send notification to user: {}", userId, e);
        }
    }
    
    private CallResponse mapToResponse(Call call) {
        return CallResponse.builder()
                .id(call.getId())
                .callerId(call.getCallerId())
                .callerName(call.getCallerName())
                .receiverId(call.getReceiverId())
                .receiverName(call.getReceiverName())
                .roomId(call.getRoomId())
                .type(call.getType())
                .status(call.getStatus())
                .createdAt(call.getCreatedAt())
                .startedAt(call.getStartedAt())
                .endedAt(call.getEndedAt())
                .duration(call.getDuration())
                .endReason(call.getEndReason())
                .build();
    }
}
