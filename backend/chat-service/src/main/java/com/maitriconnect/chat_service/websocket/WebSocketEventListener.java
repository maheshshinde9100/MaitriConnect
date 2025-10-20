package com.maitriconnect.chat_service.websocket;

import com.maitriconnect.chat_service.service.OnlineStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final OnlineStatusService onlineStatusService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        Principal principal = (Principal) event.getUser();
        if (principal != null) {
            String userId = principal.getName();
            log.info("User connected: {}", userId);
            
            // Update online status
            onlineStatusService.userConnected(userId);
            
            // Notify others in the same rooms
            onlineStatusService.getUserRooms(userId).forEach(roomId -> 
                messagingTemplate.convertAndSend(
                    "/topic/room/" + roomId + "/presence",
                    new UserPresenceEvent(userId, true)
                )
            );
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        Principal principal = (Principal) event.getUser();
        if (principal != null) {
            String userId = principal.getName();
            log.info("User disconnected: {}", userId);
            
            // Get rooms before marking as offline
            var rooms = onlineStatusService.getUserRooms(userId);
            
            // Update online status
            onlineStatusService.userDisconnected(userId);
            
            // Notify others in the same rooms
            rooms.forEach(roomId -> 
                messagingTemplate.convertAndSend(
                    "/topic/room/" + roomId + "/presence",
                    new UserPresenceEvent(userId, false)
                )
            );
        }
    }

    @EventListener
    public void handleSubscription(SessionSubscribeEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String destination = headers.getDestination();
        Principal principal = event.getUser();
        
        if (principal != null && destination != null) {
            String userId = principal.getName();
            
            // Handle room subscriptions
            if (destination.startsWith("/user/queue/")) {
                log.debug("User {} subscribed to private queue: {}", userId, destination);
            } else if (destination.startsWith("/topic/room/")) {
                String roomId = destination.replace("/topic/room/", "").split("/")[0];
                log.debug("User {} subscribed to room: {}", userId, roomId);
                
                // Update room presence
                onlineStatusService.userJoinedRoom(userId, roomId);
            }
        }
    }
    
    public record UserPresenceEvent(String userId, boolean online) {}
}
