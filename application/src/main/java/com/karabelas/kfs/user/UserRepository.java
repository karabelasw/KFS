package com.karabelas.kfs.user;

import org.springframework.data.jpa.repository.JpaRepository;

/** Package-private — Spring wires it, nothing outside this package touches it directly. */
interface UserRepository extends JpaRepository<User, Long> {
}
