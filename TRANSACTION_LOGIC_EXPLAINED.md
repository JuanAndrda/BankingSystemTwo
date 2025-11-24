# Banking System - Transaction Logic Explanation

## 📚 Table of Contents
1. [Overview](#overview)
2. [Deposit Operation](#deposit-operation)
3. [Withdraw Operation](#withdraw-operation)
4. [Transfer Operation](#transfer-operation)
5. [Key Concepts](#key-concepts)
6. [Code Flow Diagrams](#code-flow-diagrams)

---

## Overview

This document explains how the three main transaction operations (Deposit, Withdraw, Transfer) work in the Banking System, showing exactly where the "solving" or business logic happens.

### The Three Operations:
- **DEPOSIT**: Add money to an account
- **WITHDRAW**: Remove money from an account (with different rules for Savings vs Checking)
- **TRANSFER**: Move money from one account to another

---

## Deposit Operation

### 🎯 Where It Happens
**Main File:** `TransactionProcessor.java`
**Method:** `deposit(String accountNo, double amount)` - Lines 22-43

### Step-by-Step Flow:

```
User Request → TransactionProcessor.deposit() → Account.deposit() → Balance Updated
```

### The Code:

#### Step 1: TransactionProcessor receives the request
**Location:** `TransactionProcessor.java:22-43`

```java
public boolean deposit(String accountNo, double amount) {
    // Find the account in the system
    Account account = AccountUtils.findAccount(this.accountList, accountNo);
    if (account == null) {
        System.out.println("✗ Account not found");
        return false;
    }

    try {
        // Create a transaction record
        Transaction tx = new Transaction("TX" + String.format("%03d", this.txCounter++),
                TransactionType.DEPOSIT, amount);
        tx.setToAccountNo(accountNo);

        // ⭐ THE SOLVING HAPPENS HERE ⭐
        account.deposit(amount);  // Calls the Account's deposit method

        // Mark transaction as successful
        tx.setStatus("COMPLETED");
        account.addTransaction(tx);
        System.out.println("✓ Deposit processed: " + tx.getTxId());
        return true;
    } catch (IllegalArgumentException e) {
        System.out.println("✗ Error processing deposit: " + e.getMessage());
        return false;
    }
}
```

#### Step 2: Account processes the deposit
**Location:** `Account.java:20-25`

```java
public void deposit(double amount) {
    // Validate the amount is positive
    if (this.validateAmount(amount)) {
        // ⭐ THIS IS THE ACTUAL SOLVING ⭐
        this.balance += amount;  // Add money to the balance
        System.out.println("✓ Deposited $" + amount + " to " + this.accountNo);
    }
}
```

### Key Logic:
- **Line 22 in Account.java:** `this.balance += amount;` - This is where money is actually added!

---

## Withdraw Operation

### 🎯 Where It Happens
**Main File:** `TransactionProcessor.java`
**Method:** `withdraw(String accountNo, double amount)` - Lines 45-72

### Step-by-Step Flow:

```
User Request → TransactionProcessor.withdraw() → Account.withdraw()
                                                     ↓
                                    (POLYMORPHISM - Different behavior!)
                                                     ↓
                        SavingsAccount.withdraw()  OR  CheckingAccount.withdraw()
                        (No overdraft allowed)          (Overdraft allowed)
```

### The Code:

#### Step 1: TransactionProcessor receives the request
**Location:** `TransactionProcessor.java:45-72`

```java
public boolean withdraw(String accountNo, double amount) {
    // Find the account
    Account account = AccountUtils.findAccount(this.accountList, accountNo);
    if (account == null) {
        System.out.println("✗ Account not found");
        return false;
    }

    try {
        // Create transaction record
        Transaction tx = new Transaction("TX" + String.format("%03d", this.txCounter++),
                TransactionType.WITHDRAW, amount);
        tx.setFromAccountNo(accountNo);

        // ⭐ THE SOLVING HAPPENS HERE (POLYMORPHIC CALL!) ⭐
        if (account.withdraw(amount)) {  // Different behavior for different account types!
            tx.setStatus("COMPLETED");
            account.addTransaction(tx);
            System.out.println("✓ Withdrawal processed: " + tx.getTxId());
            return true;
        } else {
            tx.setStatus("FAILED");
            account.addTransaction(tx);
            System.out.println("✗ Insufficient funds or withdrawal failed");
            return false;
        }
    } catch (IllegalArgumentException e) {
        System.out.println("✗ Error processing withdrawal: " + e.getMessage());
        return false;
    }
}
```

#### Step 2A: SavingsAccount processes withdrawal (NO OVERDRAFT)
**Location:** `SavingsAccount.java:22-33`

```java
@Override
public boolean withdraw(double amount) {
    // Validate amount is positive
    if (!this.validateAmount(amount)) return false;

    // ⭐ SOLVING: Check if there's enough money (NO OVERDRAFT ALLOWED) ⭐
    if (amount > this.getBalance()) {
        System.out.println("✗ Insufficient funds. Available: $" + this.getBalance());
        return false;  // Can't withdraw more than balance
    }

    // ⭐ THE ACTUAL SOLVING ⭐
    this.setBalance(this.getBalance() - amount);  // Subtract from balance
    System.out.println("✓ Withdrew $" + amount + " from " + this.getAccountNo());
    return true;
}
```

#### Step 2B: CheckingAccount processes withdrawal (WITH OVERDRAFT)
**Location:** `CheckingAccount.java:16-28`

```java
@Override
public boolean withdraw(double amount) {
    // Validate amount is positive
    if (!this.validateAmount(amount)) return false;

    // ⭐ SOLVING: Check with overdraft limit ⭐
    if (amount > this.getBalance() + this.overdraftLimit) {
        System.out.println("✗ Exceeds overdraft limit. Available: $" +
                (this.getBalance() + this.overdraftLimit));
        return false;  // Can't exceed balance + overdraft limit
    }

    // ⭐ THE ACTUAL SOLVING ⭐
    this.setBalance(this.getBalance() - amount);  // Subtract (balance can go negative!)
    System.out.println("✓ Withdrew $" + amount + " from " + this.getAccountNo());
    return true;
}
```

### Key Logic:
- **SavingsAccount (Line 25):** Checks `amount > balance` - NO overdraft
- **CheckingAccount (Line 19):** Checks `amount > balance + overdraftLimit` - Allows overdraft
- **Both (Line 30 / 25):** `this.setBalance(this.getBalance() - amount);` - Subtracts money

### 🎭 POLYMORPHISM in Action:
When you call `account.withdraw(amount)`, Java automatically calls the correct version:
- If `account` is a `SavingsAccount` → calls `SavingsAccount.withdraw()`
- If `account` is a `CheckingAccount` → calls `CheckingAccount.withdraw()`

This is **runtime polymorphism**!

---

## Transfer Operation

### 🎯 Where It Happens
**Main File:** `TransactionProcessor.java`
**Method:** `transfer(String fromAccountNo, String toAccountNo, double amount)` - Lines 74-106

### Step-by-Step Flow:

```
User Request → TransactionProcessor.transfer() → 1. fromAccount.withdraw(amount)
                                                     ↓ (if successful)
                                                  2. toAccount.deposit(amount)
```

### The Code:

#### The Transfer Logic (Two-Phase Commit)
**Location:** `TransactionProcessor.java:74-106`

```java
public boolean transfer(String fromAccountNo, String toAccountNo, double amount) {
    // Step 1: Find BOTH accounts
    Account fromAccount = AccountUtils.findAccount(this.accountList, fromAccountNo);
    Account toAccount = AccountUtils.findAccount(this.accountList, toAccountNo);

    if (fromAccount == null || toAccount == null) {
        System.out.println("✗ One or both accounts not found");
        return false;
    }

    try {
        // Step 2: Create transaction record
        Transaction tx = new Transaction("TX" + String.format("%03d", this.txCounter++),
                TransactionType.TRANSFER, amount);
        tx.setFromAccountNo(fromAccountNo);
        tx.setToAccountNo(toAccountNo);

        // ⭐ THE SOLVING HAPPENS HERE (TWO-PHASE COMMIT) ⭐

        // Phase 1: Try to withdraw from source account
        if (fromAccount.withdraw(amount)) {
            // Phase 2: Only deposit if withdrawal succeeded!
            toAccount.deposit(amount);

            // Success - record in both accounts
            tx.setStatus("COMPLETED");
            fromAccount.addTransaction(tx);
            toAccount.addTransaction(tx);
            System.out.println("✓ Transfer processed: " + tx.getTxId());
            return true;
        } else {
            // Withdrawal failed (insufficient funds)
            tx.setStatus("FAILED");
            fromAccount.addTransaction(tx);
            System.out.println("✗ Transfer failed: Insufficient funds");
            return false;
        }
    } catch (IllegalArgumentException e) {
        System.out.println("✗ Error processing transfer: " + e.getMessage());
        return false;
    }
}
```

### Key Logic:
- **Line 89:** `fromAccount.withdraw(amount)` - Remove money from source
- **Line 90:** `toAccount.deposit(amount)` - Add money to destination
- **Two-Phase Commit:** Deposit ONLY happens if withdraw succeeds!

### Why Two-Phase Commit?
This ensures **atomicity** - either the entire transfer succeeds, or nothing happens. You can't end up with money disappearing or appearing out of nowhere!

**Example:**
- You have $100 in Account A
- You try to transfer $150 to Account B
- Withdraw fails (insufficient funds)
- Deposit never happens
- **Result:** Both accounts unchanged ✅

---

## Key Concepts

### 1. Polymorphism (Withdraw)
The same method call `account.withdraw(amount)` behaves differently based on the actual object type:

```java
Account savings = new SavingsAccount("ACC001", customer, 0.03);
Account checking = new CheckingAccount("ACC002", customer, 500.0);

savings.withdraw(1000);   // Uses SavingsAccount logic (no overdraft)
checking.withdraw(1000);  // Uses CheckingAccount logic (allows overdraft)
```

### 2. Two-Phase Commit (Transfer)
Ensures data integrity by performing operations in sequence:

```
Step 1: Withdraw from source
   ↓ (if successful)
Step 2: Deposit to destination
```

If Step 1 fails, Step 2 never executes!

### 3. Transaction Recording
Every operation creates a `Transaction` object that records:
- Transaction ID (TX001, TX002, etc.)
- Type (DEPOSIT, WITHDRAW, TRANSFER)
- Amount
- Source/Destination accounts
- Status (COMPLETED or FAILED)
- Timestamp

### 4. Handler Methods (Complete User-Facing Flow)

The core transaction methods (`deposit()`, `withdraw()`, `transfer()`) are wrapped by "handler" methods that provide:
- User input validation using InputValidator
- Access control checks (customers can only access their own accounts)
- Audit logging
- User-friendly error messages

#### Handler Method: handleDeposit()
**Location:** `TransactionProcessor.java` (approximately lines 123-143)

```java
public void handleDeposit() {
    System.out.println("\n--- DEPOSIT ---");

    // Step 1: Get and validate account number
    String accountNo = this.validator.getValidatedAccountNumber(
            "Enter account number: ", this.accountList);
    if (accountNo == null) return;

    // Step 2: Check access control (customers can only access their accounts!)
    if (!this.bankingSystem.canAccessAccount(accountNo)) {
        System.out.println("✗ Access denied. You can only deposit to your own accounts.");
        return;
    }

    // Step 3: Get and validate amount
    double amount = this.validator.getValidatedPositiveAmount("Enter amount to deposit: $");
    if (amount == -1) return;

    // Step 4: Call core deposit method
    if (this.deposit(accountNo, amount)) {
        // Step 5: Log the action for audit trail
        this.bankingSystem.logAction("DEPOSIT",
            String.format("Deposited $%.2f to %s", amount, accountNo));
    }
}
```

**Complete Flow:**
```
User → Handler Method → Access Control → Core Method → Audit Log
  ↓           ↓              ↓               ↓            ↓
Input   Validation     canAccessAccount()  deposit()  logAction()
```

#### Handler Method: handleWithdraw()
Similar structure to `handleDeposit()`:
1. Validate account number
2. **Check access control** (THIS IS CRITICAL - prevents unauthorized access)
3. Validate amount
4. Call `withdraw()`
5. Log action

#### Handler Method: handleTransfer()
Most complex handler - needs TWO accounts:
1. Validate FROM account
2. **Check access to FROM account** (can't transfer from someone else's account!)
3. Validate TO account
4. Validate amount
5. Call `transfer()`
6. Log action

**Why This Matters:**
```java
// Without access control (BAD):
Customer Bob could call: deposit("ACC001", 1000000.00)  // Alice's account!

// With access control (GOOD):
if (!bankingSystem.canAccessAccount("ACC001")) {
    System.out.println("✗ Access denied");  // Bob blocked!
    return;
}
```

### 5. Transaction Status Tracking

Every transaction has a status that's validated:

```java
// In Transaction.java
public void setStatus(String status) {
    if (!status.equals("COMPLETED") && !status.equals("FAILED")) {
        throw new IllegalArgumentException("Status must be COMPLETED or FAILED");
    }
    this.status = status;
}
```

**Important:** Failed transactions are STILL recorded in history!

**Why?** For audit purposes - the system needs to track failed attempts.

```java
// Example from transfer():
if (fromAccount.withdraw(amount)) {
    toAccount.deposit(amount);
    tx.setStatus("COMPLETED");  // Success!
} else {
    tx.setStatus("FAILED");     // Failed, but still recorded!
    fromAccount.addTransaction(tx);  // Added to history for auditing
}
```

---

## Code Flow Diagrams

### Deposit Flow
```
┌─────────────────────────────────────────────────────────────┐
│ User enters: Account Number + Amount                        │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ TransactionProcessor.deposit()                              │
│   1. Find account                                           │
│   2. Create Transaction object                              │
│   3. Call account.deposit(amount) ◄── SOLVING STARTS HERE   │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ Account.deposit()                                           │
│   1. Validate amount > 0                                    │
│   2. balance += amount  ◄── THE ACTUAL SOLVING             │
│   3. Print success message                                  │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
                 ✓ SUCCESS
```

### Withdraw Flow (Polymorphic)
```
┌─────────────────────────────────────────────────────────────┐
│ User enters: Account Number + Amount                        │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ TransactionProcessor.withdraw()                             │
│   1. Find account                                           │
│   2. Create Transaction object                              │
│   3. Call account.withdraw(amount) ◄── POLYMORPHIC CALL     │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
         ┌────────────┴────────────┐
         ▼                         ▼
┌──────────────────────┐  ┌──────────────────────┐
│ SavingsAccount       │  │ CheckingAccount      │
│ .withdraw()          │  │ .withdraw()          │
│                      │  │                      │
│ IF amount > balance  │  │ IF amount >          │
│   FAIL               │  │    balance+overdraft │
│ ELSE                 │  │   FAIL               │
│   balance -= amount  │  │ ELSE                 │
│   SUCCESS            │  │   balance -= amount  │
│                      │  │   SUCCESS            │
└──────────────────────┘  └──────────────────────┘
         ▼                         ▼
         └────────────┬────────────┘
                      ▼
              ✓ SUCCESS or ✗ FAIL
```

### Transfer Flow (Two-Phase Commit)
```
┌─────────────────────────────────────────────────────────────┐
│ User enters: From Account + To Account + Amount             │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ TransactionProcessor.transfer()                             │
│   1. Find BOTH accounts (from & to)                         │
│   2. Create Transaction object                              │
│   3. TWO-PHASE COMMIT:                                      │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────────────────┐
│ PHASE 1: fromAccount.withdraw(amount)                       │
│   ◄── Try to remove money from source                       │
└─────────────────────┬───────────────────────────────────────┘
                      ▼
              ┌───────┴───────┐
              ▼               ▼
         ✓ SUCCESS        ✗ FAILED
              │               │
              ▼               ▼
┌──────────────────────┐  ┌──────────────────────┐
│ PHASE 2:             │  │ STOP HERE            │
│ toAccount.deposit()  │  │ Mark as FAILED       │
│ ◄── Add money to     │  │ Return false         │
│     destination      │  │                      │
└──────────────────────┘  └──────────────────────┘
         ▼
    ✓ COMPLETE
    (Money moved safely!)
```

---

## Summary Table

| Operation | Main Logic Location | Core Solving Line | Special Feature |
|-----------|---------------------|-------------------|-----------------|
| **DEPOSIT** | `TransactionProcessor:34` → `Account:22` | `this.balance += amount;` | Simple addition |
| **WITHDRAW (Savings)** | `TransactionProcessor:57` → `SavingsAccount:30` | `this.setBalance(this.getBalance() - amount);` | No overdraft check |
| **WITHDRAW (Checking)** | `TransactionProcessor:57` → `CheckingAccount:25` | `this.setBalance(this.getBalance() - amount);` | Overdraft allowed |
| **TRANSFER** | `TransactionProcessor:89-90` | `withdraw()` then `deposit()` | Two-phase commit |

---

## File References

- **TransactionProcessor.java** - `src/com/banking/managers/TransactionProcessor.java`
- **Account.java** - `src/com/banking/models/Account.java`
- **SavingsAccount.java** - `src/com/banking/models/SavingsAccount.java`
- **CheckingAccount.java** - `src/com/banking/models/CheckingAccount.java`

---

**Created:** November 2024
**Project:** Banking System - OOP Project Part 2
