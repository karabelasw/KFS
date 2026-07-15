package com.karabelas.kfs.tag;

import com.karabelas.kfs.common.Auditable;

/**
 * Mock entity. Package-private. KB-scoped vocabulary (ADR-0010).
 * unique(knowledgeBaseId, name) enforced at the DB layer.
 */
class Tag extends Auditable {
    private Long id;
    private Long knowledgeBaseId;
    private String name;
}
