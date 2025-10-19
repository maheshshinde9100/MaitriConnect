package com.maitriconnect.auth_service.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRolesRequest {
    
    @NotEmpty(message = "Roles cannot be empty")
    private Set<String> roles;
}
