package com.karabelas.kfs.entry;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Package-private. Spring wires it; nothing outside this package touches it directly. */
interface EntryRepository extends JpaRepository<Entry, Long> {
    List<Entry> findByKnowledgeBaseId(Long knowledgeBaseId);
}
