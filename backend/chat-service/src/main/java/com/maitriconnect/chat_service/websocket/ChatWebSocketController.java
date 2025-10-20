package com.maitriconnect.chat_service.websocket;

import com.maitriconnect.chat_service.dto.MessageRequest;
import com.maitriconnect.chat_service.dto.MessageResponse;
import com.maitriconnect.chat_service.service.MessageService;
import com.maitriconnect.chat_service.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/message")
    public void handleChatMessage(
            @Payload MessageRequest messageRequest,
            @AuthenticationPrincipal User user) {
        
        String userId = user.getUsername();
        log.debug("Received chat message from {} for room {}", userId, messageRequest.getRoomId());
        
        // Save and process the message
        MessageResponse response = messageService.sendMessage(messageRequest, userId, user.getUsername());
        
        // Broadcast to room
        String destination = "/topic/room/" + messageRequest.getRoomId() + "/messages";
        messagingTemplate.convertAndSend(destination, response);
        
        log.debug("Broadcasted message to room {}: {}", messageRequest.getRoomId(), response.getId());
    }

    @MessageMapping("/chat/typing")
    public void handleTypingNotification(
            @RequestParam String roomId,
            @RequestParam boolean isTyping,
            @AuthenticationPrincipal User user) {
        
        String userId = user.getUsername();
        log.debug("User {} is {} in room {}", 
                userId, isTyping ? "typing" : "not typing", roomId);
        
        // Broadcast typing status to room
        var event = new TypingEvent(userId, user.getUsername(), roomId, isTyping);
        String destination = "/topic/room/" + roomId + "/typing";
        messagingTemplate.convertAndSend(destination, event);
    }

    @MessageMapping("/chat/read-receipt")
    public void handleReadReceipt(
            @RequestParam String messageId,
            @RequestParam String roomId,
            @AuthenticationPrincipal User user) {
        
        String userId = user.getUsername();
        log.debug("Read receipt from user {} for message {} in room {}", userId, messageId, roomId);
        
        // Update read status
        messageService.markMessageAsRead(messageId, userId, user.getUsername());
        
        // Broadcast read receipt to room
        var event = new ReadReceiptEvent(userId, messageId, roomId, System.currentTimeMillis());
        String destination = "/topic/room/" + roomId + "/read-receipts";
        messagingTemplate.convertAndSend(destination, event);
    }

    @SubscribeMapping("/user/queue/notifications")
    public void handleNotificationSubscription(@AuthenticationPrincipal User user) {
        log.debug("User {} subscribed to notifications", user.getUsername());
    }

    // Event classes
    public record TypingEvent(String userId, String username, String roomId, boolean isTyping) {}
    public record ReadReceiptEvent(String userId, String messageId, String roomId, long timestamp) {}
}
