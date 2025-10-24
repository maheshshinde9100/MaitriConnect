package com.maitriconnect.call_service.websocket;

import com.maitriconnect.call_service.dto.SignalingMessage;
import com.maitriconnect.call_service.service.CallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CallSignalingController {

    private final CallService callService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/call/signal")
    public void handleSignaling(@Payload SignalingMessage message) {
        log.debug("Received signaling message: {} for call: {}", message.getType(), message.getCallId());
        
        // Process signaling
        callService.handleSignaling(message);
        
        // Forward to receiver
        String destination = "/topic/user/" + message.getReceiverId() + "/calls";
        messagingTemplate.convertAndSend(destination, message);
    }
}
