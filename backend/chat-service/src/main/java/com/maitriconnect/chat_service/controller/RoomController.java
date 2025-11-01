package com.maitriconnect.chat_service.controller;

import com.maitriconnect.chat_service.dto.ApiResponse;
import com.maitriconnect.chat_service.dto.PageResponse;
import com.maitriconnect.chat_service.dto.RoomRequest;
import com.maitriconnect.chat_service.dto.RoomResponse;
import com.maitriconnect.chat_service.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    
    private final RoomService roomService;
    
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    
    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        return details != null ? (String) details.get("displayName") : "Unknown";
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(
            @Valid @RequestBody RoomRequest request) {
        log.info("POST /api/rooms - Create room: {}", request.getName());
        RoomResponse response = roomService.createRoom(request, getCurrentUserId(), getCurrentUserName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room created successfully", response));
    }
    
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable String roomId) {
        log.info("GET /api/rooms/{} - Get room details", roomId);
        RoomResponse response = roomService.getRoomById(roomId, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RoomResponse>>> getUserRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/rooms - Get user rooms (page: {}, size: {})", page, size);
        PageResponse<RoomResponse> response = roomService.getUserRooms(getCurrentUserId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<RoomResponse>>> searchRooms(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/rooms/search - Search: '{}' (page: {}, size: {})", query, page, size);
        PageResponse<RoomResponse> response = roomService.searchRooms(query, getCurrentUserId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable String roomId,
            @Valid @RequestBody RoomRequest request) {
        log.info("PUT /api/rooms/{} - Update room", roomId);
        RoomResponse response = roomService.updateRoom(roomId, request, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Room updated successfully", response));
    }
    
    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable String roomId) {
        log.info("DELETE /api/rooms/{} - Delete room", roomId);
        roomService.deleteRoom(roomId, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Room deleted successfully", null));
    }
    
    @PostMapping("/{roomId}/members")
    public ResponseEntity<ApiResponse<RoomResponse>> addMember(
            @PathVariable String roomId,
            @RequestParam String memberId) {
        log.info("POST /api/rooms/{}/members - Add member: {}", roomId, memberId);
        RoomResponse response = roomService.addMember(roomId, memberId, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Member added successfully", response));
    }
    
    @DeleteMapping("/{roomId}/members/{memberId}")
    public ResponseEntity<ApiResponse<RoomResponse>> removeMember(
            @PathVariable String roomId,
            @PathVariable String memberId) {
        log.info("DELETE /api/rooms/{}/members/{} - Remove member", roomId, memberId);
        RoomResponse response = roomService.removeMember(roomId, memberId, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Member removed successfully", response));
    }
    
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<ApiResponse<RoomResponse>> leaveRoom(@PathVariable String roomId) {
        log.info("POST /api/rooms/{}/leave - Leave room", roomId);
        RoomResponse response = roomService.leaveRoom(roomId, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Left room successfully", response));
    }
}
