package com.maitriconnect.chat_service.controller;

import com.maitriconnect.chat_service.model.ChatMessage;
import com.maitriconnect.chat_service.model.ChatRoom;
import com.maitriconnect.chat_service.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatRestController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getRoomMessages(@PathVariable String roomId) {
        List<ChatMessage> messages = chatService.getMessagesByRoom(roomId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoom> createRoom(@RequestBody CreateRoomRequest request) {
        ChatRoom room = chatService.createChatRoom(request.getName(), request.getParticipants(), request.getCreatedBy());
        return ResponseEntity.ok(room);
    }

    @GetMapping("/users/{userId}/rooms")
    public ResponseEntity<List<ChatRoom>> getUserRooms(@PathVariable String userId) {
        List<ChatRoom> rooms = chatService.getUserChatRooms(userId);
        return ResponseEntity.ok(rooms);
    }

    public static class CreateRoomRequest {
        private String name;
        private Set<String> participants;
        private String createdBy;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Set<String> getParticipants() { return participants; }
        public void setParticipants(Set<String> participants) { this.participants = participants; }

        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    }
}