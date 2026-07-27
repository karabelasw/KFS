package com.karabelas.kfs.node;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Repository for the association entity. Package-private. */
interface EntryNodeRepository extends JpaRepository<EntryNode, EntryNodeId> {
    List<EntryNode> findById_NodeIdOrderByDisplayOrder(Long nodeId);

    List<EntryNode> findById_EntryId(Long entryId);

    Optional<EntryNode> findById_EntryIdAndId_NodeId(Long entryId, Long nodeId);
}
