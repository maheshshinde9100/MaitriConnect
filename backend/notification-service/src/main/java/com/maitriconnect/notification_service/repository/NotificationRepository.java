package com.maitriconnect.notification_service.repository;

import com.maitriconnect.notification_service.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    
    Page<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(String userId, Boolean isRead, Pageable pageable);
    
    Long countByUserIdAndIsReadFalse(String userId);
    
    List<Notification> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(String userId, LocalDateTime after);
    
    @Query("{ 'userId': ?0, 'isRead': false, 'createdAt': { $gte: ?1 } }")
    List<Notification> findUnreadNotificationsSince(String userId, LocalDateTime since);
    
    void deleteByUserIdAndCreatedAtBefore(String userId, LocalDateTime before);
}
