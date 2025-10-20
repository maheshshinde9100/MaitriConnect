package com.maitriconnect.chat_service.service;

import com.maitriconnect.chat_service.model.Message;
import com.maitriconnect.chat_service.model.OnlineStatus;
import com.maitriconnect.chat_service.model.TypingIndicator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Value("${chat.cache.recent-messages.ttl:3600}")
    private long recentMessagesTtl;
    
    @Value("${chat.cache.online-users.ttl:300}")
    private long onlineUsersTtl;
    
    @Value("${chat.cache.typing-indicators.ttl:10}")
    private long typingIndicatorsTtl;
    
    private static final String RECENT_MESSAGES_KEY = "chat:room:%s:recent-messages";
    private static final String ONLINE_STATUS_KEY = "chat:user:%s:online-status";
    private static final String TYPING_INDICATOR_KEY = "chat:room:%s:typing:%s";
    private static final String ROOM_TYPING_KEY = "chat:room:%s:typing-users";
    
    // Recent Messages Cache
    public void cacheRecentMessages(String roomId, List<Message> messages) {
        String key = String.format(RECENT_MESSAGES_KEY, roomId);
        try {
            redisTemplate.delete(key);
            if (!messages.isEmpty()) {
                redisTemplate.opsForList().rightPushAll(key, messages.toArray());
                redisTemplate.expire(key, recentMessagesTtl, TimeUnit.SECONDS);
            }
            log.debug("Cached {} recent messages for room: {}", messages.size(), roomId);
        } catch (Exception e) {
            log.error("Error caching recent messages for room: {}", roomId, e);
        }
    }
    
    public void addMessageToCache(String roomId, Message message) {
        String key = String.format(RECENT_MESSAGES_KEY, roomId);
        try {
            redisTemplate.opsForList().rightPush(key, message);
            redisTemplate.expire(key, recentMessagesTtl, TimeUnit.SECONDS);
            
            // Keep only last 50 messages in cache
            Long size = redisTemplate.opsForList().size(key);
            if (size != null && size > 50) {
                redisTemplate.opsForList().trim(key, size - 50, -1);
            }
            log.debug("Added message to cache for room: {}", roomId);
        } catch (Exception e) {
            log.error("Error adding message to cache for room: {}", roomId, e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Message> getCachedRecentMessages(String roomId) {
        String key = String.format(RECENT_MESSAGES_KEY, roomId);
        try {
            List<Object> messages = redisTemplate.opsForList().range(key, 0, -1);
            if (messages != null) {
                return messages.stream()
                        .map(obj -> (Message) obj)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error getting cached messages for room: {}", roomId, e);
        }
        return null;
    }
    
    public void invalidateRoomMessagesCache(String roomId) {
        String key = String.format(RECENT_MESSAGES_KEY, roomId);
        redisTemplate.delete(key);
        log.debug("Invalidated messages cache for room: {}", roomId);
    }
    
    // Online Status Cache
    public void setOnlineStatus(String userId, OnlineStatus status) {
        String key = String.format(ONLINE_STATUS_KEY, userId);
        try {
            redisTemplate.opsForValue().set(key, status, Duration.ofSeconds(onlineUsersTtl));
            log.debug("Set online status for user: {}", userId);
        } catch (Exception e) {
            log.error("Error setting online status for user: {}", userId, e);
        }
    }
    
    public OnlineStatus getOnlineStatus(String userId) {
        String key = String.format(ONLINE_STATUS_KEY, userId);
        try {
            return (OnlineStatus) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Error getting online status for user: {}", userId, e);
            return null;
        }
    }
    
    public void removeOnlineStatus(String userId) {
        String key = String.format(ONLINE_STATUS_KEY, userId);
        redisTemplate.delete(key);
        log.debug("Removed online status for user: {}", userId);
    }
    
    // Typing Indicators Cache
    public void setTypingIndicator(String roomId, String userId, TypingIndicator indicator) {
        String key = String.format(TYPING_INDICATOR_KEY, roomId, userId);
        String roomTypingKey = String.format(ROOM_TYPING_KEY, roomId);
        try {
            if (indicator.getIsTyping()) {
                redisTemplate.opsForValue().set(key, indicator, Duration.ofSeconds(typingIndicatorsTtl));
                redisTemplate.opsForSet().add(roomTypingKey, userId);
                redisTemplate.expire(roomTypingKey, typingIndicatorsTtl, TimeUnit.SECONDS);
            } else {
                redisTemplate.delete(key);
                redisTemplate.opsForSet().remove(roomTypingKey, userId);
            }
            log.debug("Set typing indicator for user {} in room: {}", userId, roomId);
        } catch (Exception e) {
            log.error("Error setting typing indicator for user {} in room: {}", userId, roomId, e);
        }
    }
    
    public Set<String> getTypingUsers(String roomId) {
        String roomTypingKey = String.format(ROOM_TYPING_KEY, roomId);
        try {
            Set<Object> members = redisTemplate.opsForSet().members(roomTypingKey);
            if (members != null) {
                return members.stream()
                        .map(Object::toString)
                        .collect(Collectors.toSet());
            }
        } catch (Exception e) {
            log.error("Error getting typing users for room: {}", roomId, e);
        }
        return Set.of();
    }
    
    public void clearTypingIndicator(String roomId, String userId) {
        String key = String.format(TYPING_INDICATOR_KEY, roomId, userId);
        String roomTypingKey = String.format(ROOM_TYPING_KEY, roomId);
        redisTemplate.delete(key);
        redisTemplate.opsForSet().remove(roomTypingKey, userId);
        log.debug("Cleared typing indicator for user {} in room: {}", userId, roomId);
    }
}
