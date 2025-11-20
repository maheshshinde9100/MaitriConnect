package com.maitriconnect.auth_service.repository;

import com.maitriconnect.auth_service.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // Search users by username or name
    @Query("{ '$or': [ " +
           "{ 'username': { '$regex': ?0, '$options': 'i' } }, " +
           "{ 'firstName': { '$regex': ?0, '$options': 'i' } }, " +
           "{ 'lastName': { '$regex': ?0, '$options': 'i' } } " +
           "] }")
    List<User> findByUsernameOrNameContaining(String searchTerm);
    
    // Find all active users
    List<User> findByActiveTrue();
    
    // Find online users
    List<User> findByOnlineTrue();
}