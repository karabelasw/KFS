package com.karabelas.kfs.attachment;

import com.karabelas.kfs.common.DuplicateResourceException;
import com.karabelas.kfs.common.ResourceNotFoundException;
import com.karabelas.kfs.entry.EntryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/** Package-private. */
@Service
class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentTypeRepository attachmentTypeRepository;
    private final EntryService entryService;

    AttachmentServiceImpl(AttachmentRepository attachmentRepository,
            AttachmentTypeRepository attachmentTypeRepository,
            EntryService entryService) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentTypeRepository = attachmentTypeRepository;
        this.entryService = entryService;
    }

    @Override
    public List<AttachmentDto> findByEntryId(Long entryId) {
        return attachmentRepository.findByEntryId(entryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AttachmentDto addLink(Long entryId, String displayName, String url) {
        // Validates the Entry exists — throws ResourceNotFoundException if not,
        // via the public EntryService seam (never touching entry's repository directly).
        entryService.findById(entryId);

        attachmentRepository.findByEntryIdAndDisplayName(entryId, displayName)
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Attachment '" + displayName + "' already exists on Entry " + entryId);
                });

        Long linkTypeId = attachmentTypeRepository.findByCode("LINK")
                .orElseThrow(() -> new IllegalStateException(
                        "attachment_type 'LINK' is not seeded — check kfs-schema-create.sql"))
                .getId();

        Attachment attachment = new Attachment();
        attachment.setEntryId(entryId);
        attachment.setAttachmentTypeId(linkTypeId);
        attachment.setExternalUrl(url);
        attachment.setDisplayName(displayName);

        return toDto(attachmentRepository.save(attachment));
    }

    @Override
    public void removeAttachment(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment " + attachmentId + " not found"));
        attachmentRepository.delete(attachment);
    }

    private AttachmentDto toDto(Attachment attachment) {
        AttachmentDto dto = new AttachmentDto();
        dto.setId(attachment.getId());
        dto.setEntryId(attachment.getEntryId());
        dto.setDisplayName(attachment.getDisplayName());
        dto.setExternalUrl(attachment.getExternalUrl());
        return dto;
    }
}
