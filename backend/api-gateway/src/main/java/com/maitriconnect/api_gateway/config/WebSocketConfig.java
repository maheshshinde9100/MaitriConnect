package com.maitriconnect.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class WebSocketConfig {
    
    @Bean
    @Order(-1) // Higher priority than properties-based routes
    public RouteLocator websocketRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            // WebSocket route for chat service - MUST use ws:// scheme
            .route("websocket-route-override", r -> r
                .path("/ws/**")
                .and()
                .header("Upgrade", "websocket") // Only match WebSocket upgrade requests
                .uri("lb:ws://chat-service")
            )
            .build();
    }
}