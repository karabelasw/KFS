package com.karabelas.kfs.tag;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SKELETON — {@link TagServiceImpl} currently has no methods. See
 * {@code NodeServiceImplTest} for the rationale; this file reserves
 * the package/class name and lists anticipated coverage.
 *
 * Anticipated cases, per ADR-0010 (KB-scoped tags):
 *   - tag creation enforces unique(knowledgeBaseId, name) — verify
 *     the service surfaces a clean error (not a raw constraint
 *     violation) on duplicate-within-KB.
 *   - the same tag name in two different KBs is NOT a conflict —
 *     namespace isolation is the whole point of KB-scoping.
 *   - autocomplete/lookup queries are scoped to a single
 *     knowledgeBaseId and never leak tags from other KBs.
 *   - tagging/untagging an Entry via EntryTag, including the
 *     composite-key (entryId, tagId) uniqueness.
 */
@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Test
    @Disabled("TagService has no methods yet — nothing to test until the JPA/service implementation milestone")
    void placeholder() {
        // Intentionally empty. Replace with real cases once
        // TagService methods are designed.
    }
}
