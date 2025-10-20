package com.maitriconnect.chat_service.security;

import com.maitriconnect.chat_service.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    
    private final JwtTokenProvider tokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
            message, StompHeaderAccessor.class);
        
        if (accessor == null) {
            return message;
        }

        // Handle CONNECT and SUBSCRIBE messages
        if (StompCommand.CONNECT.equals(accessor.getCommand()) ||
            StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            
            // Get the token from the headers
            String token = getToken(accessor);
            
            if (token != null && tokenProvider.validateToken(token)) {
                // Extract user info from token
                String username = tokenProvider.getUsernameFromToken(token);
                List<String> roles = tokenProvider.getRolesFromToken(token);
                
                if (username != null) {
                    // Create authentication
                    UserDetails userDetails = createUserDetails(username, roles);
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    
                    // Set the authentication in the context
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    accessor.setUser(auth);
                    
                    log.debug("WebSocket user authenticated: {}", username);
                }
            } else if (!isPublicDestination(accessor.getDestination())) {
                log.warn("WebSocket authentication failed for token: {}", token);
                throw new SecurityException("Authentication failed");
            }
        }
        
        return message;
    }
    
    private String getToken(StompHeaderAccessor accessor) {
        List<String> authHeaders = accessor.getNativeHeader(TOKEN_HEADER);
        if (authHeaders == null || authHeaders.isEmpty()) {
            return null;
        }
        
        String authHeader = authHeaders.get(0);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        
        return null;
    }
    
    private boolean isPublicDestination(String destination) {
        // Define public endpoints that don't require authentication
        return destination != null && (
            destination.startsWith("/topic/public/") ||
            destination.startsWith("/app/public/")
        );
    }
    
    private UserDetails createUserDetails(String username, List<String> roles) {
        return new User(
            username,
            "", // Password is not needed after authentication
            roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList()
        );
    }
}
