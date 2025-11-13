package com.maitriconnect.chat_service.repository;

import com.maitriconnect.chat_service.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChatRoomIdOrderByTimestampAsc(String chatRoomId);
    List<ChatMessage> findBySenderIdOrReceiverIdOrderByTimestampDesc(String senderId, String receiverId);
}