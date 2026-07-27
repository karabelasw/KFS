package com.karabelas.kfs.relationship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/** Package-private. */
interface RelationshipRepository extends JpaRepository<Relationship, Long> {

    /** Both directions — an Entry's relationships as source AND as target. */
    @Query("select r from Relationship r where r.sourceEntryId = :entryId or r.targetEntryId = :entryId")
    List<Relationship> findByEntryIdEitherSide(@Param("entryId") Long entryId);
}
