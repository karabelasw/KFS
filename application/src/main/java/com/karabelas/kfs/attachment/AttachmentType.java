package com.karabelas.kfs.attachment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Package-private. A small, mostly-static reference/vocabulary table
 * (FILE / LINK, seeded once) — resolved by code at service-call time
 * rather than hardcoding ids, since seed order isn't a contract worth
 * depending on.
 */
@Entity
@Table(name = "attachment_type")
class AttachmentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
