package com.chatapp.maitriconnect.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@Document(collection = "chat_messages")
public class ChatMessage {
    @Id
    private String id;

    @Indexed
    private String roomId;

    private String senderId;
    private String senderUsername;

    private MessageType type = MessageType.TEXT;
    private String content;

    @Indexed
    private Instant timestamp = Instant.now();

    private MessageStatus status = MessageStatus.SENT;

    @Field("read_by")
    private List<String> readBy = new ArrayList<>();

    private List<Reaction> reactions = new ArrayList<>();
    private List<MessageEdit> editHistory = new ArrayList<>();
    private MessageMetadata metadata = new MessageMetadata();

    private String replyToMessageId; // For reply functionality
    private List<String> mentions = new ArrayList<>(); // User mentions

    @Transient
    private UserProfile senderProfile; // Transient - not stored in DB
    // Helper methods
    public void markAsRead(String userId) {
        if (!readBy.contains(userId)) {
            readBy.add(userId);
        }
    }

    public void addReaction(String userId, String emoji) {
        // Remove existing reaction from same user
        reactions.removeIf(reaction -> reaction.getUserId().equals(userId));

        Reaction reaction = new Reaction(userId, emoji, Instant.now());
        reactions.add(reaction);
    }

    public void removeReaction(String userId, String emoji) {
        reactions.removeIf(reaction ->
                reaction.getUserId().equals(userId) && reaction.getEmoji().equals(emoji));
    }

    public void editMessage(String newContent, String editedBy) {
        // Save current content to edit history before modifying
        if (this.editHistory == null) {
            this.editHistory = new ArrayList<>();
        }

        MessageEdit edit = new MessageEdit(this.content, Instant.now(), editedBy);
        this.editHistory.add(edit);

        this.content = newContent;
        this.metadata.setEdited(true);
        this.metadata.setLastEditedAt(Instant.now());
        this.metadata.setLastEditedBy(editedBy);
    }

    public boolean isEdited() {
        return this.metadata != null && this.metadata.isEdited();
    }
}

// Message Type Enum
enum MessageType {
    TEXT, IMAGE, FILE, VIDEO, AUDIO, SYSTEM, NOTIFICATION, POLL, LOCATION
}

// Message Status Enum
enum MessageStatus {
    SENT, DELIVERED, READ, FAILED, PENDING
}

// Reaction Embedded Document
class Reaction {
    private String userId;
    private String emoji;
    private Instant timestamp;

    // Constructors
    public Reaction() {}

    public Reaction(String userId, String emoji, Instant timestamp) {
        this.userId = userId;
        this.emoji = emoji;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}

// Message Edit History Embedded Document
@Data
class MessageEdit {
    private String previousContent;
    private Instant editedAt;
    private String editedBy;

    // Constructors
    public MessageEdit() {}

    public MessageEdit(String previousContent, Instant editedAt, String editedBy) {
        this.previousContent = previousContent;
        this.editedAt = editedAt;
        this.editedBy = editedBy;
    }
}

// Message Metadata Embedded Document
@Data
class MessageMetadata {
    private boolean edited = false;
    private Instant lastEditedAt;
    private String lastEditedBy;
    private String clientType; // WEB, MOBILE, DESKTOP
    private String ipAddress;
    private String userAgent;
    private boolean deleted = false;
    private Instant deletedAt;
    private String deletedBy;
    private String encryptionKey; // For encrypted messages
}