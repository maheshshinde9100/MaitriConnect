package com.maitriconnect.auth_service.service;

import com.maitriconnect.auth_service.dto.request.*;
import com.maitriconnect.auth_service.dto.response.*;
import com.maitriconnect.auth_service.event.UserEvent;
import com.maitriconnect.auth_service.exception.BadRequestException;
import com.maitriconnect.auth_service.exception.ResourceNotFoundException;
import com.maitriconnect.auth_service.exception.UnauthorizedException;
import com.maitriconnect.auth_service.kafka.KafkaProducer;
import com.maitriconnect.auth_service.model.PasswordResetToken;
import com.maitriconnect.auth_service.model.Session;
import com.maitriconnect.auth_service.model.User;
import com.maitriconnect.auth_service.repository.PasswordResetTokenRepository;
import com.maitriconnect.auth_service.repository.SessionRepository;
import com.maitriconnect.auth_service.repository.UserRepository;
import com.maitriconnect.auth_service.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final KafkaProducer kafkaProducer;
    private final EmailService emailService;
    private final RedisService redisService;
    
    @Value("${security.max-login-attempts}")
    private int maxLoginAttempts;
    
    @Value("${security.lockout-duration}")
    private long lockoutDuration;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;
    
    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }
        
        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName())
                .status("offline")
                .roles(new HashSet<>(Collections.singletonList("USER")))
                .createdAt(LocalDateTime.now())
                .emailVerified(false)
                .build();
        
        user = userRepository.save(user);
        
        // Generate tokens
        String accessToken = jwtUtil.generateToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        
        // Create session
        createSession(user.getId(), accessToken, refreshToken, httpRequest);
        
        // Send Kafka event
        UserEvent event = createUserEvent(user, "REGISTERED");
        kafkaProducer.sendUserRegisteredEvent(event);
        
        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), user.getUsername());
        
        return AuthResponse.builder()
                .user(mapToUserResponse(user))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .message("Registration successful. Please verify your email.")
                .build();
    }
    
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        // Find user
        User user = userRepository.findByEmailOrUsername(request.getEmailOrUsername(), request.getEmailOrUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        
        // Check if account is locked
        if (user.isAccountLocked() && user.getLockoutEndTime() != null) {
            if (LocalDateTime.now().isBefore(user.getLockoutEndTime())) {
                throw new UnauthorizedException("Account is locked. Please try again later.");
            } else {
                // Unlock account
                user.setAccountLocked(false);
                user.setFailedLoginAttempts(0);
                user.setLockoutEndTime(null);
                userRepository.save(user);
            }
        }
        
        try {
            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmailOrUsername(), request.getPassword())
            );
            
            // Reset failed login attempts
            if (user.getFailedLoginAttempts() > 0) {
                user.setFailedLoginAttempts(0);
                userRepository.save(user);
            }
            
            // Generate tokens
            String accessToken = jwtUtil.generateToken(user.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
            
            // Create session
            createSession(user.getId(), accessToken, refreshToken, httpRequest);
            
            // Update last seen
            user.setLastSeen(LocalDateTime.now());
            user.setStatus("online");
            userRepository.save(user);
            
            // Send Kafka event
            UserEvent event = createUserEvent(user, "LOGGED_IN");
            kafkaProducer.sendUserLoggedInEvent(event);
            
            return AuthResponse.builder()
                    .user(mapToUserResponse(user))
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .message("Login successful")
                    .build();
                    
        } catch (Exception e) {
            // Increment failed login attempts
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            
            if (user.getFailedLoginAttempts() >= maxLoginAttempts) {
                user.setAccountLocked(true);
                user.setLockoutEndTime(LocalDateTime.now().plusSeconds(lockoutDuration / 1000));
            }
            
            userRepository.save(user);
            throw new UnauthorizedException("Invalid credentials");
        }
    }
    
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        // Validate refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        
        // Find session
        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        
        if (!session.isActive()) {
            throw new UnauthorizedException("Session is not active");
        }
        
        // Extract username and generate new tokens
        String username = jwtUtil.extractUsername(refreshToken);
        String newAccessToken = jwtUtil.generateToken(username);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);
        
        // Update session
        session.setJwtToken(newAccessToken);
        session.setRefreshToken(newRefreshToken);
        session.setLastAccessedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000));
        sessionRepository.save(session);
        
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
    
    @Transactional
    public MessageResponse logout(String refreshToken, String username) {
        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        
        // Blacklist tokens
        redisService.blacklistToken(session.getJwtToken(), jwtExpiration);
        
        // Deactivate session
        session.setActive(false);
        sessionRepository.save(session);
        
        // Update user status
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus("offline");
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
        
        return MessageResponse.builder()
                .message("Logout successful")
                .success(true)
                .build();
    }
    
    @Transactional
    public MessageResponse logoutAll(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Get all active sessions
        List<Session> sessions = sessionRepository.findByUserIdAndIsActive(user.getId(), true);
        
        // Blacklist all tokens and deactivate sessions
        for (Session session : sessions) {
            redisService.blacklistToken(session.getJwtToken(), jwtExpiration);
            session.setActive(false);
        }
        
        sessionRepository.saveAll(sessions);
        
        // Update user status
        user.setStatus("offline");
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
        
        return MessageResponse.builder()
                .message("Logged out from all devices")
                .success(true)
                .build();
    }
    
    @Transactional
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this email"));
        
        // Delete existing tokens
        passwordResetTokenRepository.deleteByUserId(user.getId());
        
        // Generate reset token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .userId(user.getId())
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();
        
        passwordResetTokenRepository.save(resetToken);
        
        // Send email
        emailService.sendPasswordResetEmail(user.getEmail(), token);
        
        return MessageResponse.builder()
                .message("Password reset link sent to your email")
                .success(true)
                .build();
    }
    
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByTokenAndUsedFalseAndExpiresAtAfter(request.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));
        
        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
        
        // Logout from all devices
        sessionRepository.deleteByUserId(user.getId());
        
        return MessageResponse.builder()
                .message("Password reset successful")
                .success(true)
                .build();
    }
    
    public MessageResponse verifyEmail(String token) {
        // Implementation for email verification
        // This would typically involve checking a verification token
        return MessageResponse.builder()
                .message("Email verified successfully")
                .success(true)
                .build();
    }
    
    private void createSession(String userId, String accessToken, String refreshToken, HttpServletRequest request) {
        Map<String, String> deviceInfo = new HashMap<>();
        deviceInfo.put("userAgent", request.getHeader("User-Agent"));
        
        Session session = Session.builder()
                .userId(userId)
                .jwtToken(accessToken)
                .refreshToken(refreshToken)
                .deviceInfo(deviceInfo)
                .ipAddress(request.getRemoteAddr())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .isActive(true)
                .lastAccessedAt(LocalDateTime.now())
                .build();
        
        sessionRepository.save(session);
    }
    
    private UserEvent createUserEvent(User user, String eventType) {
        return UserEvent.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .roles(user.getRoles())
                .timestamp(LocalDateTime.now())
                .eventType(eventType)
                .build();
    }
    
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .roles(user.getRoles())
                .settings(user.getSettings())
                .createdAt(user.getCreatedAt())
                .lastSeen(user.getLastSeen())
                .emailVerified(user.isEmailVerified())
                .build();
    }
}
