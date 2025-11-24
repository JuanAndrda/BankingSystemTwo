package com.banking.utilities;

// Centralized validation constants and patterns
public class ValidationPatterns {

    // Customer ID validation
    public static final String CUSTOMER_ID_PATTERN = "^C\\d{3}$";
    public static final String CUSTOMER_ID_FORMAT = "C###";
    public static final String CUSTOMER_ID_ERROR = "Customer ID must be format C### (e.g., C001)";

    // Account number validation
    public static final String ACCOUNT_NO_PATTERN = "^ACC\\d{3}$";
    public static final String ACCOUNT_NO_FORMAT = "ACC###";
    public static final String ACCOUNT_NO_ERROR = "Account number must be format ACC### (e.g., ACC001)";

    // Profile ID validation
    public static final String PROFILE_ID_PATTERN = "^P\\d{3}$";
    public static final String PROFILE_ID_FORMAT = "P###";
    public static final String PROFILE_ID_ERROR = "Profile ID must be format P### (e.g., P001)";

    // Contact information validation
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String EMAIL_ERROR = "Invalid email format";
    public static final int PHONE_MIN_DIGITS = 10;
    public static final String PHONE_ERROR = "Phone must contain at least 10 digits";

    // Transaction validation
    public static final String TRANSACTION_STATUS_PATTERN = "^(COMPLETED|FAILED)$";
    public static final String TRANSACTION_STATUS_ERROR = "Status must be COMPLETED or FAILED";

    // User validation error messages
    public static final String USERNAME_EMPTY_ERROR = "Username cannot be empty";
    public static final String PASSWORD_EMPTY_ERROR = "Password cannot be empty";
    public static final String USER_ROLE_NULL_ERROR = "User role cannot be null";

    // Customer validation error messages
    public static final String CUSTOMER_NAME_EMPTY_ERROR = "Name cannot be empty";
    public static final String CUSTOMER_NULL_ERROR = "Customer cannot be null";

    // Customer profile validation error messages
    public static final String ADDRESS_EMPTY_ERROR = "Address cannot be empty";

    // Account validation error messages
    public static final String ACCOUNT_OWNER_NULL_ERROR = "Account must have an owner";
    public static final String INTEREST_RATE_RANGE_ERROR = "Interest rate must be between 0 and 1 (0% to 100%)";
    public static final String OVERDRAFT_NEGATIVE_ERROR = "Overdraft limit cannot be negative";

    // Transaction validation error messages
    public static final String TRANSACTION_ID_EMPTY_ERROR = "Transaction ID cannot be empty";
    public static final String ACCOUNT_NO_FORMAT_ERROR = "Invalid account number format";
    public static final String TRANSACTION_TYPE_NULL_ERROR = "Transaction type cannot be null";
    public static final String AMOUNT_POSITIVE_ERROR = "Amount must be positive";

    // Audit log validation error messages
    public static final String ACTION_EMPTY_ERROR = "Action cannot be empty";
    public static final String DETAILS_NULL_ERROR = "Details cannot be null";

    // Private constructor to prevent instantiation
    private ValidationPatterns() {
    }

    // Validate string matches pattern
    public static boolean matchesPattern(String value, String pattern) {
        return value != null && value.matches(pattern);
    }

    // Validate phone number has minimum digits
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null) return false;
        return phone.replaceAll("[^0-9]", "").length() >= PHONE_MIN_DIGITS;
    }
}
