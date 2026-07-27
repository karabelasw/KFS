package com.karabelas.kfs.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Package-private. */
interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByKnowledgeBaseId(Long knowledgeBaseId);

    Optional<Tag> findByKnowledgeBaseIdAndName(Long knowledgeBaseId, String name);
}
