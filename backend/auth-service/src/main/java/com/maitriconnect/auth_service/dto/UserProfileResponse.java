package com.maitriconnect.auth_service.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String status;
    private boolean online;
    private String lastSeen;

    public UserProfileResponse() {}

}