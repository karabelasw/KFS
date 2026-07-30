package com.karabelas.kfs.attachment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Package-private. */
interface AttachmentTypeRepository extends JpaRepository<AttachmentType, Long> {
    Optional<AttachmentType> findByCode(String code);
}
