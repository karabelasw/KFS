package com.karabelas.kfs.attachment;

import java.util.List;

/**
 * Public. Only the LINK side is implemented here — attaching a real
 * FILE needs a storage service (upload handling, content hashing,
 * dedup against the file table) that doesn't exist yet. Adding that
 * later means a new addFile(...)-shaped method alongside addLink,
 * not a redesign of this interface.
 */
public interface AttachmentService {

    List<AttachmentDto> findByEntryId(Long entryId);

    /**
     * Attaches an external link (e.g. a YouTube URL) to an Entry.
     * Validates the Entry exists and that displayName is unique
     * within that Entry (uq_attachment_display_name_per_entry).
     */
    AttachmentDto addLink(Long entryId, String displayName, String url);

    void removeAttachment(Long attachmentId);
}
