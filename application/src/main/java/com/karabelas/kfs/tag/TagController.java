package com.karabelas.kfs.tag;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Public entry point for /api/tags. */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/{id}")
    public TagDto getById(@PathVariable Long id) {
        return tagService.findById(id);
    }

    @GetMapping(params = "knowledgeBaseId")
    public List<TagDto> getByKnowledgeBaseId(@RequestParam Long knowledgeBaseId) {
        return tagService.findByKnowledgeBaseId(knowledgeBaseId);
    }

    @PostMapping
    public TagDto create(@RequestBody TagDto dto) {
        return tagService.create(dto);
    }

    @PostMapping("/{tagId}/entries/{entryId}")
    public void tagEntry(@PathVariable Long tagId, @PathVariable Long entryId) {
        tagService.tagEntry(entryId, tagId);
    }

    @DeleteMapping("/{tagId}/entries/{entryId}")
    public void untagEntry(@PathVariable Long tagId, @PathVariable Long entryId) {
        tagService.untagEntry(entryId, tagId);
    }
}
