package com.karabelas.kfs.tag;

import java.util.List;

/** Public. */
public interface TagService {

    TagDto findById(Long id);

    /** Autocomplete/lookup queries are always scoped to a single KB — never leak across KBs. */
    List<TagDto> findByKnowledgeBaseId(Long knowledgeBaseId);

    /**
     * Creates a Tag, enforcing unique(knowledgeBaseId, name). The same
     * tag name in two different KBs is NOT a conflict — that's the
     * whole point of KB-scoping (ADR-0010).
     */
    TagDto create(TagDto dto);

    void tagEntry(Long entryId, Long tagId, Long userId);

    void untagEntry(Long entryId, Long tagId);
}
