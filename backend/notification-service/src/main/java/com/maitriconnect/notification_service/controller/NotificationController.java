package com.maitriconnect.notification_service.controller;

import com.maitriconnect.notification_service.dto.ApiResponse;
import com.maitriconnect.notification_service.dto.NotificationRequest;
import com.maitriconnect.notification_service.dto.NotificationResponse;
import com.maitriconnect.notification_service.dto.PageResponse;
import com.maitriconnect.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    // TODO: Replace with actual authentication from JWT token
    private static final String MOCK_USER_ID = "user123";
    
    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @Valid @RequestBody NotificationRequest request) {
        log.info("POST /api/notifications - Create notification");
        NotificationResponse response = notificationService.createNotification(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notification created successfully", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/notifications - Get user notifications (page: {}, size: {})", page, size);
        PageResponse<NotificationResponse> response = notificationService
                .getUserNotifications(MOCK_USER_ID, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getUnreadNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/notifications/unread - Get unread notifications");
        PageResponse<NotificationResponse> response = notificationService
                .getUnreadNotifications(MOCK_USER_ID, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        log.info("GET /api/notifications/unread/count - Get unread count");
        Long count = notificationService.getUnreadCount(MOCK_USER_ID);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
    
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable String notificationId) {
        log.info("PUT /api/notifications/{}/read - Mark as read", notificationId);
        NotificationResponse response = notificationService.markAsRead(notificationId, MOCK_USER_ID);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", response));
    }
    
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        log.info("PUT /api/notifications/read-all - Mark all as read");
        notificationService.markAllAsRead(MOCK_USER_ID);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
    
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable String notificationId) {
        log.info("DELETE /api/notifications/{} - Delete notification", notificationId);
        notificationService.deleteNotification(notificationId, MOCK_USER_ID);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully", null));
    }
    
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteAllNotifications() {
        log.info("DELETE /api/notifications - Delete all notifications");
        notificationService.deleteAllUserNotifications(MOCK_USER_ID);
        return ResponseEntity.ok(ApiResponse.success("All notifications deleted successfully", null));
    }
}
