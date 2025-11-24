package com.banking.auth;

import java.util.LinkedList;
import com.banking.utilities.ValidationPatterns;

// Abstract base class for all user types
public abstract class User {
    private final String username;
    private final String password;
    private final UserRole userRole;
    private boolean passwordChangeRequired;

    public User(String username, String password, UserRole userRole, boolean passwordChangeRequired) {
        this.username = validateUsername(username);
        this.password = validatePassword(password);
        this.userRole = validateUserRole(userRole);
        this.passwordChangeRequired = passwordChangeRequired;
    }

    // Validation helpers
    private String validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.USERNAME_EMPTY_ERROR);
        }
        return username;
    }

    private String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.PASSWORD_EMPTY_ERROR);
        }
        return password;
    }

    private UserRole validateUserRole(UserRole userRole) {
        if (userRole == null) {
            throw new IllegalArgumentException(ValidationPatterns.USER_ROLE_NULL_ERROR);
        }
        return userRole;
    }

    public String getUsername() { return this.username; }
    public UserRole getUserRole() { return this.userRole; }
    public boolean isPasswordChangeRequired() { return this.passwordChangeRequired; }

    public void setPasswordChangeRequired(boolean required) {
        this.passwordChangeRequired = required;
    }

    // Abstract method for role-specific permissions
    public abstract LinkedList<String> getPermissions();

    // Authenticate user with password
    public boolean authenticate(String providedPassword) {
        return this.password.equals(providedPassword);
    }

    // Check if user has specific permission
    public boolean hasPermission(String permission) {
        if (permission == null) return false;
        for (String p : getPermissions()) {
            if (p.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("User[Username=%s, Role=%s]", getUsername(), getUserRole().getDisplayName());
    }
}
