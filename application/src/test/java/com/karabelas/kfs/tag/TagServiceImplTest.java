package com.karabelas.kfs.tag;

import com.karabelas.kfs.common.DuplicateResourceException;
import com.karabelas.kfs.common.ResourceNotFoundException;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TagServiceImpl}, per ADR-0010 (KB-scoped tags).
 */
@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private EntryTagRepository entryTagRepository;

    private TagServiceImpl tagService;

    @BeforeEach
    void setUp() {
        tagService = new TagServiceImpl(tagRepository, entryTagRepository);
    }

    @Test
    void create_rejectsDuplicateNameWithinSameKnowledgeBase() {
        when(tagRepository.findByKnowledgeBaseIdAndName(10L, "urgent"))
                .thenReturn(Optional.of(newTag(1L, 10L, "urgent")));

        TagDto request = newDto(10L, "urgent");

        assertThatThrownBy(() -> tagService.create(request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(tagRepository, never()).save(any());
    }

    @Test
    void create_allowsSameNameInDifferentKnowledgeBases() {
        when(tagRepository.findByKnowledgeBaseIdAndName(20L, "urgent")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> {
            Tag tag = invocation.getArgument(0);
            tag.setId(2L);
            return tag;
        });

        TagDto result = tagService.create(newDto(20L, "urgent"));

        assertThat(result.getKnowledgeBaseId()).isEqualTo(20L);
        assertThat(result.getName()).isEqualTo("urgent");
    }

    @Test
    void findByKnowledgeBaseId_neverLeaksTagsFromOtherKnowledgeBases() {
        when(tagRepository.findByKnowledgeBaseId(10L)).thenReturn(List.of(newTag(1L, 10L, "urgent")));

        List<TagDto> result = tagService.findByKnowledgeBaseId(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKnowledgeBaseId()).isEqualTo(10L);
    }

    @Test
    void tagEntry_createsAssociation_whenNotAlreadyTagged() {
        when(tagRepository.existsById(5L)).thenReturn(true);
        when(entryTagRepository.findById_EntryIdAndId_TagId(100L, 5L)).thenReturn(Optional.empty());

        tagService.tagEntry(100L, 5L);

        verify(entryTagRepository, times(1)).save(any(EntryTag.class));
    }

    @Test
    void tagEntry_isIdempotent_whenAlreadyTagged() {
        when(tagRepository.existsById(5L)).thenReturn(true);
        EntryTag existing = new EntryTag(100L, 5L, java.time.LocalDateTime.now());
        when(entryTagRepository.findById_EntryIdAndId_TagId(100L, 5L)).thenReturn(Optional.of(existing));

        tagService.tagEntry(100L, 5L);

        verify(entryTagRepository, never()).save(any());
    }

    @Test
    void tagEntry_throwsResourceNotFoundException_whenTagMissing() {
        when(tagRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> tagService.tagEntry(100L, 999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void untagEntry_removesAssociation_whenPresent() {
        EntryTag existing = new EntryTag(100L, 5L, java.time.LocalDateTime.now());
        when(entryTagRepository.findById_EntryIdAndId_TagId(100L, 5L)).thenReturn(Optional.of(existing));

        tagService.untagEntry(100L, 5L);

        verify(entryTagRepository, times(1)).delete(existing);
    }

    private Tag newTag(Long id, Long knowledgeBaseId, String name) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setKnowledgeBaseId(knowledgeBaseId);
        tag.setName(name);
        return tag;
    }

    private TagDto newDto(Long knowledgeBaseId, String name) {
        TagDto dto = new TagDto();
        dto.setKnowledgeBaseId(knowledgeBaseId);
        dto.setName(name);
        return dto;
    }
}
