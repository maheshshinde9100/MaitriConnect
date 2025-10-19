package com.maitriconnect.auth_service.repository;

import com.maitriconnect.auth_service.model.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends MongoRepository<Session, String> {
    
    Optional<Session> findByRefreshToken(String refreshToken);
    
    Optional<Session> findByJwtToken(String jwtToken);
    
    List<Session> findByUserIdAndIsActive(String userId, boolean isActive);
    
    List<Session> findByUserId(String userId);
    
    void deleteByUserId(String userId);
    
    void deleteByUserIdAndId(String userId, String sessionId);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    
    long countByUserIdAndIsActive(String userId, boolean isActive);
}
