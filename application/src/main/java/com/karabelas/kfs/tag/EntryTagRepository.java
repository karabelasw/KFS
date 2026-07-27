package com.karabelas.kfs.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Repository for the association entity. Package-private. */
interface EntryTagRepository extends JpaRepository<EntryTag, EntryTagId> {
    List<EntryTag> findById_EntryId(Long entryId);

    Optional<EntryTag> findById_EntryIdAndId_TagId(Long entryId, Long tagId);
}
