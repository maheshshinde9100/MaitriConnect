package com.maitriconnect.call_service.controller;

import com.maitriconnect.call_service.dto.ApiResponse;
import com.maitriconnect.call_service.dto.CallRequest;
import com.maitriconnect.call_service.dto.CallResponse;
import com.maitriconnect.call_service.service.CallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/calls")
@RequiredArgsConstructor
public class CallController {
    
    private final CallService callService;
    
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    
    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        return details != null ? (String) details.get("displayName") : "Unknown";
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<CallResponse>> initiateCall(
            @Valid @RequestBody CallRequest request) {
        log.info("POST /api/calls - Initiate call");
        CallResponse response = callService.initiateCall(request, getCurrentUserId(), getCurrentUserName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Call initiated successfully", response));
    }
    
    @PutMapping("/{callId}/accept")
    public ResponseEntity<ApiResponse<CallResponse>> acceptCall(@PathVariable String callId) {
        log.info("PUT /api/calls/{}/accept - Accept call", callId);
        CallResponse response = callService.acceptCall(callId, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Call accepted", response));
    }
    
    @PutMapping("/{callId}/reject")
    public ResponseEntity<ApiResponse<CallResponse>> rejectCall(
            @PathVariable String callId,
            @RequestParam(required = false) String reason) {
        log.info("PUT /api/calls/{}/reject - Reject call", callId);
        CallResponse response = callService.rejectCall(callId, getCurrentUserId(), reason);
        return ResponseEntity.ok(ApiResponse.success("Call rejected", response));
    }
    
    @PutMapping("/{callId}/end")
    public ResponseEntity<ApiResponse<CallResponse>> endCall(@PathVariable String callId) {
        log.info("PUT /api/calls/{}/end - End call", callId);
        CallResponse response = callService.endCall(callId, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Call ended", response));
    }
    
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<CallResponse>>> getCallHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/calls/history - Get call history");
        List<CallResponse> response = callService.getCallHistory(getCurrentUserId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CallResponse>>> getActiveCalls() {
        log.info("GET /api/calls/active - Get active calls");
        List<CallResponse> response = callService.getActiveCalls(getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
