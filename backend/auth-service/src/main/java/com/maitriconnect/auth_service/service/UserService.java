package com.maitriconnect.auth_service.service;

import com.maitriconnect.auth_service.dto.request.ChangePasswordRequest;
import com.maitriconnect.auth_service.dto.request.UpdateProfileRequest;
import com.maitriconnect.auth_service.dto.response.BasicUserResponse;
import com.maitriconnect.auth_service.dto.response.MessageResponse;
import com.maitriconnect.auth_service.dto.response.SessionResponse;
import com.maitriconnect.auth_service.dto.response.UserResponse;
import com.maitriconnect.auth_service.event.UserEvent;
import com.maitriconnect.auth_service.exception.BadRequestException;
import com.maitriconnect.auth_service.exception.ResourceNotFoundException;
import com.maitriconnect.auth_service.kafka.KafkaProducer;
import com.maitriconnect.auth_service.model.Session;
import com.maitriconnect.auth_service.model.User;
import com.maitriconnect.auth_service.repository.SessionRepository;
import com.maitriconnect.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducer kafkaProducer;
    private final RedisService redisService;
    
    public UserResponse getProfile(String username) {
        // Check cache first
        Object cached = redisService.getCachedUserProfile(username);
        if (cached != null) {
            return (UserResponse) cached;
        }
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        UserResponse response = mapToUserResponse(user);
        
        // Cache the profile
        redisService.cacheUserProfile(user.getId(), response);
        
        return response;
    }
    
    @Transactional
    public UserResponse updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        
        if (request.getSettings() != null) {
            user.setSettings(request.getSettings());
        }
        
        user = userRepository.save(user);
        
        // Invalidate cache
        redisService.invalidateUserProfile(user.getId());
        
        // Send Kafka event
        UserEvent event = createUserEvent(user, "UPDATED");
        kafkaProducer.sendUserUpdatedEvent(event);
        
        return mapToUserResponse(user);
    }
    
    public List<BasicUserResponse> searchUsers(String query) {
        List<User> users = userRepository.searchUsers(query);
        return users.stream()
                .map(this::mapToBasicUserResponse)
                .collect(Collectors.toList());
    }
    
    public BasicUserResponse getUserBasicInfo(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return mapToBasicUserResponse(user);
    }
    
    @Transactional
    public MessageResponse changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Logout from all devices except current
        sessionRepository.deleteByUserId(user.getId());
        
        return MessageResponse.builder()
                .message("Password changed successfully")
                .success(true)
                .build();
    }
    
    public List<SessionResponse> getActiveSessions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<Session> sessions = sessionRepository.findByUserIdAndIsActive(user.getId(), true);
        
        return sessions.stream()
                .map(this::mapToSessionResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public MessageResponse deleteSession(String username, String sessionId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        
        if (!session.getUserId().equals(user.getId())) {
            throw new BadRequestException("Session does not belong to this user");
        }
        
        // Blacklist token
        redisService.blacklistToken(session.getJwtToken(), 3600000L);
        
        // Deactivate session
        session.setActive(false);
        sessionRepository.save(session);
        
        return MessageResponse.builder()
                .message("Session deleted successfully")
                .success(true)
                .build();
    }
    
    @Transactional
    public void updateUserStatus(String userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setStatus(status);
        userRepository.save(user);
        
        // Invalidate cache
        redisService.invalidateUserProfile(userId);
    }
    
    @Transactional
    public void updateLastSeen(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
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
    
    private BasicUserResponse mapToBasicUserResponse(User user) {
        return BasicUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .build();
    }
    
    private SessionResponse mapToSessionResponse(Session session) {
        return SessionResponse.builder()
                .id(session.getId())
                .deviceInfo(session.getDeviceInfo())
                .ipAddress(session.getIpAddress())
                .createdAt(session.getCreatedAt())
                .lastAccessedAt(session.getLastAccessedAt())
                .isActive(session.isActive())
                .build();
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
}
