package com.karabelas.kfs.knowledgebase;

import com.karabelas.kfs.common.ResourceNotFoundException;
import com.karabelas.kfs.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/** Package-private. */
@Service
class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final UserService userService;

    KnowledgeBaseServiceImpl(KnowledgeBaseRepository knowledgeBaseRepository, UserService userService) {
        this.knowledgeBaseRepository = knowledgeBaseRepository;
        this.userService = userService;
    }

    @Override
    public KnowledgeBaseDto findById(Long id) {
        KnowledgeBase kb = knowledgeBaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KnowledgeBase " + id + " not found"));
        return toDto(kb);
    }

    @Override
    public List<KnowledgeBaseDto> findByOwnerId(Long ownerId) {
        return knowledgeBaseRepository.findByOwnerId(ownerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public KnowledgeBaseDto createDefaultKnowledgeBase(Long ownerId) {
        knowledgeBaseRepository.findByOwnerIdAndIsDefaultTrue(ownerId).ifPresent(kb -> {
            throw new IllegalStateException("Owner " + ownerId + " already has a default KnowledgeBase");
        });

        KnowledgeBase kb = new KnowledgeBase();
        kb.setOwnerId(ownerId);
        kb.setName("Inbox");
        kb.setDefault(true);
        kb.setCreatedBy(ownerId);

        return toDto(knowledgeBaseRepository.save(kb));
    }

    @Override
    public KnowledgeBaseDto create(KnowledgeBaseDto dto) {
        KnowledgeBase kb = new KnowledgeBase();
        kb.setOwnerId(dto.getOwnerId());
        kb.setName(dto.getName());
        kb.setDefault(false); // regular KBs are never auto-marked default
        kb.setCreatedBy(dto.getOwnerId());

        return toDto(knowledgeBaseRepository.save(kb));
    }

    private KnowledgeBaseDto toDto(KnowledgeBase kb) {
        KnowledgeBaseDto dto = new KnowledgeBaseDto();
        dto.setId(kb.getId());
        dto.setName(kb.getName());
        dto.setOwnerId(kb.getOwnerId());
        dto.setOwnerUsername(userService.findUsernameById(kb.getOwnerId()));
        dto.setDefault(kb.isDefault());
        dto.setCreatedAt(kb.getCreatedAt());
        return dto;
    }
}
