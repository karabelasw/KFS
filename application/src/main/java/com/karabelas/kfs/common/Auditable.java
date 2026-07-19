package com.karabelas.kfs.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Shared base for the created/modified audit columns present on nearly
 * every table in the schema. @MappedSuperclass means these fields are
 * mapped onto the SAME table as the subclass entity — no separate
 * "auditable" table is created.
 *
 * Does NOT include archived_at/archived_by — not every table has
 * those columns (e.g. entry uses status_id as its sole lifecycle
 * authority; entry_tag has no archive columns at all). Entities whose
 * tables DO have archived_at/archived_by should extend
 * ArchivedAuditable instead of this class directly.
 */
@MappedSuperclass
public abstract class Auditable {

    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    protected Long createdBy;

    @Column(name = "last_modified")
    protected LocalDateTime lastModified;

    @Column(name = "modified_by")
    protected Long modifiedBy;
}
