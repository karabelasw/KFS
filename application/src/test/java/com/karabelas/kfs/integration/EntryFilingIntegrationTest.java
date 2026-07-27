package com.karabelas.kfs.integration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.karabelas.kfs.knowledgebase.KnowledgeBaseDto;
import com.karabelas.kfs.knowledgebase.KnowledgeBaseService;
import com.karabelas.kfs.node.NodeDto;
import com.karabelas.kfs.node.NodeService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Feature-level integration test — controller -> service -> repository,
 * across package boundaries, exercising real Spring wiring and a real
 * datasource rather than mocks.
 *
 * STATUS UPDATE: KnowledgeBaseService, NodeService, and TagService are
 * no longer stubs — they have real methods now (see
 * KnowledgeBaseServiceImplTest / NodeServiceImplTest / TagServiceImplTest
 * for their unit-level coverage). What's still blocking these two
 * scenarios specifically is that EntryService only exposes
 * findById/findByKnowledgeBaseId — there is no create(EntryDto) method
 * on the public seam yet, so an Entry can't be created through
 * EntryService the way these scenarios need. That's the one piece left
 * before this file can run for real.
 *
 * Also needs: a test datasource (Testcontainers MySQL 8.0.16+,
 * consistent with EntryRepositoryTest) and an application-test
 * properties/profile wiring one up — neither exists in the project yet.
 */
@SpringBootTest
@ActiveProfiles("test")
class EntryFilingIntegrationTest {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Autowired
    private NodeService nodeService;

    @Test
    @Disabled("Blocked on EntryService.create(EntryDto) — not yet part of the public seam")
    void filingAnEntryIntoANode_movesItOutOfInbox() {
        // Sketch of the eventual flow, updated to reflect the services
        // that are now real:
        //
        // 1. KnowledgeBaseDto inbox = knowledgeBaseService.createDefaultKnowledgeBase(userId);
        // 2. EntryDto entry = entryService.create(new EntryDto with inbox.getId())
        //    -> entry is "in INBOX": in inbox's KB, no entry_node row.
        // 3. NodeDto node = nodeService.create(new NodeDto in inbox.getId());
        // 4. nodeService.fileEntry(node.getId(), entry.getId());
        //    -> assert an entry_node row now exists with displayOrder 0.
        // 5. Assert the Entry is no longer "INBOX" — filed status is
        //    derived from entry_node presence, not a flag on Entry.
    }

    @Test
    @Disabled("Blocked on EntryService.create(EntryDto) — not yet part of the public seam")
    void creatingAnEntry_alwaysRequiresASourceId() {
        // Sketch: attempt entryService.create(dto) with sourceId == null
        // and assert a clear validation error at the service boundary,
        // not a raw DataIntegrityViolationException from the DB layer —
        // pins down entry.source_id NOT NULL as a service-level contract,
        // not just a schema constraint (compare EntryRepositoryTest,
        // which pins down the DB-level version of the same rule).
    }

    @Test
    void knowledgeBaseAndNodeServices_areWiredIntoTheSpringContext() {
        // Narrower smoke test that IS runnable today (once a test
        // datasource/profile exists) — confirms the real beans wire up
        // correctly end-to-end, without depending on entry creation.
        assertThat(knowledgeBaseService).isNotNull();
        assertThat(nodeService).isNotNull();
    }
}
