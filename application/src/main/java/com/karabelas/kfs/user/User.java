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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Long getSystemRoleId() {
		return systemRoleId;
	}
	public void setSystemRoleId(Long systemRoleId) {
		this.systemRoleId = systemRoleId;
	}
}
