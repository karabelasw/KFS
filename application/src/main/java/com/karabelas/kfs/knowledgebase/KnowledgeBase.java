package com.karabelas.kfs.knowledgebase;

import com.karabelas.kfs.common.ArchivedAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Package-private. Represents the true tree root — no phantom root
 * Node required (per established design philosophy). Exactly one
 * KnowledgeBase per owner should have isDefault = true; that KB is
 * where entries land when filed with no explicit knowledgeBaseId
 * (the "INBOX" semantics).
 */
@Entity
@Table(name = "knowledge_base")
class KnowledgeBase extends ArchivedAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

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

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
