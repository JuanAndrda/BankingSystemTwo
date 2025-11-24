package com.banking.auth;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.banking.utilities.ValidationPatterns;

// Audit log for recording system operations
public class AuditLog {
    private final LocalDateTime timestamp;
    private final String username;
    private final UserRole userRole;
    private final String action;
    private final String details;

    public AuditLog(String username, UserRole userRole, String action, String details) {
        this.timestamp = LocalDateTime.now();
        this.username = validateUsername(username);
        this.userRole = validateUserRole(userRole);
        this.action = validateAction(action);
        this.details = validateDetails(details);
    }

    // Validation helpers
    private String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.USERNAME_EMPTY_ERROR);
        }
        return username.trim();
    }

    private UserRole validateUserRole(UserRole userRole) {
        if (userRole == null) {
            throw new IllegalArgumentException(ValidationPatterns.USER_ROLE_NULL_ERROR);
        }
        return userRole;
    }

    private String validateAction(String action) {
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.ACTION_EMPTY_ERROR);
        }
        return action.trim();
    }

    private String validateDetails(String details) {
        if (details == null) {
            throw new IllegalArgumentException(ValidationPatterns.DETAILS_NULL_ERROR);
        }
        return details.trim();
    }

    public LocalDateTime getTimestamp() { return this.timestamp; }
    public String getUsername() { return this.username; }
    public UserRole getUserRole() { return this.userRole; }
    public String getAction() { return this.action; }
    public String getDetails() { return this.details; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%s | %s (%s) | %s | %s",
                getTimestamp().format(formatter),
                getUsername(),
                getUserRole().getDisplayName(),
                getAction(),
                getDetails());
    }
}
