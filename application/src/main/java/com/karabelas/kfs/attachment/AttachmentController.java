package com.karabelas.kfs.attachment;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Public entry point for /api/entries/{entryId}/attachments. */
@RestController
class AttachmentController {

    private final AttachmentService attachmentService;

    AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping("/api/entries/{entryId}/attachments")
    public List<AttachmentDto> getByEntryId(@PathVariable Long entryId) {
        return attachmentService.findByEntryId(entryId);
    }

    @PostMapping("/api/entries/{entryId}/attachments/link")
    public AttachmentDto addLink(@PathVariable Long entryId,
            @RequestParam String displayName,
            @RequestParam String url) {
        return attachmentService.addLink(entryId, displayName, url);
    }

    @DeleteMapping("/api/attachments/{attachmentId}")
    public void removeAttachment(@PathVariable Long attachmentId) {
        attachmentService.removeAttachment(attachmentId);
    }
}
