package com.maitriconnect.auth_service.controller;

import com.maitriconnect.auth_service.dto.request.ChangePasswordRequest;
import com.maitriconnect.auth_service.dto.request.UpdateProfileRequest;
import com.maitriconnect.auth_service.dto.response.BasicUserResponse;
import com.maitriconnect.auth_service.dto.response.MessageResponse;
import com.maitriconnect.auth_service.dto.response.SessionResponse;
import com.maitriconnect.auth_service.dto.response.UserResponse;
import com.maitriconnect.auth_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        String username = authentication.getName();
        UserResponse response = userService.getProfile(username);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        UserResponse response = userService.updateProfile(username, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<BasicUserResponse>> searchUsers(@RequestParam String q) {
        List<BasicUserResponse> users = userService.searchUsers(q);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{userId}/basic")
    public ResponseEntity<BasicUserResponse> getUserBasicInfo(@PathVariable String userId) {
        BasicUserResponse response = userService.getUserBasicInfo(userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/password")
    public ResponseEntity<MessageResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        MessageResponse response = userService.changePassword(username, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/sessions")
    public ResponseEntity<List<SessionResponse>> getActiveSessions(Authentication authentication) {
        String username = authentication.getName();
        List<SessionResponse> sessions = userService.getActiveSessions(username);
        return ResponseEntity.ok(sessions);
    }
    
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<MessageResponse> deleteSession(
            @PathVariable String sessionId,
            Authentication authentication) {
        String username = authentication.getName();
        MessageResponse response = userService.deleteSession(username, sessionId);
        return ResponseEntity.ok(response);
    }
}
