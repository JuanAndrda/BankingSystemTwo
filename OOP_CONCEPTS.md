# OOP Concepts in Banking System

## 📚 Table of Contents
1. [The Four Pillars of OOP](#the-four-pillars-of-oop)
2. [Encapsulation](#1-encapsulation)
3. [Inheritance](#2-inheritance)
4. [Polymorphism](#3-polymorphism)
5. [Abstraction](#4-abstraction)
6. [Additional OOP Concepts](#additional-oop-concepts)

---

## The Four Pillars of OOP

This Banking System demonstrates all four fundamental Object-Oriented Programming principles:

1. **Encapsulation** - Data hiding with getters/setters
2. **Inheritance** - Parent-child relationships between classes
3. **Polymorphism** - Same method, different behaviors
4. **Abstraction** - Abstract classes and interfaces

---

## 1. Encapsulation

### 📖 Definition
**Encapsulation** means bundling data (fields) and methods that operate on that data within a single unit (class), and restricting direct access to some of the object's components.

### 🎯 Where It's Used

#### Example 1: Account Class
**File:** `Account.java`

```java
public abstract class Account {
    // Private fields - cannot be accessed directly from outside
    private String accountNo;
    private double balance;
    private Customer owner;
    private LinkedList<Transaction> transactionHistory;

    // Public getters - controlled read access
    public String getAccountNo() { return this.accountNo; }
    public double getBalance() { return this.balance; }

    // Public setters with validation - controlled write access
    public void setAccountNo(String accountNo) {
        if (!ValidationPatterns.matchesPattern(accountNo,
                ValidationPatterns.ACCOUNT_NO_PATTERN)) {
            throw new IllegalArgumentException(ValidationPatterns.ACCOUNT_NO_ERROR);
        }
        this.accountNo = accountNo;
    }

    // Protected setter - only accessible to subclasses
    protected void setBalance(double balance) {
        this.balance = balance;
    }
}
```

**Why This Matters:**
- ✅ Fields are `private` - can't be changed directly
- ✅ Getters provide read-only access
- ✅ Setters include validation before allowing changes
- ✅ `protected` modifier restricts access to subclasses only

#### Example 2: Customer Class
**File:** `Customer.java`

```java
public class Customer {
    private String customerId;  // Encapsulated
    private String name;        // Encapsulated

    public void setName(String name) {
        // Validation ensures data integrity
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                ValidationPatterns.CUSTOMER_NAME_EMPTY_ERROR);
        }
        this.name = name.trim();  // Also cleans the data
    }
}
```

**Benefits:**
- 🛡️ **Data Protection** - Can't set invalid values
- 🔒 **Controlled Access** - Only through getters/setters
- ✨ **Data Integrity** - Validation ensures correctness

---

## 2. Inheritance

### 📖 Definition
**Inheritance** allows a class to inherit fields and methods from another class, promoting code reuse.

### 🎯 Where It's Used

#### Example 1: Account Hierarchy
**Parent Class:** `Account.java`

```java
// Abstract parent class
public abstract class Account {
    private String accountNo;
    private double balance;

    // Common behavior for all accounts
    public void deposit(double amount) {
        if (this.validateAmount(amount)) {
            this.balance += amount;
        }
    }

    // Abstract method - children must implement
    public abstract boolean withdraw(double amount);
}
```

**Child Classes:**

```java
// SavingsAccount inherits from Account
public class SavingsAccount extends Account {
    private double interestRate;  // Additional field

    public SavingsAccount(String accountNo, Customer owner, double interestRate) {
        super(accountNo, owner);  // Call parent constructor
        this.setInterestRate(interestRate);
    }

    // Implements abstract method from parent
    @Override
    public boolean withdraw(double amount) {
        // Savings-specific logic
    }
}

// CheckingAccount also inherits from Account
public class CheckingAccount extends Account {
    private double overdraftLimit;  // Additional field

    @Override
    public boolean withdraw(double amount) {
        // Checking-specific logic (allows overdraft)
    }
}
```

**Inheritance Hierarchy:**
```
         Account (abstract)
         /              \
        /                \
SavingsAccount      CheckingAccount
```

#### Example 2: User Hierarchy
**File:** `User.java`, `Admin.java`, `UserAccount.java`

```java
// Parent class
public abstract class User {
    private String username;
    private String password;
    private UserRole role;

    // Common methods for all users
    public abstract void displayMenu();
}

// Child class 1
public class Admin extends User {
    @Override
    public void displayMenu() {
        // Admin-specific menu
    }
}

// Child class 2
public class UserAccount extends User {
    private String customerId;  // Extra field for customers

    @Override
    public void displayMenu() {
        // Customer-specific menu
    }
}
```

**Benefits:**
- ♻️ **Code Reuse** - Common fields/methods in parent
- 🎯 **Specialization** - Children add specific features
- 🏗️ **Structure** - Clear class hierarchy

---

## 3. Polymorphism

### 📖 Definition
**Polymorphism** means "many forms" - the ability to treat objects of different types through the same interface.

### 🎯 Types Used in This Project

#### Type 1: Method Overriding (Runtime Polymorphism)

**Example: Withdraw Method**

```java
// In TransactionProcessor
Account account = AccountUtils.findAccount(accountList, accountNo);

// This single line behaves differently based on the actual object type!
account.withdraw(amount);  // Polymorphic call

// If account is SavingsAccount → calls SavingsAccount.withdraw()
// If account is CheckingAccount → calls CheckingAccount.withdraw()
```

**SavingsAccount.withdraw():**
```java
@Override
public boolean withdraw(double amount) {
    // NO OVERDRAFT - strict balance check
    if (amount > this.getBalance()) {
        return false;  // Can't withdraw more than balance
    }
    this.setBalance(this.getBalance() - amount);
    return true;
}
```

**CheckingAccount.withdraw():**
```java
@Override
public boolean withdraw(double amount) {
    // ALLOWS OVERDRAFT - balance + overdraft limit
    if (amount > this.getBalance() + this.overdraftLimit) {
        return false;  // Can't exceed balance + overdraft
    }
    this.setBalance(this.getBalance() - amount);
    return true;
}
```

**How It Works:**
1. Variable type: `Account` (parent class)
2. Actual object: `SavingsAccount` or `CheckingAccount` (child class)
3. Method called: Determined at **runtime** based on actual object type
4. This is **runtime polymorphism** (also called dynamic binding)

#### Type 2: Method Overloading (Compile-time Polymorphism)

**Example: Display Methods in Food Class Pattern**

```java
public class Food extends Product {
    // Same method name, different parameters

    public void display() {
        System.out.println("Basic display");
    }

    public void display(String name) {
        System.out.println("Hi, " + name);
        this.display();
    }

    public void display(String name, String type) {
        System.out.println("Hi, " + name + " Im " + type);
    }
}
```

**Benefits:**
- 🎭 **Flexibility** - Same method name, different behaviors
- 🧩 **Simplicity** - One interface, many implementations
- 🔄 **Extensibility** - Easy to add new account types

---

## 4. Abstraction

### 📖 Definition
**Abstraction** means hiding complex implementation details and showing only the essential features.

### 🎯 Where It's Used

#### Example 1: Abstract Class
**File:** `Account.java`

```java
// Abstract class - cannot be instantiated directly
public abstract class Account {
    private String accountNo;
    private double balance;

    // Concrete method - implementation provided
    public void deposit(double amount) {
        this.balance += amount;
    }

    // Abstract method - no implementation, children must provide it
    public abstract boolean withdraw(double amount);
}

// ❌ CANNOT DO THIS:
// Account acc = new Account("ACC001", customer);  // ERROR!

// ✅ CAN DO THIS:
// Account acc = new SavingsAccount("ACC001", customer, 0.03);  // OK!
```

**Why Abstract?**
- The concept of "Account" is too general
- Withdrawal rules differ by account type
- Forces each account type to implement their own logic

#### Example 2: Abstract User Class
**File:** `User.java`

```java
public abstract class User {
    private String username;
    private String password;

    // Abstract - each user type has different menu
    public abstract void displayMenu();
}
```

**Benefits:**
- 🎯 **Focus** - Hide complexity, show only what's needed
- 📋 **Contract** - Forces children to implement required methods
- 🛡️ **Prevention** - Can't create instances of abstract concepts

---

## Additional OOP Concepts

### 5. Composition (HAS-A Relationship)

**Definition:** An object contains another object.

**Example: BankingSystem HAS-A TransactionProcessor**

```java
public class BankingSystem {
    // BankingSystem HAS-A TransactionProcessor
    private TransactionProcessor transactionProcessor;

    // BankingSystem HAS-A AccountManager
    private AccountManager accountManager;

    // BankingSystem HAS-A CustomerManager
    private CustomerManager customerManager;
}
```

**Why Composition Instead of Inheritance?**
- ✅ More flexible - can swap implementations
- ✅ Avoids deep inheritance hierarchies
- ✅ Better separation of concerns

### 6. Association

**Definition:** A relationship between two classes where one uses the other.

**Example: Account and Customer**

```java
public class Account {
    private Customer owner;  // Account associated with Customer
}

public class Customer {
    private LinkedList<Account> accounts;  // Customer associated with Accounts
}
```

**Types of Association:**

#### One-to-One (1:1)
```java
public class Customer {
    private CustomerProfile profile;  // One customer has ONE profile
}

public class CustomerProfile {
    private Customer customer;  // One profile belongs to ONE customer
}
```

#### One-to-Many (1:M)
```java
public class Customer {
    private LinkedList<Account> accounts;  // One customer has MANY accounts
}

public class Account {
    private Customer owner;  // Each account has ONE owner
}
```

### 7. Static vs Instance Members

**Static Members:** Belong to the class, shared by all instances

```java
public class ValidationPatterns {
    // Static constant - shared by all
    public static final String ACCOUNT_NO_PATTERN = "^ACC\\d{3}$";

    // Static method - can be called without creating an object
    public static boolean matchesPattern(String value, String pattern) {
        return value != null && value.matches(pattern);
    }
}

// Usage:
ValidationPatterns.matchesPattern(accountNo, ValidationPatterns.ACCOUNT_NO_PATTERN);
// No need to create a ValidationPatterns object!
```

**Instance Members:** Belong to each object

```java
public class Account {
    private double balance;  // Each account has its own balance

    public void deposit(double amount) {  // Each account deposits to its own balance
        this.balance += amount;
    }
}
```

---

## OOP Concepts Summary Table

| Concept | Definition | Example in Project | File Location |
|---------|-----------|-------------------|---------------|
| **Encapsulation** | Data hiding with getters/setters | `private double balance` with `getBalance()` | `Account.java:9, 50` |
| **Inheritance** | Parent-child relationships | `SavingsAccount extends Account` | `SavingsAccount.java:6` |
| **Polymorphism** | Same method, different behavior | `account.withdraw()` behaves differently | `TransactionProcessor.java:57` |
| **Abstraction** | Abstract classes/methods | `public abstract boolean withdraw()` | `Account.java:28` |
| **Composition** | HAS-A relationship | `BankingSystem` HAS-A `TransactionProcessor` | `BankingSystem.java` |
| **Association** | Object relationships | `Customer` ↔ `Account` | `Customer.java:11, 15` |

---

## Key OOP Benefits Demonstrated

### 1. Code Reuse
- `Account` class provides common functionality
- `SavingsAccount` and `CheckingAccount` reuse it

### 2. Maintainability
- Change validation logic in one place (`ValidationPatterns`)
- All classes that use it automatically get the update

### 3. Flexibility
- Easy to add new account types (just extend `Account`)
- Easy to add new user types (just extend `User`)

### 4. Data Integrity
- Validation in setters prevents invalid data
- Private fields prevent direct manipulation

### 5. Modularity
- Each class has a single responsibility
- `TransactionProcessor` handles transactions
- `AccountManager` handles accounts
- `CustomerManager` handles customers

---

## Real-World Analogy

Think of the banking system like a real bank:

- **Encapsulation** = Bank vault (money is hidden, accessed through teller)
- **Inheritance** = Employee types (all employees share common traits, but managers have extra responsibilities)
- **Polymorphism** = Withdraw process (process differs for savings vs checking, but you use the same "withdraw" action)
- **Abstraction** = ATM interface (you don't see the complex machinery, just simple buttons)

---

**Created:** November 2024
**Project:** Banking System - OOP Project Part 2
