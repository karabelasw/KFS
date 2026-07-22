package com.karabelas.kfs.node;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SKELETON — {@link NodeServiceImpl} currently has no methods (bare
 * {@code class NodeServiceImpl implements NodeService {}}), so there
 * is nothing yet to assert against. This class exists to reserve the
 * package/class name and pin down the test cases this service will
 * need once NodeService's methods are designed — do not add
 * meaningful tests until then.
 *
 * Follow the {@code EntryServiceImplTest} pattern in the entry
 * package once real methods land here: mock NodeRepository (and
 * EntryService, per the commented-out inward dependency), construct
 * NodeServiceImpl directly, assert on returned DTOs only.
 *
 * Anticipated methods, per prior design discussion and inline mock
 * comments (fill in real tests as each lands):
 *   - reorderEntries(nodeId, newOrder) — owns Node-scoped entry
 *     ordering (display_order on entry_node); entry package never
 *     needs to know this exists.
 *   - CRUD for Node itself, including the self-referencing
 *     parentNodeId tree structure.
 */
@ExtendWith(MockitoExtension.class)
class NodeServiceImplTest {

    @Test
    @Disabled("NodeService has no methods yet — nothing to test until the JPA/service implementation milestone")
    void placeholder() {
        // Intentionally empty. Replace with real cases once
        // NodeService methods are designed and NodeServiceImpl has
        // logic beyond the empty stub.
    }
}
