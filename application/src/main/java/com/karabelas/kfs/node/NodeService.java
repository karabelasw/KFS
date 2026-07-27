package com.karabelas.kfs.node;

import java.util.List;

/**
 * Public. entry package never needs to know Node-scoped ordering
 * exists — filing/reordering is owned entirely here.
 */
public interface NodeService {

    NodeDto findById(Long id);

    List<NodeDto> findByKnowledgeBaseId(Long knowledgeBaseId);

    NodeDto create(NodeDto dto);

    /**
     * Files an Entry into a Node — creates the entry_node row that
     * takes it out of "INBOX" status (filed status is derived from
     * entry_node presence, not a flag on Entry itself). Assigns the
     * next available displayOrder within that Node.
     */
    void fileEntry(Long nodeId, Long entryId);

    /** Removes an Entry from a Node (does not delete the Entry). */
    void unfileEntry(Long nodeId, Long entryId);

    /**
     * Reassigns displayOrder for every Entry filed under a Node to
     * match the given order. orderedEntryIds must contain exactly the
     * set of entries currently filed under nodeId.
     */
    void reorderEntries(Long nodeId, List<Long> orderedEntryIds);
}
