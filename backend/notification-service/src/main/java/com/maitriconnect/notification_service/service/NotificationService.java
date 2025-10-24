package com.maitriconnect.notification_service.service;

import com.maitriconnect.notification_service.dto.NotificationRequest;
import com.maitriconnect.notification_service.dto.NotificationResponse;
import com.maitriconnect.notification_service.dto.PageResponse;
import com.maitriconnect.notification_service.model.Notification;
import com.maitriconnect.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        log.info("Creating notification for user: {}", request.getUserId());
        
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .priority(request.getPriority())
                .relatedEntityId(request.getRelatedEntityId())
                .relatedEntityType(request.getRelatedEntityType())
                .actionUrl(request.getActionUrl())
                .imageUrl(request.getImageUrl())
                .senderName(request.getSenderName())
                .senderId(request.getSenderId())
                .build();
        
        Notification saved = notificationRepository.save(notification);
        
        // Send real-time notification via WebSocket
        NotificationResponse response = mapToResponse(saved);
        sendRealTimeNotification(request.getUserId(), response);
        
        log.info("Notification created: {}", saved.getId());
        return response;
    }
    
    public PageResponse<NotificationResponse> getUserNotifications(String userId, int page, int size) {
        log.debug("Getting notifications for user: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationPage = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        return mapToPageResponse(notificationPage);
    }
    
    public PageResponse<NotificationResponse> getUnreadNotifications(String userId, int page, int size) {
        log.debug("Getting unread notifications for user: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationPage = notificationRepository
                .findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false, pageable);
        
        return mapToPageResponse(notificationPage);
    }
    
    public Long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    @Transactional
    public NotificationResponse markAsRead(String notificationId, String userId) {
        log.info("Marking notification as read: {}", notificationId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to notification");
        }
        
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        
        Notification updated = notificationRepository.save(notification);
        return mapToResponse(updated);
    }
    
    @Transactional
    public void markAllAsRead(String userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        
        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false, PageRequest.of(0, 100))
                .getContent();
        
        unreadNotifications.forEach(notification -> {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
        
        notificationRepository.saveAll(unreadNotifications);
    }
    
    @Transactional
    public void deleteNotification(String notificationId, String userId) {
        log.info("Deleting notification: {}", notificationId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to notification");
        }
        
        notificationRepository.delete(notification);
    }
    
    @Transactional
    public void deleteAllUserNotifications(String userId) {
        log.info("Deleting all notifications for user: {}", userId);
        
        List<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 1000))
                .getContent();
        
        notificationRepository.deleteAll(notifications);
    }
    
    @Transactional
    public void deleteOldNotifications(String userId, int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        notificationRepository.deleteByUserIdAndCreatedAtBefore(userId, cutoffDate);
    }
    
    private void sendRealTimeNotification(String userId, NotificationResponse notification) {
        try {
            String destination = "/topic/user/" + userId + "/notifications";
            messagingTemplate.convertAndSend(destination, notification);
            log.debug("Sent real-time notification to user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to send real-time notification", e);
        }
    }
    
    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .priority(notification.getPriority())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .relatedEntityId(notification.getRelatedEntityId())
                .relatedEntityType(notification.getRelatedEntityType())
                .actionUrl(notification.getActionUrl())
                .imageUrl(notification.getImageUrl())
                .senderName(notification.getSenderName())
                .senderId(notification.getSenderId())
                .build();
    }
    
    private PageResponse<NotificationResponse> mapToPageResponse(Page<Notification> page) {
        List<NotificationResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<NotificationResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
