package com.maitriconnect.chat_service.service;

import com.maitriconnect.chat_service.model.ChatMessage;
import com.maitriconnect.chat_service.model.MessageReaction;
import com.maitriconnect.chat_service.repository.ChatMessageRepository;
import com.maitriconnect.chat_service.repository.MessageReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReactionService {

    @Autowired
    private MessageReactionRepository reactionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Transactional
    public MessageReaction addReaction(String messageId, String userId, String username, String emoji) {
        // Check if user already reacted with this emoji
        Optional<MessageReaction> existing = reactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, userId, emoji);
        
        if (existing.isPresent()) {
            return existing.get(); // Already reacted
        }

        // Create new reaction
        MessageReaction reaction = new MessageReaction(messageId, userId, username, emoji);
        reaction = reactionRepository.save(reaction);

        // Update message reactions count
        updateMessageReactionCounts(messageId);

        return reaction;
    }

    @Transactional
    public void removeReaction(String messageId, String userId, String emoji) {
        Optional<MessageReaction> reaction = reactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, userId, emoji);
        
        if (reaction.isPresent()) {
            reactionRepository.delete(reaction.get());
            updateMessageReactionCounts(messageId);
        }
    }

    public List<MessageReaction> getMessageReactions(String messageId) {
        return reactionRepository.findByMessageId(messageId);
    }

    public Map<String, Integer> getReactionCounts(String messageId) {
        List<MessageReaction> reactions = reactionRepository.findByMessageId(messageId);
        Map<String, Integer> counts = new HashMap<>();
        
        for (MessageReaction reaction : reactions) {
            counts.put(reaction.getEmoji(), counts.getOrDefault(reaction.getEmoji(), 0) + 1);
        }
        
        return counts;
    }

    private void updateMessageReactionCounts(String messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId).orElse(null);
        if (message != null) {
            Map<String, Integer> counts = getReactionCounts(messageId);
            message.setReactions(counts);
            chatMessageRepository.save(message);
        }
    }

    @Transactional
    public void toggleReaction(String messageId, String userId, String username, String emoji) {
        Optional<MessageReaction> existing = reactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, userId, emoji);
        
        if (existing.isPresent()) {
            removeReaction(messageId, userId, emoji);
        } else {
            addReaction(messageId, userId, username, emoji);
        }
    }
}
