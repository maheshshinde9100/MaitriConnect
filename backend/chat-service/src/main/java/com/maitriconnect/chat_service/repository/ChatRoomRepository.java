package com.maitriconnect.chat_service.repository;

import com.maitriconnect.chat_service.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    List<ChatRoom> findByParticipantsContaining(String userId);
    
    // Find direct chat room between two users
    @Query("{ 'type': 'DIRECT', 'participants': { $all: [?0, ?1], $size: 2 } }")
    Optional<ChatRoom> findDirectChatRoom(String user1, String user2);
    
    // Find group rooms for a user
    List<ChatRoom> findByParticipantsContainingAndType(String userId, ChatRoom.ChatRoomType type);
}