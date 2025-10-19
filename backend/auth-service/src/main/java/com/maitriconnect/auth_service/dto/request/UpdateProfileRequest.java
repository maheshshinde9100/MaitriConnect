package com.maitriconnect.auth_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    
    private String displayName;
    private String avatarUrl;
    private String status;
    private Map<String, Object> settings;
}
