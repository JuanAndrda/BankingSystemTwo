# Class Relationships in Banking System

## 📚 Table of Contents
1. [Overview](#overview)
2. [Inheritance Relationships](#inheritance-relationships)
3. [Association Relationships](#association-relationships)
4. [Composition Relationships](#composition-relationships)
5. [Complete Class Diagram](#complete-class-diagram)
6. [Relationship Examples](#relationship-examples)

---

## Overview

This document explains all the relationships between classes in the Banking System. Understanding these relationships is crucial for understanding how the system works.

### Types of Relationships:
1. **Inheritance (IS-A)** - Parent-child relationships
2. **Association (HAS-A / USES)** - Objects reference each other
3. **Composition (HAS-A, strong)** - Owner contains the owned object
4. **Dependency (USES)** - One class uses another temporarily

---

## Inheritance Relationships

### 📖 Definition
**Inheritance** creates an "IS-A" relationship. A child class IS-A type of the parent class.

### Account Hierarchy

```
                    Account (abstract)
                         |
         +---------------+---------------+
         |                               |
    SavingsAccount               CheckingAccount
```

**Code:**
```java
// Parent
public abstract class Account { }

// Children
public class SavingsAccount extends Account { }
public class CheckingAccount extends Account { }
```

**Relationships:**
- `SavingsAccount` **IS-A** `Account`
- `CheckingAccount` **IS-A** `Account`

**Files:**
- `Account.java` (parent)
- `SavingsAccount.java` (child)
- `CheckingAccount.java` (child)

---

### User Hierarchy

```
                    User (abstract)
                         |
         +---------------+---------------+
         |                               |
       Admin                        UserAccount
```

**Code:**
```java
// Parent
public abstract class User { }

// Children
public class Admin extends User { }
public class UserAccount extends User { }
```

**Relationships:**
- `Admin` **IS-A** `User`
- `UserAccount` **IS-A** `User`

**Files:**
- `User.java` (parent)
- `Admin.java` (child)
- `UserAccount.java` (child)

---

## Association Relationships

### 📖 Definition
**Association** creates a "HAS-A" or "USES" relationship where objects reference each other.

### 1. One-to-One (1:1) Relationship

#### Customer ↔ CustomerProfile

```
Customer (1) ←──────→ (1) CustomerProfile
```

**Visual:**
```
┌─────────────┐              ┌──────────────────┐
│  Customer   │              │ CustomerProfile  │
├─────────────┤              ├──────────────────┤
│ customerId  │              │ profileId        │
│ name        │              │ address          │
│ profile ────┼──────────→   │ phone            │
│             │              │ email            │
│             │   ←──────────┼─ customer        │
└─────────────┘              └──────────────────┘
```

**Code:**
```java
// In Customer.java
public class Customer {
    private CustomerProfile profile;  // One customer has ONE profile
}

// In CustomerProfile.java
public class CustomerProfile {
    private Customer customer;  // One profile belongs to ONE customer
}
```

**Example:**
```java
Customer alice = new Customer("C001", "Alice");
CustomerProfile aliceProfile = new CustomerProfile("P001", "123 Main St",
                                                    "555-1234", "alice@email.com");
alice.setProfile(aliceProfile);  // Establishes bidirectional relationship
```

---

### 2. One-to-Many (1:M) Relationship

#### Customer → Accounts

```
Customer (1) ──────→ (Many) Account
```

**Visual:**
```
┌─────────────┐              ┌─────────────┐
│  Customer   │              │   Account   │
├─────────────┤              ├─────────────┤
│ customerId  │              │ accountNo   │
│ name        │              │ balance     │
│ accounts ───┼──────────→   │ owner       │
│   (List)    │              │             │
└─────────────┘              └─────────────┘
       │                            ↑
       │                            │
       └────────────────────────────┘
        One customer, many accounts
```

**Code:**
```java
// In Customer.java
public class Customer {
    private LinkedList<Account> accounts;  // ONE customer has MANY accounts

    public void addAccount(Account a) {
        this.accounts.add(a);
    }
}

// In Account.java
public abstract class Account {
    private Customer owner;  // Each account has ONE owner
}
```

**Example:**
```java
Customer alice = new Customer("C001", "Alice");

Account savings = new SavingsAccount("ACC001", alice, 0.03);
Account checking = new CheckingAccount("ACC002", alice, 500.0);

alice.addAccount(savings);   // Alice now has 2 accounts
alice.addAccount(checking);
```

---

#### Account → Transactions

```
Account (1) ──────→ (Many) Transaction
```

**Visual:**
```
┌─────────────┐              ┌──────────────┐
│   Account   │              │ Transaction  │
├─────────────┤              ├──────────────┤
│ accountNo   │              │ txId         │
│ balance     │              │ type         │
│ history ────┼──────────→   │ amount       │
│   (List)    │              │ timestamp    │
└─────────────┘              └──────────────┘
       │
       └────────────────────────────
        One account, many transactions
```

**Code:**
```java
// In Account.java
public abstract class Account {
    private LinkedList<Transaction> transactionHistory;  // MANY transactions

    public void addTransaction(Transaction t) {
        this.transactionHistory.add(t);
    }
}
```

**Example:**
```java
Account account = new SavingsAccount("ACC001", customer, 0.03);

Transaction tx1 = new Transaction("TX001", TransactionType.DEPOSIT, 1000.0);
Transaction tx2 = new Transaction("TX002", TransactionType.WITHDRAW, 100.0);

account.addTransaction(tx1);  // Account now has 2 transactions
account.addTransaction(tx2);
```

---

### 3. Many-to-Many Relationship (Indirect)

#### User → Customers (through UserAccount)

```
User (1) ──────→ (1) Customer
  ↑
  │
UserAccount
```

**Code:**
```java
// In UserAccount.java
public class UserAccount extends User {
    private String customerId;  // Links to ONE customer

    public String getCustomerId() {
        return this.customerId;
    }
}
```

---

## Composition Relationships

### 📖 Definition
**Composition** is a strong "HAS-A" relationship where the contained object cannot exist without the container.

### BankingSystem Composition

```
                    BankingSystem
                         |
        +----------------+----------------+----------------+
        |                |                |                |
TransactionProcessor  AccountManager  CustomerManager  AuthenticationManager
```

**Visual:**
```
┌──────────────────────────────────────────────┐
│           BankingSystem                      │
├──────────────────────────────────────────────┤
│ - transactionProcessor: TransactionProcessor │
│ - accountManager: AccountManager             │
│ - customerManager: CustomerManager           │
│ - authManager: AuthenticationManager         │
└──────────────────────────────────────────────┘
          │                    │
          ▼                    ▼
┌────────────────────┐  ┌────────────────┐
│TransactionProcessor│  │ AccountManager │
└────────────────────┘  └────────────────┘
```

**Code:**
```java
public class BankingSystem {
    // Composition - BankingSystem HAS-A TransactionProcessor
    private TransactionProcessor transactionProcessor;

    // Composition - BankingSystem HAS-A AccountManager
    private AccountManager accountManager;

    // Composition - BankingSystem HAS-A CustomerManager
    private CustomerManager customerManager;

    // Composition - BankingSystem HAS-A AuthenticationManager
    private AuthenticationManager authManager;

    public BankingSystem(Scanner scanner) {
        // Create the components - they exist only within BankingSystem
        this.transactionProcessor = new TransactionProcessor(accounts, validator);
        this.accountManager = new AccountManager(customers, accounts, validator);
        this.customerManager = new CustomerManager(customers, accounts, validator);
        this.authManager = new AuthenticationManager();
    }
}
```

**Why Composition?**
- These managers only make sense within a BankingSystem
- If BankingSystem is destroyed, managers are destroyed too
- Strong ownership relationship

---

## Complete Class Diagram

### Full System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                       BankingSystem                         │
│  (Main orchestrator - coordinates all operations)           │
└─────────────┬───────────────────────────────────────────────┘
              │ contains (composition)
              │
      +-------+-------+-------+-------+
      │       │       │       │       │
      ▼       ▼       ▼       ▼       ▼
┌──────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌──────────┐
│Transaction│Account  │Customer│Auth    │Input     │
│Processor │Manager  │Manager │Manager │Validator │
└──────────┘ └────────┘ └────────┘ └────────┘ └──────────┘
      │            │         │
      │ manages    │manages  │manages
      ▼            ▼         ▼
┌──────────┐ ┌────────┐ ┌─────────┐
│Transaction│Account  │Customer  │
└──────────┘ └───┬────┘ └────┬────┘
                  │ IS-A      │
         +--------+-------+   │
         │                │   │ 1:1
         ▼                ▼   ▼
    ┌─────────┐    ┌──────────┐  ┌────────────┐
    │ Savings │    │ Checking │  │  Customer  │
    │ Account │    │ Account  │  │  Profile   │
    └─────────┘    └──────────┘  └────────────┘
```

---

## Relationship Examples

### Example 1: Creating a Customer with Profile and Accounts

```java
// Step 1: Create customer
Customer alice = new Customer("C001", "Alice Johnson");

// Step 2: Create and link profile (1:1 relationship)
CustomerProfile profile = new CustomerProfile("P001", "123 Main St",
                                               "555-1234", "alice@email.com");
alice.setProfile(profile);  // Bidirectional - profile.customer also set

// Step 3: Create accounts (1:M relationship)
Account savings = new SavingsAccount("ACC001", alice, 0.03);
Account checking = new CheckingAccount("ACC002", alice, 500.0);

// Step 4: Link accounts to customer
alice.addAccount(savings);
alice.addAccount(checking);

// Now Alice has:
// - 1 profile
// - 2 accounts
// Each account knows Alice is the owner
// Profile knows Alice is the customer
```

---

### Example 2: Processing a Transaction

```java
// BankingSystem contains TransactionProcessor (composition)
BankingSystem bankingSystem = new BankingSystem(scanner);

// TransactionProcessor manages transactions
TransactionProcessor processor = bankingSystem.getTransactionProcessor();

// Create transaction
Transaction tx = new Transaction("TX001", TransactionType.DEPOSIT, 1000.0);

// Find account (association)
Account account = AccountUtils.findAccount(accountList, "ACC001");

// Process deposit
account.deposit(1000.0);  // Polymorphism - actual type determines behavior

// Add transaction to account history (1:M relationship)
account.addTransaction(tx);
```

---

### Example 3: Polymorphic Withdraw

```java
// Create different account types
Account savings = new SavingsAccount("ACC001", alice, 0.03);
Account checking = new CheckingAccount("ACC002", alice, 500.0);

// Deposit to both
savings.deposit(1000.0);
checking.deposit(1000.0);

// Try to withdraw same amount from both
savings.withdraw(1200.0);   // FAILS - insufficient funds (no overdraft)
checking.withdraw(1200.0);  // SUCCESS - overdraft allows it (balance + 500)

// Same method call, different behavior!
// This is polymorphism in action
```

---

### Example 4: UserAccount → Customer Linking

```java
// UserAccount links to Customer through linkedCustomerId field
public class UserAccount extends User {
    private String linkedCustomerId;  // THE KEY LINK!

    public String getLinkedCustomerId() {
        return this.linkedCustomerId;
    }
}

// Usage in access control
public boolean canAccessAccount(String accountNo) {
    // Get current logged-in user
    User currentUser = authManager.getCurrentUser();

    if (currentUser instanceof Admin) {
        return true;  // Admins can access all accounts
    }

    if (currentUser instanceof UserAccount) {
        UserAccount userAcct = (UserAccount) currentUser;
        String customerIdFromUser = userAcct.getLinkedCustomerId();  // "C001"

        // Find the account
        Account account = AccountUtils.findAccount(accountList, accountNo);
        String customerIdFromAccount = account.getOwner().getCustomerId();  // "C001"

        // Only allow access if IDs match!
        return customerIdFromUser.equals(customerIdFromAccount);
    }

    return false;
}
```

**Flow:**
```
UserAccount (username: "alice")
    ↓ linkedCustomerId = "C001"
Customer (customerId: "C001", name: "Alice")
    ↓ accounts list
Account (accountNo: "ACC001", owner: Customer "C001")
```

**This is how the system enforces:**
- Alice can only access HER accounts
- Bob can only access HIS accounts
- Admin can access ALL accounts

---

### Example 5: Cross-Manager Dependencies

#### CustomerManager → AccountManager

```java
public class CustomerManager {
    private LinkedList<Customer> customers;
    private LinkedList<Account> accountList;
    private AccountManager accountMgr;  // Cross-manager dependency!

    // Setter injection
    public void setAccountManager(AccountManager accountMgr) {
        this.accountMgr = accountMgr;
    }

    // Used in onboarding workflow
    public void handleCreateCustomer() {
        // Step 1: Create customer
        Customer newCustomer = new Customer(...);

        // Step 2: Create profile
        CustomerProfile profile = new CustomerProfile(...);

        // Step 3: Offer to create first account
        System.out.println("Would you like to create an account now? (yes/no)");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            // CustomerManager calls AccountManager!
            this.accountMgr.handleCreateAccount(newCustomer);
        }
    }
}
```

**Why This Relationship Exists:**
- Integrated onboarding workflow needs both managers
- Creates seamless user experience
- Customer → Profile → Account in one session

---

### Example 6: Circular Dependency Resolution

#### Problem: BankingSystem ↔ Managers

Managers need BankingSystem for:
- `canAccessAccount()` - access control checks
- `getCurrentUser()` - getting logged-in user
- `logAction()` - audit logging

But BankingSystem creates the managers! This is a **circular dependency**.

#### Solution: Two-Phase Initialization

```java
public class BankingSystem {
    private CustomerManager customerMgr;
    private AccountManager accountMgr;
    private TransactionProcessor transactionProcessor;

    public BankingSystem(Scanner scanner) {
        // ========== PHASE 1: Create objects (no circular refs) ==========
        this.customerMgr = new CustomerManager(customers, accountList, scanner, validator);
        this.accountMgr = new AccountManager(customers, accountList, scanner, validator);
        this.transactionProcessor = new TransactionProcessor(accountList, scanner, validator);

        // ========== PHASE 2: Wire up circular references via setters ==========
        this.customerMgr.setBankingSystem(this);  // Now CustomerManager can call this.bankingSystem
        this.accountMgr.setBankingSystem(this);    // Now AccountManager can call this.bankingSystem
        this.transactionProcessor.setBankingSystem(this);  // Now TransactionProcessor can call this.bankingSystem

        // ========== PHASE 3: Wire up cross-manager dependencies ==========
        this.customerMgr.setAccountManager(this.accountMgr);  // For onboarding workflow
    }
}
```

**In Each Manager:**
```java
public class CustomerManager {
    private BankingSystem bankingSystem;  // Reference back to parent

    // Setter injection (called in Phase 2)
    public void setBankingSystem(BankingSystem bankingSystem) {
        this.bankingSystem = bankingSystem;
    }

    // Now can use BankingSystem methods
    public void handleDeleteCustomer() {
        // Log the action using BankingSystem
        this.bankingSystem.logAction("DELETE_CUSTOMER", "Deleted customer " + customerId);

        // Perform deletion
        customers.remove(customer);
    }
}
```

**Pattern Benefits:**
- ✅ Resolves circular dependency safely
- ✅ Managers can access parent methods (logging, access control)
- ✅ Clean separation of concerns
- ✅ No null pointer exceptions

**Visual:**
```
Phase 1 (Construction):
    BankingSystem creates → CustomerManager
                          → AccountManager
                          → TransactionProcessor

Phase 2 (Wiring):
    BankingSystem ←────────┐
         │                 │
         ├─→ CustomerManager (bankingSystem field set)
         ├─→ AccountManager (bankingSystem field set)
         └─→ TransactionProcessor (bankingSystem field set)

Phase 3 (Cross-Dependencies):
    CustomerManager.accountMgr = accountMgr
```

---

## Relationship Summary Table

| Relationship Type | Example | Description | Code Location |
|-------------------|---------|-------------|---------------|
| **Inheritance (IS-A)** | `SavingsAccount extends Account` | Child IS-A parent | `SavingsAccount.java:6` |
| **1:1 Association** | `Customer ↔ CustomerProfile` | One-to-one bidirectional | `Customer.java:12`, `CustomerProfile.java` |
| **1:M Association** | `Customer → Accounts` | One customer, many accounts | `Customer.java:11` |
| **1:M Association** | `Account → Transactions` | One account, many transactions | `Account.java:11` |
| **Composition** | `BankingSystem HAS-A TransactionProcessor` | Strong ownership | `BankingSystem.java` |
| **Dependency** | `TransactionProcessor uses Account` | Temporary usage | `TransactionProcessor.java:23` |

---

## Key Relationship Patterns

### Pattern 1: Bidirectional Association (Customer ↔ Profile)
Both objects know about each other:
```java
customer.getProfile();  // Customer knows its profile
profile.getCustomer();  // Profile knows its customer
```

### Pattern 2: Unidirectional Association (Account → Transaction)
Only one side knows about the other:
```java
account.getTransactionHistory();  // Account knows transactions
// Transaction doesn't have a direct reference to Account
```

### Pattern 3: Composition (BankingSystem contains Managers)
Container creates and owns the contained objects:
```java
// In BankingSystem constructor
this.transactionProcessor = new TransactionProcessor(...);
// TransactionProcessor cannot exist without BankingSystem
```

---

## UML Notation Guide

```
Association:      ClassA ────────> ClassB    (A uses B)
Inheritance:      ClassA ───────▷  ClassB    (A extends B)
Composition:      ClassA ◆────── ClassB    (A owns B, strong)
Aggregation:      ClassA ◇────── ClassB    (A has B, weak)

Multiplicity:
  1      - exactly one
  0..1   - zero or one
  *      - zero or more
  1..*   - one or more
```

**Example:**
```
Customer (1) ────────> (*) Account
  One customer has zero or more accounts
```

---

**Created:** November 2024
**Project:** Banking System - OOP Project Part 2
