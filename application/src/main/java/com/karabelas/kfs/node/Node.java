package com.karabelas.kfs.node;

import com.karabelas.kfs.common.ArchivedAuditable;

/**
 * Mock entity. Package-private. Self-referencing (parentNodeId).
 * Organizes Entries — does not own them.
 */
class Node extends ArchivedAuditable {
    private Long id;
    private Long knowledgeBaseId;
    private String name;
    private Long parentNodeId;
    private int displayOrder;
}
