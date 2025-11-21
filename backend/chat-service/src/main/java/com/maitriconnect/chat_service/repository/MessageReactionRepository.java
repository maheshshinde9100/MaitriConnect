package com.maitriconnect.chat_service.repository;

import com.maitriconnect.chat_service.model.MessageReaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageReactionRepository extends MongoRepository<MessageReaction, String> {
    List<MessageReaction> findByMessageId(String messageId);
    Optional<MessageReaction> findByMessageIdAndUserIdAndEmoji(String messageId, String userId, String emoji);
    void deleteByMessageIdAndUserIdAndEmoji(String messageId, String userId, String emoji);
    long countByMessageIdAndEmoji(String messageId, String emoji);
}
