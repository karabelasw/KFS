package com.karabelas.kfs.entry;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public. REST entry point for /api/entries. Talks only to
 * EntryService — never the repository or the Entry entity directly.
 * Every response carries EntryDto, so createdByUsername/
 * modifiedByUsername are already resolved by the time they reach
 * the client.
 */
@RestController
@RequestMapping("/api/entries")
public class EntryController {

    private final EntryService entryService;

    public EntryController(EntryService entryService) {
        this.entryService = entryService;
    }

    @GetMapping("/{id}")
    public EntryDto getById(@PathVariable Long id) {
        return entryService.findById(id);
    }

    @GetMapping(params = "knowledgeBaseId")
    public List<EntryDto> getByKnowledgeBaseId(@RequestParam Long knowledgeBaseId) {
        return entryService.findByKnowledgeBaseId(knowledgeBaseId);
    }
}
