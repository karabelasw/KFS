package com.karabelas.kfs.tag;

import com.karabelas.kfs.common.ArchivedAuditable;

/**
 * Mock entity. Package-private. KB-scoped vocabulary (ADR-0010).
 * unique(knowledgeBaseId, name) enforced at the DB layer.
 */
class Tag extends ArchivedAuditable {
    private Long id;
    private Long knowledgeBaseId;
    private String name;
}
