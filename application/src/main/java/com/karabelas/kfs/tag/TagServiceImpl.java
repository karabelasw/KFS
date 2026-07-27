package com.karabelas.kfs.tag;

import com.karabelas.kfs.common.DuplicateResourceException;
import com.karabelas.kfs.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/** Package-private. */
@Service
class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final EntryTagRepository entryTagRepository;

    TagServiceImpl(TagRepository tagRepository, EntryTagRepository entryTagRepository) {
        this.tagRepository = tagRepository;
        this.entryTagRepository = entryTagRepository;
    }

    @Override
    public TagDto findById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag " + id + " not found"));
        return toDto(tag);
    }

    @Override
    public List<TagDto> findByKnowledgeBaseId(Long knowledgeBaseId) {
        return tagRepository.findByKnowledgeBaseId(knowledgeBaseId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TagDto create(TagDto dto) {
        tagRepository.findByKnowledgeBaseIdAndName(dto.getKnowledgeBaseId(), dto.getName())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Tag '" + dto.getName() + "' already exists in KnowledgeBase " + dto.getKnowledgeBaseId());
                });

        Tag tag = new Tag();
        tag.setKnowledgeBaseId(dto.getKnowledgeBaseId());
        tag.setName(dto.getName());

        return toDto(tagRepository.save(tag));
    }

    @Override
    public void tagEntry(Long entryId, Long tagId, Long userId) {
        if (!tagRepository.existsById(tagId)) {
            throw new ResourceNotFoundException("Tag " + tagId + " not found");
        }
        entryTagRepository.findById_EntryIdAndId_TagId(entryId, tagId)
                .orElseGet(() -> entryTagRepository.save(
                        new EntryTag(entryId, tagId, LocalDateTime.now(), userId)));
    }

    @Override
    public void untagEntry(Long entryId, Long tagId) {
        entryTagRepository.findById_EntryIdAndId_TagId(entryId, tagId)
                .ifPresent(entryTagRepository::delete);
    }

    private TagDto toDto(Tag tag) {
        TagDto dto = new TagDto();
        dto.setId(tag.getId());
        dto.setKnowledgeBaseId(tag.getKnowledgeBaseId());
        dto.setName(tag.getName());
        return dto;
    }
}
