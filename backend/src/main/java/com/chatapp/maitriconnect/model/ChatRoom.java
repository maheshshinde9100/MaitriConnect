package com.chatapp.maitriconnect.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Document(collection = "chat_rooms")
public class ChatRoom {
    @Id
    private String id;

    private String name;
    private String description;
    private RoomType type = RoomType.DIRECT;

    @Indexed
    private String createdBy;

    @Field("created_at")
    private Instant createdAt = Instant.now();

    @Indexed
    @Field("last_activity")
    private Instant lastActivity = Instant.now();

    private List<RoomMember> members = new ArrayList<>();
    private List<String> adminIds = new ArrayList<>();
    private RoomSettings settings = new RoomSettings();
    private String avatarUrl;

    // Helper methods
    public void addMember(RoomMember member) {
        if (members.stream().noneMatch(m -> m.getUserId().equals(member.getUserId()))) {
            members.add(member);
            updateLastActivity();
        }
    }

    public void removeMember(String userId) {
        members.removeIf(member -> member.getUserId().equals(userId));
        adminIds.remove(userId);
        updateLastActivity();
    }

    public void addAdmin(String userId) {
        if (!adminIds.contains(userId)) {
            adminIds.add(userId);
        }
    }

    public boolean isUserAdmin(String userId) {
        return adminIds.contains(userId);
    }

    public void updateLastActivity() {
        this.lastActivity = Instant.now();
    }

    public RoomMember getMember(String userId) {
        return members.stream()
                .filter(member -> member.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}

// Room Type Enum
enum RoomType {
    DIRECT, GROUP, CHANNEL, BROADCAST
}

// Room Member Embedded Document
@Data
@AllArgsConstructor
class RoomMember {
    private String userId;
    private MemberRole role = MemberRole.MEMBER;
    private Instant joinedAt = Instant.now();
    private Instant lastSeen = Instant.now();
    private Instant lastRead = Instant.now();
    private String nickname;

    public void updateLastSeen() {
        this.lastSeen = Instant.now();
    }

    public void updateLastRead() {
        this.lastRead = Instant.now();
    }
}

// Member Role Enum
enum MemberRole {
    OWNER, ADMIN, MODERATOR, MEMBER
}

// Room Settings Embedded Document
@Data
class RoomSettings {
    private boolean readReceiptsEnabled = true;
    private boolean typingIndicatorsEnabled = true;
    private boolean fileSharingEnabled = true;
    private boolean memberAddingEnabled = true;
    private int maxMembers = 100;
    private String roomColor = "#3498db";
    private boolean encrypted = false;
    private List<String> allowedFileTypes = List.of("jpg", "png", "pdf", "doc", "txt");
    private int maxFileSizeMB = 10;
}