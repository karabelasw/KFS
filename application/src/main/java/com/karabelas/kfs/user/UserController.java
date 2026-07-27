package com.karabelas.kfs.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public entry point for /api/users. Deliberately thin: UserService's
 * real purpose is the internal cross-package seam (resolving ids to
 * usernames for other features' DTOs), not a rich user-management API
 * — that's out of scope until the deferred sharing/access model
 * (ADR-0008) is un-deferred.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}/username")
    public String getUsername(@PathVariable Long id) {
        return userService.findUsernameById(id);
    }
}
