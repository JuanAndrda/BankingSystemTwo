package com.banking.managers;

import com.banking.models.*;
import com.banking.utilities.*;
import com.banking.auth.User;
import com.banking.auth.UserRole;
import com.banking.auth.UserAccount;
import com.banking.BankingSystem;
import java.util.*;

public class AccountManager {
    private LinkedList<Customer> customers;
    private LinkedList<Account> accountList;
    private InputValidator validator;
    private BankingSystem bankingSystem;  // Reference for permission checks and audit logging
    private int accountCounter = 1;  // Auto-generate Account Numbers (ACC001, ACC002, etc.)

    public AccountManager(LinkedList<Customer> customers, LinkedList<Account> accountList, InputValidator validator) {
        this.customers = customers;
        this.accountList = accountList;
        this.validator = validator;
        this.bankingSystem = null;  // Set later via setBankingSystem()

        // FIXED: Initialize counter properly by scanning existing account numbers
        // OLD APPROACH (BUGGY): this.accountCounter = accountList.size() + 1;
        // Problem: If you have accounts ACC001, ACC002, ACC005 (size=3), counter becomes 4
        //          Then generateNextAccountNumber() would create ACC004, but what if ACC004 exists?
        //
        // NEW APPROACH: Find the highest existing account number and set counter to max+1
        // This ensures no ID collisions even with gaps in the sequence
        int maxAccountNum = 0;
        for (Account a : accountList) {
            String accNo = a.getAccountNo();
            // Parse account number (format: ACC###)
            if (accNo.startsWith("ACC") && accNo.length() == 6) {
                try {
                    int num = Integer.parseInt(accNo.substring(3));  // Extract ### from ACC###
                    if (num > maxAccountNum) {
                        maxAccountNum = num;  // Track highest number found
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid account numbers (safety check)
                }
            }
        }
        this.accountCounter = maxAccountNum + 1;  // Next account number will be max+1
    }


    public String generateNextAccountNumber() {
        // Find the highest existing account number
        int maxNum = 0;
        for (Account a : this.accountList) {
            String accNo = a.getAccountNo();
            if (accNo.startsWith("ACC") && accNo.length() == 6) {
                try {
                    int num = Integer.parseInt(accNo.substring(3));
                    if (num > maxNum) {
                        maxNum = num;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid account numbers
                }
            }
        }

        // Next account number is maxNum + 1
        this.accountCounter = maxNum + 1;
        return "ACC" + String.format("%03d", this.accountCounter++);
    }


    public Account createAccount(String customerId, String accountType, String accountNo) {
        // Calls this.findCustomer() to search LinkedList
        Customer customer = this.findCustomer(customerId);
        if (customer == null) {
            System.out.println("✗ Customer not found: " + customerId);
            return null;
        }

        // Calls this.validateAccountExists() to prevent duplicates
        if (this.validateAccountExists(accountNo)) {
            System.out.println("✗ Account number already exists: " + accountNo);
            return null;
        }

        try {
            Account account = null;
            if (accountType.equalsIgnoreCase("SAVINGS")) {
                // Creates SavingsAccount (polymorphic - extends Account)
                account = new SavingsAccount(accountNo, customer, 0.03);  // 3% interest
            } else if (accountType.equalsIgnoreCase("CHECKING")) {
                // Creates CheckingAccount (polymorphic - extends Account)
                account = new CheckingAccount(accountNo, customer, 500.0);  // $500 overdraft limit
            } else {
                System.out.println("✗ Invalid account type: " + accountType);
                System.out.println("  Must be either SAVINGS or CHECKING");
                return null;
            }

            // Note: No null check needed here - 'new' operator cannot return null
            // If validation fails, constructor throws IllegalArgumentException (caught below)

            // Add to system LinkedList
            this.accountList.add(account);
            // Calls Customer.addAccount() to establish bidirectional relationship
            customer.addAccount(account);
            // Calls Account.getDetails() - polymorphic (SavingsAccount vs CheckingAccount)
            System.out.println("✓ Account created: " + account.getDetails());
            return account;
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error creating account: " + e.getMessage());
            return null;
        }
    }

    public Account createAndDisplayAccount(Customer customer, String accountType) {
        if (customer == null) {
            System.out.println("✗ Customer is null");
            return null;
        }

        // Auto-generate account number
        String accountNo = this.generateNextAccountNumber();

        // Create the account using core CRUD method
        Account account = this.createAccount(customer.getCustomerId(), accountType, accountNo);
        if (account != null) {
            System.out.println("✓ Account successfully created and linked to customer!");
            System.out.println("  • Account Number: " + accountNo);
            System.out.println("  • Type: " + accountType);
            System.out.println("  • Balance: $0.00");

            // Log the action
            if (this.bankingSystem != null && this.bankingSystem.getCurrentUser() != null) {
                this.bankingSystem.logAction("CREATE_ACCOUNT",
                        "Account: " + accountNo + " Type: " + accountType + " Customer: " + customer.getCustomerId());
            }
        }
        return account;
    }

    public boolean deleteAccount(String accountNo) {
        Account account = this.findAccount(accountNo);
        if (account == null) {
            System.out.println("✗ Account not found: " + accountNo);
            return false;
        }

        this.accountList.remove(account);
        Customer owner = account.getOwner();
        if (owner != null) {
            owner.removeAccount(accountNo);
        } else {
            System.out.println("⚠ Warning: Account had no owner, only removed from system");
        }
        System.out.println("✓ Account deleted: " + accountNo);
        return true;
    }

    public Account findAccount(String accountNo) {
        return AccountUtils.findAccount(this.accountList, accountNo);
    }

    public boolean validateAccountExists(String accountNo) {
        return this.findAccount(accountNo) != null;
    }


    private void displayAccounts(String title) {
        System.out.println("\n=== ACCOUNTS (" + title + ") ===");
        if (this.accountList.isEmpty()) {
            System.out.println("No accounts in the system.");
        } else {
            for (Account acc : this.accountList) {
                System.out.println(acc.getDetails());
            }
        }
    }

    private void insertionSortByName(LinkedList<Account> accountList) {
        // For each unsorted element (starting at index 1, first element already "sorted")
        for (int i = 1; i < accountList.size(); i++) {
            // Get current account to insert into sorted portion
            Account currentAccount = accountList.get(i);
            String currentName = (currentAccount.getOwner() != null)
                    ? currentAccount.getOwner().getName() : "";

            // Find the correct position to insert currentAccount in sorted portion
            int j = i - 1;
            while (j >= 0) {
                Account compareAccount = accountList.get(j);
                String compareName = (compareAccount.getOwner() != null)
                        ? compareAccount.getOwner().getName() : "";

                // If current name comes before compare name alphabetically, shift right
                if (currentName.compareToIgnoreCase(compareName) < 0) {
                    accountList.set(j + 1, compareAccount);  // Shift element right
                    j--;  // Continue searching left
                } else {
                    break;  // Found correct position - stop searching
                }
            }

            // Insert current account at its correct sorted position
            accountList.set(j + 1, currentAccount);
        }
    }

    public void sortAccountsByName() {
        this.insertionSortByName(this.accountList);
        System.out.println("✓ Accounts sorted by customer name");
    }

    public void displayAccountsBeforeSort() {
        this.displayAccounts("BEFORE SORTING");
    }

    public void displayAccountsAfterSort() {
        this.displayAccounts("AFTER SORTING");
    }

    private void insertionSortByBalance(LinkedList<Account> accountList) {
        // Iterate through each account starting from index 1
        for (int i = 1; i < accountList.size(); i++) {
            Account currentAccount = accountList.get(i);
            double currentBalance = currentAccount.getBalance();

            // Find the correct position to insert currentAccount (descending order)
            int j = i - 1;
            while (j >= 0) {
                Account compareAccount = accountList.get(j);
                double compareBalance = compareAccount.getBalance();

                // If current balance is greater than compare balance, shift right (descending)
                if (currentBalance > compareBalance) {
                    accountList.set(j + 1, compareAccount);
                    j--;
                } else {
                    break;  // Found correct position
                }
            }

            // Insert current account at correct position
            accountList.set(j + 1, currentAccount);
        }
    }

    public void sortAccountsByBalance() {
        this.insertionSortByBalance(this.accountList);
        System.out.println("✓ Accounts sorted by balance (descending)");
    }

    public void handleSortByName() {
        this.displayAccountsBeforeSort();
        this.sortAccountsByName();
        this.displayAccountsAfterSort();
    }

    public void handleSortByBalance() {
        this.displayAccountsBeforeSort();
        this.sortAccountsByBalance();
        this.displayAccountsAfterSort();
    }


    public void applyInterestToAllSavings() {
        System.out.println("\n--- APPLY INTEREST TO SAVINGS ACCOUNTS ---");

        boolean foundSavings = false;
        for (Account acc : this.accountList) {
            if (acc instanceof SavingsAccount) {
                foundSavings = true;
                SavingsAccount savings = (SavingsAccount) acc;
                double oldBalance = savings.getBalance();
                savings.applyInterest();
                double newBalance = savings.getBalance();
                System.out.println(savings.getAccountNo() + " - Balance: $" +
                        String.format("%.2f", oldBalance) + " → $" + String.format("%.2f", newBalance));
            }
        }

        if (!foundSavings) {
            System.out.println("✗ No savings accounts found");
        }
    }

    public void handleApplyInterest() {
        this.applyInterestToAllSavings();
        InputValidator.safeLogAction(bankingSystem, "APPLY_INTEREST", "Interest applied to all savings accounts");
    }

    public boolean updateOverdraftLimit(String accountNo, double newLimit) {
        Account account = this.findAccount(accountNo);
        if (account == null) {
            System.out.println("✗ Account not found");
            return false;
        }

        if (!(account instanceof CheckingAccount)) {
            System.out.println("✗ This is not a checking account. Only checking accounts have overdraft limits.");
            return false;
        }

        CheckingAccount checking = (CheckingAccount) account;
        try {
            checking.setOverdraftLimit(newLimit);
            System.out.println("✓ Overdraft limit updated to: $" + checking.getOverdraftLimit());
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error: " + e.getMessage());
            return false;
        }
    }

    public void handleUpdateOverdraftLimit() {
        System.out.println("\n--- UPDATE OVERDRAFT LIMIT ---");
        String accNo = this.validator.getValidatedInput("Account Number",
                com.banking.utilities.ValidationPatterns.ACCOUNT_NO_PATTERN,
                "(format: " + com.banking.utilities.ValidationPatterns.ACCOUNT_NO_FORMAT + " e.g., ACC001)");
        if (accNo == null) return;

        Account account = this.findAccount(accNo);
        if (account == null) {
            System.out.println("✗ Account not found");
            return;
        }

        if (!(account instanceof CheckingAccount)) {
            System.out.println("✗ This is not a checking account. Only checking accounts have overdraft limits.");
            return;
        }

        CheckingAccount checking = (CheckingAccount) account;
        System.out.println("Current overdraft limit: $" + checking.getOverdraftLimit());

        Double newLimit = this.validator.getValidatedAmount("New Overdraft Limit");
        if (newLimit == null) return;

        boolean success = this.updateOverdraftLimit(accNo, newLimit);
        if (success) {
            System.out.println("✓ Overdraft limit updated successfully!");
            InputValidator.safeLogAction(bankingSystem, "UPDATE_OVERDRAFT_LIMIT", "Account: " + accNo + " New Limit: $" + newLimit);
        } else {
            System.out.println("✗ Failed to update overdraft limit. Please try again.");
        }
    }


    public void handleCreateAccount() {
        System.out.println("\n--- CREATE ACCOUNT ---");

        // Step 1: Get the customer
        Customer customer = this.validator.getValidatedCustomer(
                "✗ Customer not found. Please create customer first (Option 1).");
        if (customer == null) return;

        // Step 2: Get account type from user
        String type = this.validator.getValidatedAccountType("Account Type (SAVINGS/CHECKING)");
        if (type == null) return;

        // Step 3: Create account using unified helper (auto-generates number, displays result, logs action)
        this.createAndDisplayAccount(customer, type);
    }

    public void handleViewAllAccounts() {
        System.out.println("\n=== ALL ACCOUNTS ===");
        if (this.accountList.isEmpty()) {
            System.out.println("No accounts in the system.");
        } else {
            for (Account acc : this.accountList) {
                System.out.println(acc.getDetails());
            }
        }
    }

    public void handleViewAccountDetails() {
        System.out.println("\n--- VIEW ACCOUNT DETAILS ---");

        // Use access-controlled input for customers
        Account account = this.validator.getValidatedAccountWithAccessControl(this.bankingSystem.getCurrentUser());
        if (account == null) return;

        System.out.println("\n" + account.getDetails());
        System.out.println("Account Number: " + account.getAccountNo());
        System.out.println("Current Balance: $" + String.format("%.2f", account.getBalance()));

        Customer owner = account.getOwner();
        if (owner != null) {
            System.out.println("Owner: " + owner.getName());
            System.out.println("Owner ID: " + owner.getCustomerId());
        } else {
            System.out.println("Owner: NO OWNER");
            System.out.println("Owner ID: N/A");
        }

        if (account instanceof SavingsAccount) {
            SavingsAccount savings = (SavingsAccount) account;
            System.out.println("Account Type: SAVINGS");
            System.out.println("Interest Rate: " + (savings.getInterestRate() * 100) + "%");
        } else if (account instanceof CheckingAccount) {
            CheckingAccount checking = (CheckingAccount) account;
            System.out.println("Account Type: CHECKING");
            System.out.println("Overdraft Limit: $" + checking.getOverdraftLimit());
            System.out.println("Available Credit: $" + (checking.getBalance() + checking.getOverdraftLimit()));
        }

        LinkedList<Transaction> txHistory = account.getTransactionHistory();
        System.out.println("Total Transactions: " + (txHistory != null ? txHistory.size() : 0));
    }

    public void handleDeleteAccount() {
        System.out.println("\n--- DELETE ACCOUNT ---");

        String accNo = this.validator.getValidatedInput("Account Number",
                com.banking.utilities.ValidationPatterns.ACCOUNT_NO_PATTERN,
                "(format: " + com.banking.utilities.ValidationPatterns.ACCOUNT_NO_FORMAT + " e.g., ACC001)");
        if (accNo == null) return;

        // Check if account exists
        if (!this.validateAccountExists(accNo)) {
            System.out.println("✗ Account does not exist");
            return;
        }

        // Confirm deletion
        if (!this.validator.confirmAction("⚠ Are you sure you want to delete this account?")) {
            System.out.println("→ Deletion cancelled.");
            return;
        }

        boolean success = this.deleteAccount(accNo);
        if (success) {
            System.out.println("✓ Account deleted successfully!");
            InputValidator.safeLogAction(bankingSystem, "DELETE_ACCOUNT", "Account: " + accNo);
        } else {
            System.out.println("✗ Failed to delete account.");
        }
    }

    // Note: canAccessAccount() check is delegated to BankingSystem.canAccessAccount()
    // This ensures a single source of truth for authorization logic across the entire system.

    private Customer findCustomer(String customerId) {
        for (Customer c : this.customers) {
            if (c.getCustomerId().equals(customerId)) {
                return c;
            }
        }
        return null;
    }



    public void setBankingSystem(BankingSystem bankingSystem) {
        this.bankingSystem = bankingSystem;
    }

}
