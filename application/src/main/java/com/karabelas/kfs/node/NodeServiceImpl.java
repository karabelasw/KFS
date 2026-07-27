package com.karabelas.kfs.node;

import com.karabelas.kfs.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/** Package-private. */
@Service
class NodeServiceImpl implements NodeService {

    private final NodeRepository nodeRepository;
    private final EntryNodeRepository entryNodeRepository;

    NodeServiceImpl(NodeRepository nodeRepository, EntryNodeRepository entryNodeRepository) {
        this.nodeRepository = nodeRepository;
        this.entryNodeRepository = entryNodeRepository;
    }

    @Override
    public NodeDto findById(Long id) {
        Node node = nodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Node " + id + " not found"));
        return toDto(node);
    }

    @Override
    public List<NodeDto> findByKnowledgeBaseId(Long knowledgeBaseId) {
        return nodeRepository.findByKnowledgeBaseId(knowledgeBaseId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public NodeDto create(NodeDto dto) {
        Node node = new Node();
        node.setKnowledgeBaseId(dto.getKnowledgeBaseId());
        node.setName(dto.getName());
        node.setParentNodeId(dto.getParentNodeId());
        node.setDisplayOrder(dto.getDisplayOrder());
        return toDto(nodeRepository.save(node));
    }

    @Override
    public void fileEntry(Long nodeId, Long entryId) {
        if (!nodeRepository.existsById(nodeId)) {
            throw new ResourceNotFoundException("Node " + nodeId + " not found");
        }
        int nextOrder = entryNodeRepository.findById_NodeIdOrderByDisplayOrder(nodeId).size();
        entryNodeRepository.save(new EntryNode(entryId, nodeId, nextOrder));
    }

    @Override
    public void unfileEntry(Long nodeId, Long entryId) {
        entryNodeRepository.findById_EntryIdAndId_NodeId(entryId, nodeId)
                .ifPresent(entryNodeRepository::delete);
    }

    @Override
    public void reorderEntries(Long nodeId, List<Long> orderedEntryIds) {
        List<EntryNode> filed = entryNodeRepository.findById_NodeIdOrderByDisplayOrder(nodeId);

        for (EntryNode entryNode : filed) {
            int newOrder = orderedEntryIds.indexOf(entryNode.getId().getEntryId());
            if (newOrder < 0) {
                throw new IllegalArgumentException(
                        "orderedEntryIds must contain every entry currently filed under node " + nodeId);
            }
            entryNode.setDisplayOrder(newOrder);
        }
        entryNodeRepository.saveAll(filed);
    }

    private NodeDto toDto(Node node) {
        NodeDto dto = new NodeDto();
        dto.setId(node.getId());
        dto.setKnowledgeBaseId(node.getKnowledgeBaseId());
        dto.setName(node.getName());
        dto.setParentNodeId(node.getParentNodeId());
        dto.setDisplayOrder(node.getDisplayOrder());
        return dto;
    }
}
