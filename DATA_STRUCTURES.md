# Data Structures in Banking System

## 📚 Table of Contents
1. [Overview](#overview)
2. [LinkedList](#linkedlist)
3. [Stack](#stack)
4. [Sorting Algorithms](#sorting-algorithms)
5. [Data Structure Summary](#data-structure-summary)

---

## Overview

This Banking System uses various data structures to efficiently store and manage data. Understanding these data structures is essential for the CC 204 (Data Structures and Algorithms) requirement.

### Data Structures Used:
1. **LinkedList** - Dynamic list for customers, accounts, transactions, users
2. **Stack** - LIFO structure for displaying transaction history
3. **Insertion Sort** - Sorting algorithm for accounts

---

## LinkedList

### 📖 Definition
**LinkedList** is a dynamic data structure where elements are stored in nodes, and each node points to the next node. Unlike arrays, LinkedLists can grow and shrink dynamically.

### 🎯 Where It's Used

#### 1. Customer List
**File:** `BankingSystem.java`, `CustomerManager.java`

```java
private LinkedList<Customer> customers;
```

**Purpose:** Store all customers in the banking system

**Operations:**
- **Add:** `customers.add(customer)` - O(1) at end
- **Search:** Loop through list - O(n)
- **Remove:** `customers.remove(customer)` - O(n)

**Example:**
```java
LinkedList<Customer> customers = new LinkedList<>();

// Add customers
customers.add(new Customer("C001", "Alice"));
customers.add(new Customer("C002", "Bob"));
customers.add(new Customer("C003", "Charlie"));

// Search for a customer
for (Customer c : customers) {
    if (c.getCustomerId().equals("C001")) {
        System.out.println("Found: " + c.getName());
    }
}
```

---

#### 2. Account List
**File:** `BankingSystem.java`, `AccountManager.java`

```java
private LinkedList<Account> accountList;
```

**Purpose:** Store all accounts in the banking system

**Why LinkedList?**
- ✅ Dynamic size - can add/remove accounts easily
- ✅ Maintains insertion order
- ✅ Good for iteration (frequent operation)

**Example:**
```java
LinkedList<Account> accountList = new LinkedList<>();

// Add accounts
accountList.add(new SavingsAccount("ACC001", alice, 0.03));
accountList.add(new CheckingAccount("ACC002", alice, 500.0));

// Find account
Account found = null;
for (Account acc : accountList) {
    if (acc.getAccountNo().equals("ACC001")) {
        found = acc;
        break;
    }
}
```

---

#### 3. Transaction History (Per Account)
**File:** `Account.java`

```java
private LinkedList<Transaction> transactionHistory;
```

**Purpose:** Store all transactions for a specific account in chronological order

**Why LinkedList?**
- ✅ Maintains order (oldest to newest)
- ✅ Can add transactions easily
- ✅ Can iterate through history

**Example:**
```java
public class Account {
    private LinkedList<Transaction> transactionHistory;

    public Account(String accountNo, Customer owner) {
        this.transactionHistory = new LinkedList<>();
    }

    public void addTransaction(Transaction t) {
        this.transactionHistory.add(t);  // Add to end (chronological order)
    }

    public LinkedList<Transaction> getTransactionHistory() {
        return this.transactionHistory;  // Returns in order added
    }
}
```

**Timeline Visualization:**
```
Transaction History (LinkedList):
[TX001: Deposit $1000] → [TX002: Withdraw $100] → [TX003: Deposit $500]
   (oldest)                                            (newest)
```

---

#### 4. Customer's Account List
**File:** `Customer.java`

```java
private LinkedList<Account> accounts;
```

**Purpose:** Store all accounts belonging to a specific customer

**Operations:**
```java
public class Customer {
    private LinkedList<Account> accounts;

    public void addAccount(Account a) {
        if (a != null) {
            this.accounts.add(a);  // Add account to customer's list
        }
    }

    public boolean removeAccount(String accountNo) {
        Iterator<Account> iterator = this.accounts.iterator();
        while (iterator.hasNext()) {
            Account acc = iterator.next();
            if (acc.getAccountNo().equals(accountNo)) {
                iterator.remove();  // Safe removal during iteration
                return true;
            }
        }
        return false;
    }
}
```

**Example:**
```java
Customer alice = new Customer("C001", "Alice");

Account savings = new SavingsAccount("ACC001", alice, 0.03);
Account checking = new CheckingAccount("ACC002", alice, 500.0);

alice.addAccount(savings);   // Alice's accounts: [ACC001]
alice.addAccount(checking);  // Alice's accounts: [ACC001, ACC002]
```

---

#### 5. User Registry
**File:** `AuthenticationManager.java`

```java
private LinkedList<User> userRegistry;
```

**Purpose:** Store all users (admins and customers) in the system

---

#### 6. Audit Trail
**File:** `AuthenticationManager.java`

```java
private LinkedList<AuditLog> auditTrail;
```

**Purpose:** Store all system operations for security auditing

**Example:**
```java
LinkedList<AuditLog> auditTrail = new LinkedList<>();

// Log actions
auditTrail.add(new AuditLog("admin", "CREATE_CUSTOMER", "Customer C001 created"));
auditTrail.add(new AuditLog("alice", "DEPOSIT", "Amount: $1000 to ACC001"));

// View audit trail (chronological order)
for (AuditLog log : auditTrail) {
    System.out.println(log.getTimestamp() + ": " + log.getAction());
}
```

---

### LinkedList Time Complexity

| Operation | Time Complexity | Example |
|-----------|----------------|---------|
| **Add at end** | O(1) | `list.add(element)` |
| **Add at beginning** | O(1) | `list.addFirst(element)` |
| **Search** | O(n) | Loop through entire list |
| **Remove** | O(n) | Find then remove |
| **Get by index** | O(n) | `list.get(index)` |
| **Size** | O(1) | `list.size()` |

---

## Stack

### 📖 Definition
**Stack** is a Last-In-First-Out (LIFO) data structure. Think of it like a stack of plates - you can only add or remove from the top.

### 🎯 Where It's Used

#### Transaction History Display
**File:** `TransactionProcessor.java:109-121`

```java
public Stack<Transaction> getAccountTransactionsAsStack(String accountNo) {
    Account acc = AccountUtils.findAccount(this.accountList, accountNo);
    if (acc == null) return new Stack<>();

    Stack<Transaction> txStack = new Stack<>();
    LinkedList<Transaction> history = acc.getTransactionHistory();

    // Convert LinkedList to Stack
    for (Transaction tx : history) {
        txStack.push(tx);  // Push each transaction onto stack
    }

    return txStack;  // Stack with most recent on top
}
```

**Why Stack?**
- ✅ Shows most recent transactions first (user-friendly)
- ✅ Demonstrates LIFO data structure (CC 204 requirement)
- ✅ Easy to display in reverse order

**Visualization:**
```
LinkedList (chronological):
[TX001: Deposit $1000] → [TX002: Withdraw $100] → [TX003: Deposit $500]
   (oldest)                                            (newest)

↓ Convert to Stack (push each one)

Stack (LIFO - newest on top):
     ┌─────────────────────┐
     │ TX003: Deposit $500 │ ← Top (pop first)
     ├─────────────────────┤
     │ TX002: Withdraw $100│
     ├─────────────────────┤
     │ TX001: Deposit $1000│
     └─────────────────────┘
```

**Display Code:**
```java
Stack<Transaction> txStack = getAccountTransactionsAsStack("ACC001");

System.out.println("Transaction History (Most Recent First):");
while (!txStack.isEmpty()) {
    System.out.println(txStack.pop());  // Pop from top
}

// Output:
// TX003: Deposit $500.00 [COMPLETED] 2024-11-22 15:30:00
// TX002: Withdraw $100.00 [COMPLETED] 2024-11-22 15:20:00
// TX001: Deposit $1000.00 [COMPLETED] 2024-11-22 15:10:00
```

---

### Stack Operations

| Operation | Description | Time Complexity |
|-----------|-------------|-----------------|
| **push(element)** | Add element to top | O(1) |
| **pop()** | Remove and return top element | O(1) |
| **peek()** | View top element without removing | O(1) |
| **isEmpty()** | Check if stack is empty | O(1) |
| **size()** | Get number of elements | O(1) |

**Example:**
```java
Stack<Transaction> stack = new Stack<>();

// Push (add to top)
stack.push(tx1);  // Stack: [tx1]
stack.push(tx2);  // Stack: [tx1, tx2]
stack.push(tx3);  // Stack: [tx1, tx2, tx3] ← top

// Peek (view top without removing)
Transaction top = stack.peek();  // Returns tx3, stack unchanged

// Pop (remove and return top)
Transaction t1 = stack.pop();  // Returns tx3, Stack: [tx1, tx2]
Transaction t2 = stack.pop();  // Returns tx2, Stack: [tx1]
Transaction t3 = stack.pop();  // Returns tx1, Stack: []

// isEmpty
boolean empty = stack.isEmpty();  // true
```

---

## Sorting Algorithms

### 📖 Insertion Sort

**Definition:** A simple sorting algorithm that builds the final sorted list one item at a time by inserting each element into its correct position.

### 🎯 Where It's Used

#### Sort Accounts by Name
**File:** `AccountManager.java`

```java
private void insertionSortByName(LinkedList<Account> accountList) {
    // For each unsorted element (starting at index 1)
    for (int i = 1; i < accountList.size(); i++) {
        // Get current account to insert into sorted portion
        Account currentAccount = accountList.get(i);
        String currentName = (currentAccount.getOwner() != null)
                ? currentAccount.getOwner().getName() : "";

        // Find the correct position to insert currentAccount
        int j = i - 1;
        while (j >= 0) {
            Account compareAccount = accountList.get(j);
            String compareName = (compareAccount.getOwner() != null)
                    ? compareAccount.getOwner().getName() : "";

            // If current name comes before compare name, shift right
            if (currentName.compareToIgnoreCase(compareName) < 0) {
                accountList.set(j + 1, compareAccount);  // Shift element right
                j--;  // Continue searching left
            } else {
                break;  // Found correct position
            }
        }

        // Insert current account at correct position
        accountList.set(j + 1, currentAccount);
    }
}
```

**Example Step-by-Step:**

**Initial:** `[Charlie, Alice, Bob]`

**Pass 1** (i=1, current=Alice):
- Compare Alice with Charlie → Alice < Charlie
- Shift Charlie right: `[Charlie, Charlie, Bob]`
- Insert Alice: `[Alice, Charlie, Bob]` ✓

**Pass 2** (i=2, current=Bob):
- Compare Bob with Charlie → Bob < Charlie
- Shift Charlie right: `[Alice, Charlie, Charlie]`
- Compare Bob with Alice → Bob > Alice
- Insert Bob: `[Alice, Bob, Charlie]` ✓

**Final:** `[Alice, Bob, Charlie]`

---

#### Sort Accounts by Balance
**File:** `AccountManager.java`

```java
private void insertionSortByBalance(LinkedList<Account> accountList) {
    for (int i = 1; i < accountList.size(); i++) {
        Account currentAccount = accountList.get(i);
        double currentBalance = currentAccount.getBalance();

        int j = i - 1;
        while (j >= 0) {
            Account compareAccount = accountList.get(j);
            double compareBalance = compareAccount.getBalance();

            // If current balance is GREATER (descending order)
            if (currentBalance > compareBalance) {
                accountList.set(j + 1, compareAccount);
                j--;
            } else {
                break;
            }
        }

        accountList.set(j + 1, currentAccount);
    }
}
```

**Example:**

**Initial:** `[$500, $2000, $1000]`

**Pass 1** (i=1, current=$2000):
- Compare $2000 with $500 → $2000 > $500
- Shift $500 right: `[$500, $500, $1000]`
- Insert $2000: `[$2000, $500, $1000]` ✓

**Pass 2** (i=2, current=$1000):
- Compare $1000 with $500 → $1000 > $500
- Shift $500 right: `[$2000, $500, $500]`
- Compare $1000 with $2000 → $1000 < $2000 (stop)
- Insert $1000: `[$2000, $1000, $500]` ✓

**Final (Descending):** `[$2000, $1000, $500]`

---

### Insertion Sort Characteristics

| Property | Value |
|----------|-------|
| **Best Case** | O(n) - Already sorted |
| **Average Case** | O(n²) |
| **Worst Case** | O(n²) - Reverse sorted |
| **Space Complexity** | O(1) - In-place sorting |
| **Stable?** | Yes - maintains relative order |

**When to Use:**
- ✅ Small datasets (like account lists in this project)
- ✅ Nearly sorted data
- ✅ Simple to implement and understand
- ❌ Not for large datasets (use QuickSort or MergeSort instead)

---

## Data Structure Summary

### Complete Overview Table

| Data Structure | Where Used | Purpose | Key Operations |
|----------------|------------|---------|----------------|
| **LinkedList&lt;Customer&gt;** | `BankingSystem`, `CustomerManager` | Store all customers | add(), remove(), iterate |
| **LinkedList&lt;Account&gt;** | `BankingSystem`, `AccountManager` | Store all accounts | add(), remove(), sort(), search |
| **LinkedList&lt;Transaction&gt;** | `Account` | Store account transaction history | add(), iterate |
| **LinkedList&lt;Account&gt;** | `Customer` | Store customer's accounts | add(), remove(), iterate |
| **LinkedList&lt;User&gt;** | `AuthenticationManager` | Store all users | add(), search |
| **LinkedList&lt;AuditLog&gt;** | `AuthenticationManager` | Store audit trail | add(), iterate |
| **Stack&lt;Transaction&gt;** | `TransactionProcessor` | Display transactions (LIFO) | push(), pop(), isEmpty() |
| **Insertion Sort** | `AccountManager` | Sort accounts by name/balance | In-place sorting |

---

### Why These Data Structures?

#### LinkedList Over ArrayList
**Pros:**
- ✅ Better for frequent insertions/deletions
- ✅ No need to resize (like ArrayList)
- ✅ Maintains insertion order

**Cons:**
- ❌ Slower random access (get by index)
- ❌ More memory per element (stores references)

**Decision:** Since this banking system frequently adds/removes customers and accounts, LinkedList is appropriate.

#### Stack for Transaction Display
**Why not just reverse the LinkedList?**
- ✅ Stack is more semantic (LIFO makes sense for "most recent first")
- ✅ Demonstrates understanding of Stack data structure (CC 204)
- ✅ Cleaner code with push/pop operations

#### Insertion Sort Over QuickSort
**Why Insertion Sort?**
- ✅ Simple to implement and understand
- ✅ Good for small datasets (typical bank has few accounts)
- ✅ In-place (no extra memory needed)
- ✅ Stable (maintains relative order)

**When QuickSort would be better:**
- Large datasets (1000+ accounts)
- Performance-critical applications

---

## Practical Examples

### Example 1: Account Search (Linear Search)
```java
public Account findAccount(String accountNo) {
    // O(n) time complexity - must check each account
    for (Account acc : accountList) {
        if (acc.getAccountNo().equals(accountNo)) {
            return acc;  // Found!
        }
    }
    return null;  // Not found
}
```

### Example 2: Transaction History Reversal (LinkedList → Stack)
```java
// LinkedList stores chronologically (oldest → newest)
LinkedList<Transaction> history = account.getTransactionHistory();
// [TX001 (old), TX002, TX003, TX004, TX005 (new)]

// Convert to Stack for display (newest → oldest)
Stack<Transaction> stack = new Stack<>();
for (Transaction tx : history) {
    stack.push(tx);
}
// Stack: TX005 (top), TX004, TX003, TX002, TX001 (bottom)

// Display newest first
while (!stack.isEmpty()) {
    System.out.println(stack.pop());  // TX005, TX004, TX003...
}
```

### Example 3: Sorting Demonstration
```java
// Before sorting
List: [Charlie ($500), Alice ($2000), Bob ($1000)]

// Sort by name (ascending)
insertionSortByName(accountList);
List: [Alice ($2000), Bob ($1000), Charlie ($500)]

// Sort by balance (descending)
insertionSortByBalance(accountList);
List: [Alice ($2000), Bob ($1000), Charlie ($500)]
```

---

**Created:** November 2024
**Project:** Banking System - OOP Project Part 2
