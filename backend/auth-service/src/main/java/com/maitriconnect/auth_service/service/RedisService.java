package com.maitriconnect.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public void setValue(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
    
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }
    
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
    
    public void incrementValue(String key) {
        redisTemplate.opsForValue().increment(key);
    }
    
    public void setExpire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }
    
    // User profile caching
    public void cacheUserProfile(String userId, Object userProfile) {
        setValue("user:" + userId + ":profile", userProfile, 1, TimeUnit.HOURS);
    }
    
    public Object getCachedUserProfile(String userId) {
        return getValue("user:" + userId + ":profile");
    }
    
    public void invalidateUserProfile(String userId) {
        deleteValue("user:" + userId + ":profile");
    }
    
    // Token blacklisting
    public void blacklistToken(String token, long expirationTime) {
        setValue("token:" + token, "blacklisted", expirationTime, TimeUnit.MILLISECONDS);
    }
    
    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(hasKey("token:" + token));
    }
    
    // Rate limiting
    public void incrementRateLimit(String userId, String endpoint) {
        String key = "rate_limit:" + userId + ":" + endpoint;
        incrementValue(key);
        setExpire(key, 1, TimeUnit.MINUTES);
    }
    
    public Long getRateLimitCount(String userId, String endpoint) {
        String key = "rate_limit:" + userId + ":" + endpoint;
        Object value = getValue(key);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }
}
