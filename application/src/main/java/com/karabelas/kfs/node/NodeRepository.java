package com.karabelas.kfs.node;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Package-private. */
interface NodeRepository extends JpaRepository<Node, Long> {
    List<Node> findByKnowledgeBaseId(Long knowledgeBaseId);
}
