package com.karabelas.kfs.tag;

/**
 * Mock association entity (composite key: entryId + tagId).
 * Lives here, not in entry — tagging is applied by Tag onto Entry
 * (ADR-0010). References Entry only by id.
 */
class EntryTag {
    private EntryTagId id; // composite key: entryId + tagId
    private java.time.LocalDateTime addedAt;
}
