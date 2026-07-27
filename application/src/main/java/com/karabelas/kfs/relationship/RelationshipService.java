package com.karabelas.kfs.relationship;

import java.util.List;

/** Public. */
public interface RelationshipService {

    RelationshipDto findById(Long id);

    /** Returns relationships where the entry appears as either source or target. */
    List<RelationshipDto> findByEntryId(Long entryId);

    /**
     * Creates a Relationship. Validates both sourceEntryId and
     * targetEntryId exist (via EntryService) and rejects
     * self-referencing relationships (sourceEntryId == targetEntryId).
     */
    RelationshipDto create(RelationshipDto dto);
}
