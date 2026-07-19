package com.karabelas.kfs.entry;

import java.util.List;

/**
 * Public service interface — the seam node/tag/relationship packages
 * depend on (e.g. to validate an entryId exists), and the seam the
 * controller layer talks to. Returns EntryDto, never the Entry entity.
 */
public interface EntryService {
    EntryDto findById(Long id);
    List<EntryDto> findByKnowledgeBaseId(Long knowledgeBaseId);
}
