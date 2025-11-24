# Customer & User Account Linking Explanation

## 📚 Table of Contents
1. [Overview](#overview)
2. [The Customer Creation Process](#the-customer-creation-process)
3. [User Account Creation & Linking](#user-account-creation--linking)
4. [How Authentication Works](#how-authentication-works)
5. [Multiple Accounts Per Customer](#multiple-accounts-per-customer)
6. [Complete Flow Diagrams](#complete-flow-diagrams)
7. [Code Examples](#code-examples)

---

## Overview

This document explains how the Banking System creates customers and links them to user accounts, allowing customers to log in and access their multiple bank accounts.

### The Key Relationship:

```
Admin → Creates Customer → Creates User Account → Customer Can Login → Access Multiple Accounts
```

### Three Main Entities:

1. **Customer** - The bank customer (stores personal info, owns accounts)
2. **UserAccount** - Login credentials (username/password) linked to a customer
3. **Account** - Bank accounts (Savings/Checking) owned by customer

---

## The Customer Creation Process

### 🎯 Where It Happens

**File:** `CustomerManager.java`
**Method:** `createCustomer(String customerId, String name)` - Lines vary

### Step-by-Step Flow:

```
Admin logs in → Selects "Create Customer" → Enters customer details →
System creates Customer object → Stores in customer list → Returns Customer
```

### The Code:

#### Step 1: Admin Creates Customer
**Location:** `CustomerManager.java`

```java
public Customer createCustomer(String customerId, String name) {
    // Step 1: Check if customer already exists
    if (this.findCustomer(customerId) != null) {
        System.out.println("✗ Customer already exists: " + customerId);
        return null;
    }

    try {
        // ⭐ SOLVING: Create the Customer object
        Customer customer = new Customer(customerId, name);

        // Step 2: Add to customer list
        this.customers.add(customer);

        System.out.println("✓ Customer created: " + customer.getCustomerId() +
                          " - " + customer.getName());
        return customer;
    } catch (IllegalArgumentException e) {
        System.out.println("✗ Error creating customer: " + e.getMessage());
        return null;
    }
}
```

#### Step 2: Customer Object Created
**Location:** `Customer.java`

```java
public class Customer {
    private String customerId;        // Format: C001, C002, etc.
    private String name;              // Customer's full name
    private LinkedList<Account> accounts;      // Multiple accounts (1:M)
    private CustomerProfile profile;  // Customer profile (1:1)

    public Customer(String customerId, String name) {
        this.setCustomerId(customerId);  // Validates format (C###)
        this.setName(name);              // Validates not empty
        this.accounts = new LinkedList<>();  // Initialize empty account list
    }
}
```

**Example:**
```java
// Admin creates customer
Customer alice = customerManager.createCustomer("C001", "Alice Johnson");

// Now alice exists:
// - customerId: "C001"
// - name: "Alice Johnson"
// - accounts: [] (empty list, no accounts yet)
// - profile: null (no profile yet)
```

---

## User Account Creation & Linking

### 🎯 Where It Happens

**File:** `AuthenticationManager.java`
**Method:** `registerUser(User user)`

### The Magic: Linking UserAccount to Customer

#### What is UserAccount?

```java
public class UserAccount extends User {
    private String customerId;  // ⭐ THIS LINKS TO THE CUSTOMER!

    public UserAccount(String username, String password, String customerId) {
        super(username, password, UserRole.CUSTOMER);
        this.customerId = customerId;  // Store the customer ID
    }

    public String getCustomerId() {
        return this.customerId;  // Used to find which customer this user belongs to
    }
}
```

### Step-by-Step Flow:

```
Admin creates Customer "C001" → Admin/System registers UserAccount →
UserAccount stores customerId "C001" → User can login →
System finds Customer by customerId → Customer can access their accounts
```

### The Code:

#### Step 1: Register User Account (Manual or Automatic)
**Location:** `AuthenticationManager.java`

```java
public boolean registerUser(User user) {
    if (user == null) {
        System.out.println("✗ User cannot be null");
        return false;
    }

    // Check if username already exists
    for (User u : this.userRegistry) {
        if (u.getUsername().equalsIgnoreCase(user.getUsername())) {
            System.out.println("✗ Username already exists: " + user.getUsername());
            return false;
        }
    }

    // ⭐ SOLVING: Add user to registry
    this.userRegistry.add(user);
    System.out.println("✓ User registered: " + user.getUsername() +
                      " (Role: " + user.getRole() + ")");
    return true;
}
```

#### Step 2: User Account Created and Linked
**Location:** `Main.java` (Demo Setup)

```java
// After creating customer C001 (Alice Johnson)
Customer alice = bankingSystem.createCustomer("C001", "Alice Johnson");

// Create a user account linked to this customer
UserAccount aliceUser = new UserAccount("alice", "alice123", "C001");
                                                              // ↑
                                                    // Links to Customer C001!

// Register the user
bankingSystem.registerUser(aliceUser);
```

**Visual:**
```
┌────────────────┐              ┌──────────────────┐
│    Customer    │              │   UserAccount    │
├────────────────┤              ├──────────────────┤
│ customerId:    │              │ username: alice  │
│   "C001"       │ ←──────────  │ password: ****** │
│ name: "Alice"  │   linked by  │ customerId:      │
│ accounts: []   │              │   "C001"         │
└────────────────┘              └──────────────────┘
```

---

## How Authentication Works

### 🎯 The Login Process

**File:** `AuthenticationManager.java`
**Method:** `login(String username, String password)`

### Step-by-Step Flow:

```
User enters username/password → System searches user registry →
Validates password → Sets currentUser → User logged in!
```

### The Code:

#### Login Method
**Location:** `AuthenticationManager.java`

```java
public boolean login(String username, String password) {
    // Step 1: Find user by username
    for (User user : this.userRegistry) {
        if (user.getUsername().equalsIgnoreCase(username)) {

            // Step 2: Verify password
            if (user.getPassword().equals(password)) {
                // ⭐ SOLVING: Login successful - set current user
                this.currentUser = user;
                this.loginAttempts = 0;  // Reset failed attempts

                System.out.println("✓ Login successful! Welcome, " + username);
                System.out.println("  Role: " + user.getRole());

                // Log the action
                this.logAction(username, "LOGIN", "Successful login");
                return true;
            } else {
                // Password incorrect
                this.loginAttempts++;
                System.out.println("✗ Incorrect password");
                return false;
            }
        }
    }

    // Username not found
    System.out.println("✗ User not found: " + username);
    return false;
}
```

---

## Multiple Accounts Per Customer

### 🎯 How One Customer Has Many Accounts

**Relationship:** Customer (1) → Accounts (Many)

### The Code:

#### Step 1: Customer Can Have Multiple Accounts
**Location:** `Customer.java`

```java
public class Customer {
    private LinkedList<Account> accounts;  // ⭐ Can store MANY accounts

    public void addAccount(Account a) {
        if (a != null) {
            // Check for duplicates
            for (Account existing : this.accounts) {
                if (existing.getAccountNo().equals(a.getAccountNo())) {
                    System.out.println("✗ Account already added");
                    return;
                }
            }

            // ⭐ SOLVING: Add account to customer's list
            this.accounts.add(a);
            System.out.println("✓ Account " + a.getAccountNo() +
                              " added to customer " + this.name);
        }
    }

    public LinkedList<Account> getAccounts() {
        return this.accounts;  // Returns all accounts for this customer
    }
}
```

#### Step 2: Creating Multiple Accounts for One Customer
**Location:** `AccountManager.java` or `Main.java`

```java
// Customer C001 (Alice) already exists
Customer alice = customerManager.findCustomer("C001");

// Create multiple accounts for Alice
Account savingsAccount = new SavingsAccount("ACC001", alice, 0.03);
Account checkingAccount = new CheckingAccount("ACC002", alice, 500.0);
Account anotherSavings = new SavingsAccount("ACC003", alice, 0.05);

// Add all accounts to Alice
alice.addAccount(savingsAccount);    // Alice now has 1 account
alice.addAccount(checkingAccount);   // Alice now has 2 accounts
alice.addAccount(anotherSavings);    // Alice now has 3 accounts

// Each account also knows who owns it
System.out.println(savingsAccount.getOwner().getName());   // "Alice Johnson"
System.out.println(checkingAccount.getOwner().getName());  // "Alice Johnson"
```

**Visual:**
```
                Customer: Alice (C001)
                        |
        +---------------+---------------+
        |               |               |
        ▼               ▼               ▼
   ACC001          ACC002          ACC003
  (Savings)       (Checking)      (Savings)
  $1000           $500            $2000
```

---

## Complete Flow Diagrams

### Flow 1: Admin Creates Customer with Login

```
┌─────────────────────────────────────────────────────────────┐
│ Step 1: Admin logs in                                       │
│   Username: admin                                           │
│   Password: admin123                                        │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 2: Admin selects "Create Customer"                     │
│   Enters: Customer ID (C001), Name (Alice Johnson)          │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 3: CustomerManager.createCustomer()                    │
│   ⭐ SOLVING: new Customer("C001", "Alice Johnson")         │
│   Adds to customer list                                     │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 4: Create UserAccount for this customer                │
│   new UserAccount("alice", "alice123", "C001")              │
│                                          ↑                   │
│                          Links to Customer C001!            │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 5: AuthenticationManager.registerUser()                │
│   ⭐ SOLVING: Adds UserAccount to userRegistry              │
│   Now user can login!                                       │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
                 ✓ COMPLETE
         Customer & User Created!
```

---

### Flow 2: Customer Logs In and Accesses Accounts

```
┌─────────────────────────────────────────────────────────────┐
│ Step 1: User enters login credentials                       │
│   Username: alice                                           │
│   Password: alice123                                        │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 2: AuthenticationManager.login()                       │
│   Searches userRegistry for username "alice"                │
│   Finds: UserAccount with customerId "C001"                 │
│   Verifies password matches                                 │
│   ⭐ SOLVING: Sets currentUser = alice's UserAccount        │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 3: System checks user's role                           │
│   currentUser.getRole() → CUSTOMER                          │
│   currentUser.getCustomerId() → "C001"                      │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 4: Find customer by ID                                 │
│   CustomerManager.findCustomer("C001")                      │
│   ⭐ SOLVING: Returns Customer object for Alice             │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 5: Access customer's accounts                          │
│   customer.getAccounts() → LinkedList<Account>              │
│   Returns: [ACC001, ACC002, ACC003]                         │
│   ⭐ Alice can now perform transactions on any account!     │
└─────────────────────────────────────────────────────────────┘
                      ▼
                 ✓ SUCCESS
         Alice logged in with 3 accounts!
```

---

### Flow 3: Creating Multiple Accounts for Customer

```
┌─────────────────────────────────────────────────────────────┐
│ Customer "Alice" (C001) already exists                      │
│ Alice has UserAccount "alice"/"alice123"                    │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ Admin creates Account #1 (Savings)                          │
│   AccountManager.createAccount("C001", "SAVINGS", "ACC001") │
│   ⭐ SOLVING: new SavingsAccount("ACC001", alice, 0.03)     │
│   alice.addAccount(account) → Alice now has 1 account       │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ Admin creates Account #2 (Checking)                         │
│   AccountManager.createAccount("C001", "CHECKING", "ACC002")│
│   ⭐ SOLVING: new CheckingAccount("ACC002", alice, 500.0)   │
│   alice.addAccount(account) → Alice now has 2 accounts      │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ Admin creates Account #3 (Savings)                          │
│   AccountManager.createAccount("C001", "SAVINGS", "ACC003") │
│   ⭐ SOLVING: new SavingsAccount("ACC003", alice, 0.05)     │
│   alice.addAccount(account) → Alice now has 3 accounts      │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ Result: Alice's Customer object now contains:               │
│   accounts = [ACC001 (Savings), ACC002 (Checking),          │
│               ACC003 (Savings)]                             │
│                                                              │
│ When Alice logs in with "alice"/"alice123":                 │
│   → System finds UserAccount with customerId "C001"         │
│   → System finds Customer "C001" (Alice)                    │
│   → Alice can access all 3 accounts!                        │
└─────────────────────────────────────────────────────────────┘
```

---

## Code Examples

### Example 1: Complete Customer & User Creation

```java
// ===== AS ADMIN =====

// Step 1: Create the customer
Customer alice = customerManager.createCustomer("C001", "Alice Johnson");
// Result: Customer object created
//   - customerId: "C001"
//   - name: "Alice Johnson"
//   - accounts: [] (empty)

// Step 2: Create customer profile (optional)
CustomerProfile profile = new CustomerProfile("P001", "123 Main St",
                                               "555-1234", "alice@email.com");
alice.setProfile(profile);
// Result: Alice now has a profile

// Step 3: Create user account linked to this customer
UserAccount aliceUser = new UserAccount("alice", "alice123", "C001");
                                                            // ↑
                                                    // Links to C001!
authManager.registerUser(aliceUser);
// Result: User "alice" can now login and access customer C001

// Step 4: Create bank accounts for this customer
Account savings1 = accountManager.createAccount("C001", "SAVINGS", "ACC001");
Account checking = accountManager.createAccount("C001", "CHECKING", "ACC002");
Account savings2 = accountManager.createAccount("C001", "SAVINGS", "ACC003");

// Result: Customer C001 (Alice) now has 3 bank accounts
//   alice.getAccounts().size() → 3
```

---

### Example 2: Customer Logs In and Performs Transaction

```java
// ===== AS CUSTOMER ALICE =====

// Step 1: Login
boolean loggedIn = authManager.login("alice", "alice123");
// Result: currentUser = UserAccount("alice", customerId="C001")

// Step 2: System determines which customer this user belongs to
User currentUser = authManager.getCurrentUser();
if (currentUser instanceof UserAccount) {
    UserAccount userAccount = (UserAccount) currentUser;
    String customerId = userAccount.getCustomerId();  // Returns "C001"

    // Step 3: Find the customer object
    Customer customer = customerManager.findCustomer(customerId);
    // Result: customer = Alice Johnson (C001)

    // Step 4: Get all accounts for this customer
    LinkedList<Account> accounts = customer.getAccounts();
    // Result: [ACC001 (Savings), ACC002 (Checking), ACC003 (Savings)]

    // Step 5: Perform transaction on any account
    transactionProcessor.deposit("ACC001", 1000.0);
    // ✓ Deposited $1000.00 to ACC001

    transactionProcessor.withdraw("ACC002", 100.0);
    // ✓ Withdrew $100.00 from ACC002

    transactionProcessor.transfer("ACC001", "ACC003", 250.0);
    // ✓ Transfer processed: TX003
}
```

---

### Example 3: Access Control (Security)

```java
// ===== SECURITY: Customers can only access their own accounts =====

// Alice (C001) is logged in
User currentUser = authManager.getCurrentUser();  // UserAccount with customerId="C001"

// Alice tries to access her own account ACC001
boolean canAccess1 = bankingSystem.canAccessAccount("ACC001");
// ✓ TRUE - ACC001 belongs to C001 (Alice)

// Alice tries to access Bob's account ACC004
boolean canAccess2 = bankingSystem.canAccessAccount("ACC004");
// ✗ FALSE - ACC004 belongs to C002 (Bob), not Alice!

// Implementation of canAccessAccount:
public boolean canAccessAccount(String accountNo) {
    User currentUser = authManager.getCurrentUser();

    // Admins can access any account
    if (currentUser.getRole() == UserRole.ADMIN) {
        return true;
    }

    // Customers can only access their own accounts
    if (currentUser instanceof UserAccount) {
        UserAccount userAccount = (UserAccount) currentUser;
        String customerId = userAccount.getCustomerId();  // e.g., "C001"

        // Find the account
        Account account = accountManager.findAccount(accountNo);
        if (account == null) return false;

        // Check if account's owner matches this customer
        Customer owner = account.getOwner();
        return owner != null && owner.getCustomerId().equals(customerId);
    }

    return false;
}
```

---

## Key Relationships Summary

### The Complete Picture

```
┌──────────────────────────────────────────────────────────────┐
│                    ADMIN (UserRole.ADMIN)                    │
│                 Can access ALL customers/accounts            │
└────────────────────────┬─────────────────────────────────────┘
                         │ creates
                         ▼
┌──────────────────────────────────────────────────────────────┐
│                    CUSTOMER (C001)                           │
│                  Name: Alice Johnson                         │
│                  Profile: P001                               │
└────────────┬─────────────────────────┬───────────────────────┘
             │                         │
             │ linked to (customerId)  │ owns (1:M)
             ▼                         ▼
┌──────────────────────┐   ┌────────────────────────────────┐
│  UserAccount         │   │  Accounts (LinkedList)         │
│  username: alice     │   │  ┌─────────────────────┐       │
│  password: alice123  │   │  │ ACC001 (Savings)    │       │
│  customerId: C001 ───┼───┤  ├─────────────────────┤       │
│  role: CUSTOMER      │   │  │ ACC002 (Checking)   │       │
└──────────────────────┘   │  ├─────────────────────┤       │
                           │  │ ACC003 (Savings)    │       │
                           │  └─────────────────────┘       │
                           └────────────────────────────────┘
```

### Relationship Table

| Entity | Relationship | Entity | Description |
|--------|--------------|--------|-------------|
| **UserAccount** | links to (1:1) | **Customer** | UserAccount.customerId = Customer.customerId |
| **Customer** | owns (1:M) | **Account** | One customer has many accounts |
| **Account** | belongs to (M:1) | **Customer** | Each account has one owner |
| **Customer** | has (1:1) | **CustomerProfile** | Optional profile information |

---

## Summary: The Linking Magic ✨

### How It All Connects:

1. **Admin creates Customer** → `Customer("C001", "Alice Johnson")`

2. **System creates UserAccount** → `UserAccount("alice", "alice123", "C001")`
   - The `customerId` field ("C001") is the **magic link**!

3. **Customer logs in** → System finds UserAccount → Gets customerId → Finds Customer

4. **Customer accesses accounts** → Customer has LinkedList of accounts → Can perform transactions

5. **Security enforced** → System checks if account.owner.customerId matches user.customerId

### The Core Logic:

```java
// When user logs in:
UserAccount user = (UserAccount) currentUser;
String customerId = user.getCustomerId();  // "C001"

// Find customer:
Customer customer = findCustomer(customerId);  // Alice Johnson

// Get accounts:
LinkedList<Account> accounts = customer.getAccounts();  // [ACC001, ACC002, ACC003]

// ✓ User can now access all accounts belonging to their customer!
```

---

## Password Management & Security

### Auto-Generated Password System

When a new customer is created through the integrated onboarding workflow, the system automatically generates login credentials:

#### Password Format: `Welcomexx####`

- **"Welcome"** - Prefix
- **"xx"** - First 2 letters of first name (lowercase)
- **"####"** - 4 random digits

**Example:**
```java
// Customer name: "Alice Johnson"
// Generated password: "Welcomeal1234"
//                      ^^^^^^ ^^----
//                      prefix  name  random
```

#### Username Generation

Usernames are generated from the customer's name:
- Format: `firstname_lastname` (lowercase)
- Handles duplicates by adding counter: `alice_johnson2`, `alice_johnson3`

**Code Location:** `AuthenticationManager.java`

```java
public String generateUsername(String fullName) {
    String[] parts = fullName.toLowerCase().split(" ");
    String baseUsername = parts[0];
    if (parts.length > 1) {
        baseUsername += "_" + parts[1];
    }

    // Handle duplicates
    String username = baseUsername;
    int counter = 2;
    while (usernameExists(username)) {
        username = baseUsername + counter;
        counter++;
    }

    return username;
}

public String generateTemporaryPassword(String firstName) {
    String prefix = "Welcome";
    String namepart = firstName.substring(0, Math.min(2, firstName.length())).toLowerCase();
    int randomDigits = (int) (Math.random() * 10000);
    return String.format("%s%s%04d", prefix, namepart, randomDigits);
}
```

### Password Change Requirement

**New customers MUST change their auto-generated password on first login.**

```java
// In UserAccount.java
public class UserAccount extends User {
    private boolean passwordChangeRequired;  // Set to true for new accounts

    // Customer sees this message on first login:
    // "⚠ You must change your password before accessing the system."
    // "Please select option #21 (Change Password) from the menu."
}
```

**Password Change Flow:**
1. New customer created with auto-generated password
2. Customer logs in with temporary password
3. System detects `passwordChangeRequired = true`
4. Forces customer to menu option #21 (Change Password)
5. Customer enters new password
6. System creates NEW User object (immutable pattern)
7. Old User replaced with new User
8. `passwordChangeRequired = false`

**Security Benefits:**
- ✅ Prevents use of predictable passwords
- ✅ Ensures only the customer knows their password
- ✅ Admin never needs to know customer passwords
- ✅ Complies with security best practices

---

## Integrated Onboarding Workflow

The system provides a seamless onboarding experience that creates everything in one session.

### Complete Flow: Customer → Profile → Account

**Location:** `CustomerManager.handleCreateCustomer()`

```
┌────────────────────────────────────────────────────────────┐
│ STEP 1: Create Customer                                    │
│   - Auto-generate customer ID (C001, C002, etc.)          │
│   - Enter customer name                                    │
│   - Add to customer list                                   │
└──────────────────┬─────────────────────────────────────────┘
                   ▼
┌────────────────────────────────────────────────────────────┐
│ STEP 2: Auto-Generate Login Credentials                   │
│   - Username: firstname_lastname                           │
│   - Password: Welcomexx#### (temporary)                    │
│   - Create UserAccount linked to customerId                │
│   - Set passwordChangeRequired = true                      │
└──────────────────┬─────────────────────────────────────────┘
                   ▼
┌────────────────────────────────────────────────────────────┐
│ STEP 3: Create Customer Profile (Optional)                │
│   - Prompt: "Create profile for [name]? (yes/no)"         │
│   - If yes: Enter address, phone, email                   │
│   - Auto-generate profile ID (P001, P002, etc.)           │
│   - Link profile to customer (bidirectional)              │
└──────────────────┬─────────────────────────────────────────┘
                   ▼
┌────────────────────────────────────────────────────────────┐
│ STEP 4: Create First Account (Optional)                   │
│   - Prompt: "Create account now? (yes/no)"                │
│   - If yes: Choose Savings or Checking                    │
│   - Enter initial deposit, interest rate, or overdraft    │
│   - Auto-generate account number (ACC001, ACC002, etc.)   │
│   - Link account to customer                              │
└──────────────────┬─────────────────────────────────────────┘
                   ▼
┌────────────────────────────────────────────────────────────┐
│ ONBOARDING COMPLETE!                                       │
│                                                            │
│ ╔════════════════════════════════════════╗                │
│ ║    ONBOARDING COMPLETE                 ║                │
│ ╠════════════════════════════════════════╣                │
│ ║ Customer: Alice Johnson (C001)         ║                │
│ ║ Username: alice_johnson                ║                │
│ ║ Password: Welcomeal1234 (CHANGE REQ)   ║                │
│ ║ Profile: Created ✓                     ║                │
│ ║ Account: ACC001 (Savings) ✓            ║                │
│ ╚════════════════════════════════════════╝                │
└────────────────────────────────────────────────────────────┘
```

### Why This Workflow?

**Before (Manual Process):**
```
1. Admin: Create customer       (Menu #10)
2. Admin: Create user           (Menu #XX)
3. Admin: Create profile        (Menu #12)
4. Admin: Create account        (Menu #1)
   Total: 4 separate operations!
```

**After (Integrated Onboarding):**
```
1. Admin: Create customer → All done in one flow!
   Total: 1 operation!
```

**Benefits:**
- ✅ Faster customer onboarding
- ✅ Reduces human error (no forgotten steps)
- ✅ Consistent data creation
- ✅ Better user experience for admin
- ✅ Automatic credential generation prevents weak passwords

---

## Summary: The Complete Chain

### From Creation to Login to Access

1. **Admin creates Customer** → `Customer("C001", "Alice Johnson")`

2. **System creates UserAccount** → `UserAccount("alice", "alice123", "C001")`
   - The `customerId` field ("C001") is the **magic link**!

3. **Customer logs in** → System finds UserAccount → Gets customerId → Finds Customer

4. **Customer accesses accounts** → Customer has LinkedList of accounts → Can perform transactions

5. **Security enforced** → System checks if account.owner.customerId matches user.customerId

### The Core Logic:

```java
// When user logs in:
UserAccount user = (UserAccount) currentUser;
String customerId = user.getCustomerId();  // "C001"

// Find customer:
Customer customer = findCustomer(customerId);  // Alice Johnson

// Get accounts:
LinkedList<Account> accounts = customer.getAccounts();  // [ACC001, ACC002, ACC003]

// ✓ User can now access all accounts belonging to their customer!
```

---

**Created:** November 2024
**Project:** Banking System - OOP Project Part 2
