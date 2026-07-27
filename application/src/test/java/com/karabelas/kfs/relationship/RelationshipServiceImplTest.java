package com.karabelas.kfs.relationship;

import com.karabelas.kfs.common.ResourceNotFoundException;
import com.karabelas.kfs.entry.EntryDto;
import com.karabelas.kfs.entry.EntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RelationshipServiceImpl} — the typed peer
 * graph connecting Entries, separate from the Node hierarchy.
 */
@ExtendWith(MockitoExtension.class)
class RelationshipServiceImplTest {

    @Mock
    private RelationshipRepository relationshipRepository;

    @Mock
    private EntryService entryService;

    private RelationshipServiceImpl relationshipService;

    @BeforeEach
    void setUp() {
        relationshipService = new RelationshipServiceImpl(relationshipRepository, entryService);
    }

    @Test
    void create_validatesBothEntriesExist() {
        when(entryService.findById(1L)).thenReturn(new EntryDto());
        when(entryService.findById(2L)).thenReturn(new EntryDto());
        when(relationshipRepository.save(any(Relationship.class))).thenAnswer(invocation -> {
            Relationship r = invocation.getArgument(0);
            r.setId(5L);
            return r;
        });

        RelationshipDto result = relationshipService.create(newDto(1L, 2L, 10L));

        assertThat(result.getId()).isEqualTo(5L);
        verify(entryService).findById(1L);
        verify(entryService).findById(2L);
    }

    @Test
    void create_propagatesResourceNotFoundException_whenSourceEntryMissing() {
        when(entryService.findById(999L)).thenThrow(new ResourceNotFoundException("Entry 999 not found"));

        assertThatThrownBy(() -> relationshipService.create(newDto(999L, 2L, 10L)))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(relationshipRepository, never()).save(any());
    }

    @Test
    void create_rejectsSelfReferencingRelationship() {
        RelationshipDto request = newDto(1L, 1L, 10L);

        assertThatThrownBy(() -> relationshipService.create(request))
                .isInstanceOf(IllegalArgumentException.class);

        verify(entryService, never()).findById(any());
        verify(relationshipRepository, never()).save(any());
    }

    @Test
    void findByEntryId_returnsRelationshipsFromEitherDirection() {
        Relationship asSource = newRelationship(1L, 100L, 200L);
        Relationship asTarget = newRelationship(2L, 300L, 100L);
        when(relationshipRepository.findByEntryIdEitherSide(100L)).thenReturn(List.of(asSource, asTarget));

        List<RelationshipDto> result = relationshipService.findByEntryId(100L);

        assertThat(result).hasSize(2);
    }

    @Test
    void findById_throwsResourceNotFoundException_whenMissing() {
        when(relationshipRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> relationshipService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private RelationshipDto newDto(Long sourceEntryId, Long targetEntryId, Long relationshipTypeId) {
        RelationshipDto dto = new RelationshipDto();
        dto.setSourceEntryId(sourceEntryId);
        dto.setTargetEntryId(targetEntryId);
        dto.setRelationshipTypeId(relationshipTypeId);
        return dto;
    }

    private Relationship newRelationship(Long id, Long sourceEntryId, Long targetEntryId) {
        Relationship r = new Relationship();
        r.setId(id);
        r.setSourceEntryId(sourceEntryId);
        r.setTargetEntryId(targetEntryId);
        r.setRelationshipTypeId(10L);
        return r;
    }
}
