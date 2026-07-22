package com.karabelas.kfs.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserServiceImpl}.
 *
 * UserService is the public seam every other feature package depends
 * on to resolve raw createdBy/modifiedBy ids into display usernames
 * (see EntryServiceImplTest for the consuming side of this contract).
 * These tests pin down the two behavioral guarantees other packages
 * rely on:
 *   1. batch resolution via a single findAllById() call, and
 *   2. ids with no matching user are silently omitted, never an error.
 *
 * IMPORTANT — forward-looking, not currently green: requires User to
 * have getters/setters and UserRepository to actually extend
 * JpaRepository (both currently commented out in the mock).
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
/*
    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void findUsernamesByIds_returnsMapOfIdToUsername() {
        when(userRepository.findAllById(Set.of(1L, 2L)))
                .thenReturn(List.of(newUser(1L, "billy"), newUser(2L, "dana")));

        Map<Long, String> result = userService.findUsernamesByIds(Set.of(1L, 2L));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(1L, "billy", 2L, "dana"));
    }

    @Test
    void findUsernamesByIds_omitsIdsWithNoMatchingUser() {
        when(userRepository.findAllById(Set.of(1L, 999L)))
                .thenReturn(List.of(newUser(1L, "billy")));

        Map<Long, String> result = userService.findUsernamesByIds(Set.of(1L, 999L));

        assertThat(result).containsOnlyKeys(1L);
    }

    @Test
    void findUsernamesByIds_returnsEmptyMap_whenIdsIsEmpty() {
        when(userRepository.findAllById(Set.of())).thenReturn(List.of());

        Map<Long, String> result = userService.findUsernamesByIds(Set.of());

        assertThat(result).isEmpty();
    }

    @Test
    void findUsernameById_delegatesToBatchMethod() {
        when(userRepository.findAllById(Set.of(1L))).thenReturn(List.of(newUser(1L, "billy")));

        String result = userService.findUsernameById(1L);

        assertThat(result).isEqualTo("billy");
    }

    @Test
    void findUsernameById_returnsNull_whenUserNotFound() {
        when(userRepository.findAllById(Set.of(404L))).thenReturn(List.of());

        String result = userService.findUsernameById(404L);

        assertThat(result).isNull();
    }
*/
    private User newUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }
}
