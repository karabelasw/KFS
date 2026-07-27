package com.karabelas.kfs.relationship;

public class RelationshipDto {
    private Long id;
    private Long sourceEntryId;
    private Long targetEntryId;
    private Long relationshipTypeId;
    private String notes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceEntryId() {
        return sourceEntryId;
    }

    public void setSourceEntryId(Long sourceEntryId) {
        this.sourceEntryId = sourceEntryId;
    }

    public Long getTargetEntryId() {
        return targetEntryId;
    }

    public void setTargetEntryId(Long targetEntryId) {
        this.targetEntryId = targetEntryId;
    }

    public Long getRelationshipTypeId() {
        return relationshipTypeId;
    }

    public void setRelationshipTypeId(Long relationshipTypeId) {
        this.relationshipTypeId = relationshipTypeId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
