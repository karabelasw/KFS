package com.karabelas.kfs.tag;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Association entity (composite key: entryId + tagId). Lives here,
 * not in entry — tagging is applied by Tag onto Entry (ADR-0010).
 * References Entry only by id. No archive columns (unlike its
 * sibling entry_node) — a tag is either applied or it isn't.
 */
@Entity
@Table(name = "entry_tag")
class EntryTag {

    @EmbeddedId
    private EntryTagId id;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    EntryTag() {
    }

    EntryTag(Long entryId, Long tagId, LocalDateTime addedAt, Long createdBy) {
        this.id = new EntryTagId(entryId, tagId);
        this.addedAt = addedAt;
        this.createdBy = createdBy;
    }

    public EntryTagId getId() {
        return id;
    }

    public void setId(EntryTagId id) {
        this.id = id;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}
