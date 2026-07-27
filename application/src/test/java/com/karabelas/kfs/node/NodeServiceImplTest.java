package com.karabelas.kfs.node;

import com.karabelas.kfs.common.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
 * Unit tests for {@link NodeServiceImpl}, covering CRUD plus the
 * filing/reordering behavior owned entirely by this service (the
 * entry package never needs to know it exists).
 */
@ExtendWith(MockitoExtension.class)
class NodeServiceImplTest {

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private EntryNodeRepository entryNodeRepository;

    private NodeServiceImpl nodeService;

    @BeforeEach
    void setUp() {
        nodeService = new NodeServiceImpl(nodeRepository, entryNodeRepository);
    }

    @Test
    void findById_returnsDto() {
        when(nodeRepository.findById(1L)).thenReturn(Optional.of(newNode(1L, 10L, "Projects")));

        NodeDto dto = nodeService.findById(1L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Projects");
    }

    @Test
    void findById_throwsResourceNotFoundException_whenMissing() {
        when(nodeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> nodeService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void fileEntry_assignsNextDisplayOrder_afterExistingFilings() {
        when(nodeRepository.existsById(1L)).thenReturn(true);
        when(entryNodeRepository.findById_NodeIdOrderByDisplayOrder(1L))
                .thenReturn(List.of(new EntryNode(100L, 1L, 0), new EntryNode(101L, 1L, 1)));

        nodeService.fileEntry(1L, 102L);

        ArgumentCaptor<EntryNode> captor = ArgumentCaptor.forClass(EntryNode.class);
        verify(entryNodeRepository).save(captor.capture());
        assertThat(captor.getValue().getDisplayOrder()).isEqualTo(2);
        assertThat(captor.getValue().getId().getEntryId()).isEqualTo(102L);
    }

    @Test
    void fileEntry_throwsResourceNotFoundException_whenNodeMissing() {
        when(nodeRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> nodeService.fileEntry(99L, 100L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(entryNodeRepository, never()).save(any());
    }

    @Test
    void unfileEntry_deletesTheFilingRow_whenPresent() {
        EntryNode filing = new EntryNode(100L, 1L, 0);
        when(entryNodeRepository.findById_EntryIdAndId_NodeId(100L, 1L)).thenReturn(Optional.of(filing));

        nodeService.unfileEntry(1L, 100L);

        verify(entryNodeRepository, times(1)).delete(filing);
    }

    @Test
    void unfileEntry_doesNothing_whenNotFiled() {
        when(entryNodeRepository.findById_EntryIdAndId_NodeId(100L, 1L)).thenReturn(Optional.empty());

        nodeService.unfileEntry(1L, 100L);

        verify(entryNodeRepository, never()).delete(any());
    }

    @Test
    void reorderEntries_reassignsDisplayOrderToMatchGivenSequence() {
        EntryNode e100 = new EntryNode(100L, 1L, 0);
        EntryNode e101 = new EntryNode(101L, 1L, 1);
        when(entryNodeRepository.findById_NodeIdOrderByDisplayOrder(1L)).thenReturn(List.of(e100, e101));

        nodeService.reorderEntries(1L, List.of(101L, 100L));

        assertThat(e101.getDisplayOrder()).isEqualTo(0);
        assertThat(e100.getDisplayOrder()).isEqualTo(1);
        verify(entryNodeRepository).saveAll(List.of(e100, e101));
    }

    @Test
    void reorderEntries_rejectsListMissingACurrentlyFiledEntry() {
        EntryNode e100 = new EntryNode(100L, 1L, 0);
        when(entryNodeRepository.findById_NodeIdOrderByDisplayOrder(1L)).thenReturn(List.of(e100));

        assertThatThrownBy(() -> nodeService.reorderEntries(1L, List.of(999L)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Node newNode(Long id, Long knowledgeBaseId, String name) {
        Node node = new Node();
        node.setId(id);
        node.setKnowledgeBaseId(knowledgeBaseId);
        node.setName(name);
        return node;
    }
}
