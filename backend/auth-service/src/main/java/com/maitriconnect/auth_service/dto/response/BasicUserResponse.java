package com.maitriconnect.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasicUserResponse {
    
    private String id;
    private String username;
    private String displayName;
    private String avatarUrl;
    private String status;
}
