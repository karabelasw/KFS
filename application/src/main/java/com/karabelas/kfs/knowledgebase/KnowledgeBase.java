package com.karabelas.kfs.knowledgebase;

import com.karabelas.kfs.common.ArchivedAuditable;

/**
 * Mock entity. Package-private. Represents the true tree root — no
 * phantom root Node required (per established design philosophy).
 */
class KnowledgeBase extends ArchivedAuditable {
    private Long id;
    private String name;
    private Long ownerId;
    private boolean isDefault;
}
