package com.maitriconnect.chat_service.repository;

import com.maitriconnect.chat_service.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChatRoomIdOrderByTimestampAsc(String chatRoomId);
    
    // For direct messages between two users
    @Query("{ '$or': [ " +
           "{ 'senderId': ?0, 'receiverId': ?1 }, " +
           "{ 'senderId': ?1, 'receiverId': ?0 } " +
           "] }")
    List<ChatMessage> findDirectMessages(String user1, String user2);
    
    // Find messages by sender or receiver (for user's chat history)
    List<ChatMessage> findBySenderIdOrReceiverIdOrderByTimestampDesc(String senderId, String receiverId);
    
    // Find last message in a room
    ChatMessage findFirstByChatRoomIdOrderByTimestampDesc(String chatRoomId);
    
    // Count unread messages
    Long countByReceiverIdAndStatus(String receiverId, ChatMessage.MessageStatus status);
}