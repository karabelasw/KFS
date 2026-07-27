package com.karabelas.kfs.entry;

import org.springframework.stereotype.Service;
import com.karabelas.kfs.common.ResourceNotFoundException;
import com.karabelas.kfs.user.UserService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Package-private. Only EntryService (the interface) is exposed.
 *
 * Depends on UserService (public interface from the user package) to
 * resolve createdBy/modifiedBy ids into display usernames — this is
 * the only cross-package dependency this class has, and it goes
 * through the public seam only, never touching the User entity.
 */
@Service
class EntryServiceImpl implements EntryService {

    private final EntryRepository entryRepository;
    private final UserService userService;

    EntryServiceImpl(EntryRepository entryRepository, UserService userService) {
        this.entryRepository = entryRepository;
        this.userService = userService;
    }

    @Override
    public EntryDto findById(Long id) {
        Entry entry = entryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entry " + id + " not found"));
        Map<Long, String> usernames = userService.findUsernamesByIds(collectUserIds(List.of(entry)));
        return toDto(entry, usernames);
    }

    @Override
    public List<EntryDto> findByKnowledgeBaseId(Long knowledgeBaseId) {
        List<Entry> entries = entryRepository.findByKnowledgeBaseId(knowledgeBaseId);
        if (entries.isEmpty()) {
            return List.of();
        }
        Map<Long, String> usernames = userService.findUsernamesByIds(collectUserIds(entries));
        return entries.stream()
                .map(entry -> toDto(entry, usernames))
                .collect(Collectors.toList());
    }

    /**
     * Gathers every distinct createdBy/modifiedBy id across a batch of
     * Entries into a single Set, so findUsernamesByIds() is called
     * once per list, not once per Entry (avoids N+1).
     */
    private Set<Long> collectUserIds(List<Entry> entries) {
        return entries.stream()
                .flatMap(e -> Stream.of(e.getCreatedBy(), e.getModifiedBy()))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /** Maps an Entry + a pre-resolved username lookup map into an EntryDto. */
    private EntryDto toDto(Entry entry, Map<Long, String> usernames) {
        EntryDto dto = new EntryDto();
        dto.setId(entry.getId());
        dto.setKnowledgeBaseId(entry.getKnowledgeBaseId());
        dto.setTitle(entry.getTitle());
        dto.setContent(entry.getContent());
        dto.setStatusId(entry.getStatusId());
        dto.setSourceId(entry.getSourceId());
        dto.setContentTypeId(entry.getContentTypeId());

        dto.setCreatedAt(entry.getCreatedAt());
        dto.setCreatedByUsername(usernames.get(entry.getCreatedBy()));
        dto.setLastModified(entry.getLastModified());
        dto.setModifiedByUsername(
                entry.getModifiedBy() != null ? usernames.get(entry.getModifiedBy()) : null);

        return dto;
    }
}
