package com.karabelas.kfs.node;

import com.karabelas.kfs.common.ArchivedAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Table;

/**
 * Association entity (composite key: entryId + nodeId). Lives here,
 * not in entry — reordering-within-a-Node is a Node-side behavior
 * (see ADR-0007). References Entry only by id.
 *
 * Extends ArchivedAuditable: entry_node carries archived_at/archived_by
 * in the schema, unlike its sibling association table entry_tag, which
 * does not. This lets a filing be archived (e.g. "unfiled but history
 * kept") independently of the Entry or Node it connects.
 */
@Entity
@Table(name = "entry_node")
class EntryNode extends ArchivedAuditable {

    @EmbeddedId
    private EntryNodeId id;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    EntryNode() {
    }

    EntryNode(Long entryId, Long nodeId, int displayOrder) {
        this.id = new EntryNodeId(entryId, nodeId);
        this.displayOrder = displayOrder;
    }

    public EntryNodeId getId() {
        return id;
    }

    public void setId(EntryNodeId id) {
        this.id = id;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
