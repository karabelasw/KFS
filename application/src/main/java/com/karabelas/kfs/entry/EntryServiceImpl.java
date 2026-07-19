package com.karabelas.kfs.entry;

// import org.springframework.stereotype.Service;
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
// @Service
class EntryServiceImpl implements EntryService {

    private final EntryRepository entryRepository;
    private final UserService userService;

    EntryServiceImpl(EntryRepository entryRepository, UserService userService) {
        this.entryRepository = entryRepository;
        this.userService = userService;
    }

    @Override
    public EntryDto findById(Long id) {
        // Mock: Entry entry = entryRepository.findById(id)
        //         .orElseThrow(() -> new ResourceNotFoundException("Entry " + id + " not found"));
        // return toDto(entry, userService.findUsernamesByIds(collectUserIds(List.of(entry))));
        return null;
    }

    @Override
    public List<EntryDto> findByKnowledgeBaseId(Long knowledgeBaseId) {
        // Mock outline of the batch-resolution pattern:
        //
        // List<Entry> entries = entryRepository.findByKnowledgeBaseId(knowledgeBaseId);
        // Map<Long, String> usernames = userService.findUsernamesByIds(collectUserIds(entries));
        // return entries.stream()
        //         .map(entry -> toDto(entry, usernames))
        //         .collect(Collectors.toList());
        return List.of();
    }

    /**
     * Gathers every distinct createdBy/modifiedBy id across a batch of
     * Entries into a single Set, so findUsernamesByIds() is called
     * once per list, not once per Entry (avoids N+1).
     */
    private Set<Long> collectUserIds(List<Entry> entries) {
        // Mock: return entries.stream()
        //         .flatMap(e -> Stream.of(e.getCreatedBy(), e.getModifiedBy()))
        //         .filter(java.util.Objects::nonNull)
        //         .collect(Collectors.toSet());
        return Set.of();
    }

    /** Maps an Entry + a pre-resolved username lookup map into an EntryDto. */
    private EntryDto toDto(Entry entry, Map<Long, String> usernames) {
        // Mock: build EntryDto from entry fields, using
        // usernames.get(entry.getCreatedBy()) / usernames.get(entry.getModifiedBy())
        // for createdByUsername / modifiedByUsername.
        return new EntryDto();
    }
}
