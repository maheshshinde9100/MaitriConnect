package com.maitriconnect.chat_service.controller;

import com.maitriconnect.chat_service.dto.ApiResponse;
import com.maitriconnect.chat_service.dto.MessageRequest;
import com.maitriconnect.chat_service.dto.MessageResponse;
import com.maitriconnect.chat_service.dto.PageResponse;
import com.maitriconnect.chat_service.dto.ReactionRequest;
import com.maitriconnect.chat_service.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;
    
    // TODO: Replace with actual authentication from JWT token
    private static final String MOCK_USER_ID = "user123";
    private static final String MOCK_USER_NAME = "Test User";
    
    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @Valid @RequestBody MessageRequest request) {
        log.info("POST /api/messages - Send message to room: {}", request.getRoomId());
        MessageResponse response = messageService.sendMessage(request, MOCK_USER_ID, MOCK_USER_NAME);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message sent successfully", response));
    }
    
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiResponse<PageResponse<MessageResponse>>> getRoomMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        log.info("GET /api/messages/room/{} - Get messages (page: {}, size: {})", roomId, page, size);
        PageResponse<MessageResponse> response = messageService.getRoomMessages(roomId, MOCK_USER_ID, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/room/{roomId}/search")
    public ResponseEntity<ApiResponse<PageResponse<MessageResponse>>> searchMessages(
            @PathVariable String roomId,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        log.info("GET /api/messages/room/{}/search - Search: '{}' (page: {}, size: {})", 
                roomId, query, page, size);
        PageResponse<MessageResponse> response = messageService.searchMessages(roomId, query, MOCK_USER_ID, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{messageId}")
    public ResponseEntity<ApiResponse<MessageResponse>> updateMessage(
            @PathVariable String messageId,
            @RequestParam String content) {
        log.info("PUT /api/messages/{} - Update message", messageId);
        MessageResponse response = messageService.updateMessage(messageId, content, MOCK_USER_ID);
        return ResponseEntity.ok(ApiResponse.success("Message updated successfully", response));
    }
    
    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable String messageId) {
        log.info("DELETE /api/messages/{} - Delete message", messageId);
        messageService.deleteMessage(messageId, MOCK_USER_ID);
        return ResponseEntity.ok(ApiResponse.success("Message deleted successfully", null));
    }
    
    @PostMapping("/{messageId}/reactions")
    public ResponseEntity<ApiResponse<MessageResponse>> addReaction(
            @PathVariable String messageId,
            @Valid @RequestBody ReactionRequest request) {
        log.info("POST /api/messages/{}/reactions - Add reaction: {}", messageId, request.getEmoji());
        MessageResponse response = messageService.addReaction(messageId, request.getEmoji(), 
                MOCK_USER_ID, MOCK_USER_NAME);
        return ResponseEntity.ok(ApiResponse.success("Reaction added successfully", response));
    }
    
    @DeleteMapping("/{messageId}/reactions/{emoji}")
    public ResponseEntity<ApiResponse<MessageResponse>> removeReaction(
            @PathVariable String messageId,
            @PathVariable String emoji) {
        log.info("DELETE /api/messages/{}/reactions/{} - Remove reaction", messageId, emoji);
        MessageResponse response = messageService.removeReaction(messageId, emoji, MOCK_USER_ID);
        return ResponseEntity.ok(ApiResponse.success("Reaction removed successfully", response));
    }
    
    @PostMapping("/{messageId}/read")
    public ResponseEntity<ApiResponse<Void>> markMessageAsRead(@PathVariable String messageId) {
        log.info("POST /api/messages/{}/read - Mark as read", messageId);
        messageService.markMessageAsRead(messageId, MOCK_USER_ID, MOCK_USER_NAME);
        return ResponseEntity.ok(ApiResponse.success("Message marked as read", null));
    }
    
    @PostMapping("/room/{roomId}/read-all")
    public ResponseEntity<ApiResponse<Void>> markRoomMessagesAsRead(@PathVariable String roomId) {
        log.info("POST /api/messages/room/{}/read-all - Mark all as read", roomId);
        messageService.markRoomMessagesAsRead(roomId, MOCK_USER_ID, MOCK_USER_NAME);
        return ResponseEntity.ok(ApiResponse.success("All messages marked as read", null));
    }
    
    @GetMapping("/room/{roomId}/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable String roomId) {
        log.info("GET /api/messages/room/{}/unread-count", roomId);
        Long count = messageService.getUnreadMessageCount(roomId, MOCK_USER_ID);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
