package com.karabelas.kfs.entry;

import java.time.LocalDateTime;

/**
 * Public DTO — the shape returned to clients. Unlike the Entry entity
 * (package-private, a thin mirror of the entry table), this is where
 * raw user-id FKs get enriched into display-friendly values.
 *
 * createdByUsername / modifiedByUsername are resolved via
 * UserService.findUsernamesByIds(...) in EntryServiceImpl — never by
 * this package reaching into the User entity directly.
 */
public class EntryDto {
    private Long id;
    private Long knowledgeBaseId;
    private String title;
    private String content;
    private Long statusId;
    private Long sourceId;
    private Long contentTypeId;

    private LocalDateTime createdAt;
    private String createdByUsername;   // resolved, not raw Long
    private LocalDateTime lastModified;
    private String modifiedByUsername;  // resolved, nullable

    // getters/setters omitted from mock
}
