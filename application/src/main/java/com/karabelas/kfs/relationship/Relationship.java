package com.karabelas.kfs.relationship;

import com.karabelas.kfs.common.Auditable;

/**
 * Mock entity. Package-private. Peer graph connecting two Entries
 * (sourceEntryId -> targetEntryId), typed via relationshipTypeId.
 */
class Relationship extends Auditable {
    private Long id;
    private Long sourceEntryId;
    private Long targetEntryId;
    private Long relationshipTypeId;
    private String notes;
}
