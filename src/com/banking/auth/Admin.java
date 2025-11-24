package com.banking.auth;

import java.util.LinkedList;

// Admin user with full system access
public class Admin extends User {

    public Admin(String username, String password) {
        super(username, password, UserRole.ADMIN, false);
    }

    @Override
    public LinkedList<String> getPermissions() {
        LinkedList<String> permissions = new LinkedList<>();

        // Session management
        permissions.add("LOGOUT");
        permissions.add("EXIT_APP");

        // Customer operations
        permissions.add("CREATE_CUSTOMER");
        permissions.add("VIEW_CUSTOMER");
        permissions.add("VIEW_ALL_CUSTOMERS");
        permissions.add("DELETE_CUSTOMER");

        // Account operations
        permissions.add("CREATE_ACCOUNT");
        permissions.add("VIEW_ACCOUNT_DETAILS");
        permissions.add("VIEW_ALL_ACCOUNTS");
        permissions.add("DELETE_ACCOUNT");
        permissions.add("UPDATE_OVERDRAFT");

        // Profile operations
        permissions.add("CREATE_PROFILE");
        permissions.add("UPDATE_PROFILE");

        // Transaction operations (viewing/managing)
        permissions.add("VIEW_TRANSACTION_HISTORY");

        // Reporting & utilities
        permissions.add("APPLY_INTEREST");
        permissions.add("SORT_BY_NAME");
        permissions.add("SORT_BY_BALANCE");
        permissions.add("VIEW_AUDIT_TRAIL");

        return permissions;
    }

    @Override
    public String toString() {
        return String.format("Admin[Username=%s]", getUsername());
    }
}
