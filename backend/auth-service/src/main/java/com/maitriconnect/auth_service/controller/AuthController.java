package com.maitriconnect.auth_service.controller;

import com.maitriconnect.auth_service.dto.request.*;
import com.maitriconnect.auth_service.dto.response.AuthResponse;
import com.maitriconnect.auth_service.dto.response.MessageResponse;
import com.maitriconnect.auth_service.dto.response.TokenResponse;
import com.maitriconnect.auth_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        AuthResponse response = authService.register(request, httpRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        AuthResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @Valid @RequestBody RefreshTokenRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        MessageResponse response = authService.logout(request.getRefreshToken(), username);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout-all")
    public ResponseEntity<MessageResponse> logoutAll(Authentication authentication) {
        String username = authentication.getName();
        MessageResponse response = authService.logoutAll(username);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        MessageResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        MessageResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token) {
        MessageResponse response = authService.verifyEmail(token);
        return ResponseEntity.ok(response);
    }
}
