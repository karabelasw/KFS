package com.karabelas.kfs.knowledgebase;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SKELETON — {@link KnowledgeBaseServiceImpl} currently has no
 * methods. See {@code NodeServiceImplTest} for the rationale; this
 * file reserves the package/class name and lists anticipated coverage.
 *
 * Anticipated cases, per established design principles:
 *   - auto-provisioning the default/INBOX KB for a new user (the
 *     "in the default auto-provisioned KB, not yet deliberately
 *     filed" INBOX semantics depend on exactly one KB per user having
 *     isDefault = true).
 *   - a user cannot end up with zero or multiple default KBs.
 *   - KnowledgeBase acts as a true tree root — no phantom root Node
 *     is created alongside it.
 *   - KnowledgeBaseAccess (deferred sharing model, ADR-0008) — likely
 *     stays untested here until that model is un-deferred; note that
 *     explicitly if/when this class grows real methods.
 */
@ExtendWith(MockitoExtension.class)
class KnowledgeBaseServiceImplTest {

    @Test
    @Disabled("KnowledgeBaseService has no methods yet — nothing to test until the JPA/service implementation milestone")
    void placeholder() {
        // Intentionally empty. Replace with real cases once
        // KnowledgeBaseService methods are designed.
    }
}
