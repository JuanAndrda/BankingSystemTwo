package com.banking.managers;

import com.banking.models.*;
import com.banking.utilities.*;
import com.banking.BankingSystem;
import java.util.*;

public class CustomerManager {
    private LinkedList<Customer> customers;
    private LinkedList<Account> accountList;
    private InputValidator validator;
    private BankingSystem bankingSystem;  // Reference for permission checks and audit logging
    private AccountManager accountMgr;    // Reference for account creation in onboarding workflow
    private int customerIdCounter = 1;    // Auto-generate Customer IDs (C001, C002, etc.)

    public CustomerManager(LinkedList<Customer> customers, LinkedList<Account> accountList, InputValidator validator) {
        this.customers = customers;
        this.accountList = accountList;
        this.validator = validator;
        this.bankingSystem = null;  // Set later via setBankingSystem()
        this.accountMgr = null;     // Set later via setAccountManager()

        // FIXED: Initialize counter properly by scanning existing customer IDs
        // OLD APPROACH (BUGGY): this.customerIdCounter = customers.size() + 1;
        // Problem: If you have customers C001, C002, C005 (size=3), counter becomes 4
        //          Then generateNextCustomerId() would create C004, but what if C004 exists?
        //
        // NEW APPROACH: Find the highest existing customer ID and set counter to max+1
        // This ensures no ID collisions even with gaps in the sequence
        int maxCustomerId = 0;
        for (Customer c : customers) {
            String id = c.getCustomerId();
            // Parse customer ID (format: C###)
            if (id.startsWith("C") && id.length() == 4) {
                try {
                    int num = Integer.parseInt(id.substring(1));  // Extract ### from C###
                    if (num > maxCustomerId) {
                        maxCustomerId = num;  // Track highest number found
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid customer IDs (safety check)
                }
            }
        }
        this.customerIdCounter = maxCustomerId + 1;  // Next customer ID will be max+1
    }


    public String generateNextCustomerId() {
        // Scan all customers to find highest ID number
        int maxId = 0;
        for (Customer c : this.customers) {
            String id = c.getCustomerId();
            if (id.startsWith("C") && id.length() == 4) {
                try {
                    // Extract numeric part: C001 -> 001 -> 1
                    int num = Integer.parseInt(id.substring(1));
                    if (num > maxId) {
                        maxId = num;  // Track highest number found
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs (safety check)
                }
            }
        }

        // Next ID is maxId + 1 (ensures no collisions even with gaps)
        this.customerIdCounter = maxId + 1;
        return "C" + String.format("%03d", this.customerIdCounter++);
    }

    public String generateNextProfileId() {
        int maxId = 0;

        // Find the highest existing profile ID number
        for (Customer c : this.customers) {
            if (c.getProfile() != null) {
                String profileId = c.getProfile().getProfileId();
                if (profileId != null && profileId.startsWith("P") && profileId.length() == 4) {
                    try {
                        int num = Integer.parseInt(profileId.substring(1));
                        if (num > maxId) {
                            maxId = num;
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid profile IDs
                    }
                }
            }
        }

        // Return next profile ID
        return "P" + String.format("%03d", maxId + 1);
    }


    public Customer createCustomer(String customerId, String name) {
        // Check for duplicate customer ID
        if (this.validateCustomerExists(customerId)) {
            System.out.println("✗ Customer ID already exists: " + customerId);
            return null;
        }

        try {
            Customer c = new Customer(customerId, name);
            this.customers.add(c);
            System.out.println("✓ Customer created: " + c);
            return c;
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error creating customer: " + e.getMessage());
            return null;
        }
    }

    public Customer findCustomer(String customerId) {
        for (Customer c : this.customers) {
            if (c.getCustomerId().equals(customerId)) {
                return c;
            }
        }
        return null;
    }

    public boolean validateCustomerExists(String customerId) {
        return this.findCustomer(customerId) != null;
    }

    public boolean deleteCustomer(String customerId) {
        // Calls this.findCustomer() to search LinkedList
        Customer customer = this.findCustomer(customerId);
        if (customer == null) {
            System.out.println("✗ Customer not found: " + customerId);
            return false;
        }

        // Remove all accounts associated with this customer (cascading delete)
        // Create copy to avoid ConcurrentModificationException during iteration
        LinkedList<Account> accountsCopy = new LinkedList<>(customer.getAccounts());
        boolean allAccountsDeleted = true;
        for (Account acc : accountsCopy) {
            // Calls this.deleteAccount() to remove from system and customer
            boolean accDeleted = this.deleteAccount(acc.getAccountNo());
            if (!accDeleted) {
                System.out.println("⚠ Warning: Failed to delete account " + acc.getAccountNo() + " for customer " + customerId);
                allAccountsDeleted = false;
            }
        }
        if (!allAccountsDeleted) {
            System.out.println("⚠ Warning: Not all accounts were successfully deleted");
        }

        // Remove customer from LinkedList
        this.customers.remove(customer);
        System.out.println("✓ Customer deleted: " + customerId);
        return true;
    }


    public boolean createOrUpdateProfile(String custId, String profileId, String address, String phone, String email, boolean allowReplace) {
        Customer customer = this.findCustomer(custId);
        if (customer == null) {
            System.out.println("✗ Customer not found");
            return false;
        }

        // Check if profile already exists
        if (customer.getProfile() != null && !allowReplace) {
            System.out.println("✗ Customer already has a profile. Use update instead.");
            return false;
        }

        // Check for duplicate profile ID (but allow if replacing customer's own profile)
        if (this.validator.profileIdExists(profileId) &&
            (customer.getProfile() == null || !customer.getProfile().getProfileId().equals(profileId))) {
            System.out.println("✗ Profile ID already exists: " + profileId);
            return false;
        }

        try {
            CustomerProfile profile = new CustomerProfile(profileId, address, phone, email);
            customer.setProfile(profile);
            System.out.println("✓ Profile created/updated for customer: " + customer.getName());
            System.out.println(profile);

            // Verify bidirectional relationship (demonstrates one-to-one)
            if (profile.getCustomer() != null && profile.getCustomer().equals(customer)) {
                System.out.println("→ One-to-one relationship successfully established!");
            }
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error creating profile: " + e.getMessage());
            return false;
        }
    }

    public void handleCreateCustomerProfile() {
        System.out.println("\n--- CREATE/UPDATE CUSTOMER PROFILE ---");
        String custId = this.validator.getValidatedInput("Customer ID",
                com.banking.utilities.ValidationPatterns.CUSTOMER_ID_PATTERN,
                "(format: " + com.banking.utilities.ValidationPatterns.CUSTOMER_ID_FORMAT + " e.g., C001)");
        if (custId == null) return;

        Customer customer = this.findCustomer(custId);
        if (customer == null) {
            System.out.println("✗ Customer not found");
            return;
        }

        // Check if profile already exists
        if (customer.getProfile() != null) {
            System.out.println("⚠ This customer already has a profile.");
            if (!this.validator.confirmAction("Do you want to replace it?")) {
                System.out.println("→ Operation cancelled.");
                return;
            }
        }

        String profileId = this.validator.getValidatedInput("Profile ID",
                com.banking.utilities.ValidationPatterns.PROFILE_ID_PATTERN,
                "(format: " + com.banking.utilities.ValidationPatterns.PROFILE_ID_FORMAT + " e.g., P001)");
        if (profileId == null) return;

        // Get profile details using unified helper
        String[] profileDetails = this.promptForProfileDetails();
        if (profileDetails == null) return;

        // Create or update profile
        this.createOrUpdateProfile(custId, profileId, profileDetails[0], profileDetails[1], profileDetails[2], true);
    }

    public void handleUpdateCustomerProfile() {
        System.out.println("\n--- UPDATE CUSTOMER PROFILE ---");

        Customer customer = this.validator.getValidatedCustomerWithProfile();
        if (customer == null) return;

        CustomerProfile profile = customer.getProfile();
        System.out.println("\nCurrent Profile:");
        System.out.println("Address: " + profile.getAddress());
        System.out.println("Phone: " + profile.getPhone());
        System.out.println("Email: " + profile.getEmail());

        System.out.println("\n--- What would you like to update? ---");
        System.out.println("1. Address");
        System.out.println("2. Phone");
        System.out.println("3. Email");
        System.out.println("0. Cancel");
        System.out.print("→ Choice: ");

        // FIXED: Use validator's scanner instead of passed parameter
        Scanner sc = this.validator.getScanner();
        try {
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    String newAddress = this.validator.getValidatedString("New Address");
                    if (newAddress != null) {
                        profile.setAddress(newAddress);
                        System.out.println("✓ Address updated");
                    }
                    break;
                case 2:
                    String newPhone = this.validator.getValidatedString("New Phone");
                    if (newPhone != null) {
                        profile.setPhone(newPhone);
                        System.out.println("✓ Phone updated");
                    }
                    break;
                case 3:
                    String newEmail = this.validator.getValidatedString("New Email");
                    if (newEmail != null) {
                        profile.setEmail(newEmail);
                        System.out.println("✓ Email updated");
                    }
                    break;
                case 0:
                    System.out.println("Cancelled");
                    break;
                default:
                    System.out.println("✗ Invalid choice");
            }
        } catch (InputMismatchException e) {
            System.out.println("✗ Invalid input");
            sc.nextLine();
        }
    }


    public void handleCreateCustomer() {
        System.out.println("\n--- CREATE CUSTOMER (Auto-Generation) ---");

        // Step 1: Get customer name (only required input)
        // Calls InputValidator.getValidatedString() from this.validator
        String custName = this.validator.getValidatedString("Customer Name");
        if (custName == null) return;

        // Step 2: Auto-generate Customer ID
        // Calls this.generateNextCustomerId() to create unique ID (C###)
        String custId = this.generateNextCustomerId();
        System.out.println("✓ Auto-generated Customer ID: " + custId);

        // Step 3: Create the Customer in the system
        // Calls this.createCustomer() to add customer to LinkedList
        Customer newCustomer = this.createCustomer(custId, custName);
        if (newCustomer == null) {
            System.out.println("✗ Failed to create customer");
            return;
        }

        // Step 4: Get AuthenticationManager for username/password generation
        // Calls BankingSystem.getAuthenticationManager()
        com.banking.managers.AuthenticationManager authManager = this.bankingSystem.getAuthenticationManager();
        if (authManager == null) {
            System.out.println("✗ Authentication system not available");
            return;
        }

        // Step 5: Auto-generate username and password
        // Calls AuthenticationManager.generateUsername() - converts "John Doe" to "john_doe"
        String username = authManager.generateUsername(custName);
        // Calls AuthenticationManager.generateTemporaryPassword() - format: "Welcomexx####"
        String tempPassword = authManager.generateTemporaryPassword(username);

        // Step 6: Create UserAccount (linked to this customer)
        // Calls BankingSystem.registerUser() to add to user registry
        com.banking.auth.UserAccount userAccount = new com.banking.auth.UserAccount(username, tempPassword, custId);
        if (!this.bankingSystem.registerUser(userAccount)) {
            System.out.println("✗ Failed to create login account for customer");
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                   CUSTOMER CREATION SUCCESSFUL                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println("\n✓ Customer Information:");
        System.out.println("  • Customer ID:     " + custId);
        System.out.println("  • Customer Name:   " + custName);
        System.out.println("\n✓ Login Credentials (System Generated):");
        System.out.println("  • Username:        " + username);
        System.out.println("  • Temp Password:   " + tempPassword);
        System.out.println("  • Note: Customer MUST change password on first login\n");

        // Log the action
        InputValidator.safeLogAction(bankingSystem, "CREATE_CUSTOMER",
                "Customer ID: " + custId + " Name: " + custName + " Username: " + username);

        // Step 7-10: Integrated Onboarding Workflow
        boolean profileCreated = this.promptAndCreateProfile(newCustomer);

        boolean accountCreated = false;
        if (profileCreated) {
            accountCreated = this.promptAndCreateAccount(newCustomer);
        }

        // Display Onboarding Summary
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    ONBOARDING COMPLETE                          ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println("\n✓ Summary:");
        System.out.println("  • Customer: " + custName + " (" + custId + ")");
        if (profileCreated) {
            String profileId = newCustomer.getProfile() != null ? newCustomer.getProfile().getProfileId() : "N/A";
            System.out.println("  • Profile: " + profileId + " ✓ CREATED");
        } else {
            System.out.println("  • Profile: SKIPPED");
        }
        if (accountCreated) {
            System.out.println("  • Account: CREATED ✓");
        } else {
            System.out.println("  • Account: NOT CREATED (Optional)");
        }
        System.out.println("\nCustomer is ready to use the banking system!\n");
    }

    public void handleViewCustomerDetails() {
        System.out.println("\n--- VIEW CUSTOMER DETAILS ---");

        Customer customer = this.validator.getValidatedCustomer();
        if (customer == null) return;

        System.out.println("\n=== CUSTOMER INFORMATION ===");
        System.out.println("Customer ID: " + customer.getCustomerId());
        System.out.println("Name: " + customer.getName());
        System.out.println("Number of Accounts: " + customer.getAccounts().size());

        if (customer.getProfile() != null) {
            CustomerProfile profile = customer.getProfile();
            System.out.println("\n--- PROFILE ---");
            System.out.println("Profile ID: " + profile.getProfileId());
            System.out.println("Address: " + profile.getAddress());
            System.out.println("Phone: " + profile.getPhone());
            System.out.println("Email: " + profile.getEmail());
        } else {
            System.out.println("\n--- PROFILE ---");
            System.out.println("No profile information available");
        }

        if (!customer.getAccounts().isEmpty()) {
            System.out.println("\n--- ACCOUNTS ---");
            for (Account acc : customer.getAccounts()) {
                System.out.println("  • " + acc.getDetails());
            }
        }
    }

    public void handleViewAllCustomers() {
        System.out.println("\n=== ALL CUSTOMERS ===");
        if (this.customers.isEmpty()) {
            System.out.println("No customers in the system.");
        } else {
            for (Customer c : this.customers) {
                System.out.println(c);
            }
        }
    }

    public void handleDeleteCustomer() {
        System.out.println("\n--- DELETE CUSTOMER ---");

        Customer customer = this.validator.getValidatedCustomer(
                "✗ Customer not found. Cannot delete non-existent customer.");
        if (customer == null) return;

        int accountCount = customer.getAccounts().size();

        // Warn about cascading delete
        if (accountCount > 0) {
            System.out.println("⚠ WARNING: This customer has " + accountCount + " account(s).");
            System.out.println("⚠ Deleting the customer will also delete all their accounts!");
        }

        if (!this.validator.confirmAction("Are you sure you want to delete this customer?")) {
            System.out.println("→ Deletion cancelled.");
            return;
        }

        String custId = customer.getCustomerId();
        boolean deleted = this.deleteCustomer(custId);
        if (deleted) {
            System.out.println("✓ Customer and all associated accounts deleted successfully!");
            InputValidator.safeLogAction(bankingSystem, "DELETE_CUSTOMER", "Customer ID: " + custId);
        } else {
            System.out.println("✗ Failed to delete customer. Please try again.");
        }
    }


    private String[] promptForProfileDetails() {
        String address = this.validator.getValidatedString("Address");
        if (address == null) return null;

        String phone = this.validator.getValidatedString("Phone Number (min 10 digits)");
        if (phone == null) return null;

        String email = this.validator.getValidatedString("Email");
        if (email == null) return null;

        return new String[]{address, phone, email};
    }

    private boolean createAndLinkProfile(Customer customer, String profileId, String address, String phone, String email) {
        try {
            CustomerProfile profile = new CustomerProfile(profileId, address, phone, email);
            customer.setProfile(profile);
            System.out.println("✓ Profile created and linked to customer!");
            System.out.println("  • Profile ID: " + profileId);
            System.out.println("  • Email: " + email);
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error creating profile: " + e.getMessage());
            return false;
        }
    }


    public boolean promptAndCreateProfile(Customer customer) {
        System.out.println("\n--- NEXT STEP: CREATE PROFILE ---");

        // Auto-generate next profile ID
        String profileId = this.generateNextProfileId();
        System.out.println("✓ Profile ID auto-generated: " + profileId);

        // Get profile details using unified helper
        String[] profileDetails = this.promptForProfileDetails();
        if (profileDetails == null) {
            System.out.println("✗ Profile creation cancelled.");
            return false;
        }

        // Create and link profile using unified helper
        return this.createAndLinkProfile(customer, profileId, profileDetails[0], profileDetails[1], profileDetails[2]);
    }

    public boolean promptAndCreateAccount(Customer customer) {
        System.out.println("\n--- NEXT STEP: CREATE ACCOUNT (Optional) ---");

        // Ask if they want to create account
        if (!this.validator.confirmAction("Would you like to create an account for this customer now?")) {
            System.out.println("→ Skipping account creation. You can create accounts later from the menu.");
            return false;
        }

        // Show account type menu
        System.out.println("\nSelect Account Type:");
        System.out.println("  1. Savings (3.0% interest)");
        System.out.println("  2. Checking ($500 overdraft)");
        System.out.print("→ Enter choice (1 or 2): ");

        try {
            int choice = this.validator.getValidatedInt("account type choice (1-2)");
            if (choice == 0) return false;  // Validation failed or cancelled

            String accountType;
            if (choice == 1) {
                accountType = "SAVINGS";
            } else if (choice == 2) {
                accountType = "CHECKING";
            } else {
                System.out.println("✗ Invalid choice. Please select 1 or 2.");
                return false;
            }

            // Create account using unified helper (auto-generates number, displays result, logs action)
            if (this.accountMgr != null) {
                Account createdAccount = this.accountMgr.createAndDisplayAccount(customer, accountType);
                return createdAccount != null;
            } else {
                System.out.println("✗ Account manager not available.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Error creating account: " + e.getMessage());
            return false;
        }
    }


    private boolean deleteAccount(String accountNo) {
        Account account = AccountUtils.findAccount(this.accountList, accountNo);
        if (account == null) {
            return false;
        }

        this.accountList.remove(account);
        Customer owner = account.getOwner();
        if (owner == null) {
            System.out.println("⚠ Warning: Account had no owner");
            return false;
        }
        boolean removedFromCustomer = owner.removeAccount(accountNo);
        if (!removedFromCustomer) {
            System.out.println("⚠ Warning: Account was removed from system but not from customer record");
            return false;
        }
        return true;
    }


    public void setBankingSystem(BankingSystem bankingSystem) {
        this.bankingSystem = bankingSystem;
    }

    public void setAccountManager(AccountManager accountMgr) {
        this.accountMgr = accountMgr;
    }

}
