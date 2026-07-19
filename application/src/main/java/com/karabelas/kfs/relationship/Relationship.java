package com.karabelas.kfs.relationship;

import com.karabelas.kfs.common.ArchivedAuditable;

/**
 * Mock entity. Package-private. Peer graph connecting two Entries
 * (sourceEntryId -> targetEntryId), typed via relationshipTypeId.
 */
class Relationship extends ArchivedAuditable {
    private Long id;
    private Long sourceEntryId;
    private Long targetEntryId;
    private Long relationshipTypeId;
    private String notes;
}
