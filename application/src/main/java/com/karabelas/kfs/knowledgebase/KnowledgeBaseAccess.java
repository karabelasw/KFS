package com.karabelas.kfs.knowledgebase;

import com.karabelas.kfs.common.ArchivedAuditable;

/**
 * Mock entity. Package-private. Placeholder for the deferred sharing/
 * access model (knowledge_base_access table).
 */
class KnowledgeBaseAccess extends ArchivedAuditable {
    private Long id;
    private Long knowledgeBaseId;
    private Long userId;
    private Long accessLevelId;
}
