package com.karabelas.kfs.entry;

import com.karabelas.kfs.common.ResourceNotFoundException;
import com.karabelas.kfs.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EntryServiceImpl}.
 *
 * REFERENCE TEMPLATE — this is the pattern to copy for every other
 * *ServiceImpl in the codebase: mock the repository + any collaborator
 * service (here, UserService), construct the impl directly (it's
 * package-private, so this test class must live in the same package),
 * and assert on the public DTO contract only.
 *
 * IMPORTANT — forward-looking, not currently green:
 * These tests are written against the INTENDED behavior spelled out
 * in EntryServiceImpl's inline mock comments, not the current stub
 * bodies (which return null / List.of() / Set.of()). They will not
 * compile/pass until, during the JPA entities milestone:
 *   1. Entry gains standard getters/setters (and a no-args constructor)
 *   2. EntryDto gains standard getters/setters
 *   3. EntryServiceImpl's commented-out logic is implemented for real
 * Treat this file as acceptance criteria for that work, not a
 * currently-passing suite. Once implemented, delete this note.
 */
@ExtendWith(MockitoExtension.class)
class EntryServiceImplTest {

    @Mock
    private EntryRepository entryRepository;

    @Mock
    private UserService userService;

    private EntryServiceImpl entryService;

    @BeforeEach
    void setUp() {
        entryService = new EntryServiceImpl(entryRepository, userService);
    }

    // ---- findById ----

    @Test
    void findById_returnsDtoWithResolvedUsernames() {
        Entry entry = buildEntry(1L, 10L, 100L, 200L);
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(userService.findUsernamesByIds(Set.of(100L, 200L)))
                .thenReturn(Map.of(100L, "billy", 200L, "editor2"));

        EntryDto dto = entryService.findById(1L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getKnowledgeBaseId()).isEqualTo(10L);
        assertThat(dto.getCreatedByUsername()).isEqualTo("billy");
        assertThat(dto.getModifiedByUsername()).isEqualTo("editor2");
    }

    @Test
    void findById_throwsResourceNotFoundException_whenEntryMissing() {
        when(entryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> entryService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findById_leavesModifiedByUsernameNull_whenEntryNeverModified() {
        // createdBy set, modifiedBy null — entry.modifiedBy is nullable per schema
        Entry entry = buildEntry(1L, 10L, 100L, null);
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(userService.findUsernamesByIds(Set.of(100L)))
                .thenReturn(Map.of(100L, "billy"));

        EntryDto dto = entryService.findById(1L);

        assertThat(dto.getCreatedByUsername()).isEqualTo("billy");
        assertThat(dto.getModifiedByUsername()).isNull();
    }

    // ---- findByKnowledgeBaseId ----

    @Test
    void findByKnowledgeBaseId_returnsEmptyList_whenNoEntries() {
        when(entryRepository.findByKnowledgeBaseId(10L)).thenReturn(List.of());

        List<EntryDto> result = entryService.findByKnowledgeBaseId(10L);

        assertThat(result).isEmpty();
        // no entries means no ids to resolve — the batch lookup shouldn't fire at all
        verifyNoInteractions(userService);
    }

    @Test
    void findByKnowledgeBaseId_batchResolvesUsernamesInASingleCall() {
        // Guards the N+1 pattern called out in EntryServiceImpl's Javadoc:
        // one findUsernamesByIds() call per list, never one per Entry.
        Entry e1 = buildEntry(1L, 10L, 100L, null);
        Entry e2 = buildEntry(2L, 10L, 100L, 200L);
        when(entryRepository.findByKnowledgeBaseId(10L)).thenReturn(List.of(e1, e2));
        when(userService.findUsernamesByIds(Set.of(100L, 200L)))
                .thenReturn(Map.of(100L, "billy", 200L, "editor2"));

        List<EntryDto> result = entryService.findByKnowledgeBaseId(10L);

        assertThat(result).hasSize(2);
        verify(userService, times(1)).findUsernamesByIds(any());
    }

    @Test
    void findByKnowledgeBaseId_omitsUnknownUser_ratherThanFailing() {
        // findUsernamesByIds contract: a missing key means "unknown user",
        // not an error — the service must not throw or fabricate a name.
        Entry entry = buildEntry(1L, 10L, 999L, null);
        when(entryRepository.findByKnowledgeBaseId(10L)).thenReturn(List.of(entry));
        when(userService.findUsernamesByIds(Set.of(999L))).thenReturn(Map.of());

        List<EntryDto> result = entryService.findByKnowledgeBaseId(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCreatedByUsername()).isNull();
    }

    // ---- helpers ----

    private Entry buildEntry(Long id, Long knowledgeBaseId, Long createdBy, Long modifiedBy) {
        Entry entry = new Entry();
        entry.setId(id);
        entry.setKnowledgeBaseId(knowledgeBaseId);
        entry.setStatusId(1L);
        entry.setSourceId(1L);
        entry.setContentTypeId(1L);
        entry.setCreatedBy(createdBy);
        entry.setModifiedBy(modifiedBy);
        return entry;
    }
}
