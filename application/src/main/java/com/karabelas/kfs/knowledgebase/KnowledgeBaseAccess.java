package com.karabelas.kfs.knowledgebase;

import com.karabelas.kfs.common.Auditable;

/**
 * Mock entity. Package-private. Placeholder for the deferred sharing/
 * access model (knowledge_base_access table).
 */
class KnowledgeBaseAccess extends Auditable {
    private Long id;
    private Long knowledgeBaseId;
    private Long userId;
    private Long accessLevelId;
}
