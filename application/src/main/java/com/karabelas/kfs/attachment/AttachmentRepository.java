package com.karabelas.kfs.attachment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Package-private. */
interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByEntryId(Long entryId);

    Optional<Attachment> findByEntryIdAndDisplayName(Long entryId, String displayName);
}
