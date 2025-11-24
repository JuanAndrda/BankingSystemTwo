package com.banking.auth;

// User role enumeration
public enum UserRole {
    ADMIN("Administrator"),
    CUSTOMER("Customer");

    private String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
