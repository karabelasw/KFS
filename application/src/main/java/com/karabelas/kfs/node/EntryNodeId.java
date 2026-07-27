package com.karabelas.kfs.node;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/** Composite key for EntryNode. Package-private. */
@Embeddable
class EntryNodeId implements Serializable {

    private Long entryId;
    private Long nodeId;

    EntryNodeId() {
    }

    EntryNodeId(Long entryId, Long nodeId) {
        this.entryId = entryId;
        this.nodeId = nodeId;
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntryNodeId)) return false;
        EntryNodeId that = (EntryNodeId) o;
        return Objects.equals(entryId, that.entryId) && Objects.equals(nodeId, that.nodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryId, nodeId);
    }
}
