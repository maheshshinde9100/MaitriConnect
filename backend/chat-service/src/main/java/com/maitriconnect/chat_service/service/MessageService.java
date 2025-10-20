package com.maitriconnect.chat_service.service;

import com.maitriconnect.chat_service.dto.MessageRequest;
import com.maitriconnect.chat_service.dto.MessageResponse;
import com.maitriconnect.chat_service.dto.PageResponse;
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
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final CacheService cacheService;
    private final KafkaProducerService kafkaProducerService;
    
    @Value("${chat.pagination.default-page-size:50}")
    private int defaultPageSize;
    
    @Value("${chat.pagination.max-page-size:100}")
    private int maxPageSize;
    
    @Transactional
    public MessageResponse sendMessage(MessageRequest request, String userId, String userName) {
        log.info("User {} sending message to room: {}", userId, request.getRoomId());
        
        // Verify room exists and user is a member
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", request.getRoomId()));
        
        if (!room.getMemberIds().contains(userId)) {
            throw new UnauthorizedException("You are not a member of this room");
        }
        
        // Create message
        Message message = Message.builder()
                .roomId(request.getRoomId())
                .senderId(userId)
                .senderName(userName)
                .content(request.getContent())
                .type(request.getType())
                .status(Message.MessageStatus.SENT)
                .replyToMessageId(request.getReplyToMessageId())
                .build();
        
        // Add media metadata if present
        if (request.getMediaMetadata() != null) {
            message.setMediaMetadata(Message.MediaMetadata.builder()
                    .fileName(request.getMediaMetadata().getFileName())
                    .fileUrl(request.getMediaMetadata().getFileUrl())
                    .fileType(request.getMediaMetadata().getFileType())
                    .fileSize(request.getMediaMetadata().getFileSize())
                    .width(request.getMediaMetadata().getWidth())
                    .height(request.getMediaMetadata().getHeight())
                    .duration(request.getMediaMetadata().getDuration())
                    .thumbnailUrl(request.getMediaMetadata().getThumbnailUrl())
                    .build());
        }
        
        Message savedMessage = messageRepository.save(message);
        
        // Update room's last message timestamp
        room.setLastMessageAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());
        roomRepository.save(room);
        
        // Add to cache
        cacheService.addMessageToCache(request.getRoomId(), savedMessage);
        
        // Publish event
        kafkaProducerService.publishMessageSent(userId, request.getRoomId(), savedMessage.getId());
        
        log.info("Message sent successfully: {}", savedMessage.getId());
        return mapToResponse(savedMessage);
    }
    
    public PageResponse<MessageResponse> getRoomMessages(String roomId, String userId, 
                                                         int page, int size) {
        log.debug("Getting messages for room: {} by user: {}", roomId, userId);
        
        // Verify room exists and user is a member
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        
        if (!room.getMemberIds().contains(userId)) {
            throw new UnauthorizedException("You are not a member of this room");
        }
        
        // Check cache first for recent messages
        if (page == 0 && size <= 50) {
            List<Message> cachedMessages = cacheService.getCachedRecentMessages(roomId);
            if (cachedMessages != null && !cachedMessages.isEmpty()) {
                log.debug("Returning {} cached messages for room: {}", cachedMessages.size(), roomId);
                List<MessageResponse> responses = cachedMessages.stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList());
                
                return PageResponse.<MessageResponse>builder()
                        .content(responses)
                        .pageNumber(0)
                        .pageSize(responses.size())
                        .totalElements((long) responses.size())
                        .totalPages(1)
                        .first(true)
                        .last(true)
                        .empty(responses.isEmpty())
                        .build();
            }
        }
        
        // Fetch from database
        size = Math.min(size, maxPageSize);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Message> messagePage = messageRepository
                .findByRoomIdAndIsDeletedFalseOrderByCreatedAtDesc(roomId, pageable);
        
        // Cache first page
        if (page == 0) {
            cacheService.cacheRecentMessages(roomId, messagePage.getContent());
        }
        
        return mapToPageResponse(messagePage);
    }
    
    public PageResponse<MessageResponse> searchMessages(String roomId, String searchText, 
                                                        String userId, int page, int size) {
        log.debug("Searching messages in room: {} with text: {}", roomId, searchText);
        
        // Verify room exists and user is a member
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        
        if (!room.getMemberIds().contains(userId)) {
            throw new UnauthorizedException("You are not a member of this room");
        }
        
        size = Math.min(size, maxPageSize);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Message> messagePage = messageRepository.searchMessagesByContent(roomId, searchText, pageable);
        
        return mapToPageResponse(messagePage);
    }
    
    @Transactional
    public MessageResponse updateMessage(String messageId, String content, String userId) {
        log.info("User {} updating message: {}", userId, messageId);
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));
        
        if (!message.getSenderId().equals(userId)) {
            throw new UnauthorizedException("You can only edit your own messages");
        }
        
        if (message.getIsDeleted()) {
            throw new BadRequestException("Cannot edit a deleted message");
        }
        
        message.setContent(content);
        message.setIsEdited(true);
        message.setEditedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        
        Message updatedMessage = messageRepository.save(message);
        
        // Invalidate cache
        cacheService.invalidateRoomMessagesCache(message.getRoomId());
        
        // Publish event
        kafkaProducerService.publishMessageEdited(userId, message.getRoomId(), messageId);
        
        log.info("Message updated successfully: {}", messageId);
        return mapToResponse(updatedMessage);
    }
    
    @Transactional
    public void deleteMessage(String messageId, String userId) {
        log.info("User {} deleting message: {}", userId, messageId);
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));
        
        if (!message.getSenderId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own messages");
        }
        
        message.setIsDeleted(true);
        message.setContent("[Message deleted]");
        message.setUpdatedAt(LocalDateTime.now());
        
        messageRepository.save(message);
        
        // Invalidate cache
        cacheService.invalidateRoomMessagesCache(message.getRoomId());
        
        // Publish event
        kafkaProducerService.publishMessageDeleted(userId, message.getRoomId(), messageId);
        
        log.info("Message deleted successfully: {}", messageId);
    }
    
    @Transactional
    public MessageResponse addReaction(String messageId, String emoji, String userId, String userName) {
        log.info("User {} adding reaction {} to message: {}", userId, emoji, messageId);
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));
        
        // Check if user already reacted with this emoji
        boolean alreadyReacted = message.getReactions().stream()
                .anyMatch(r -> r.getUserId().equals(userId) && r.getEmoji().equals(emoji));
        
        if (alreadyReacted) {
            throw new BadRequestException("You have already reacted with this emoji");
        }
        
        Message.Reaction reaction = Message.Reaction.builder()
                .userId(userId)
                .userName(userName)
                .emoji(emoji)
                .build();
        
        message.getReactions().add(reaction);
        message.setUpdatedAt(LocalDateTime.now());
        
        Message updatedMessage = messageRepository.save(message);
        
        // Invalidate cache
        cacheService.invalidateRoomMessagesCache(message.getRoomId());
        
        log.info("Reaction added successfully to message: {}", messageId);
        return mapToResponse(updatedMessage);
    }
    
    @Transactional
    public MessageResponse removeReaction(String messageId, String emoji, String userId) {
        log.info("User {} removing reaction {} from message: {}", userId, emoji, messageId);
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));
        
        message.getReactions().removeIf(r -> r.getUserId().equals(userId) && r.getEmoji().equals(emoji));
        message.setUpdatedAt(LocalDateTime.now());
        
        Message updatedMessage = messageRepository.save(message);
        
        // Invalidate cache
        cacheService.invalidateRoomMessagesCache(message.getRoomId());
        
        log.info("Reaction removed successfully from message: {}", messageId);
        return mapToResponse(updatedMessage);
    }
    
    @Transactional
    public void markMessageAsRead(String messageId, String userId, String userName) {
        log.debug("User {} marking message as read: {}", userId, messageId);
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageId));
        
        // Don't add read receipt if user is the sender
        if (message.getSenderId().equals(userId)) {
            return;
        }
        
        // Check if already marked as read
        boolean alreadyRead = message.getReadReceipts().stream()
                .anyMatch(r -> r.getUserId().equals(userId));
        
        if (!alreadyRead) {
            Message.ReadReceipt receipt = Message.ReadReceipt.builder()
                    .userId(userId)
                    .userName(userName)
                    .build();
            
            message.getReadReceipts().add(receipt);
            message.setUpdatedAt(LocalDateTime.now());
            
            messageRepository.save(message);
            
            // Invalidate cache
            cacheService.invalidateRoomMessagesCache(message.getRoomId());
            
            log.debug("Message marked as read: {}", messageId);
        }
    }
    
    @Transactional
    public void markRoomMessagesAsRead(String roomId, String userId, String userName) {
        log.info("User {} marking all messages as read in room: {}", userId, roomId);
        
        // Verify room exists and user is a member
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        
        if (!room.getMemberIds().contains(userId)) {
            throw new UnauthorizedException("You are not a member of this room");
        }
        
        // Get all unread messages
        Pageable pageable = PageRequest.of(0, 100);
        Page<Message> messages = messageRepository
                .findByRoomIdAndIsDeletedFalseOrderByCreatedAtDesc(roomId, pageable);
        
        messages.getContent().forEach(message -> {
            if (!message.getSenderId().equals(userId)) {
                boolean alreadyRead = message.getReadReceipts().stream()
                        .anyMatch(r -> r.getUserId().equals(userId));
                
                if (!alreadyRead) {
                    Message.ReadReceipt receipt = Message.ReadReceipt.builder()
                            .userId(userId)
                            .userName(userName)
                            .build();
                    message.getReadReceipts().add(receipt);
                    message.setUpdatedAt(LocalDateTime.now());
                }
            }
        });
        
        messageRepository.saveAll(messages.getContent());
        
        // Invalidate cache
        cacheService.invalidateRoomMessagesCache(roomId);
        
        log.info("All messages marked as read in room: {}", roomId);
    }
    
    public Long getUnreadMessageCount(String roomId, String userId) {
        return messageRepository.countUnreadMessages(roomId, userId);
    }
    
    private MessageResponse mapToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .roomId(message.getRoomId())
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .content(message.getContent())
                .type(message.getType())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .editedAt(message.getEditedAt())
                .isEdited(message.getIsEdited())
                .isDeleted(message.getIsDeleted())
                .replyToMessageId(message.getReplyToMessageId())
                .mediaMetadata(message.getMediaMetadata())
                .reactions(message.getReactions())
                .readReceipts(message.getReadReceipts())
                .reactionCount(message.getReactions().size())
                .readCount(message.getReadReceipts().size())
                .build();
    }
    
    private PageResponse<MessageResponse> mapToPageResponse(Page<Message> page) {
        List<MessageResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<MessageResponse>builder()
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
