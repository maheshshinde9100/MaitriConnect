package com.maitriconnect.chat_service.controller;

import com.maitriconnect.chat_service.dto.ChatRoomResponse;
import com.maitriconnect.chat_service.dto.CreateRoomRequest;
import com.maitriconnect.chat_service.dto.SendMessageRequest;
import com.maitriconnect.chat_service.model.ChatMessage;
import com.maitriconnect.chat_service.model.ChatRoom;
import com.maitriconnect.chat_service.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    @Autowired
    private ChatService chatService;

    // Health check endpoint
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Chat REST API is working!");
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getRoomMessages(@PathVariable String roomId) {
        List<ChatMessage> messages = chatService.getMessagesByRoom(roomId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoom> createRoom(@RequestBody CreateRoomRequest request) {
        ChatRoom.ChatRoomType type = ChatRoom.ChatRoomType.valueOf(request.getType().toUpperCase());
        ChatRoom room = chatService.createChatRoom(request.getName(), request.getParticipants(), request.getCreatedBy(), type);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/users/{userId}/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getUserRooms(@PathVariable String userId) {
        List<ChatRoomResponse> rooms = chatService.getUserChatRoomsWithDetails(userId);
        return ResponseEntity.ok(rooms);
    }

    // Get or create direct chat room between two users
    @GetMapping("/rooms/direct")
    public ResponseEntity<ChatRoom> getOrCreateDirectRoom(@RequestParam String user1, @RequestParam String user2) {
        ChatRoom room = chatService.getOrCreateDirectChatRoom(user1, user2);
        return ResponseEntity.ok(room);
    }

    // Get direct messages between two users
    @GetMapping("/messages/direct")
    public ResponseEntity<List<ChatMessage>> getDirectMessages(@RequestParam String user1, @RequestParam String user2) {
        List<ChatMessage> messages = chatService.getDirectMessages(user1, user2);
        return ResponseEntity.ok(messages);
    }

    // Mark message as delivered/seen
    @PutMapping("/messages/{messageId}/status")
    public ResponseEntity<?> updateMessageStatus(@PathVariable String messageId, @RequestParam String status) {
        try {
            ChatMessage.MessageStatus messageStatus = ChatMessage.MessageStatus.valueOf(status.toUpperCase());
            chatService.updateMessageStatus(messageId, messageStatus);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status");
        }
    }

    // Get unread message count
    @GetMapping("/users/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String userId) {
        Long count = chatService.getUnreadMessageCount(userId);
        return ResponseEntity.ok(count);
    }

    // Send message via REST (for offline users)
    @PostMapping("/messages")
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody SendMessageRequest request) {
        ChatMessage.MessageType type = ChatMessage.MessageType.valueOf(request.getType().toUpperCase());
        ChatMessage message = new ChatMessage(
            request.getSenderId(),
            request.getReceiverId(),
            request.getContent(),
            request.getChatRoomId(),
            type
        );
        ChatMessage savedMessage = chatService.saveMessage(message);
        return ResponseEntity.ok(savedMessage);
    }
}