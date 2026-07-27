package com.karabelas.kfs.relationship;

import com.karabelas.kfs.common.ResourceNotFoundException;
import com.karabelas.kfs.entry.EntryService; // depends inward on entry
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/** Package-private. */
@Service
class RelationshipServiceImpl implements RelationshipService {

    private final RelationshipRepository relationshipRepository;
    private final EntryService entryService;

    RelationshipServiceImpl(RelationshipRepository relationshipRepository, EntryService entryService) {
        this.relationshipRepository = relationshipRepository;
        this.entryService = entryService;
    }

    @Override
    public RelationshipDto findById(Long id) {
        Relationship relationship = relationshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relationship " + id + " not found"));
        return toDto(relationship);
    }

    @Override
    public List<RelationshipDto> findByEntryId(Long entryId) {
        return relationshipRepository.findByEntryIdEitherSide(entryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RelationshipDto create(RelationshipDto dto) {
        if (dto.getSourceEntryId() != null && dto.getSourceEntryId().equals(dto.getTargetEntryId())) {
            throw new IllegalArgumentException("An Entry cannot have a Relationship to itself");
        }

        // Validates both ends exist — EntryService.findById throws
        // ResourceNotFoundException if either id is missing.
        entryService.findById(dto.getSourceEntryId());
        entryService.findById(dto.getTargetEntryId());

        Relationship relationship = new Relationship();
        relationship.setSourceEntryId(dto.getSourceEntryId());
        relationship.setTargetEntryId(dto.getTargetEntryId());
        relationship.setRelationshipTypeId(dto.getRelationshipTypeId());
        relationship.setNotes(dto.getNotes());

        return toDto(relationshipRepository.save(relationship));
    }

    private RelationshipDto toDto(Relationship relationship) {
        RelationshipDto dto = new RelationshipDto();
        dto.setId(relationship.getId());
        dto.setSourceEntryId(relationship.getSourceEntryId());
        dto.setTargetEntryId(relationship.getTargetEntryId());
        dto.setRelationshipTypeId(relationship.getRelationshipTypeId());
        dto.setNotes(relationship.getNotes());
        return dto;
    }
}
