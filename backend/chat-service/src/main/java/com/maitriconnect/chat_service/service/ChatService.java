package com.maitriconnect.chat_service.service;

import com.maitriconnect.chat_service.dto.ChatRoomResponse;
import com.maitriconnect.chat_service.model.ChatMessage;
import com.maitriconnect.chat_service.model.ChatRoom;
import com.maitriconnect.chat_service.repository.ChatMessageRepository;
import com.maitriconnect.chat_service.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        // Update last message in chat room
        ChatRoom room = chatRoomRepository.findById(message.getChatRoomId()).orElse(null);
        if (room != null) {
            room.setLastMessage(message.getContent());
            room.setLastMessageTime(LocalDateTime.now());
            chatRoomRepository.save(room);
        }
        
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessagesByRoom(String roomId) {
        return chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(roomId);
    }

    public ChatRoom createChatRoom(String name, Set<String> participants, String createdBy, ChatRoom.ChatRoomType type) {
        ChatRoom room = new ChatRoom(name, participants, createdBy, type);
        return chatRoomRepository.save(room);
    }

    public List<ChatRoom> getUserChatRooms(String userId) {
        return chatRoomRepository.findByParticipantsContaining(userId);
    }

    // Get or create direct chat room between two users
    public ChatRoom getOrCreateDirectChatRoom(String user1, String user2) {
        Optional<ChatRoom> existingRoom = chatRoomRepository.findDirectChatRoom(user1, user2);
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        Set<String> participants = new HashSet<>();
        participants.add(user1);
        participants.add(user2);
        
        // Create room name from usernames
        String roomName = user1 + "_" + user2;
        
        return createChatRoom(roomName, participants, user1, ChatRoom.ChatRoomType.DIRECT);
    }

    // Get direct messages between two users
    public List<ChatMessage> getDirectMessages(String user1, String user2) {
        return chatMessageRepository.findDirectMessages(user1, user2);
    }

    // Mark messages as delivered or seen
    public void updateMessageStatus(String messageId, ChatMessage.MessageStatus status) {
        ChatMessage message = chatMessageRepository.findById(messageId).orElse(null);
        if (message != null) {
            message.setStatus(status);
            chatMessageRepository.save(message);
        }
    }

    // Mark all messages in a room as seen by a user
    public void markMessagesAsSeen(String roomId, String userId) {
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(roomId);
        for (ChatMessage message : messages) {
            if (!message.getSenderId().equals(userId)) {
                message.setStatus(ChatMessage.MessageStatus.READ);
                message.setReadAt(LocalDateTime.now());
                chatMessageRepository.save(message);
            }
        }
    }

    // Get unread message count for a user
    public Long getUnreadMessageCount(String userId) {
        return chatMessageRepository.countByReceiverIdAndStatus(userId, ChatMessage.MessageStatus.SENT);
    }

    // Convert ChatRoom to ChatRoomResponse
    public ChatRoomResponse convertToChatRoomResponse(ChatRoom room, String currentUserId) {
        ChatRoomResponse response = new ChatRoomResponse();
        response.setId(room.getId());
        response.setName(room.getName());
        response.setType(room.getType().toString());
        response.setLastMessage(room.getLastMessage());
        response.setLastMessageTime(room.getLastMessageTime());
        response.setCreatedAt(room.getCreatedAt());
        response.setParticipants(room.getParticipants());
        
        // For direct chats, find the other participant
        if (room.getType() == ChatRoom.ChatRoomType.DIRECT) {
            String otherUser = room.getParticipants().stream()
                    .filter(user -> !user.equals(currentUserId))
                    .findFirst()
                    .orElse(null);
            response.setOtherParticipant(otherUser);
        }
        
        return response;
    }

    public List<ChatRoomResponse> getUserChatRoomsWithDetails(String userId) {
        List<ChatRoom> rooms = getUserChatRooms(userId);
        return rooms.stream()
                .map(room -> convertToChatRoomResponse(room, userId))
                .collect(Collectors.toList());
    }

    // Enhanced read receipts with timestamps
    public void markMessageAsDelivered(String messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId).orElse(null);
        if (message != null && message.getStatus() == ChatMessage.MessageStatus.SENT) {
            message.setStatus(ChatMessage.MessageStatus.DELIVERED);
            message.setDeliveredAt(LocalDateTime.now());
            chatMessageRepository.save(message);
        }
    }

    public void markMessageAsRead(String messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId).orElse(null);
        if (message != null) {
            message.setStatus(ChatMessage.MessageStatus.READ);
            message.setReadAt(LocalDateTime.now());
            chatMessageRepository.save(message);
        }
    }

    // Batch mark messages as delivered/read
    public void markRoomMessagesAsDelivered(String roomId, String userId) {
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(roomId);
        for (ChatMessage message : messages) {
            if (!message.getSenderId().equals(userId) && 
                message.getStatus() == ChatMessage.MessageStatus.SENT) {
                message.setStatus(ChatMessage.MessageStatus.DELIVERED);
                message.setDeliveredAt(LocalDateTime.now());
                chatMessageRepository.save(message);
            }
        }
    }

    public void markRoomMessagesAsRead(String roomId, String userId) {
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(roomId);
        for (ChatMessage message : messages) {
            if (!message.getSenderId().equals(userId)) {
                message.setStatus(ChatMessage.MessageStatus.READ);
                message.setReadAt(LocalDateTime.now());
                chatMessageRepository.save(message);
            }
        }
    }

    // Add file attachment to message
    public void addFileAttachment(String messageId, String fileId) {
        ChatMessage message = chatMessageRepository.findById(messageId).orElse(null);
        if (message != null) {
            message.getFileAttachments().add(fileId);
            chatMessageRepository.save(message);
        }
    }
}