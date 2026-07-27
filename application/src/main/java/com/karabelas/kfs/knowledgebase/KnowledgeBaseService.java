package com.karabelas.kfs.knowledgebase;

import java.util.List;

/**
 * Public — the seam entry/node/tag packages depend on when they need
 * to validate a knowledgeBaseId, etc.
 */
public interface KnowledgeBaseService {

    KnowledgeBaseDto findById(Long id);

    List<KnowledgeBaseDto> findByOwnerId(Long ownerId);

    /**
     * Auto-provisions the single default/INBOX KnowledgeBase for a
     * user. Fails fast if the owner already has one — a user must
     * never end up with zero or multiple default KBs.
     */
    KnowledgeBaseDto createDefaultKnowledgeBase(Long ownerId);

    /** Creates a regular (non-default) KnowledgeBase for an owner. */
    KnowledgeBaseDto create(KnowledgeBaseDto dto);
}
