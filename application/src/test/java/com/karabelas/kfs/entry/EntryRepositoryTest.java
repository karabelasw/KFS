package com.karabelas.kfs.entry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.ActiveProfiles;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@code @DataJpaTest} slice for {@link EntryRepository} — loads only
 * the JPA layer, real database round-trip, no service/controller
 * beans.
 *
 * DATABASE CHOICE: runs against a real MySQL 8.0.16+ instance (see
 * application-test.properties), not an in-memory H2 "MySQL mode" —
 * @AutoConfigureTestDatabase(replace = NONE) below keeps Spring from
 * swapping in an embedded database. The schema explicitly targets
 * MySQL 8.0.16+ for CHECK constraint enforcement; H2's MySQL
 * compatibility mode does not enforce those the same way, so a green
 * H2 run here would be a false signal once CHECK constraints land on
 * columns like entry.status_id.
 *
 * PARENT ROWS: entry.knowledge_base_id carries a real FK
 * (fk_entry_kb) to knowledge_base — this test can't construct a
 * KnowledgeBase directly (it's package-private, in a different
 * package, by design — see ADR-0006 no-lateral-dependencies rule), so
 * a minimal parent row is inserted via plain JDBC in @BeforeEach
 * instead of going through the entity layer.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class EntryRepositoryTest {

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long knowledgeBaseId;
    private Long otherKnowledgeBaseId;

    @BeforeEach
    void setUpParentRows() {
        Long systemUserId = jdbcTemplate.queryForObject(
                "SELECT id FROM user WHERE username = 'system'", Long.class);
        knowledgeBaseId = insertKnowledgeBase("Entry Repository Test KB", systemUserId);
        otherKnowledgeBaseId = insertKnowledgeBase("Entry Repository Test KB (other)", systemUserId);
    }

    @Test
    void findByKnowledgeBaseId_returnsOnlyEntriesInThatKnowledgeBase() {
        entryRepository.save(newEntry(knowledgeBaseId));
        entryRepository.save(newEntry(otherKnowledgeBaseId));

        List<Entry> result = entryRepository.findByKnowledgeBaseId(knowledgeBaseId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKnowledgeBaseId()).isEqualTo(knowledgeBaseId);
    }

    @Test
    void findByKnowledgeBaseId_returnsEmptyList_whenKnowledgeBaseHasNoEntries() {
        List<Entry> result = entryRepository.findByKnowledgeBaseId(otherKnowledgeBaseId);

        assertThat(result).isEmpty();
    }

    @Test
    void save_rejectsNullSourceId_becauseSourceIdIsNotNullInSchema() {
        // Pins down the design decision that every Entry must declare
        // its origin — entry.source_id is NOT NULL.
        Entry entry = newEntry(knowledgeBaseId);
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

    /** Inserts a minimal knowledge_base row via JDBC and returns its generated id. */
    private Long insertKnowledgeBase(String name, Long ownerId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO knowledge_base (name, owner_id, is_default, created_by) "
                            + "VALUES (?, ?, FALSE, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setLong(2, ownerId);
            ps.setLong(3, ownerId);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }
}