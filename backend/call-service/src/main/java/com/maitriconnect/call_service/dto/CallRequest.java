package com.maitriconnect.call_service.dto;

import com.maitriconnect.call_service.model.Call;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallRequest {
    
    @NotBlank(message = "Receiver ID is required")
    private String receiverId;
    
    @NotNull(message = "Call type is required")
    private Call.CallType type;
    
    private String roomId;  // For group calls
}
