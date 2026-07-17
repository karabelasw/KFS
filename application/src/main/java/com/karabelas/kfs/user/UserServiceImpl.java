package com.karabelas.kfs.user;

// import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Package-private — only UserService (the interface) is exposed. */
// @Service
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Map<Long, String> findUsernamesByIds(Set<Long> ids) {
        // Mock: real implementation would be something like:
        // return userRepository.findAllById(ids).stream()
        //         .collect(Collectors.toMap(User::getId, User::getUsername));
        return Map.of();
    }

    @Override
    public String findUsernameById(Long id) {
        return findUsernamesByIds(Set.of(id)).get(id);
    }
}
