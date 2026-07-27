package com.karabelas.kfs.node;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Public entry point for /api/nodes. */
@RestController
@RequestMapping("/api/nodes")
public class NodeController {

    private final NodeService nodeService;

    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping("/{id}")
    public NodeDto getById(@PathVariable Long id) {
        return nodeService.findById(id);
    }

    @GetMapping(params = "knowledgeBaseId")
    public List<NodeDto> getByKnowledgeBaseId(@RequestParam Long knowledgeBaseId) {
        return nodeService.findByKnowledgeBaseId(knowledgeBaseId);
    }

    @PostMapping
    public NodeDto create(@RequestBody NodeDto dto) {
        return nodeService.create(dto);
    }

    @PostMapping("/{nodeId}/entries/{entryId}")
    public void fileEntry(@PathVariable Long nodeId, @PathVariable Long entryId) {
        nodeService.fileEntry(nodeId, entryId);
    }

    @DeleteMapping("/{nodeId}/entries/{entryId}")
    public void unfileEntry(@PathVariable Long nodeId, @PathVariable Long entryId) {
        nodeService.unfileEntry(nodeId, entryId);
    }

    @PutMapping("/{nodeId}/entries/order")
    public void reorderEntries(@PathVariable Long nodeId, @RequestBody List<Long> orderedEntryIds) {
        nodeService.reorderEntries(nodeId, orderedEntryIds);
    }
}
