package com.maitriconnect.chat_service.controller;

import com.maitriconnect.chat_service.dto.ReactionRequest;
import com.maitriconnect.chat_service.model.ChatMessage;
import com.maitriconnect.chat_service.model.MessageReaction;
import com.maitriconnect.chat_service.service.ChatService;
import com.maitriconnect.chat_service.service.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ReactionService reactionService;

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

    @MessageMapping("/chat.delivered")
    public void markAsDelivered(@Payload ChatMessage chatMessage) {
        String roomId = chatMessage.getChatRoomId();
        String userId = chatMessage.getSenderId();
        
        chatService.markRoomMessagesAsDelivered(roomId, userId);
        
        // Notify sender that messages have been delivered
        ChatMessage deliveryReceipt = new ChatMessage();
        deliveryReceipt.setType(ChatMessage.MessageType.DELIVERED);
        deliveryReceipt.setSenderId(userId);
        deliveryReceipt.setChatRoomId(roomId);
        
        messagingTemplate.convertAndSend("/topic/room." + roomId, deliveryReceipt);
    }

    @MessageMapping("/chat.read")
    public void markAsRead(@Payload ChatMessage chatMessage) {
        String roomId = chatMessage.getChatRoomId();
        String userId = chatMessage.getSenderId();
        
        chatService.markRoomMessagesAsRead(roomId, userId);
        
        // Notify sender that messages have been read
        ChatMessage readReceipt = new ChatMessage();
        readReceipt.setType(ChatMessage.MessageType.READ);
        readReceipt.setSenderId(userId);
        readReceipt.setChatRoomId(roomId);
        
        messagingTemplate.convertAndSend("/topic/room." + roomId, readReceipt);
    }

    @MessageMapping("/chat.reaction")
    public void handleReaction(@Payload ReactionRequest reactionRequest) {
        try {
            // Toggle reaction (add if not exists, remove if exists)
            reactionService.toggleReaction(
                reactionRequest.getMessageId(),
                reactionRequest.getUserId(),
                reactionRequest.getUsername(),
                reactionRequest.getEmoji()
            );
            
            // Get updated reaction counts
            Map<String, Integer> reactionCounts = reactionService.getReactionCounts(reactionRequest.getMessageId());
            
            // Broadcast reaction update to all users in the room
            Map<String, Object> reactionUpdate = new HashMap<>();
            reactionUpdate.put("type", "REACTION");
            reactionUpdate.put("messageId", reactionRequest.getMessageId());
            reactionUpdate.put("userId", reactionRequest.getUserId());
            reactionUpdate.put("emoji", reactionRequest.getEmoji());
            reactionUpdate.put("action", reactionRequest.getAction());
            reactionUpdate.put("reactions", reactionCounts);
            
            // We need to get the chatRoomId from the message
            // For now, we'll broadcast to a general reactions topic
            messagingTemplate.convertAndSend("/topic/reactions/" + reactionRequest.getMessageId(), reactionUpdate);
            
        } catch (Exception e) {
            System.err.println("Error handling reaction: " + e.getMessage());
        }
    }
}