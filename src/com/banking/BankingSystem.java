package com.banking;

import com.banking.models.*;
import com.banking.managers.*;
import com.banking.auth.*;
import com.banking.utilities.*;
import java.util.*;

/**
 * Banking Management System - Main Controller Class
 *
 * This class demonstrates BOTH types of POLYMORPHISM:
 *
 * 1. COMPILE-TIME POLYMORPHISM (Method Overloading):
 *    - Account.withdraw() and other overloaded methods in manager classes
 *    - The compiler decides which method to call based on the arguments.
 *
 * 2. RUNTIME POLYMORPHISM (Method Override):
 *    - Account.withdraw() is overridden by SavingsAccount.withdraw() and CheckingAccount.withdraw()
 *    - Account.getDetails() is overridden by SavingsAccount.getDetails() and CheckingAccount.getDetails()
 *    - The JVM decides which method to call based on the actual object type at runtime.
 *
 * INHERITANCE is demonstrated through:
 *    - Account (parent) → SavingsAccount, CheckingAccount (children)
 *
 * ABSTRACTION is demonstrated through:
 *    - Account is abstract, cannot be instantiated directly
 *    - Must create SavingsAccount or CheckingAccount
 *
 * COMPOSITION is demonstrated through:
 *    - BankingSystem HAS-A InputValidator (NOT IS-A)
 *    - BankingSystem HAS-A CustomerManager
 *    - BankingSystem HAS-A AccountManager
 *    - BankingSystem HAS-A TransactionProcessor
 *
 * This refactoring shows the SINGLE RESPONSIBILITY PRINCIPLE:
 *    - BankingSystem: Orchestrates and manages main menu
 *    - InputValidator: All input validation logic
 *    - CustomerManager: All customer operations
 *    - AccountManager: All account operations
 *    - TransactionProcessor: All transaction operations
 */
public class BankingSystem {
    // ===== SHARED DATA COLLECTIONS =====
    private LinkedList<Customer> customers;
    private LinkedList<Account> accountList;

    // ===== MANAGER OBJECTS (COMPOSITION) =====
    private InputValidator validator;
    private CustomerManager customerMgr;
    private AccountManager accountMgr;
    private TransactionProcessor txProcessor;
    private AuthenticationManager authManager;  // Handles login and permissions

    // ===== I/O RESOURCE =====
    private Scanner sc;  // Scanner for user input (injected via constructor)
    private User currentUser;  // Currently logged-in user

    /**
     * Constructor: Initialize all manager objects with shared collections and Scanner.
     * Uses Constructor Injection pattern for Scanner (Dependency Injection).
     *
     * @param sc Scanner instance for user input (required for interactive menu)
     */
    public BankingSystem(Scanner sc) {
        // Store injected Scanner
        this.sc = sc;

        // Initialize shared collections
        this.customers = new LinkedList<>();
        this.accountList = new LinkedList<>();

        // Initialize manager objects with shared collections and Scanner
        this.validator = new InputValidator(this.customers, this.accountList, sc);
        this.customerMgr = new CustomerManager(this.customers, this.accountList, this.validator);
        this.accountMgr = new AccountManager(this.customers, this.accountList, this.validator);
        this.txProcessor = new TransactionProcessor(this.accountList, this.validator);

        // CIRCULAR DEPENDENCY RESOLUTION:
        // Why we use setBankingSystem() instead of passing 'this' in constructor:
        //
        // Problem: Managers need BankingSystem reference for:
        //   - canAccessAccount() - security checks
        //   - logAction() - audit trail logging
        //   - getCurrentUser() - get logged-in user
        //
        // If we passed 'this' in constructor:
        //   new CustomerManager(..., this)  // ❌ Would work, but fragile
        //
        // Issues with constructor approach:
        //   1. 'this' escapes before BankingSystem is fully constructed
        //   2. If manager calls bankingSystem.method() in constructor, authManager might be null
        //   3. Order of initialization matters - hard to maintain
        //
        // Solution: Two-phase initialization
        //   Phase 1: Create all manager objects (no circular refs)
        //   Phase 2: Wire up circular references via setters (safe, all objects exist)
        //
        // This is a common pattern in enterprise Java (Spring Framework uses this)
        this.customerMgr.setBankingSystem(this);
        this.customerMgr.setAccountManager(this.accountMgr);  // For onboarding workflow
        this.accountMgr.setBankingSystem(this);
        this.txProcessor.setBankingSystem(this);

        // Initialize authentication manager (no circular dependency)
        this.authManager = new AuthenticationManager();
        this.currentUser = null;
    }

    // ===== ROLE-BASED MENU DISPLAY METHODS =====
    /**
     * Displays the admin menu with all available options.
     * Shows customer operations, account operations, profile operations, and reports.
     * No conditional checks - all options displayed for admin users.
     *
     * Called by: this.runConsoleMenu() based on current user's role
     */
    private void displayAdminMenu() {
        System.out.println("│ FULL ADMIN MENU");
        System.out.println("│");

        // Customer Operations
        System.out.println("│ CUSTOMER OPERATIONS");
        System.out.println("│  1. Create Customer");
        System.out.println("│  2. View Customer Details");
        System.out.println("│  3. View All Customers");
        System.out.println("│  4. Delete Customer");
        System.out.println("│");

        // Account Operations
        System.out.println("│ ACCOUNT OPERATIONS");
        System.out.println("│  5. Create Account");
        System.out.println("│  6. View Account Details");
        System.out.println("│  7. View All Accounts");
        System.out.println("│  8. Delete Account");
        System.out.println("│  9. Update Overdraft Limit (Checking)");
        System.out.println("│");

        // Transaction Operations
        System.out.println("│ TRANSACTION OPERATIONS");
        System.out.println("│  10. Deposit Money");
        System.out.println("│  11. Withdraw Money");
        System.out.println("│  12. Transfer Money");
        System.out.println("│  13. View Transaction History");
        System.out.println("│");

        // Profile Operations
        System.out.println("│ PROFILE OPERATIONS");
        System.out.println("│  14. Create/Update Customer Profile");
        System.out.println("│  15. Update Profile Information");
        System.out.println("│");

        // Reports & Utilities
        System.out.println("│ REPORTS & UTILITIES");
        System.out.println("│  16. Apply Interest (All Savings Accounts)");
        System.out.println("│  17. Sort Accounts by Name");
        System.out.println("│  18. Sort Accounts by Balance");
        System.out.println("│  19. View Audit Trail (Admin Only)");
        System.out.println("│");

        // Session Management
        System.out.println("│ SESSION MANAGEMENT");
        System.out.println("│  0. Logout (Return to Login)");
        System.out.println("│  20. Exit Application");
    }

    /**
     * Displays the customer menu with limited options (ATM mode).
     * Shows only account and transaction operations.
     * No conditional checks - only relevant options displayed for customer users.
     *
     * Called by: this.runConsoleMenu() based on current user's role
     */
    private void displayCustomerMenu() {
        System.out.println("│ TRANSACTION MENU (ATM Mode)");
        System.out.println("│");

        // Account Operations (only View Account Details)
        System.out.println("│ ACCOUNT OPERATIONS");
        System.out.println("│  6. View Account Details");
        System.out.println("│");

        // Transaction Operations
        System.out.println("│ TRANSACTION OPERATIONS");
        System.out.println("│  10. Deposit Money");
        System.out.println("│  11. Withdraw Money");
        System.out.println("│  12. Transfer Money");
        System.out.println("│  13. View Transaction History");
        System.out.println("│");

        // Security Operations
        System.out.println("│ SECURITY OPERATIONS");
        System.out.println("│  21. Change Password");
        System.out.println("│");

        // Session Management
        System.out.println("│ SESSION MANAGEMENT");
        System.out.println("│  0. Logout (Return to Login)");
        System.out.println("│  20. Exit Application");
    }

    // ===== MAIN ENTRY POINT: CONSOLE MENU =====
    /**
     * Runs the interactive console menu for the Banking System.
     * Displays role-based menu options for all CRUD and transaction operations.
     * Uses switch statement to route to appropriate manager handler methods.
     * Scanner is injected via constructor (Dependency Injection).
     *
     * This method demonstrates:
     * - FACADE PATTERN: Simplified interface to complex subsystems
     * - RBAC (Role-Based Access Control): Different menus for different roles
     * - Polymorphic behavior: Different operations available per role
     *
     * @return MenuAction indicating whether user chose LOGOUT or EXIT_APPLICATION
     */
    public MenuAction runConsoleMenu() {
        // Require login before accessing menu
        User loggedInUser = this.login();
        if (loggedInUser == null) {
            System.out.println("✗ Unable to proceed without authentication.");
            return MenuAction.EXIT_APPLICATION;  // Exit after failed login attempts
        }

        // Scanner already initialized from constructor (via Dependency Injection)
        boolean running = true;

        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║   BANKING MANAGEMENT SYSTEM v2.0   ║");
        System.out.println("╚════════════════════════════════════╝\n");

        while (running) {
            System.out.println("\n┌─ MAIN MENU ─────────────────────────────────────");

            // Display menu based on user role (no conditional checks needed)
            if (this.currentUser.getUserRole() == UserRole.ADMIN) {
                displayAdminMenu();
            } else {
                displayCustomerMenu();
            }

            System.out.println("└─────────────────────────────────────────────────");
            System.out.print("→ Enter choice: ");

            try {
                int choice = this.sc.nextInt();
                this.sc.nextLine(); // ⚠️ CRITICAL: Consume leftover newline

                System.out.println(); // Add blank line for readability

                switch (choice) {
                    // ===== CUSTOMER OPERATIONS (Admin Only) =====
                    case 1:
                        // Create Customer - Admin only
                        if (this.currentUser.getUserRole() == UserRole.ADMIN) {
                            // Calls CustomerManager.handleCreateCustomer()
                            this.customerMgr.handleCreateCustomer();
                        } else {
                            System.out.println("✗ Invalid choice. Please try again.");
                        }
                        break;
                    case 2:
                        // View Customer Details - Admin only
                        if (this.currentUser.getUserRole() == UserRole.ADMIN) {
                            // Calls CustomerManager.handleViewCustomerDetails()
                            this.customerMgr.handleViewCustomerDetails();
                        } else {
                            System.out.println("✗ Invalid choice. Please try again.");
                        }
                        break;
                    case 3:
                        // View All Customers - Admin only
                        if (this.currentUser.getUserRole() == UserRole.ADMIN) {
                            // Calls CustomerManager.handleViewAllCustomers()
                            this.customerMgr.handleViewAllCustomers();
                        } else {
                            System.out.println("✗ Invalid choice. Please try again.");
                        }
                        break;
                    case 4:
                        // Delete Customer - Admin only
                        if (this.currentUser.getUserRole() == UserRole.ADMIN) {
                            // Calls CustomerManager.handleDeleteCustomer()
                            this.customerMgr.handleDeleteCustomer();
                        } else {
                            System.out.println("✗ Invalid choice. Please try again.");
                        }
                        break;

                    // ===== ACCOUNT OPERATIONS =====
                    case 5:
                        // Create Account - Admin only
                        if (this.currentUser.getUserRole() == UserRole.ADMIN) {
                            // Calls AccountManager.handleCreateAccount()
                            this.accountMgr.handleCreateAccount();
                        } else {
                            System.out.println("✗ Invalid choice. Please try again.");
                        }
                        break;
                    case 6:
                        // View Account Details - Both roles allowed
                        // Access control enforced inside handler via canAccessAccount()
                        // Calls AccountManager.handleViewAccountDetails()
                        this.accountMgr.handleViewAccountDetails();
                        break;
                    case 7:
                        // View All Accounts - Admin only
                        if (this.currentUser.getUserRole() == UserRole.ADMIN) {
                            // Calls AccountManager.handleViewAllAccounts()
                            this.accountMgr.handleViewAllAccounts();
                        } else {
                            System.out.println("✗ Invalid choice. Please try again.");
                        }
                        break;
                    case 8:
                        // Delete Account - Admin only
                        if (this.currentUser.getUserRole() == UserRole.ADMIN) {
                            // Calls AccountManager.handleDeleteAccount()
                            this.accountMgr.handleDeleteAccount();
                        } else {
                            System.out.println("✗ Invalid choice. Please try again.");
                        }
                        break;
                    case 9:
                        // Update Overdraft Limit - Admin only
                        if (this.currentUser.getUserRole() == UserRole.ADMIN) {
                            // Calls AccountManager.handleUpdateOverdraftLimit()
                            this.accountMgr.handleUpdateOverdraftLimit();
                        } else {
                            System.out.println("✗ Invalid choice. Please try again.");
                        }
                        break;

                    // ===== TRANSACTION OPERATIONS (Both Roles) =====
                    // Customers can only transact on their own accounts
                    // Access control enforced inside handlers via canAccessAccount()
                    case 10:
                        // Calls TransactionProcessor.handleDeposit()
                        this.txProcessor.handleDeposit();
                        break;
                    case 11:
                        // Calls TransactionProcessor.handleWithdraw()
                        this.txProcessor.handleWithdraw();
                        break;
                    case 12:
                        // Calls TransactionProcessor.handleTransfer()
                        this.txProcessor.handleTransfer();
                        break;
                    case 13:
                        // Calls TransactionProcessor.handleViewTransactionHistory()
                        this.txProcessor.handleViewTransactionHistory();
                        break;

                    // ===== PROFILE OPERATIONS (Admin Only) =====
                    case 14:
                        // Create Customer Profile - Admin only
                        if (this.currentUser.getUserRole() == UserRole.ADMIN) {
                            // Calls CustomerManager.handleCreateCustomerProfile()
                            this.customerMgr.handleCreateCustomerProfile();
                        } else {
                            System.out.println("✗ Invalid choice. Please try again.");
                        }
                        break;
                    case 15:
                        // Update Customer Profile - Admin only
                        if (this.currentUser.getUserRole() == UserRole.ADMIN) {
                            // Calls CustomerManager.handleUpdateCustomerProfile()
                            this.customerMgr.handleUpdateCustomerProfile();
                        } else {
                            System.out.println("✗ Invalid choice. Please try again.");
                        }
                        break;

                    // ===== REPORTS & UTILITIES (Admin Only) =====
                    case 16:
                        // Apply Interest - Admin only
                        if (this.currentUser.getUserRole() == UserRole.ADMIN) {
                            // Calls AccountManager.handleApplyInterest()
                            this.accountMgr.handleApplyInterest();
                        } else {
                            System.out.println("✗ Invalid choice. Please try again.");
                        }
                        break;
                    case 17:
                        // Sort Accounts by Name - Admin only
                        if (this.currentUser.getUserRole() == UserRole.ADMIN) {
                            // Calls AccountManager.handleSortByName()
                            this.accountMgr.handleSortByName();
                        } else {
                            System.out.println("✗ Invalid choice. Please try again.");
                        }
                        break;
                    case 18:
                        // Sort Accounts by Balance - Admin only
                        if (this.currentUser.getUserRole() == UserRole.ADMIN) {
                            // Calls AccountManager.handleSortByBalance()
                            this.accountMgr.handleSortByBalance();
                        } else {
                            System.out.println("✗ Invalid choice. Please try again.");
                        }
                        break;
                    case 19:
                        // Audit trail viewing - admin only
                        // Double-check permission for sensitive operation
                        // Calls this.hasPermission() to verify authorization
                        if (this.hasPermission("VIEW_AUDIT_TRAIL")) {
                            // Calls this.displayAuditTrail() to show audit log
                            this.displayAuditTrail();
                        } else {
                            System.out.println("✗ You do not have permission to view the audit trail.");
                            if (this.currentUser != null) {
                                // Calls this.logAction() to record denial
                                this.logAction("VIEW_AUDIT_DENIED", "User attempted to view audit trail without permission");
                            }
                        }
                        break;

                    // ===== SECURITY OPERATIONS (Both Roles) =====
                    case 21:
                        // Calls this.handleChangePassword() to update user password
                        this.handleChangePassword();
                        break;

                    // ===== SESSION MANAGEMENT (Both Roles) =====
                    case 0:
                        System.out.println("\n✓ Logging out...");
                        System.out.println("   Please wait while we log you out...\n");
                        this.logout();
                        return MenuAction.LOGOUT;  // Return to login screen

                    case 20:
                        System.out.println("\n✓ Thank you for using the Banking System!");
                        System.out.println("   Goodbye!\n");
                        this.logout();
                        return MenuAction.EXIT_APPLICATION;  // Exit program

                    default:
                        System.out.println("✗ Invalid choice. Try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("✗ Invalid input. Please enter a number.");
                this.sc.nextLine(); // clear buffer
            }
        }
        // This should not be reached, but return default action
        return MenuAction.LOGOUT;
    }

    // ===== APPLICATION LIFECYCLE =====

    /**
     * Starts the banking application with multi-user login loop.
     *
     * Handles the complete application lifecycle:
     * - Display login screen
     * - Call runConsoleMenu() for user to login and perform operations
     * - Check MenuAction result to determine next step:
     *   - If LOGOUT: Loop back to login screen (next user can login)
     *   - If EXIT_APPLICATION: Exit loop and terminate application
     *
     * This is the main entry point after demo data initialization.
     */
    public void startApplication() {
        boolean appRunning = true;

        while (appRunning) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║      BANKING SYSTEM - LOGIN         ║");
            System.out.println("╚════════════════════════════════════╝");

            // Run console menu (handles login + menu + returns action)
            MenuAction action = runConsoleMenu();

            // Check what user chose
            if (action == MenuAction.EXIT_APPLICATION) {
                appRunning = false;  // Exit the login loop
            }
            // If action is LOGOUT, loop continues and user sees login screen again
        }

        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║        Thank you. Goodbye!         ║");
        System.out.println("╚════════════════════════════════════╝\n");
    }

    // ===== MENU HELPER: ROLE-BASED ACCESS FILTERING =====
    // REMOVED: getPermissionForOption() and shouldShowMenuOption() methods
    //
    // These methods were part of an abandoned redundant permission-checking approach.
    // They checked permissions in the switch statement even though role-based menus
    // (displayAdminMenu vs displayCustomerMenu) already filter options by role.
    //
    // Access control is now enforced at two proper layers:
    // 1. Menu Display Layer: Only show options user has access to
    // 2. Handler Layer: Check canAccessAccount() for shared operations (like viewing accounts)
    //
    // See docs/technical/CODE_FIXES_SUMMARY.md for detailed explanation.

    // ===== AUTHENTICATION FLOW =====

    /**
     * Initiates the login process.
     * Prompts user for username/password with 3-attempt limit.
     *
     * @return the logged-in User, or null if login failed
     */
    public User login() {
        this.currentUser = this.authManager.login(this.sc);
        return this.currentUser;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        this.authManager.logout();
        this.currentUser = null;
    }

    /**
     * Registers a new user in the authentication system.
     * Used during demo data setup to create admin and customer accounts.
     *
     * @param user the user to register
     * @return true if registration successful
     */
    public boolean registerUser(User user) {
        return this.authManager.registerUser(user);
    }

    /**
     * Returns the currently logged-in user.
     */
    public User getCurrentUser() {
        return this.currentUser;
    }

    /**
     * Returns the authentication manager (for username/password generation).
     * Used by customer and account managers for auto-generation.
     */
    public AuthenticationManager getAuthenticationManager() {
        return this.authManager;
    }

    // ===== AUTHORIZATION =====

    /**
     * Checks if current user has a specific permission.
     */
    public boolean hasPermission(String permission) {
        return this.authManager.hasPermission(permission);
    }

    /**
     * Checks if the current user can access a specific account.
     * Implements role-based access control:
     * - ADMIN users can access ANY account
     * - CUSTOMER users can only access their own linked account
     *
     * This is the centralized access control method - all access checks should use this.
     * Prevents customers from viewing/modifying other customers' accounts.
     *
     * Method calls:
     * - Uses AccountUtils.findAccount() to locate the account
     * - Calls Account.getOwner() to get account owner
     * - Calls Customer.getCustomerId() to compare ownership
     *
     * @param accountNo the account number to check access for
     * @return true if current user can access this account, false otherwise
     */
    public boolean canAccessAccount(String accountNo) {
        if (this.currentUser == null) {
            return false;
        }

        User user = this.currentUser;

        // Admins can access any account
        if (user.getUserRole() == UserRole.ADMIN) {
            return true;
        }

        // Customers can access accounts owned by their linked customer
        if (user instanceof UserAccount) {
            UserAccount customerAccount = (UserAccount) user;
            String linkedCustomerId = customerAccount.getLinkedCustomerId();

            // Find the account and check if its owner matches the linked customer ID
            Account account = AccountUtils.findAccount(this.accountList, accountNo);
            if (account != null && account.getOwner() != null) {
                return account.getOwner().getCustomerId().equals(linkedCustomerId);
            }
            return false;
        }

        return false;
    }

    // ===== BUSINESS OPERATIONS (DELEGATED) =====
    // These methods delegate to the appropriate manager objects.
    // This allows Main.java to call simple methods without needing to access managers directly.

    /**
     * Creates a new customer (delegates to CustomerManager).
     */
    public Customer createCustomer(String customerId, String name) {
        return this.customerMgr.createCustomer(customerId, name);
    }

    /**
     * Creates a new account (delegates to AccountManager).
     */
    public Account createAccount(String customerId, String accountType, String accountNo) {
        return this.accountMgr.createAccount(customerId, accountType, accountNo);
    }

    /**
     * Deposits money into an account (delegates to TransactionProcessor).
     */
    public boolean deposit(String accountNo, double amount) {
        return this.txProcessor.deposit(accountNo, amount);
    }

    /**
     * Withdraws money from an account (delegates to TransactionProcessor).
     */
    public boolean withdraw(String accountNo, double amount) {
        return this.txProcessor.withdraw(accountNo, amount);
    }

    /**
     * Transfers money between two accounts (delegates to TransactionProcessor).
     */
    public boolean transfer(String fromAccountNo, String toAccountNo, double amount) {
        return this.txProcessor.transfer(fromAccountNo, toAccountNo, amount);
    }

    // ===== AUDIT & ADMIN UTILITIES =====

    /**
     * Logs an action to the audit trail.
     */
    public void logAction(String action, String details) {
        if (currentUser != null) {
            this.authManager.logAction(currentUser.getUsername(), currentUser.getUserRole(), action, details);
        }
    }

    /**
     * Handler: Change password for current user.
     * Customers must change their auto-generated password on first login.
     * Uses AuthenticationManager to create new User object (immutable pattern).
     *
     * Method calls:
     * - Calls User.getUsername() to get current username
     * - Calls InputValidator.getCancellableInput() for password prompts (twice)
     * - Calls AuthenticationManager.changePassword() to create new User object
     * - Calls User.setPasswordChangeRequired() to mark password as changed
     */
    private void handleChangePassword() {
        System.out.println("\n--- CHANGE PASSWORD ---");

        // Get current user
        if (this.currentUser == null) {
            System.out.println("✗ No user logged in");
            return;
        }

        String username = this.currentUser.getUsername();

        // Step 1: Prompt for current password
        // Calls InputValidator.getCancellableInput() from this.validator
        String oldPassword = this.validator.getCancellableInput("Enter current password");
        if (oldPassword == null) {
            return;  // User cancelled
        }

        // Step 2: Prompt for new password
        // Calls InputValidator.getCancellableInput() from this.validator
        String newPassword = this.validator.getCancellableInput("Enter new password");
        if (newPassword == null) {
            return;  // User cancelled
        }

        // Step 3: Call AuthenticationManager to change password (creates new User object)
        // Calls AuthenticationManager.changePassword() - immutable pattern
        User newUser = this.authManager.changePassword(username, oldPassword, newPassword);

        if (newUser == null) {
            System.out.println("✗ Password change failed. Please try again.");
            return;
        }

        // Step 4: Update currentUser reference to new User object
        this.currentUser = newUser;

        // Step 5: Set passwordChangeRequired to false (password has been changed)
        // Calls User.setPasswordChangeRequired() on new user object
        this.currentUser.setPasswordChangeRequired(false);

        // Step 6: Success message
        System.out.println("\n✓ Password changed successfully!");
        System.out.println("  • You are still logged in with your new password");
        System.out.println("  • New password will be used for all future logins\n");
    }

    /**
     * Displays the audit trail (admin only).
     */
    public void displayAuditTrail() {
        this.authManager.displayAuditTrail();
    }

}
