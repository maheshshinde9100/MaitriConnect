package com.maitriconnect.auth_service.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String status;

    public UpdateProfileRequest() {}

}