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
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getKnowledgeBaseId() {
		return knowledgeBaseId;
	}
	public void setKnowledgeBaseId(Long knowledgeBaseId) {
		this.knowledgeBaseId = knowledgeBaseId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Long getStatusId() {
		return statusId;
	}
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}
	public Long getSourceId() {
		return sourceId;
	}
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	public Long getContentTypeId() {
		return contentTypeId;
	}
	public void setContentTypeId(Long contentTypeId) {
		this.contentTypeId = contentTypeId;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public String getCreatedByUsername() {
		return createdByUsername;
	}
	public void setCreatedByUsername(String createdByUsername) {
		this.createdByUsername = createdByUsername;
	}
	public LocalDateTime getLastModified() {
		return lastModified;
	}
	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}
	public String getModifiedByUsername() {
		return modifiedByUsername;
	}
	public void setModifiedByUsername(String modifiedByUsername) {
		this.modifiedByUsername = modifiedByUsername;
	}


}
