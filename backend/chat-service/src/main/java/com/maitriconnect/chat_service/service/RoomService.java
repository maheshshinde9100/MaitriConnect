package com.maitriconnect.chat_service.service;

import com.maitriconnect.chat_service.dto.PageResponse;
import com.maitriconnect.chat_service.dto.RoomRequest;
import com.maitriconnect.chat_service.dto.RoomResponse;
import com.maitriconnect.chat_service.exception.BadRequestException;
import com.maitriconnect.chat_service.exception.ResourceNotFoundException;
import com.maitriconnect.chat_service.exception.UnauthorizedException;
import com.maitriconnect.chat_service.model.Message;
import com.maitriconnect.chat_service.model.Room;
import com.maitriconnect.chat_service.repository.MessageRepository;
import com.maitriconnect.chat_service.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {
    
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final MessageService messageService;
    private final KafkaProducerService kafkaProducerService;
    
    @Value("${chat.pagination.default-page-size:50}")
    private int defaultPageSize;
    
    @Value("${chat.pagination.max-page-size:100}")
    private int maxPageSize;
    
    @Transactional
    public RoomResponse createRoom(RoomRequest request, String userId, String userName) {
        log.info("User {} creating room: {}", userId, request.getName());
        
        // For direct messages, check if room already exists
        if (request.getType() == Room.RoomType.DIRECT) {
            if (request.getMemberIds() == null || request.getMemberIds().size() != 1) {
                throw new BadRequestException("Direct message room must have exactly one other member");
            }
            
            String otherUserId = request.getMemberIds().get(0);
            Optional<Room> existingRoom = roomRepository.findDirectRoomBetweenUsers(userId, otherUserId);
            
            if (existingRoom.isPresent()) {
                log.info("Direct room already exists between users: {} and {}", userId, otherUserId);
                return mapToResponse(existingRoom.get(), userId);
            }
        }
        
        // Create room
        Room room = Room.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .createdBy(userId)
                .avatarUrl(request.getAvatarUrl())
                .memberIds(new ArrayList<>())
                .adminIds(new ArrayList<>())
                .build();
        
        // Add creator as member and admin
        room.getMemberIds().add(userId);
        room.getAdminIds().add(userId);
        
        // Add other members
        if (request.getMemberIds() != null) {
            request.getMemberIds().forEach(memberId -> {
                if (!room.getMemberIds().contains(memberId)) {
                    room.getMemberIds().add(memberId);
                }
            });
        }
        
        // Set user IDs for direct messages
        if (request.getType() == Room.RoomType.DIRECT) {
            room.setUser1Id(userId);
            room.setUser2Id(request.getMemberIds().get(0));
        }
        
        // Set room settings
        if (request.getSettings() != null) {
            Room.RoomSettings settings = Room.RoomSettings.builder()
                    .allowMemberInvites(request.getSettings().getAllowMemberInvites() != null ? 
                            request.getSettings().getAllowMemberInvites() : true)
                    .allowFileSharing(request.getSettings().getAllowFileSharing() != null ? 
                            request.getSettings().getAllowFileSharing() : true)
                    .muteNotifications(request.getSettings().getMuteNotifications() != null ? 
                            request.getSettings().getMuteNotifications() : false)
                    .maxMembers(request.getSettings().getMaxMembers() != null ? 
                            request.getSettings().getMaxMembers() : 100)
                    .build();
            room.setSettings(settings);
        }
        
        Room savedRoom = roomRepository.save(room);
        
        // Publish event
        kafkaProducerService.publishRoomCreated(userId, savedRoom.getId());
        
        log.info("Room created successfully: {}", savedRoom.getId());
        return mapToResponse(savedRoom, userId);
    }
    
    public RoomResponse getRoomById(String roomId, String userId) {
        log.debug("Getting room: {} for user: {}", roomId, userId);
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        
        if (!room.getMemberIds().contains(userId)) {
            throw new UnauthorizedException("You are not a member of this room");
        }
        
        return mapToResponse(room, userId);
    }
    
    public PageResponse<RoomResponse> getUserRooms(String userId, int page, int size) {
        log.debug("Getting rooms for user: {}", userId);
        
        size = Math.min(size, maxPageSize);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastMessageAt"));
        Page<Room> roomPage = roomRepository.findByMemberId(userId, pageable);
        
        return mapToPageResponse(roomPage, userId);
    }
    
    public PageResponse<RoomResponse> searchRooms(String searchText, String userId, int page, int size) {
        log.debug("Searching rooms for user: {} with text: {}", userId, searchText);
        
        size = Math.min(size, maxPageSize);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastMessageAt"));
        Page<Room> roomPage = roomRepository.searchRoomsByName(searchText, userId, pageable);
        
        return mapToPageResponse(roomPage, userId);
    }
    
    @Transactional
    public RoomResponse updateRoom(String roomId, RoomRequest request, String userId) {
        log.info("User {} updating room: {}", userId, roomId);
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        
        if (!room.getAdminIds().contains(userId)) {
            throw new UnauthorizedException("Only room admins can update the room");
        }
        
        if (request.getName() != null) {
            room.setName(request.getName());
        }
        if (request.getDescription() != null) {
            room.setDescription(request.getDescription());
        }
        if (request.getAvatarUrl() != null) {
            room.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getSettings() != null) {
            Room.RoomSettings settings = room.getSettings();
            if (request.getSettings().getAllowMemberInvites() != null) {
                settings.setAllowMemberInvites(request.getSettings().getAllowMemberInvites());
            }
            if (request.getSettings().getAllowFileSharing() != null) {
                settings.setAllowFileSharing(request.getSettings().getAllowFileSharing());
            }
            if (request.getSettings().getMuteNotifications() != null) {
                settings.setMuteNotifications(request.getSettings().getMuteNotifications());
            }
            if (request.getSettings().getMaxMembers() != null) {
                settings.setMaxMembers(request.getSettings().getMaxMembers());
            }
            room.setSettings(settings);
        }
        
        room.setUpdatedAt(LocalDateTime.now());
        Room updatedRoom = roomRepository.save(room);
        
        log.info("Room updated successfully: {}", roomId);
        return mapToResponse(updatedRoom, userId);
    }
    
    @Transactional
    public void deleteRoom(String roomId, String userId) {
        log.info("User {} deleting room: {}", userId, roomId);
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        
        if (!room.getCreatedBy().equals(userId)) {
            throw new UnauthorizedException("Only the room creator can delete the room");
        }
        
        room.setIsActive(false);
        room.setUpdatedAt(LocalDateTime.now());
        roomRepository.save(room);
        
        log.info("Room deleted successfully: {}", roomId);
    }
    
    @Transactional
    public RoomResponse addMember(String roomId, String memberIdToAdd, String userId) {
        log.info("User {} adding member {} to room: {}", userId, memberIdToAdd, roomId);
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        
        // Check permissions
        if (!room.getSettings().getAllowMemberInvites() && !room.getAdminIds().contains(userId)) {
            throw new UnauthorizedException("Only admins can add members to this room");
        }
        
        if (!room.getMemberIds().contains(userId)) {
            throw new UnauthorizedException("You are not a member of this room");
        }
        
        if (room.getMemberIds().contains(memberIdToAdd)) {
            throw new BadRequestException("User is already a member of this room");
        }
        
        if (room.getMemberIds().size() >= room.getSettings().getMaxMembers()) {
            throw new BadRequestException("Room has reached maximum member capacity");
        }
        
        room.getMemberIds().add(memberIdToAdd);
        room.setUpdatedAt(LocalDateTime.now());
        Room updatedRoom = roomRepository.save(room);
        
        // Publish event
        kafkaProducerService.publishMemberJoined(memberIdToAdd, roomId);
        
        log.info("Member added successfully to room: {}", roomId);
        return mapToResponse(updatedRoom, userId);
    }
    
    @Transactional
    public RoomResponse removeMember(String roomId, String memberIdToRemove, String userId) {
        log.info("User {} removing member {} from room: {}", userId, memberIdToRemove, roomId);
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        
        // Check permissions
        if (!room.getAdminIds().contains(userId) && !userId.equals(memberIdToRemove)) {
            throw new UnauthorizedException("Only admins can remove other members");
        }
        
        if (!room.getMemberIds().contains(memberIdToRemove)) {
            throw new BadRequestException("User is not a member of this room");
        }
        
        room.getMemberIds().remove(memberIdToRemove);
        room.getAdminIds().remove(memberIdToRemove);
        room.setUpdatedAt(LocalDateTime.now());
        Room updatedRoom = roomRepository.save(room);
        
        // Publish event
        kafkaProducerService.publishMemberLeft(memberIdToRemove, roomId);
        
        log.info("Member removed successfully from room: {}", roomId);
        return mapToResponse(updatedRoom, userId);
    }
    
    @Transactional
    public RoomResponse leaveRoom(String roomId, String userId) {
        log.info("User {} leaving room: {}", userId, roomId);
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        
        if (!room.getMemberIds().contains(userId)) {
            throw new BadRequestException("You are not a member of this room");
        }
        
        if (room.getType() == Room.RoomType.DIRECT) {
            throw new BadRequestException("Cannot leave a direct message room");
        }
        
        room.getMemberIds().remove(userId);
        room.getAdminIds().remove(userId);
        
        // If creator leaves, assign a new admin
        if (room.getCreatedBy().equals(userId) && !room.getMemberIds().isEmpty()) {
            String newAdmin = room.getMemberIds().get(0);
            if (!room.getAdminIds().contains(newAdmin)) {
                room.getAdminIds().add(newAdmin);
            }
            room.setCreatedBy(newAdmin);
        }
        
        room.setUpdatedAt(LocalDateTime.now());
        Room updatedRoom = roomRepository.save(room);
        
        // Publish event
        kafkaProducerService.publishMemberLeft(userId, roomId);
        
        log.info("User left room successfully: {}", roomId);
        return mapToResponse(updatedRoom, userId);
    }
    
    private RoomResponse mapToResponse(Room room, String currentUserId) {
        Message lastMessage = messageRepository.findFirstByRoomIdAndIsDeletedFalseOrderByCreatedAtDesc(room.getId());
        Long unreadCount = messageService.getUnreadMessageCount(room.getId(), currentUserId);
        
        RoomResponse response = RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .type(room.getType())
                .createdBy(room.getCreatedBy())
                .memberIds(room.getMemberIds())
                .adminIds(room.getAdminIds())
                .avatarUrl(room.getAvatarUrl())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .lastMessageAt(room.getLastMessageAt())
                .isActive(room.getIsActive())
                .settings(room.getSettings())
                .memberCount(room.getMemberIds().size())
                .unreadCount(unreadCount.intValue())
                .build();
        
        if (lastMessage != null) {
            response.setLastMessage(messageService.mapToResponse(lastMessage));
        }
        
        return response;
    }
    
    private PageResponse<RoomResponse> mapToPageResponse(Page<Room> page, String currentUserId) {
        List<RoomResponse> content = page.getContent().stream()
                .map(room -> mapToResponse(room, currentUserId))
                .collect(Collectors.toList());
        
        return PageResponse.<RoomResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
