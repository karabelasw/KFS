package com.karabelas.kfs.entry;

import com.karabelas.kfs.common.Auditable;

/**
 * Mock entity. Package-private. Foundational aggregate — deliberately
 * has NO dependency on node/tag/relationship packages. Node/Tag/
 * Relationship reference Entry by id only, never the reverse.
 */
class Entry extends Auditable {
    private Long id;
    private Long knowledgeBaseId;
    private String title;
    private String content;
    private Long statusId;   // sole lifecycle authority — no archived_at
    private Long sourceId;   // NOT NULL — every Entry declares its origin
    private Long contentTypeId;
}
