package com.karabelas.kfs.knowledgebase;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Package-private. */
interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Long> {
    List<KnowledgeBase> findByOwnerId(Long ownerId);

    Optional<KnowledgeBase> findByOwnerIdAndIsDefaultTrue(Long ownerId);
}
