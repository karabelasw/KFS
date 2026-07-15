package com.karabelas.kfs.node;

/**
 * Mock association entity (composite key: entryId + nodeId).
 * Lives here, not in entry — reordering-within-a-Node is a Node-side
 * behavior (see ADR-0007). References Entry only by id.
 */
class EntryNode {
    private EntryNodeId id; // composite key: entryId + nodeId
    private int displayOrder;
}
