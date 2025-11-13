package com.maitriconnect.auth_service.dto;

public class LoginRequest {
    private String username;
    private String password;

    // Default constructor
    public LoginRequest() {}

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}