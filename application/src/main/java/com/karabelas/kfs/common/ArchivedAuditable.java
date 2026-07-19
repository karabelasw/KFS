package com.karabelas.kfs.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Extends Auditable with archived_at / archived_by, for entities whose
 * tables carry soft-archive columns in addition to the standard
 * created/modified audit fields.
 *
 * Confirmed via schema (kfs-schema-v2.sql) to apply to: knowledge_base,
 * content_type, node, entry_node, relationship, knowledge_base_access,
 * comment, file, attachment, tag.
 *
 * Does NOT apply to: entry (status_id is the sole lifecycle authority),
 * entry_tag (no archive columns at all), user, or any reference/
 * vocabulary table.
 */
@MappedSuperclass
public abstract class ArchivedAuditable extends Auditable {

    @Column(name = "archived_at")
    protected LocalDateTime archivedAt;

    @Column(name = "archived_by")
    protected Long archivedBy;
}
