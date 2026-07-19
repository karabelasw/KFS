package com.karabelas.kfs.entry;

import com.karabelas.kfs.common.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Package-private — accessed only through EntryRepository / EntryService,
 * never referenced directly outside this package. Foundational
 * aggregate: deliberately has NO dependency on node/tag/relationship
 * packages. Node/Tag/Relationship reference Entry by id only, never
 * the reverse.
 *
 * Extends plain Auditable, NOT ArchivedAuditable — entry has no
 * archived_at/archived_by columns; status_id is the sole lifecycle
 * authority (see schema comments / prior ADR discussion).
 *
 * custom_attributes (JSON column) is intentionally omitted from this
 * mock — will need a JSON converter (e.g. Hibernate's @JdbcTypeCode
 * or a custom AttributeConverter) once that's tackled.
 */
@Entity
@Table(name = "entry")
class Entry extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "knowledge_base_id", nullable = false)
    private Long knowledgeBaseId;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "status_id", nullable = false)
    private Long statusId;   // sole lifecycle authority — no archived_at

    @Column(name = "source_id", nullable = false)
    private Long sourceId;   // NOT NULL — every Entry declares its origin

    @Column(name = "content_type_id", nullable = false)
    private Long contentTypeId;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    // getters/setters omitted from mock
}
