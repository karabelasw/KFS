package com.karabelas.kfs.attachment;

import com.karabelas.kfs.common.ArchivedAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Package-private. Connects an Entry to either a stored File or an
 * external link — exactly one of fileId/externalUrl set, enforced by
 * ck_attachment_exactly_one_target at the DB layer. This first cut
 * only implements the LINK side (see AttachmentService); fileId stays
 * unused until file upload/storage exists.
 */
@Entity
@Table(name = "attachment")
class Attachment extends ArchivedAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "entry_id", nullable = false)
    private Long entryId;

    @Column(name = "attachment_type_id", nullable = false)
    private Long attachmentTypeId;

    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "external_url")
    private String externalUrl;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public Long getAttachmentTypeId() {
        return attachmentTypeId;
    }

    public void setAttachmentTypeId(Long attachmentTypeId) {
        this.attachmentTypeId = attachmentTypeId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
