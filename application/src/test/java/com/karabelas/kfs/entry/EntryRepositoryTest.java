package com.karabelas.kfs.entry;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@code @DataJpaTest} slice for {@link EntryRepository} — loads only
 * the JPA layer, real database round-trip, no service/controller
 * beans.
 *
 * DATABASE CHOICE: run this against a real MySQL 8.0.16+ instance
 * (Testcontainers recommended — see spring-boot-testcontainers /
 * spring-boot-docker-compose starters), not an in-memory H2
 * "MySQL mode". The schema explicitly targets MySQL 8.0.16+ for CHECK
 * constraint enforcement; H2's MySQL compatibility mode does not
 * enforce those the same way, so a green H2 run here would be a false
 * signal once CHECK constraints land on columns like entry.status_id.
 *
 * IMPORTANT — forward-looking, not currently green: requires Entry to
 * have getters/setters, a no-args constructor, and active JPA
 * annotations (currently commented out in the mock). See
 * EntryServiceImplTest for the same caveat.
 */
@DataJpaTest
@ActiveProfiles("test")
class EntryRepositoryTest {

    @Autowired
    private EntryRepository entryRepository;

    @Test
    void findByKnowledgeBaseId_returnsOnlyEntriesInThatKnowledgeBase() {
        entryRepository.save(newEntry(10L));
        entryRepository.save(newEntry(20L));

        List<Entry> result = entryRepository.findByKnowledgeBaseId(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKnowledgeBaseId()).isEqualTo(10L);
    }

    @Test
    void findByKnowledgeBaseId_returnsEmptyList_whenKnowledgeBaseHasNoEntries() {
        List<Entry> result = entryRepository.findByKnowledgeBaseId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void save_rejectsNullSourceId_becauseSourceIdIsNotNullInSchema() {
        // Pins down the design decision that every Entry must declare
        // its origin — entry.source_id is NOT NULL.
        Entry entry = newEntry(10L);
        entry.setSourceId(null);

        assertThatThrownBy(() -> entryRepository.saveAndFlush(entry))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void save_rejectsNullKnowledgeBaseId() {
        Entry entry = newEntry(null);

        assertThatThrownBy(() -> entryRepository.saveAndFlush(entry))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private Entry newEntry(Long knowledgeBaseId) {
        Entry entry = new Entry();
        entry.setKnowledgeBaseId(knowledgeBaseId);
        entry.setStatusId(1L);
        entry.setSourceId(1L);
        entry.setContentTypeId(1L);
        entry.setCreatedBy(1L);
        entry.setCreatedAt(LocalDateTime.now());
        return entry;
    }
}
