package com.maitriconnect.auth_service.service;

import com.maitriconnect.auth_service.dto.request.UpdateRolesRequest;
import com.maitriconnect.auth_service.dto.response.AdminStatsResponse;
import com.maitriconnect.auth_service.dto.response.MessageResponse;
import com.maitriconnect.auth_service.dto.response.PageResponse;
import com.maitriconnect.auth_service.dto.response.UserResponse;
import com.maitriconnect.auth_service.event.UserEvent;
import com.maitriconnect.auth_service.exception.ResourceNotFoundException;
import com.maitriconnect.auth_service.kafka.KafkaProducer;
import com.maitriconnect.auth_service.model.User;
import com.maitriconnect.auth_service.repository.SessionRepository;
import com.maitriconnect.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final KafkaProducer kafkaProducer;
    private final RedisService redisService;
    
    public PageResponse<UserResponse> getAllUsers(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Page<User> userPage;
        if (search != null && !search.isEmpty()) {
            userPage = userRepository.searchUsersWithPagination(search, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        
        List<UserResponse> users = userPage.getContent().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<UserResponse>builder()
                .content(users)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .build();
    }
    
    @Transactional
    public UserResponse updateUserRoles(String userId, UpdateRolesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setRoles(request.getRoles());
        user = userRepository.save(user);
        
        // Invalidate cache
        redisService.invalidateUserProfile(userId);
        
        // Send Kafka event
        UserEvent event = createUserEvent(user, "UPDATED");
        kafkaProducer.sendUserUpdatedEvent(event);
        
        return mapToUserResponse(user);
    }
    
    @Transactional
    public MessageResponse deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Delete all sessions
        sessionRepository.deleteByUserId(userId);
        
        // Delete user
        userRepository.delete(user);
        
        // Invalidate cache
        redisService.invalidateUserProfile(userId);
        
        // Send Kafka event
        UserEvent event = createUserEvent(user, "DELETED");
        kafkaProducer.sendUserDeletedEvent(event);
        
        return MessageResponse.builder()
                .message("User deleted successfully")
                .success(true)
                .build();
    }
    
    public AdminStatsResponse getStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus("online");
        
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        long newUsersToday = userRepository.countByCreatedAtAfter(startOfDay);
        
        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .newUsersToday(newUsersToday)
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
