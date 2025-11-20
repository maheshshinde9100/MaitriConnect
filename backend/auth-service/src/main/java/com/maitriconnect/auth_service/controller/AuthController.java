package com.maitriconnect.auth_service.controller;

import com.maitriconnect.auth_service.dto.AuthResponse;
import com.maitriconnect.auth_service.dto.LoginRequest;
import com.maitriconnect.auth_service.dto.RegisterRequest;
import com.maitriconnect.auth_service.dto.UpdateProfileRequest;
import com.maitriconnect.auth_service.dto.UserProfileResponse;
import com.maitriconnect.auth_service.model.User;
import com.maitriconnect.auth_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            // Extract user ID from token and update online status
            String userId = extractUserIdFromToken(token);
            authService.updateOnlineStatus(userId, false);
            return ResponseEntity.ok().body("{\"message\":\"Logged out successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId) {
        try {
            User user = authService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserProfileResponse>> searchUsers(@RequestParam String q) {
        List<UserProfileResponse> users = authService.searchUsers(q);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/online")
    public ResponseEntity<List<UserProfileResponse>> getOnlineUsers() {
        List<UserProfileResponse> users = authService.getOnlineUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/user/{userId}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable String userId, 
                                         @Valid @RequestBody UpdateProfileRequest request) {
        try {
            UserProfileResponse response = authService.updateUserProfile(userId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/user/{userId}/status")
    public ResponseEntity<?> updateOnlineStatus(@PathVariable String userId, 
                                               @RequestParam boolean online) {
        try {
            authService.updateOnlineStatus(userId, online);
            return ResponseEntity.ok().body("{\"message\":\"Status updated successfully\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Helper method to extract user ID from token
    private String extractUserIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            // In a real implementation, you would use JwtUtil to extract user ID
            // For now, this is a placeholder
            return jwtToken; // This should be replaced with actual token parsing
        }
        throw new RuntimeException("Invalid token");
    }

    // Simple error response class
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}