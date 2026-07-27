package com.karabelas.kfs.tag;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/** Composite key for EntryTag. Package-private. */
@Embeddable
class EntryTagId implements Serializable {

    private Long entryId;
    private Long tagId;

    EntryTagId() {
    }

    EntryTagId(Long entryId, Long tagId) {
        this.entryId = entryId;
        this.tagId = tagId;
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntryTagId)) return false;
        EntryTagId that = (EntryTagId) o;
        return Objects.equals(entryId, that.entryId) && Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryId, tagId);
    }
}
