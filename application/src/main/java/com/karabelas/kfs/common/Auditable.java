package com.karabelas.kfs.common;

import java.time.LocalDateTime;

/**
 * Mock: shared base for audit columns (created_at, created_by, last_modified,
 * modified_by, archived_at, archived_by) used across most entities in the
 * schema. Would be a @MappedSuperclass in the real implementation.
 */
public abstract class Auditable {
    protected LocalDateTime createdAt;
    protected Long createdBy;
    protected LocalDateTime lastModified;
    protected Long modifiedBy;
    protected LocalDateTime archivedAt;
    protected Long archivedBy;
}
