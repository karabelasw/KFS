package com.karabelas.kfs.common;

/**
 * Mock: generic not-found exception shared across feature packages
 * (e.g. thrown by EntryService, NodeService, TagService).
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
