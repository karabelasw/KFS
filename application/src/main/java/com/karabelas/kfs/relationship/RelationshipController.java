package com.karabelas.kfs.relationship;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Public entry point for /api/relationships. */
@RestController
@RequestMapping("/api/relationships")
public class RelationshipController {

    private final RelationshipService relationshipService;

    public RelationshipController(RelationshipService relationshipService) {
        this.relationshipService = relationshipService;
    }

    @GetMapping("/{id}")
    public RelationshipDto getById(@PathVariable Long id) {
        return relationshipService.findById(id);
    }

    @GetMapping(params = "entryId")
    public List<RelationshipDto> getByEntryId(@RequestParam Long entryId) {
        return relationshipService.findByEntryId(entryId);
    }

    @PostMapping
    public RelationshipDto create(@RequestBody RelationshipDto dto) {
        return relationshipService.create(dto);
    }
}
