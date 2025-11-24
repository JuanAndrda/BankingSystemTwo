# Banking System - Project Overview

## 📚 Table of Contents
1. [Project Summary](#project-summary)
2. [Features](#features)
3. [System Architecture](#system-architecture)
4. [Project Structure](#project-structure)
5. [How to Run](#how-to-run)
6. [Demo Data](#demo-data)
7. [Key Learning Outcomes](#key-learning-outcomes)

---

## Project Summary

**Project Name:** Banking System
**Version:** Part 2 (Refactored & Simplified)
**Language:** Java
**Course Requirements:** Object-Oriented Programming (OOP) + Data Structures (CC 204)

### What Is This Project?

A command-line banking application that simulates real banking operations including:
- Customer management (create, view, update, delete)
- Account management (savings and checking accounts)
- Transaction processing (deposit, withdraw, transfer)
- User authentication (admin and customer roles)
- Audit logging (track all operations)

### Why Was It Built?

To demonstrate comprehensive understanding of:
1. **OOP Principles** - Encapsulation, Inheritance, Polymorphism, Abstraction
2. **Data Structures** - LinkedList, Stack, Sorting Algorithms
3. **Software Design** - Clean code, separation of concerns, modularity
4. **Real-World Application** - Simulates actual banking operations

---

## Features

### 1. User Management
- ✅ **Role-Based Access Control (RBAC)**
  - Admin users can access all features
  - Customer users can only access their own accounts
- ✅ **Authentication**
  - Secure login with username/password
  - Failed attempt tracking
- ✅ **User Types**
  - Admin (full system access)
  - Customer (limited to own data)

### 2. Customer Management
- ✅ **CRUD Operations**
  - Create new customers with validation
  - Read/View customer information
  - Update customer details
  - Delete customers (with confirmation)
- ✅ **Customer Profiles**
  - One-to-one relationship with CustomerProfile
  - Store address, phone, email
- ✅ **Integrated Onboarding**
  - Create customer → profile → account in one workflow

### 3. Account Management
- ✅ **Account Types**
  - **Savings Account** - Earns interest, no overdraft
  - **Checking Account** - Allows overdraft up to limit
- ✅ **Account Operations**
  - Create accounts (auto-generated account numbers)
  - View account details (polymorphic display)
  - Delete accounts
  - Apply interest to savings accounts
  - Update overdraft limits for checking accounts
- ✅ **Sorting**
  - Sort accounts by customer name (alphabetical)
  - Sort accounts by balance (descending)
  - Before/after display demonstrates sorting

### 4. Transaction Processing
- ✅ **Transaction Types**
  - **Deposit** - Add money to account
  - **Withdraw** - Remove money (different rules for savings vs checking)
  - **Transfer** - Move money between accounts
- ✅ **Transaction History**
  - Track all transactions per account
  - Display in LIFO order (most recent first) using Stack
  - Record success and failure
- ✅ **Two-Phase Commit**
  - Transfer uses two-phase commit for atomicity
  - Withdraw first, then deposit (only if withdraw succeeds)

### 5. Security & Auditing
- ✅ **Access Control**
  - Customers can only access their own accounts
  - Admins have full access
- ✅ **Audit Trail**
  - Log all operations (create, delete, deposit, etc.)
  - Timestamp all actions
  - Track which user performed each action
- ✅ **Data Validation**
  - Validate all input (account numbers, customer IDs, etc.)
  - Centralized validation patterns
  - Prevent invalid data entry

---

## System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────┐
│                   Main.java                     │
│  (Entry point - initializes demo data)          │
└──────────────────┬──────────────────────────────┘
                   │ creates
                   ▼
┌─────────────────────────────────────────────────┐
│              BankingSystem.java                 │
│  (Main orchestrator - coordinates all managers) │
└─────────┬───────────────────────────────────────┘
          │ contains (composition)
          │
  +-------+-------+-------+-------+
  │       │       │       │       │
  ▼       ▼       ▼       ▼       ▼
┌───────────┐ ┌──────────┐ ┌───────────┐ ┌───────────┐
│Transaction│ │ Account  │ │ Customer  │ │    Auth   │
│ Processor │ │ Manager  │ │  Manager  │ │  Manager  │
└─────┬─────┘ └────┬─────┘ └─────┬─────┘ └─────┬─────┘
      │            │             │             │
      │ manages    │ manages     │ manages     │ manages
      ▼            ▼             ▼             ▼
┌──────────┐ ┌─────────┐ ┌──────────┐ ┌──────────┐
│Transaction│ │ Account │ │ Customer │ │   User   │
└──────────┘ └───┬─────┘ └────┬─────┘ └────┬─────┘
                  │            │            │
         +--------+------+     │            │
         │               │     │            │
         ▼               ▼     ▼            ▼
    ┌─────────┐   ┌──────────┐ ┌──────────┐ ┌──────┐
    │ Savings │   │ Checking │ │  Profile │ │ Admin│
    │ Account │   │ Account  │ │          │ │Customer│
    └─────────┘   └──────────┘ └──────────┘ └──────┘
```

### Design Patterns Used

1. **Composition over Inheritance**
   - `BankingSystem` HAS-A managers (not IS-A)
   - Better flexibility and modularity

2. **Dependency Injection**
   - Scanner injected into BankingSystem
   - Shared collections passed to managers

3. **Single Responsibility Principle**
   - Each manager handles one concern
   - TransactionProcessor only handles transactions
   - AccountManager only handles accounts

4. **Strategy Pattern** (via Polymorphism)
   - Different withdrawal strategies for different account types

---

## Project Structure

### Package Organization

```
src/com/banking/
│
├── Main.java                    # Entry point
├── BankingSystem.java           # Main orchestrator
├── MenuAction.java              # Menu options enum
│
├── models/                      # Domain models (business entities)
│   ├── Account.java             # Abstract account class
│   ├── SavingsAccount.java      # Savings account (with interest)
│   ├── CheckingAccount.java     # Checking account (with overdraft)
│   ├── Customer.java            # Customer entity
│   ├── CustomerProfile.java     # Customer profile (1:1)
│   ├── Transaction.java         # Transaction record
│   └── TransactionType.java     # Transaction type enum
│
├── managers/                    # Service classes (business logic)
│   ├── TransactionProcessor.java   # Transaction operations
│   ├── AccountManager.java         # Account CRUD + sorting
│   ├── CustomerManager.java        # Customer CRUD + onboarding
│   └── AuthenticationManager.java  # Login, auth, audit
│
├── auth/                        # Authentication & authorization
│   ├── User.java                # Abstract user class
│   ├── Admin.java               # Admin user
│   ├── UserAccount.java         # Customer user
│   ├── UserRole.java            # Role enum
│   └── AuditLog.java            # Audit log entry
│
└── utilities/                   # Utility classes
    ├── ValidationPatterns.java  # Validation constants
    ├── InputValidator.java      # Input validation helpers
    └── AccountUtils.java        # Account search utilities
```

### File Count & Line Count

| Package | Files | Lines (Simplified) |
|---------|-------|--------------------|
| **models** | 7 files | 482 lines |
| **managers** | 4 files | 1,529 lines |
| **auth** | 5 files | 490 lines |
| **utilities** | 3 files | 639 lines |
| **main** | 3 files | 918 lines |
| **TOTAL** | 22 files | ~4,058 lines |

---

## How to Run

### Prerequisites
- Java JDK 11 or higher
- Command line / Terminal

### Compilation

```bash
# Navigate to project directory
cd C:/Users/juanr/IdeaProjects/BankingProjectPart2

# Compile all Java files
javac -d out -sourcepath src src/com/banking/Main.java
```

### Execution

```bash
# Run the application
java -cp out com.banking.Main
```

### Login Credentials

#### Admin Users
| Username | Password |
|----------|----------|
| admin | admin123 |
| alice_admin | pass123 |

#### Customer Users
| Username | Password | Customer ID |
|----------|----------|-------------|
| alice | alice123 | C001 |
| bob | bob123 | C002 |
| charlie | charlie123 | C003 |

---

## Demo Data

The system comes pre-loaded with demo data for testing:

### Customers
- **C001** - Alice Johnson
  - Profile: 123 Main St, 555-1234567, alice@email.com
  - Accounts: ACC001 (Savings), ACC002 (Checking), ACC005 (Savings)

- **C002** - Bob Smith
  - Profile: 456 Oak Ave, 555-2345678, bob@email.com
  - Accounts: ACC003 (Savings), ACC006 (Savings)

- **C003** - Charlie Brown
  - Profile: 789 Pine Rd, 555-3456789, charlie@email.com
  - Accounts: ACC004 (Checking), ACC007 (Checking)

### Sample Transactions
1. Deposit $1000 to ACC001
2. Deposit $500 to ACC002
3. Deposit $2000 to ACC003
4. Withdraw $100 from ACC002
5. Transfer $300 from ACC003 to ACC001

---

## Key Learning Outcomes

### OOP Principles Demonstrated

#### 1. Encapsulation
- ✅ Private fields with public getters/setters
- ✅ Validation in setters
- ✅ Protected access for subclasses
- **Example:** `Account.java:8-71`

#### 2. Inheritance
- ✅ Abstract parent class (Account, User)
- ✅ Concrete child classes (SavingsAccount, CheckingAccount)
- ✅ Method overriding
- **Example:** `SavingsAccount extends Account`

#### 3. Polymorphism
- ✅ Runtime polymorphism (withdraw behaves differently)
- ✅ Method overriding
- ✅ Same interface, different implementations
- **Example:** `account.withdraw()` - Line varies by type

#### 4. Abstraction
- ✅ Abstract classes (Account, User)
- ✅ Abstract methods (withdraw, displayMenu)
- ✅ Hide implementation details
- **Example:** `public abstract boolean withdraw(double amount)`

### Data Structures Demonstrated

#### 1. LinkedList
- ✅ Dynamic list for customers, accounts, transactions
- ✅ Insertion order maintained
- ✅ Efficient add/remove operations
- **Used in:** 6 different places

#### 2. Stack (LIFO)
- ✅ Display transactions newest-first
- ✅ Push/pop operations
- ✅ Demonstrates LIFO principle
- **Used in:** `TransactionProcessor.java:109`

#### 3. Insertion Sort
- ✅ Sort accounts by name (alphabetical)
- ✅ Sort accounts by balance (descending)
- ✅ In-place sorting algorithm
- **Used in:** `AccountManager.java`

### Software Engineering Practices

1. ✅ **Clean Code**
   - Meaningful variable names
   - Single responsibility per class
   - DRY (Don't Repeat Yourself)

2. ✅ **Separation of Concerns**
   - Models separate from managers
   - Business logic separate from UI
   - Authentication separate from business logic

3. ✅ **Input Validation**
   - Centralized validation patterns
   - Consistent error messages
   - Prevent invalid data

4. ✅ **Security**
   - Role-based access control
   - Audit logging
   - Input sanitization

---

## Documentation Files

This project includes comprehensive documentation:

1. **PROJECT_OVERVIEW.md** (this file) - Overall project summary
2. **OOP_CONCEPTS.md** - Detailed OOP explanations with examples
3. **CLASS_RELATIONSHIPS.md** - Class diagrams and relationships
4. **DATA_STRUCTURES.md** - Data structures and algorithms explained
5. **TRANSACTION_LOGIC_EXPLAINED.md** - Transaction processing flow

---

## Future Enhancements

### Possible Improvements:
1. **Database Integration** - Replace LinkedList with SQL database
2. **GUI Interface** - Add JavaFX or Swing GUI
3. **Interest Calculation** - Automatic monthly interest
4. **Loan System** - Add loan accounts
5. **Transaction Limits** - Daily withdrawal limits
6. **Multi-Currency** - Support multiple currencies
7. **Email Notifications** - Send transaction confirmations
8. **Reports** - Monthly statements, year-end summaries

---

## Troubleshooting

### Common Issues

#### Issue 1: "Cannot find symbol" errors
**Solution:** Make sure you compiled from the correct directory
```bash
javac -d out -sourcepath src src/com/banking/Main.java
```

#### Issue 2: "ClassNotFoundException"
**Solution:** Run from the out directory
```bash
java -cp out com.banking.Main
```

#### Issue 3: Login fails
**Solution:** Use the demo credentials provided in the [Login Credentials](#login-credentials) section

---

## Credits

**Developed by:** Juan R.
**Course:** Object-Oriented Programming + Data Structures
**Date:** November 2024
**Version:** 2.0 (Refactored & Simplified)

### Technologies Used:
- Java 11+
- LinkedList (java.util)
- Stack (java.util)
- Scanner (java.util)
- LocalDateTime (java.time)

---

## License

This project is for educational purposes only.

---

**Created:** November 2024
**Project:** Banking System - OOP Project Part 2
