package com.maitriconnect.chat_service.dto;

import java.util.Set;

public class CreateRoomRequest {
    private String name;
    private Set<String> participants;
    private String createdBy;
    private String type;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<String> getParticipants() { return participants; }
    public void setParticipants(Set<String> participants) { this.participants = participants; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}