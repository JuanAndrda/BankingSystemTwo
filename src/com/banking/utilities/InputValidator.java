package com.banking.utilities;

import com.banking.models.*;
import java.util.*;

// Input validation utility class
public class InputValidator {
    private LinkedList<Customer> customers;
    private LinkedList<Account> accountList;
    private Scanner sc;

    public InputValidator(LinkedList<Customer> customers, LinkedList<Account> accountList, Scanner sc) {
        this.customers = customers;
        this.accountList = accountList;
        this.sc = sc;
    }

    public Scanner getScanner() {
        return this.sc;
    }

    // Find customer by ID
    private Customer findCustomer(String customerId) {
        for (Customer c : this.customers) {
            if (c.getCustomerId().equals(customerId)) {
                return c;
            }
        }
        return null;
    }

    // Check if profile ID exists
    public boolean profileIdExists(String profileId) {
        for (Customer c : this.customers) {
            if (c.getProfile() != null && c.getProfile().getProfileId().equals(profileId)) {
                return true;
            }
        }
        return false;
    }

    // Get user input with cancellation support
    public String getCancellableInput(String prompt) {
        System.out.print(prompt + " (or type 'back' to cancel): ");
        String input = sc.nextLine().trim();
        if (input.equalsIgnoreCase("back")) {
            System.out.println("✗ Operation cancelled. Returning to menu...\n");
            return null;
        }
        return input;
    }

    // Get double input with cancellation support
    public Double getCancellableDouble(String prompt) {
        System.out.print(prompt + " (or type 'back' to cancel): ");
        String input = sc.nextLine().trim();
        if (input.equalsIgnoreCase("back")) {
            System.out.println("✗ Operation cancelled. Returning to menu...\n");
            return null;
        }
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid number format");
            return null;
        }
    }

    // Get validated input matching pattern
    public String getValidatedInput(String prompt, String pattern, String formatHint) {
        while (true) {
            String input = this.getCancellableInput(prompt + " " + formatHint);
            if (input == null) return null;  // User cancelled

            if (input.matches(pattern)) {
                return input;
            } else {
                System.out.println("✗ Invalid format! " + formatHint);
                System.out.println("   Please try again...\n");
            }
        }
    }

    // Get validated non-empty string
    public String getValidatedString(String prompt) {
        while (true) {
            String input = this.getCancellableInput(prompt);
            if (input == null) return null;

            if (!input.trim().isEmpty()) {
                return input.trim();
            } else {
                System.out.println("✗ This field cannot be empty!");
                System.out.println("   Please try again...\n");
            }
        }
    }

    // Get validated positive amount
    public Double getValidatedAmount(String prompt) {
        while (true) {
            Double amount = this.getCancellableDouble(prompt);
            if (amount == null) return null;

            if (amount > 0) {
                return amount;
            } else {
                System.out.println("✗ Amount must be greater than zero!");
                System.out.println("   Please try again...\n");
            }
        }
    }

    // Get validated account type (SAVINGS or CHECKING)
    public String getValidatedAccountType(String prompt) {
        while (true) {
            String type = this.getCancellableInput(prompt);
            if (type == null) return null;

            if (type.equalsIgnoreCase("SAVINGS") || type.equalsIgnoreCase("CHECKING")) {
                return type.toUpperCase();
            } else {
                System.out.println("✗ Invalid account type! Must be either SAVINGS or CHECKING");
                System.out.println("   Please try again...\n");
            }
        }
    }

    // Get validated customer (overloaded)
    public Customer getValidatedCustomer() {
        return this.getValidatedCustomer("✗ Customer not found");
    }

    // Get validated customer with custom error message (overloaded)
    public Customer getValidatedCustomer(String errorMessage) {
        String custId = this.getValidatedInput("Customer ID",
                ValidationPatterns.CUSTOMER_ID_PATTERN,
                "(format: " + ValidationPatterns.CUSTOMER_ID_FORMAT + " e.g., C001)");
        if (custId == null) return null;  // User cancelled

        Customer customer = this.findCustomer(custId);
        if (customer == null) {
            System.out.println(errorMessage);
        }
        return customer;
    }

    // Get validated account (overloaded)
    public Account getValidatedAccount() {
        return this.getValidatedAccount("✗ Account not found");
    }

    // Get validated account with custom error message (overloaded)
    public Account getValidatedAccount(String errorMessage) {
        String accNo = this.getValidatedInput("Account Number",
                ValidationPatterns.ACCOUNT_NO_PATTERN,
                "(format: " + ValidationPatterns.ACCOUNT_NO_FORMAT + " e.g., ACC001)");
        if (accNo == null) return null;  // User cancelled

        Account account = AccountUtils.findAccount(this.accountList, accNo);
        if (account == null) {
            System.out.println(errorMessage);
        }
        return account;
    }

    // Get validated customer with profile
    public Customer getValidatedCustomerWithProfile() {
        Customer customer = this.getValidatedCustomer(
                "✗ Customer not found. Cannot access profile.");
        if (customer == null) return null;

        if (customer.getProfile() == null) {
            System.out.println("✗ Customer has no profile. Please create one first (Option 15)");
            return null;
        }
        return customer;
    }

    // Get validated account with access control
    public Account getValidatedAccountWithAccessControl(com.banking.auth.User currentUser) {
        // Check if user is null (no one logged in)
        if (currentUser == null) {
            System.out.println("✗ No user logged in");
            return null;
        }

        // If user is admin, allow any account input
        if (currentUser.getUserRole() == com.banking.auth.UserRole.ADMIN) {
            return this.getValidatedAccount("✗ Account not found");
        }

        // For customers: show their linked customer's accounts and let them choose
        if (currentUser instanceof com.banking.auth.UserAccount) {
            com.banking.auth.UserAccount customerAccount = (com.banking.auth.UserAccount) currentUser;
            String linkedCustomerId = customerAccount.getLinkedCustomerId();

            // Find all accounts owned by this customer
            java.util.LinkedList<com.banking.models.Account> customerAccounts = new java.util.LinkedList<>();
            for (com.banking.models.Account acc : this.accountList) {
                if (acc.getOwner() != null && acc.getOwner().getCustomerId().equals(linkedCustomerId)) {
                    customerAccounts.add(acc);
                }
            }

            // If customer has no accounts, inform them
            if (customerAccounts.isEmpty()) {
                System.out.println("✗ You have no accounts in the system");
                return null;
            }

            // Show available accounts
            System.out.println("\nYour accounts:");
            int index = 1;
            for (com.banking.models.Account acc : customerAccounts) {
                System.out.println("  " + index + ". " + acc.getAccountNo() + " (" + acc.getDetails() + ")");
                index++;
            }

            // Let customer choose an account
            String accNo = this.getValidatedInput("Account Number",
                    ValidationPatterns.ACCOUNT_NO_PATTERN,
                    "(format: " + ValidationPatterns.ACCOUNT_NO_FORMAT + " e.g., ACC001)");
            if (accNo == null) return null;  // User cancelled

            // Verify the chosen account belongs to this customer
            for (com.banking.models.Account acc : customerAccounts) {
                if (acc.getAccountNo().equals(accNo)) {
                    return acc;
                }
            }

            System.out.println("✗ You can only access accounts that belong to you");
            return null;
        }

        System.out.println("✗ Cannot determine user type");
        return null;
    }

    // Prompt user for yes/no confirmation
    public boolean confirmAction(String message) {
        System.out.print(message + " (yes/no): ");
        String response = this.sc.nextLine().trim();
        return response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("y");
    }

    // Safely log action with null-safety check
    public static void safeLogAction(com.banking.BankingSystem bankingSystem, String action, String details) {
        if (bankingSystem != null && bankingSystem.getCurrentUser() != null) {
            bankingSystem.logAction(action, details);
        }
    }

    // Get validated integer input
    public int getValidatedInt(String prompt) {
        System.out.print(prompt + " (or type 'back' to cancel): ");
        String input = this.sc.nextLine().trim();
        if (input.equalsIgnoreCase("back")) {
            System.out.println("✗ Operation cancelled. Returning to menu...\n");
            return 0;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid number format");
            return 0;
        }
    }
}
