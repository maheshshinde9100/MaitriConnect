package com.maitriconnect.auth_service.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String userId;
    private String username;

    public AuthResponse(String token, String userId, String username) {
        this.token = token;
        this.userId = userId;
        this.username = username;
    }
}