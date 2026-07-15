package com.karabelas.kfs.node;

import com.karabelas.kfs.common.Auditable;

/**
 * Mock entity. Package-private. Self-referencing (parentNodeId).
 * Organizes Entries — does not own them.
 */
class Node extends Auditable {
    private Long id;
    private Long knowledgeBaseId;
    private String name;
    private Long parentNodeId;
    private int displayOrder;
}
