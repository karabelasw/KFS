package com.karabelas.kfs.node;

import com.karabelas.kfs.common.ArchivedAuditable;

/**
 * Mock association entity (composite key: entryId + nodeId).
 * Lives here, not in entry — reordering-within-a-Node is a Node-side
 * behavior (see ADR-0007). References Entry only by id.
 *
 * Extends ArchivedAuditable: entry_node carries archived_at/archived_by
 * in the schema, unlike its sibling association table entry_tag, which
 * does not. This lets a filing be archived (e.g. "unfiled but history
 * kept") independently of the Entry or Node it connects.
 */
class EntryNode extends ArchivedAuditable {
    private EntryNodeId id; // composite key: entryId + nodeId
    private int displayOrder;
}
