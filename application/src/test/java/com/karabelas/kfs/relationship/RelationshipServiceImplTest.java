package com.karabelas.kfs.relationship;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SKELETON — {@link RelationshipServiceImpl} currently has no
 * methods. See {@code NodeServiceImplTest} for the rationale; this
 * file reserves the package/class name and lists anticipated coverage.
 *
 * Anticipated cases, per the Relationship entity's role as the typed
 * peer graph connecting Entries (separate from the Node hierarchy):
 *   - creating a Relationship validates both sourceEntryId and
 *     targetEntryId exist (via EntryService, the inward dependency
 *     already noted in the commented-out import).
 *   - self-referencing relationships (sourceEntryId == targetEntryId)
 *     — decide and test whether these are allowed or rejected.
 *   - relationshipTypeId drives any directional/symmetric semantics,
 *     if that distinction exists in the reference data.
 *   - querying relationships for an Entry returns both directions
 *     (as source and as target) if that's the intended API shape.
 */
@ExtendWith(MockitoExtension.class)
class RelationshipServiceImplTest {

    @Test
    @Disabled("RelationshipService has no methods yet — nothing to test until the JPA/service implementation milestone")
    void placeholder() {
        // Intentionally empty. Replace with real cases once
        // RelationshipService methods are designed.
    }
}
