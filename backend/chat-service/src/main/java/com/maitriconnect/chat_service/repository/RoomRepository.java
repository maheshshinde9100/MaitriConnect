package com.maitriconnect.chat_service.repository;

import com.maitriconnect.chat_service.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends MongoRepository<Room, String> {
    
    // Find rooms where user is a member
    @Query("{ 'memberIds': ?0, 'isActive': true }")
    Page<Room> findByMemberId(String userId, Pageable pageable);
    
    // Find rooms where user is a member (list)
    @Query("{ 'memberIds': ?0, 'isActive': true }")
    List<Room> findByMemberIdList(String userId);
    
    // Find direct message room between two users
    @Query("{ 'type': 'DIRECT', $or: [ { 'user1Id': ?0, 'user2Id': ?1 }, { 'user1Id': ?1, 'user2Id': ?0 } ], 'isActive': true }")
    Optional<Room> findDirectRoomBetweenUsers(String userId1, String userId2);
    
    // Find rooms by type
    Page<Room> findByTypeAndIsActiveTrue(Room.RoomType type, Pageable pageable);
    
    // Search rooms by name
    @Query("{ 'name': { $regex: ?0, $options: 'i' }, 'memberIds': ?1, 'isActive': true }")
    Page<Room> searchRoomsByName(String searchText, String userId, Pageable pageable);
    
    // Find rooms created by user
    Page<Room> findByCreatedByAndIsActiveTrue(String userId, Pageable pageable);
    
    // Find rooms where user is admin
    @Query("{ 'adminIds': ?0, 'isActive': true }")
    Page<Room> findByAdminId(String userId, Pageable pageable);
    
    // Check if user is member of room
    @Query(value = "{ '_id': ?0, 'memberIds': ?1 }", exists = true)
    boolean isUserMemberOfRoom(String roomId, String userId);
    
    // Check if user is admin of room
    @Query(value = "{ '_id': ?0, 'adminIds': ?1 }", exists = true)
    boolean isUserAdminOfRoom(String roomId, String userId);
    
    // Find public channels
    @Query("{ 'type': 'CHANNEL', 'isActive': true }")
    Page<Room> findPublicChannels(Pageable pageable);
}
