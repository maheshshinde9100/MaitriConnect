package com.chatapp.maitriconnect.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@Document(collection = "user_profiles")
public class UserProfile {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @TextIndexed
    private String displayName;

    private String email;
    private String avatarUrl;
    private UserStatus status = UserStatus.OFFLINE;

    @Field("created_at")
    private Instant createdAt = Instant.now();

    @Field("last_seen")
    private Instant lastSeen = Instant.now();

    private ProfileSettings settings = new ProfileSettings();
    private List<String> blockedUsers = new ArrayList<>();
    private List<DeviceInfo> devices = new ArrayList<>();

    // Helper methods
    public void addBlockedUser(String username) {
        if (!blockedUsers.contains(username)) {
            blockedUsers.add(username);
        }
    }

    public void removeBlockedUser(String username) {
        blockedUsers.remove(username);
    }

    public void updateLastSeen() {
        this.lastSeen = Instant.now();
    }
}

// User Status Enum
enum UserStatus {
    ONLINE, OFFLINE, AWAY, BUSY, DO_NOT_DISTURB
}

// Profile Settings Embedded Document
@Data
class ProfileSettings {
    private boolean notificationsEnabled = true;
    private boolean soundEnabled = true;
    private boolean readReceiptsEnabled = true;
    private String theme = "light";
    private String language = "en";
    private boolean onlineStatusVisible = true;
    private boolean typingIndicatorEnabled = true;
}

// Device Info Embedded Document
@Data
@AllArgsConstructor
class DeviceInfo {
    private String deviceId;
    private String deviceType; // MOBILE, DESKTOP, TABLET
    private String userAgent;
    private String ipAddress;
    private Instant lastActive;
    private boolean isActive;
}