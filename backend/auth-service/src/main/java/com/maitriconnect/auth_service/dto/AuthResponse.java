package com.maitriconnect.auth_service.dto;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String userId;
    private String username;

    // Default constructor
    public AuthResponse() {}

    public AuthResponse(String token, String userId, String username) {
        this.token = token;
        this.userId = userId;
        this.username = username;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}