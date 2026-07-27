package com.karabelas.kfs.common;

/**
 * Thrown when a create operation would violate a uniqueness rule that
 * the service layer chooses to surface as a clean, intentional error
 * rather than letting a raw DataIntegrityViolationException bubble up
 * from the repository (e.g. Tag's unique(knowledgeBaseId, name)
 * constraint per ADR-0010).
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
