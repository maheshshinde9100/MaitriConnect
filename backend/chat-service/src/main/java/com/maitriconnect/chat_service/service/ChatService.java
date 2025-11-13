package com.maitriconnect.chat_service.service;

import com.maitriconnect.chat_service.model.ChatMessage;
import com.maitriconnect.chat_service.model.ChatRoom;
import com.maitriconnect.chat_service.repository.ChatMessageRepository;
import com.maitriconnect.chat_service.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessagesByRoom(String roomId) {
        return chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(roomId);
    }

    public ChatRoom createChatRoom(String name, Set<String> participants, String createdBy) {
        ChatRoom room = new ChatRoom(name, participants, createdBy);
        return chatRoomRepository.save(room);
    }

    public List<ChatRoom> getUserChatRooms(String userId) {
        return chatRoomRepository.findByParticipantsContaining(userId);
    }
}