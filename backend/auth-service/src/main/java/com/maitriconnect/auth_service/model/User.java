package com.maitriconnect.auth_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String username;
    
    @Indexed(unique = true)
    private String email;
    
    private String password;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String status = "Hey there! I'm using MaitriConnect";
    private boolean online = false;
    private LocalDateTime lastSeen;
    private boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.lastSeen = LocalDateTime.now();
    }

    public User(String username, String email, String password, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = LocalDateTime.now();
        this.lastSeen = LocalDateTime.now();
        this.active = true;
        this.status = "Hey there! I'm using MaitriConnect";
    }

}