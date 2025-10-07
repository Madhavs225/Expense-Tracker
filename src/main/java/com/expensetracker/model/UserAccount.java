package com.expensetracker.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a user account for authentication.
 */
public class UserAccount {

    private Integer id;
    private String username;
    private String passwordHash;
    private String salt;
    private Role role;
    private boolean active = true;
    private LocalDateTime createdAt;

    public UserAccount(String username, String passwordHash, String salt, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public UserAccount(Integer id, String username, String passwordHash, String salt, Role role, boolean active, LocalDateTime createdAt) {
        this(username, passwordHash, salt, role);
        this.id = id;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAccount)) {
            return false;
        }
        UserAccount that = (UserAccount) o;
        return Objects.equals(username.toLowerCase(), that.username.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(username.toLowerCase());
    }
}
