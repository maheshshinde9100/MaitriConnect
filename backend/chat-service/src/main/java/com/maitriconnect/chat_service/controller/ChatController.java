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
        ChatMessage savedMessage = chatService.saveMessage(chatMessage);
        
        // Send to specific room
        messagingTemplate.convertAndSend("/topic/room." + chatMessage.getChatRoomId(), savedMessage);
        
        // Also send to user-specific queue for notifications
        if (chatMessage.getReceiverId() != null) {
            messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiverId(), 
                "/queue/messages", 
                savedMessage
            );
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderId());
        
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        messagingTemplate.convertAndSend("/topic/room." + chatMessage.getChatRoomId(), chatMessage);
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