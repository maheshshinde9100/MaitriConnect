package com.maitriconnect.auth_service.controller;

import com.maitriconnect.auth_service.dto.request.UpdateRolesRequest;
import com.maitriconnect.auth_service.dto.response.AdminStatsResponse;
import com.maitriconnect.auth_service.dto.response.MessageResponse;
import com.maitriconnect.auth_service.dto.response.PageResponse;
import com.maitriconnect.auth_service.dto.response.UserResponse;
import com.maitriconnect.auth_service.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/users")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        PageResponse<UserResponse> response = adminService.getAllUsers(page, size, search);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<UserResponse> updateUserRoles(
            @PathVariable String userId,
            @Valid @RequestBody UpdateRolesRequest request) {
        UserResponse response = adminService.updateUserRoles(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable String userId) {
        MessageResponse response = adminService.deleteUser(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<AdminStatsResponse> getStatistics() {
        AdminStatsResponse response = adminService.getStatistics();
        return ResponseEntity.ok(response);
    }
}
