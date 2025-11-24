package com.banking.models;

import com.banking.utilities.ValidationPatterns;

/**
 * Extended customer information and contact details.
 * Demonstrates 1-to-1 relationship with Customer.
 * All contact fields are validated for correctness and completeness.
 */
public class CustomerProfile {
    private String profileId;      // Format: P###
    private String address;        // Customer's mailing address (non-empty)
    private String phone;          // Phone number (minimum 10 digits)
    private String email;          // Email address (valid format)
    private Customer customer;     // 1-to-1: Reference to associated customer

    /**
     * Creates a new customer profile with contact information.
     * All fields are validated during construction.
     *
     * @param profileId unique profile ID (format: P###)
     * @param address customer's mailing address (non-empty)
     * @param phone phone number (minimum 10 digits, can contain formatting)
     * @param email email address (must be valid format)
     * @throws IllegalArgumentException if any field is invalid
     */
    public CustomerProfile(String profileId, String address, String phone, String email) {
        this.setProfileId(profileId);
        this.setAddress(address);
        this.setPhone(phone);
        this.setEmail(email);
    }

    // ===== GETTERS =====
    /** @return profile ID (format P###) */
    public String getProfileId() { return this.profileId; }

    /** @return customer's mailing address */
    public String getAddress() { return this.address; }

    /** @return customer's phone number */
    public String getPhone() { return this.phone; }

    /** @return customer's email address */
    public String getEmail() { return this.email; }

    /** @return associated customer (1-to-1 relationship) */
    public Customer getCustomer() { return this.customer; }

    // ===== SETTERS WITH VALIDATION =====
    /**
     * Sets and validates the profile ID.
     *
     * @param profileId must be format P### (e.g., P001)
     * @throws IllegalArgumentException if format is invalid
     */
    public void setProfileId(String profileId) {
        if (!com.banking.utilities.ValidationPatterns.matchesPattern(profileId,
                com.banking.utilities.ValidationPatterns.PROFILE_ID_PATTERN)) {
            throw new IllegalArgumentException(com.banking.utilities.ValidationPatterns.PROFILE_ID_ERROR);
        }
        this.profileId = profileId;
    }

    /**
     * Sets and validates the address.
     *
     * @param address customer's mailing address (non-empty)
     * @throws IllegalArgumentException if address is empty or null
     */
    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.ADDRESS_EMPTY_ERROR);
        }
        this.address = address.trim();
    }

    /**
     * Sets and validates the phone number.
     * Accepts phone with or without formatting; validates minimum 10 digits.
     * Uses centralized validation pattern from ValidationPatterns utility class.
     *
     * @param phone phone number (minimum 10 digits)
     * @throws IllegalArgumentException if fewer than 10 digits
     */
    public void setPhone(String phone) {
        if (!com.banking.utilities.ValidationPatterns.isValidPhoneNumber(phone)) {
            throw new IllegalArgumentException(com.banking.utilities.ValidationPatterns.PHONE_ERROR);
        }
        this.phone = phone;
    }

    /**
     * Sets and validates the email address.
     * Uses ValidationPatterns constant to validate standard email format.
     *
     * @param email email address (must be valid format)
     * @throws IllegalArgumentException if email format is invalid
     */
    public void setEmail(String email) {
        if (!com.banking.utilities.ValidationPatterns.matchesPattern(email,
                com.banking.utilities.ValidationPatterns.EMAIL_PATTERN)) {
            throw new IllegalArgumentException(com.banking.utilities.ValidationPatterns.EMAIL_ERROR);
        }
        this.email = email;
    }

    /**
     * Sets the associated customer (establishes 1-to-1 relationship).
     * Validates that customer is non-null to maintain data integrity.
     *
     * @param customer the customer this profile belongs to (must be non-null)
     * @throws IllegalArgumentException if customer is null
     */
    public void setCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException(ValidationPatterns.CUSTOMER_NULL_ERROR);
        }
        this.customer = customer;
    }

    /**
     * Returns a formatted string representation of this profile.
     * Format: Profile[ID=P###, Address=..., Phone=..., Email=...]
     * Uses getters to maintain encapsulation.
     *
     * @return formatted profile details
     */
    @Override
    public String toString() {
        return String.format("Profile[ID=%s, Address=%s, Phone=%s, Email=%s]",
                this.getProfileId(), this.getAddress(), this.getPhone(), this.getEmail());
    }
}
