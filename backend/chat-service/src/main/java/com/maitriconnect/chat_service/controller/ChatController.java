package com.maitriconnect.chat_service.controller;

import com.maitriconnect.chat_service.model.ChatMessage;
import com.maitriconnect.chat_service.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        System.out.println("Received message via WebSocket: " + chatMessage.getContent());
        
        // Save message to database
        ChatMessage savedMessage = chatService.saveMessage(chatMessage);
        
        // Broadcast to all users in the room
        String destination = "/topic/room." + chatMessage.getChatRoomId();
        System.out.println("Broadcasting to: " + destination);
        messagingTemplate.convertAndSend(destination, savedMessage);
        
        // Also send to specific user if it's a direct message
        if (chatMessage.getReceiverId() != null && !chatMessage.getReceiverId().isEmpty()) {
            String userDestination = "/queue/messages/" + chatMessage.getReceiverId();
            System.out.println("Sending to user queue: " + userDestination);
            messagingTemplate.convertAndSendToUser(chatMessage.getReceiverId(), "/queue/messages", savedMessage);
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderId());
        
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        String destination = "/topic/room." + chatMessage.getChatRoomId();
        messagingTemplate.convertAndSend(destination, chatMessage);
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload ChatMessage chatMessage) {
        chatMessage.setType(ChatMessage.MessageType.TYPING);
        messagingTemplate.convertAndSend("/topic/room." + chatMessage.getChatRoomId(), chatMessage);
    }

    @MessageMapping("/chat.stopTyping")
    public void stopTyping(@Payload ChatMessage chatMessage) {
        chatMessage.setType(ChatMessage.MessageType.STOP_TYPING);
        messagingTemplate.convertAndSend("/topic/room." + chatMessage.getChatRoomId(), chatMessage);
    }

    @MessageMapping("/chat.seen")
    public void markAsSeen(@Payload ChatMessage chatMessage) {
        String roomId = chatMessage.getChatRoomId();
        String userId = chatMessage.getSenderId();
        
        chatService.markMessagesAsSeen(roomId, userId);
        
        // Notify others in the room that messages have been seen
        chatMessage.setType(ChatMessage.MessageType.SEEN);
        messagingTemplate.convertAndSend("/topic/room." + roomId, chatMessage);
    }
}