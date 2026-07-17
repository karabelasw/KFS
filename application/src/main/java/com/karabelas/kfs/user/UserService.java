package com.karabelas.kfs.user;

import java.util.Map;
import java.util.Set;

/**
 * Public service interface — the seam other feature packages (entry,
 * knowledgebase, node, tag, relationship) are allowed to depend on.
 * No feature package may reference the User entity directly; this
 * interface is the only crossing point.
 */
public interface UserService {

    /**
     * Batch-resolves a set of user ids to display usernames in a single
     * call, so callers building a list of DTOs (e.g. EntryServiceImpl
     * mapping a page of Entries) issue one lookup instead of one per
     * record (avoids N+1). Ids with no matching user are simply
     * omitted from the returned map — callers should treat a missing
     * key as "unknown user" rather than fail.
     */
    Map<Long, String> findUsernamesByIds(Set<Long> ids);

    /** Convenience single-id form, built on the batch method. */
    String findUsernameById(Long id);
}
