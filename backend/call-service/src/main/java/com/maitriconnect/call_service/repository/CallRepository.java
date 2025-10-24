package com.maitriconnect.call_service.repository;

import com.maitriconnect.call_service.model.Call;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CallRepository extends MongoRepository<Call, String> {
    
    Page<Call> findByCallerIdOrReceiverIdOrderByCreatedAtDesc(String callerId, String receiverId, Pageable pageable);
    
    Optional<Call> findByIdAndStatus(String id, Call.CallStatus status);
    
    List<Call> findByReceiverIdAndStatusIn(String receiverId, List<Call.CallStatus> statuses);
    
    @Query("{ $or: [ { 'callerId': ?0 }, { 'receiverId': ?0 } ], 'createdAt': { $gte: ?1 } }")
    List<Call> findRecentCallsByUserId(String userId, LocalDateTime since);
    
    Long countByCallerIdAndStatus(String callerId, Call.CallStatus status);
    
    Long countByReceiverIdAndStatus(String receiverId, Call.CallStatus status);
}
