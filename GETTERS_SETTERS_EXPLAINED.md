# Getters and Setters Explained

## 📚 Table of Contents
1. [Overview](#overview)
2. [Why Some Classes Have Getters/Setters](#why-some-classes-have-getterssetters)
3. [Types of Classes](#types-of-classes)
4. [How Managers Use Model Getters](#how-managers-use-model-getters)
5. [Design Principles](#design-principles)
6. [Complete Examples](#complete-examples)

---

## Overview

In this Banking System, different types of classes have different structures. Some have getters and setters, while others don't. This document explains **why** and **how** they work together.

### Key Question:
> "Why do models have getters/setters but managers don't?"

**Answer:**
- **Models** (Customer, Account) are **data containers** - they store data that other classes need to access
- **Managers** (CustomerManager, TransactionProcessor) are **service classes** - they perform operations but hide their internal data

---

## Why Some Classes Have Getters/Setters

### What are Getters and Setters?

```java
public class Customer {
    private String name;  // Private field - cannot access directly

    // Getter - allows READ access
    public String getName() {
        return this.name;
    }

    // Setter - allows WRITE access
    public void setName(String name) {
        this.name = name;
    }
}
```

**Usage:**
```java
Customer customer = new Customer("C001", "Alice");

// Can't do this - name is private:
// String name = customer.name;  ❌ ERROR

// Must use getter:
String name = customer.getName();  ✅ Works

// Must use setter:
customer.setName("Alice Johnson");  ✅ Works
```

---

## Types of Classes

### 1. **Entity/Model Classes** (HAVE getters/setters)

**Location:** `src/com/banking/models/`

These represent real-world objects and need to expose their data.

#### Customer.java
```java
public class Customer {
    // Private fields
    private String customerId;
    private String name;
    private LinkedList<Account> accounts;

    // Constructor
    public Customer(String customerId, String name) {
        this.customerId = customerId;
        this.name = name;
        this.accounts = new LinkedList<>();
    }

    // Getters - allow others to READ
    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public LinkedList<Account> getAccounts() { return accounts; }

    // Setters - allow others to MODIFY
    public void setName(String name) { this.name = name; }
}
```

**Why getters/setters?**
- ✅ Other classes need to read customer data
- ✅ Other classes need to modify customer data
- ✅ Encapsulation - fields are private, access is controlled

---

#### Account.java
```java
public abstract class Account {
    private String accountNo;
    private double balance;
    private Customer owner;

    // Getters
    public String getAccountNo() { return accountNo; }
    public double getBalance() { return balance; }
    public Customer getOwner() { return owner; }

    // Setters
    protected void setBalance(double balance) {
        this.balance = balance;
    }
}
```

**Why getters/setters?**
- ✅ TransactionProcessor needs to check balance
- ✅ Display methods need to show account number
- ✅ Managers need to access owner information

---

#### Transaction.java
```java
public class Transaction {
    private String transactionId;
    private TransactionType type;
    private double amount;
    private String status;

    // Getters
    public String getTransactionId() { return transactionId; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }

    // Setters
    public void setStatus(String status) { this.status = status; }
}
```

---

### 2. **Manager/Service Classes** (Few or NO getters/setters)

**Location:** `src/com/banking/managers/`

These perform operations and hide their internal implementation.

#### CustomerManager.java
```java
public class CustomerManager {
    // Private internal data - NO getters for these!
    private LinkedList<Customer> customers;
    private LinkedList<Account> accountList;
    private InputValidator validator;
    private Scanner scanner;

    // Added fields for additional functionality:
    private BankingSystem bankingSystem;  // For audit logging & access control
    private AccountManager accountMgr;    // For integrated onboarding workflow
    private int customerIdCounter = 1;    // For auto-generating customer IDs

    // Constructor
    public CustomerManager(LinkedList<Customer> customers,
                          LinkedList<Account> accountList,
                          Scanner scanner,
                          InputValidator validator) {
        this.customers = customers;
        this.accountList = accountList;
        this.scanner = scanner;
        this.validator = validator;
        // Counter initialized by scanning existing customers
        this.customerIdCounter = findMaxCustomerId(customers) + 1;
    }

    // Setter injection for circular dependency resolution
    public void setBankingSystem(BankingSystem bankingSystem) {
        this.bankingSystem = bankingSystem;
    }

    public void setAccountManager(AccountManager accountMgr) {
        this.accountMgr = accountMgr;
    }

    // NO getters like getCustomers() or getAccountList()

    // Only business operation methods:
    public void createNewCustomer() { }
    public void deleteCustomer() { }
    public void displayAllCustomers() { }
}
```

**Why NO getters/setters?**
- ❌ Internal lists should NOT be exposed
- ❌ Other classes shouldn't directly modify the customer list
- ✅ Only provide specific operations (create, delete, display)
- ✅ **Encapsulation** - hide implementation details

**Bad Example (if it had getters):**
```java
// BAD DESIGN
public LinkedList<Customer> getCustomers() {
    return customers;  // Exposes internal list
}

// Someone could do this:
CustomerManager manager = new CustomerManager(...);
LinkedList<Customer> list = manager.getCustomers();
list.clear();  // 💥 DELETED ALL CUSTOMERS!
```

---

#### TransactionProcessor.java
```java
public class TransactionProcessor {
    // Private internal data - NO getters
    private LinkedList<Account> accountList;
    private InputValidator validator;
    private int txCounter;

    // NO getters for accountList, validator, txCounter

    // Only business operations:
    public boolean deposit(String accountNo, double amount) { }
    public boolean withdraw(String accountNo, double amount) { }
    public boolean transfer(String from, String to, double amount) { }
}
```

---

### 3. **Enum Classes** (NO getters/setters)

**Examples:** `TransactionType.java`, `UserRole.java`, `MenuAction.java`

```java
public enum TransactionType {
    DEPOSIT,
    WITHDRAW,
    TRANSFER
}
```

**Why NO getters/setters?**
- Enums are **constants** - they never change
- Just use them directly:
```java
Transaction tx = new Transaction("TX001", TransactionType.DEPOSIT, 1000.0);

if (tx.getType() == TransactionType.DEPOSIT) {
    System.out.println("This is a deposit");
}
```

---

### 4. **Utility Classes** (NO getters/setters)

**Location:** `src/com/banking/utilities/`

#### ValidationPatterns.java
```java
public class ValidationPatterns {
    // Just constants - no getters/setters needed
    public static final String ACCOUNT_NUMBER_PATTERN = "^ACC\\d{3,}$";
    public static final String CUSTOMER_ID_PATTERN = "^C\\d{3,}$";
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
}
```

**Why NO getters/setters?**
- Just holds **constants**
- Used directly:
```java
if (accountNo.matches(ValidationPatterns.ACCOUNT_NUMBER_PATTERN)) {
    System.out.println("Valid account number");
}
```

---

#### InputValidator.java
```java
public class InputValidator {
    // No fields at all - just static helper methods

    public static boolean isValidAccountNumber(String accountNo) {
        return accountNo != null &&
               accountNo.matches(ValidationPatterns.ACCOUNT_NUMBER_PATTERN);
    }

    public static boolean isValidAmount(double amount) {
        return amount > 0;
    }
}
```

**Why NO getters/setters?**
- No data to store
- Just **helper functions**
- Called directly:
```java
if (InputValidator.isValidAccountNumber("ACC001")) {
    System.out.println("Valid");
}
```

---

### 5. **Immutable/Record Classes** (Only getters, NO setters)

#### AuditLog.java
```java
public class AuditLog {
    private String username;
    private String action;
    private String details;
    private LocalDateTime timestamp;

    // Constructor
    public AuditLog(String username, String action, String details) {
        this.username = username;
        this.action = action;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    // Only getters - NO setters!
    public String getUsername() { return username; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
```

**Why NO setters?**
- Audit logs should be **immutable** - once created, never changed
- Prevents tampering with audit trail
- Security best practice

---

## How Managers Use Model Getters

### Key Concept:
> Managers DON'T have getters, but they USE getters from Models

### Example 1: CustomerManager uses Customer getters

**Customer.java (model)** - HAS getters
```java
public class Customer {
    private String customerId;
    private String name;

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
}
```

**CustomerManager.java (manager)** - USES Customer's getters
```java
public class CustomerManager {
    private LinkedList<Customer> customers;  // No getter for this!

    public void displayAllCustomers() {
        System.out.println("All Customers:");

        for (Customer customer : this.customers) {
            // Using Customer's getters! ↓↓↓
            System.out.println("ID: " + customer.getCustomerId());
            System.out.println("Name: " + customer.getName());
            //                            ↑↑↑
            // CustomerManager doesn't have getName()
            // It's calling Customer's getName()!
        }
    }
}
```

**Visual Flow:**
```
┌─────────────────────┐
│  CustomerManager    │  ← NO getName() getter
│  (Manager)          │
└──────────┬──────────┘
           │
           │ stores a list of
           ▼
    ┌─────────────┐
    │  Customer   │  ← HAS getName() getter
    │  (Model)    │
    ├─────────────┤
    │ - name      │  ← Private field
    │ + getName() │  ← Public getter
    └─────────────┘

Flow:
1. CustomerManager has a LinkedList<Customer>
2. CustomerManager loops through customers
3. For each customer, calls customer.getName()
4. getName() is defined in Customer class!
```

---

### Example 2: TransactionProcessor uses Account getters

**Account.java (model)** - HAS getters
```java
public abstract class Account {
    private String accountNo;
    private double balance;

    public String getAccountNo() { return accountNo; }
    public double getBalance() { return balance; }
}
```

**TransactionProcessor.java (manager)** - USES Account's getters
```java
public class TransactionProcessor {
    private LinkedList<Account> accountList;  // No getter for this!

    public boolean deposit(String accountNo, double amount) {
        // Find the account
        Account account = AccountUtils.findAccount(this.accountList, accountNo);

        if (account == null) {
            System.out.println("Account not found");
            return false;
        }

        // Using Account's getters! ↓↓↓
        System.out.println("Account: " + account.getAccountNo());
        System.out.println("Current Balance: $" + account.getBalance());
        //                                        ↑↑↑
        // TransactionProcessor doesn't have getBalance()
        // It's calling Account's getBalance()!

        account.deposit(amount);
        return true;
    }
}
```

---

### Example 3: AccountManager uses Customer getters (for sorting)

**AccountManager.java** - USES Customer's getters
```java
public class AccountManager {

    private void insertionSortByName(LinkedList<Account> accountList) {
        for (int i = 1; i < accountList.size(); i++) {
            Account currentAccount = accountList.get(i);

            // Get the owner (Customer object) using Account's getter
            Customer owner = currentAccount.getOwner();

            // Use Customer's getter! ↓↓↓
            String currentName = owner.getName();
            //                         ↑↑↑
            // AccountManager doesn't have getName()
            // It's using Customer's getName()!

            // Compare names for sorting...
            int j = i - 1;
            while (j >= 0) {
                Account compareAccount = accountList.get(j);
                Customer compareOwner = compareAccount.getOwner();

                // Using Customer's getter again! ↓↓↓
                String compareName = compareOwner.getName();

                if (currentName.compareToIgnoreCase(compareName) < 0) {
                    accountList.set(j + 1, compareAccount);
                    j--;
                } else {
                    break;
                }
            }

            accountList.set(j + 1, currentAccount);
        }
    }
}
```

**Chain of calls:**
```
AccountManager (no getters)
    ↓ calls
Account.getOwner()       ← Returns Customer object
    ↓ then calls
Customer.getName()       ← Returns customer name string
```

---

### Example 4: Complete Chain - Multiple Model Getters

**TransactionProcessor.java**
```java
public void displayAccountInfo(String accountNo) {
    // Find account
    Account account = AccountUtils.findAccount(this.accountList, accountNo);

    // Using Account's getters ↓↓↓
    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    System.out.println("Account Number: " + account.getAccountNo());
    System.out.println("Balance: $" + account.getBalance());

    // Get the owner (Customer object) ↓↓↓
    Customer owner = account.getOwner();

    // Using Customer's getters ↓↓↓
    System.out.println("Owner: " + owner.getName());
    System.out.println("Customer ID: " + owner.getCustomerId());

    // Get the profile (CustomerProfile object) ↓↓↓
    CustomerProfile profile = owner.getProfile();

    // Using CustomerProfile's getters ↓↓↓
    System.out.println("Email: " + profile.getEmail());
    System.out.println("Phone: " + profile.getPhoneNumber());
    System.out.println("Address: " + profile.getAddress());
    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
}
```

**Chain of getter calls:**
```
TransactionProcessor (manager - no getters)
    ↓ uses
Account.getAccountNo()      ← Account's getter
Account.getBalance()        ← Account's getter
Account.getOwner()          ← Account's getter → returns Customer
    ↓ returns Customer, then uses
Customer.getName()          ← Customer's getter
Customer.getCustomerId()    ← Customer's getter
Customer.getProfile()       ← Customer's getter → returns CustomerProfile
    ↓ returns CustomerProfile, then uses
CustomerProfile.getEmail()       ← CustomerProfile's getter
CustomerProfile.getPhoneNumber() ← CustomerProfile's getter
CustomerProfile.getAddress()     ← CustomerProfile's getter
```

---

## Design Principles

### 1. **Encapsulation**

**Definition:** Hide internal data, expose only what's necessary

**Models - Expose Data:**
```java
public class Customer {
    private String name;  // Hidden

    public String getName() {  // Exposed
        return name;
    }
}
```

**Managers - Hide Data:**
```java
public class CustomerManager {
    private LinkedList<Customer> customers;  // Hidden

    // NO public getter for customers list!

    public void displayAllCustomers() {  // Only expose operations
        for (Customer c : customers) {
            System.out.println(c.getName());
        }
    }
}
```

---

### 2. **Principle of Least Privilege**

**Definition:** Only give access to what's absolutely needed

```java
// GOOD - Customer provides getName()
Customer customer = findCustomer("C001");
String name = customer.getName();  ✅ Allowed

// BAD - if CustomerManager exposed its list
CustomerManager manager = new CustomerManager();
LinkedList<Customer> list = manager.getCustomers();  ❌ Should NOT be allowed
list.clear();  // 💥 Could delete all customers!
```

---

### 3. **Single Responsibility Principle**

**Models:** Responsible for storing and providing access to data
```java
public class Account {
    private double balance;

    public double getBalance() { return balance; }  // Data access
}
```

**Managers:** Responsible for business operations
```java
public class TransactionProcessor {
    public boolean deposit(String accountNo, double amount) {
        // Business logic
    }
}
```

---

## Complete Examples

### Example 1: Customer Creation Flow

```java
// In CustomerManager.java
public void createNewCustomer() {
    // Get input from user
    System.out.print("Enter customer ID: ");
    String customerId = scanner.nextLine();

    System.out.print("Enter customer name: ");
    String name = scanner.nextLine();

    // Create customer (model)
    Customer customer = new Customer(customerId, name);

    // Create profile
    System.out.print("Enter address: ");
    String address = scanner.nextLine();

    CustomerProfile profile = new CustomerProfile(customerId, address, phone, email);

    // Link profile to customer (uses Customer's setter)
    customer.setProfile(profile);  ← Using Customer's setter

    // Add to list
    this.customers.add(customer);

    // Display confirmation (uses Customer's getters)
    System.out.println("Created customer: " + customer.getName());  ← Using getter
    System.out.println("ID: " + customer.getCustomerId());  ← Using getter
}
```

---

### Example 2: Account Display Flow

```java
// In AccountManager.java
public void displayAccountDetails(String accountNo) {
    // Find account
    Account account = AccountUtils.findAccount(this.accountList, accountNo);

    // Display account info (uses Account's getters)
    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    System.out.println("Account Number: " + account.getAccountNo());  ← Account getter
    System.out.println("Balance: $" + account.getBalance());  ← Account getter

    // Get owner and display (uses Customer's getters)
    Customer owner = account.getOwner();  ← Account getter returns Customer
    System.out.println("Owner: " + owner.getName());  ← Customer getter

    // Polymorphic display (calls overridden method)
    account.displayAccountInfo();
    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
}
```

---

### Example 3: Transaction Processing Flow

```java
// In TransactionProcessor.java
public boolean withdraw(String accountNo, double amount) {
    // Validate
    if (!validator.isValidAccountNumber(accountNo)) {
        return false;
    }

    // Find account
    Account account = AccountUtils.findAccount(this.accountList, accountNo);

    // Display current balance (uses Account's getter)
    System.out.println("Current Balance: $" + account.getBalance());  ← Getter

    // Create transaction
    Transaction tx = new Transaction("TX" + txCounter++,
                                     TransactionType.WITHDRAW,
                                     amount);

    // Attempt withdrawal (polymorphic behavior)
    if (account.withdraw(amount)) {
        tx.setStatus("COMPLETED");  ← Transaction setter
        account.addTransaction(tx);

        // Display new balance (uses Account's getter)
        System.out.println("New Balance: $" + account.getBalance());  ← Getter
        return true;
    } else {
        tx.setStatus("FAILED");
        account.addTransaction(tx);
        return false;
    }
}
```

---

## Summary Table

| Class Type | Location | Has Getters? | Has Setters? | Why? |
|------------|----------|--------------|--------------|------|
| **Models** | `models/` | ✅ Yes | ✅ Yes | Data containers - need to be accessed/modified by other classes |
| **Managers** | `managers/` | ❌ Rare | ❌ Rare | Business logic - hide internal data, only expose operations |
| **Enums** | Various | ❌ No | ❌ No | Constants - use directly |
| **Utilities** | `utilities/` | ❌ No | ❌ No | Static helpers - no state to store |
| **Records/Logs** | `auth/` | ✅ Yes | ❌ No | Immutable - read-only after creation |

---

## Key Takeaways

### ✅ When to Use Getters/Setters:
1. **Data classes** (Customer, Account, Transaction) - need to expose data
2. **When validation is needed** in setters
3. **When other classes need to read/modify** the data

### ❌ When NOT to Use Getters/Setters:
1. **Internal implementation details** (Manager's private lists)
2. **Constants** (Enums, ValidationPatterns)
3. **Immutable data** (AuditLog - only getters, no setters)
4. **Utility classes with no state** (InputValidator)

### 🎯 The Pattern:
```
Manager (no getters)
  → stores Model objects in private lists
    → Model (has getters/setters)
      → Manager calls Model's getters to access data
```

**Rule:** Managers are **users** of model getters, not **providers** of getters themselves.

---

**Created:** November 2024
**Project:** Banking System - OOP Project Part 2
