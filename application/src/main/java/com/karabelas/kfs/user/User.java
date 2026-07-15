package com.karabelas.kfs.user;

import com.karabelas.kfs.common.Auditable;

/**
 * Mock entity. Package-private: accessed only through UserRepository /
 * UserService, never referenced directly outside this package.
 */
class User extends Auditable {
    private Long id;
    private String username;
    private String email;
    private Long systemRoleId;
}
