package com.karabelas.kfs.integration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * TEMPLATE for feature-level integration tests — the layer above
 * per-class unit tests, exercising a behavior that spans package
 * boundaries end-to-end (controller -> service -> repository, across
 * two or more of the 8 feature packages).
 *
 * This is where the cross-cutting design decisions actually get
 * validated — they only mean something when tested through a
 * realistic flow, not against an isolated class:
 *   - entry.source_id NOT NULL
 *   - entry.status_id as the sole lifecycle authority
 *   - INBOX semantics (default KB, not yet deliberately filed)
 *   - entry_node as a first-class many-to-many (ADR-0007), including
 *     display_order reordering
 *   - KB-scoped tag namespace isolation (ADR-0010)
 *
 * This file lives in its own `integration` package (not inside
 * `entry` or `node`) because it deliberately depends on both public
 * seams (EntryService, NodeService) rather than reaching into either
 * package's internals — exactly the dependency direction the
 * package-by-feature convention is meant to enforce.
 *
 * NOT RUNNABLE YET: needs a real Spring context (application beans
 * uncommented/active), a test datasource (Testcontainers MySQL
 * 8.0.16+, consistent with EntryRepositoryTest), and EntryService /
 * NodeService to have real methods. Copy this shape for the other
 * cross-package scenarios (KB auto-provisioning + INBOX filing,
 * tagging + KB-scoped autocomplete, relationship creation validating
 * both entry ids) once each is ready to test.
 */
@SpringBootTest
@ActiveProfiles("test")
class EntryFilingIntegrationTest {

    @Test
    @Disabled("Needs active Spring wiring + real Node/Entry service methods before this can run")
    void filingAnEntryIntoANode_movesItOutOfInbox() {
        // Sketch of the eventual flow:
        //
        // 1. Create a user -> triggers default/INBOX KB auto-provisioning
        //    (KnowledgeBaseService)
        // 2. Create an Entry with no nodeId via EntryService -> assert
        //    it shows up as "in INBOX" (i.e. in the default KB, no
        //    entry_node row exists for it)
        // 3. Create a Node in that same KB via NodeService
        // 4. File the Entry into the Node (NodeService or a dedicated
        //    filing endpoint) -> assert an entry_node row now exists
        //    with the correct displayOrder
        // 5. Assert the Entry is no longer considered "INBOX" — filed
        //    status is derived from entry_node presence, not a flag
        //    on Entry itself
    }

    @Test
    @Disabled("Needs active Spring wiring + real Entry service methods before this can run")
    void creatingAnEntry_alwaysRequiresASourceId() {
        // Sketch: attempt to create an Entry via the public API/service
        // without a sourceId and assert the request is rejected with a
        // clear validation error, not a raw DB constraint failure —
        // pins down entry.source_id NOT NULL at the service boundary,
        // not just the schema.
    }
}
