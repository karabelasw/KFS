package com.karabelas.kfs.knowledgebase;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Public entry point for /api/knowledge-bases. */
@RestController
@RequestMapping("/api/knowledge-bases")
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    public KnowledgeBaseController(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @GetMapping("/{id}")
    public KnowledgeBaseDto getById(@PathVariable Long id) {
        return knowledgeBaseService.findById(id);
    }

    @GetMapping(params = "ownerId")
    public List<KnowledgeBaseDto> getByOwnerId(@RequestParam Long ownerId) {
        return knowledgeBaseService.findByOwnerId(ownerId);
    }

    @PostMapping
    public KnowledgeBaseDto create(@RequestBody KnowledgeBaseDto dto) {
        return knowledgeBaseService.create(dto);
    }

    @PostMapping("/default")
    public KnowledgeBaseDto createDefault(@RequestParam Long ownerId) {
        return knowledgeBaseService.createDefaultKnowledgeBase(ownerId);
    }
}
