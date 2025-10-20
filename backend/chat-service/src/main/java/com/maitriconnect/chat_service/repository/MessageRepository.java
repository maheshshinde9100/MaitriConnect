package com.maitriconnect.chat_service.repository;

import com.maitriconnect.chat_service.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    
    // Find messages by room with pagination
    Page<Message> findByRoomIdAndIsDeletedFalseOrderByCreatedAtDesc(String roomId, Pageable pageable);
    
    // Find messages by room and sender
    Page<Message> findByRoomIdAndSenderIdAndIsDeletedFalseOrderByCreatedAtDesc(
            String roomId, String senderId, Pageable pageable);
    
    // Find messages by room after a specific timestamp
    List<Message> findByRoomIdAndCreatedAtAfterAndIsDeletedFalseOrderByCreatedAtAsc(
            String roomId, LocalDateTime after);
    
    // Find messages by room before a specific timestamp (for loading older messages)
    Page<Message> findByRoomIdAndCreatedAtBeforeAndIsDeletedFalseOrderByCreatedAtDesc(
            String roomId, LocalDateTime before, Pageable pageable);
    
    // Search messages by content
    @Query("{ 'roomId': ?0, 'content': { $regex: ?1, $options: 'i' }, 'isDeleted': false }")
    Page<Message> searchMessagesByContent(String roomId, String searchText, Pageable pageable);
    
    // Find messages by type
    Page<Message> findByRoomIdAndTypeAndIsDeletedFalseOrderByCreatedAtDesc(
            String roomId, Message.MessageType type, Pageable pageable);
    
    // Count unread messages for a user in a room
    @Query(value = "{ 'roomId': ?0, 'senderId': { $ne: ?1 }, 'readReceipts.userId': { $ne: ?1 }, 'isDeleted': false }", count = true)
    Long countUnreadMessages(String roomId, String userId);
    
    // Find last message in a room
    Message findFirstByRoomIdAndIsDeletedFalseOrderByCreatedAtDesc(String roomId);
    
    // Delete all messages in a room
    void deleteByRoomId(String roomId);
    
    // Find messages with media
    @Query("{ 'roomId': ?0, 'type': { $in: ['IMAGE', 'VIDEO', 'FILE', 'AUDIO'] }, 'isDeleted': false }")
    Page<Message> findMediaMessages(String roomId, Pageable pageable);
}
