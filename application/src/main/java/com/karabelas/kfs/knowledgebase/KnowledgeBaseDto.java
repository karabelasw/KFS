package com.karabelas.kfs.knowledgebase;

import java.time.LocalDateTime;

/**
 * Public DTO. ownerUsername is resolved via UserService, same pattern
 * as EntryDto's createdByUsername/modifiedByUsername — this package
 * never touches the User entity directly.
 */
public class KnowledgeBaseDto {
    private Long id;
    private String name;
    private Long ownerId;
    private String ownerUsername;
    private boolean isDefault;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
