# Banking System - UML with Explicit Connections

## Complete Diagram with Numbered Connection Points

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                            BANKING SYSTEM - ALL CONNECTIONS SHOWN                       │
└─────────────────────────────────────────────────────────────────────────────────────────┘


                                    ┌────────────────┐
                                    │      Main      │
                                    └────────┬───────┘
                                             │
                                             │ (1) creates
                                             ▼
                           ┌─────────────────────────────────┐
                           │      BankingSystem              │
                           │                                 │
                           │  -customers: List<Customer>     │
                           │  -accounts: List<Account>  ─────┼────┐
                           │                                 │    │
                           │  -customerManager ──────────────┼─┐  │
                           │  -accountManager ───────────────┼─┼┐ │
                           │  -transactionProcessor ─────────┼─┼┼┐│
                           │  -authManager ──────────────────┼─┼┼││
                           └─────────────────────────────────┘ │││││
                                    │  │  │  │                 │││││
                        ┌───────────┘  │  │  └─────────────┐   │││││
                        │              │  │                 │   │││││
                (2) owns│      (3) owns│  │(4) owns         │(5)│││││
                   ◆    │          ◆   │  │   ◆            │   │││││
                        ▼              ▼  ▼                 ▼   │││││
          ┌──────────────────────┐  ┌────────────────┐  ┌──────────────────┐
          │  CustomerManager     │  │ AccountManager │  │TransactionProc   │
          │                      │  │                │  │                  │
          │ -allCustomers ───────┼──┼────────────────┼──┼──┐               │
          │ -allAccounts ────────┼──┼────────────────┼──┼──┼───┐           │
          │ -bankingSystem ◄─────┼──┼────────────────┼──┼──┼───┼────┐      │
          │                   (6)│  │             (7)│  │  │(8)│    │      │
          └──────┬───────────────┘  └───────┬────────┘  └──┼───┼────┼──────┘
                 │                          │               │   │    │
                 │                          │               │   │    │
          ┌──────────────────────┐          │               │   │    │
          │AuthenticationManager │          │               │   │    │
          │                      │          │               │   │    │
          │ -users: List<User>   │          │               │   │    │
          │ -auditTrail ─────────┼─┐        │               │   │    │
          │ -currentUser         │ │        │               │   │    │
          │                   (9)│ │        │               │   │    │
          └──────┬───────────────┘ │        │               │   │    │
                 │                 │        │               │   │    │
                 │                 │        │               │   │    │
═════════════════╪═════════════════╪════════╪═══════════════╪═══╪════╪═══════════════════
                 │                 │        │               │   │    │
        manages  │                 │        │  manages      │   │    │
                 ▼                 │        ▼               ▼   ▼    ▼
                                   │
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                              DOMAIN MODELS LAYER                                       │
└────────────────────────────────────────────────────────────────────────────────────────┘


         (10) manages              (11) manages             (12) processes
              │                         │                         │
              ▼                         ▼                         ▼
    ┌──────────────────┐      ┌─────────────────────┐      ┌──────────────┐
    │    Customer      │      │      Account        │      │  Transaction │
    │                  │      │     (abstract)      │      │              │
    │ -customerId      │      │                     │      │ -txId        │
    │ -name            │      │ -accountNo          │      │ -type ───────┼──┐
    │ -accounts ───────┼──┐   │ -balance            │      │ -amount      │  │
    │ -profile ────────┼─┐│   │ -owner ◄────────────┼──┐   │ -status      │  │
    │               (13)│ ││   │ -txHistory ─────────┼─┐│   │           (18)│  │
    └──────────────────┘ ││   └─────────┬───────────┘ ││   └──────────────┘  │
            ▲            ││             │            ││                      │
            │            ││             │(14)        ││                      │
            │ (15)       ││             │ Inheritance││                      │
            │ 1:1        ││     ┌───────┴───────┐    ││                      │
            │            ││     │               │    ││                      │
            │            ││     ▼               ▼    ││                      │
            │            ││  ┌──────────┐  ┌──────────┐                     │
            │            ││  │ Savings  │  │ Checking │                     │
            │            ││  │ Account  │  │ Account  │                     │
            │            ││  │          │  │          │                     │
            │            ││  │-interest │  │-overdraft│                     │
            │            ││  │ Rate     │  │ Limit    │                     │
            │            ││  └──────────┘  └──────────┘                     │
            │            ││       ▲             ▲                            │
            │            ││       │             │                            │
            │            ││       └──────┬──────┘                            │
            │            ││              │                                   │
            │            ││              │ (19) operates on                  │
            │            ││              │      (polymorphic)                │
            │            ││              │                                   │
            │            ││    ┌─────────┴──────────┐                       │
            │            ││    │ TransactionProc    │ (from above)          │
            │            ││    └────────────────────┘                       │
            │            ││                                                  │
            │            │└──────────┐                                       │
            │            │      (16) │ 1:* (Customer owns many Accounts)    │
            │            │           │                                       │
            │            ▼           ▼                                       │
            │      [ Customer ] ──1:*──> [ Account ]                        │
            │                                  │                             │
            │                                  │ (17) 1:* (Account has      │
            │                                  │       many Transactions)    │
            │                                  ▼                             │
            │                          [ Transaction ]                       │
            │                                  │                             │
            │                                  │ uses                        │
            │                                  ▼                             │
            │                      ┌──────────────────────┐                 │
            │                      │  TransactionType     │                 │
            │                      │  <<enumeration>>     │                 │
            │                      │                      │                 │
            │                      │  • DEPOSIT           │                 │
            │                      │  • WITHDRAW          │                 │
            │                      │  • TRANSFER          │                 │
            ▼                      └──────────────────────┘                 │
    ┌──────────────────┐                                                    │
    │ CustomerProfile  │                                                    │
    │                  │                                                    │
    │ -profileId       │                                                    │
    │ -address         │                                                    │
    │ -phone           │                                                    │
    │ -email           │                                                    │
    │ -customer ◄──────┼────────────────────────────────────────────────────┘
    │               (20) 1:1 bidirectional (Profile belongs to Customer)
    └──────────────────┘


═══════════════════════════════════════════════════════════════════════════════════════

┌────────────────────────────────────────────────────────────────────────────────────────┐
│                         AUTHENTICATION & AUTHORIZATION LAYER                           │
└────────────────────────────────────────────────────────────────────────────────────────┘

    (21) manages
         │
         ▼
    ┌──────────────────┐
    │      User        │
    │   (abstract)     │
    │                  │
    │ -username        │
    │ -password        │
    │ -userRole ───────┼────────┐
    │ -pwdChangeReq    │        │ (22) uses
    └────────┬─────────┘        │
             │                  │
             │ (23) Inheritance │
       ┌─────┴─────┐            │
       │           │            │
       ▼           ▼            ▼
  ┌─────────┐  ┌────────────────────┐     ┌──────────────┐
  │  Admin  │  │   UserAccount      │     │  UserRole    │
  │         │  │                    │     │ <<enum>>     │
  │         │  │ -linkedCustomerId ─┼──┐  │              │
  └─────────┘  └────────────────────┘  │  │ • ADMIN      │
                                       │  │ • CUSTOMER   │
                                       │  └──────────────┘
                                       │
                                       │ (24) linked to
                                       │      (via customerId)
                                       │
                                       ▼
                              ┌──────────────────┐
                              │    Customer      │ (from above)
                              └──────────────────┘

                                       ▲
                                       │
                                       │ (25) can only access
                                       │      accounts where
                                       │      owner == this customer
                                       │
                              ┌────────┴────────┐
                              │   Access Check  │
                              │   in Auth Mgr   │
                              └─────────────────┘


═══════════════════════════════════════════════════════════════════════════════════════

┌────────────────────────────────────────────────────────────────────────────────────────┐
│                                  AUDIT TRAIL                                           │
└────────────────────────────────────────────────────────────────────────────────────────┘

    (26) maintains
         │
         ▼
    ┌──────────────────┐
    │    AuditLog      │
    │                  │
    │ -logId           │
    │ -username        │
    │ -action          │
    │ -timestamp       │
    └──────────────────┘

```

---

## 📋 Connection Reference Guide

### **🔗 Connection #1: Main → BankingSystem**
```java
// In Main.java
BankingSystem system = new BankingSystem(scanner);
system.start();
```
**What:** Main creates and starts the BankingSystem
**Type:** Dependency (uses)

---

### **🔗 Connections #2-5: BankingSystem → Managers (Composition)**

```java
// In BankingSystem.java
public class BankingSystem {
    private CustomerManager customerManager;      // (2) ◆ owns
    private AccountManager accountManager;        // (3) ◆ owns
    private TransactionProcessor transactionProcessor; // (4) ◆ owns
    private AuthenticationManager authManager;    // (5) ◆ owns

    public BankingSystem(Scanner scanner) {
        // Create all managers
        this.customerManager = new CustomerManager(scanner);
        this.accountManager = new AccountManager(scanner);
        this.transactionProcessor = new TransactionProcessor(scanner);
        this.authManager = new AuthenticationManager(scanner);
    }
}
```
**What:** BankingSystem OWNS all four managers
**Type:** Composition ◆ (strong ownership - if BankingSystem dies, managers die)

---

### **🔗 Connections #6-8: Shared Collections**

```java
// In BankingSystem.java (two-phase initialization)
public BankingSystem(Scanner scanner) {
    // ... create managers ...

    // Share collections with managers
    customerManager.setBankingSystem(this);    // (6) gives access to customers/accounts
    accountManager.setBankingSystem(this);     // (7) gives access to customers/accounts
    transactionProcessor.setBankingSystem(this); // (8) gives access to accounts
}

// In CustomerManager.java
public void setBankingSystem(BankingSystem bs) {
    this.bankingSystem = bs;
    this.allCustomers = bs.getCustomers();  // (6) points to same list
    this.allAccounts = bs.getAccounts();    // (6) points to same list
}
```
**What:** Managers share references to the same customer/account lists
**Type:** Dependency (uses shared data)

---

### **🔗 Connection #9: AuthenticationManager → AuditLog**

```java
// In AuthenticationManager.java
public class AuthenticationManager {
    private LinkedList<AuditLog> auditTrail;

    public void logAction(String action) {
        String logId = "LOG" + String.format("%03d", auditTrail.size() + 1);
        AuditLog log = new AuditLog(logId, currentUser.getUsername(), action);
        auditTrail.add(log);  // (9) maintains list of audit logs
    }
}
```
**What:** AuthenticationManager maintains a list of AuditLog objects
**Type:** Association 1:* (one manager has many logs)

---

### **🔗 Connection #10: CustomerManager → Customer**

```java
// In CustomerManager.java
public Customer createCustomer() {
    String customerId = generateCustomerId();
    String name = scanner.nextLine();
    Customer customer = new Customer(customerId, name);  // (10) creates
    allCustomers.add(customer);  // (10) manages
    return customer;
}

public Customer findCustomerById(String id) {
    for (Customer c : allCustomers) {  // (10) searches/manages
        if (c.getCustomerId().equals(id)) return c;
    }
    return null;
}
```
**What:** CustomerManager creates, reads, updates, deletes Customer objects
**Type:** Dependency (manages)

---

### **🔗 Connection #11: AccountManager → Account**

```java
// In AccountManager.java
public void createAccount() {
    Customer customer = // ... find customer ...
    String accountNo = generateAccountNo();

    Account account;
    if (type.equals("S")) {
        account = new SavingsAccount(accountNo, customer, interestRate);  // (11) creates
    } else {
        account = new CheckingAccount(accountNo, customer, overdraftLimit); // (11) creates
    }

    allAccounts.add(account);  // (11) manages
    customer.addAccount(account);
}
```
**What:** AccountManager creates and manages Account objects (polymorphic)
**Type:** Dependency (manages)

---

### **🔗 Connection #12: TransactionProcessor → Transaction**

```java
// In TransactionProcessor.java
public void transfer() {
    Account fromAccount = // ... find account ...
    Account toAccount = // ... find account ...

    if (fromAccount.withdraw(amount)) {  // (12) processes accounts
        toAccount.deposit(amount);

        String txId = generateTransactionId();
        Transaction tx = new Transaction(txId, TransactionType.TRANSFER, amount); // (12) creates
        tx.setFromAccountNo(fromAccount.getAccountNo());
        tx.setToAccountNo(toAccount.getAccountNo());
        tx.setStatus("COMPLETED");

        fromAccount.addTransaction(tx);  // (12) adds to accounts
        toAccount.addTransaction(tx);
    }
}
```
**What:** TransactionProcessor creates Transaction objects and adds them to accounts
**Type:** Dependency (creates and processes)

---

### **🔗 Connection #13: Customer → Account (1:*)**

```java
// In Customer.java
public class Customer {
    private LinkedList<Account> accounts;  // (13) has many accounts

    public void addAccount(Account a) {
        accounts.add(a);  // (13) owns the account
    }
}
```
**What:** One Customer owns many Accounts
**Type:** Association 1:* (one-to-many)

---

### **🔗 Connection #14: Account Inheritance**

```java
// Inheritance hierarchy
public abstract class Account {
    public abstract boolean withdraw(double amount);  // abstract method
}

public class SavingsAccount extends Account {  // (14) IS-A Account
    @Override
    public boolean withdraw(double amount) {
        // No overdraft allowed
        if (amount > balance) return false;
        balance -= amount;
        return true;
    }
}

public class CheckingAccount extends Account {  // (14) IS-A Account
    @Override
    public boolean withdraw(double amount) {
        // Overdraft allowed
        if (amount > balance + overdraftLimit) return false;
        balance -= amount;
        return true;
    }
}
```
**What:** SavingsAccount and CheckingAccount inherit from Account
**Type:** Inheritance ▲ (IS-A relationship)

---

### **🔗 Connection #15: Customer ↔ CustomerProfile (1:1 Bidirectional)**

```java
// In Customer.java
public class Customer {
    private CustomerProfile profile;  // (15) has one profile

    public void setProfile(CustomerProfile profile) {
        this.profile = profile;
        profile.setCustomer(this);  // (15) bidirectional - set back-reference
    }
}

// In CustomerProfile.java
public class CustomerProfile {
    private Customer customer;  // (15) belongs to one customer

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
```
**What:** Customer and CustomerProfile reference each other (bidirectional)
**Type:** Association 1:1 bidirectional ↔

---

### **🔗 Connection #16: Visualization of Customer → Account**

```
Customer "1" ──────> "*" Account

One Customer object can have:
• 0 accounts (just created)
• 1 account (has one savings)
• Many accounts (has savings + checking)
```

---

### **🔗 Connection #17: Account → Transaction (1:*)**

```java
// In Account.java
public abstract class Account {
    private LinkedList<Transaction> transactionHistory;  // (17) has many transactions

    public void addTransaction(Transaction t) {
        transactionHistory.add(t);  // (17) owns transactions
    }
}
```
**What:** One Account has many Transactions
**Type:** Association 1:* (one-to-many)

---

### **🔗 Connection #18: Transaction → TransactionType**

```java
// In Transaction.java
public class Transaction {
    private TransactionType type;  // (18) uses enum

    public Transaction(String txId, TransactionType type, double amount) {
        this.type = type;  // (18) DEPOSIT, WITHDRAW, or TRANSFER
    }
}
```
**What:** Transaction uses TransactionType enum
**Type:** Dependency (uses)

---

### **🔗 Connection #19: TransactionProcessor → Account (Polymorphic)**

```java
// In TransactionProcessor.java
public void withdraw() {
    Account account = findAccount(accountNo);  // Could be Savings or Checking

    // (19) Polymorphic call - runtime determines which withdraw() to use
    boolean success = account.withdraw(amount);

    // If account is SavingsAccount → calls SavingsAccount.withdraw()
    // If account is CheckingAccount → calls CheckingAccount.withdraw()
}
```
**What:** TransactionProcessor calls withdraw() polymorphically on any Account type
**Type:** Dependency (uses polymorphism)

---

### **🔗 Connection #20: CustomerProfile → Customer (Back-reference)**

Already explained in #15 - bidirectional relationship

---

### **🔗 Connection #21: AuthenticationManager → User**

```java
// In AuthenticationManager.java
public class AuthenticationManager {
    private LinkedList<User> users;  // (21) manages all users

    public User login() {
        String username = // ... get input ...
        String password = // ... get input ...

        for (User user : users) {  // (21) searches users
            if (user.getUsername().equals(username)) {
                if (user.authenticate(password)) {  // (21) uses user
                    return user;  // Could be Admin or UserAccount
                }
            }
        }
        return null;
    }
}
```
**What:** AuthenticationManager creates and manages User objects
**Type:** Dependency (manages)

---

### **🔗 Connection #22: User → UserRole**

```java
// In User.java
public abstract class User {
    private UserRole userRole;  // (22) uses enum

    public User(String username, String password, UserRole userRole, boolean pwdReq) {
        this.userRole = userRole;  // (22) ADMIN or CUSTOMER
    }
}
```
**What:** User has a UserRole (ADMIN or CUSTOMER)
**Type:** Dependency (uses enum)

---

### **🔗 Connection #23: User Inheritance**

```java
// Inheritance hierarchy
public abstract class User {
    public abstract LinkedList<String> getPermissions();  // abstract
}

public class Admin extends User {  // (23) IS-A User
    @Override
    public LinkedList<String> getPermissions() {
        // Return full permissions (all operations)
    }
}

public class UserAccount extends User {  // (23) IS-A User
    @Override
    public LinkedList<String> getPermissions() {
        // Return limited permissions (own accounts only)
    }
}
```
**What:** Admin and UserAccount inherit from User
**Type:** Inheritance ▲ (IS-A relationship)

---

### **🔗 Connection #24: UserAccount → Customer (via linkedCustomerId)**

```java
// In UserAccount.java
public class UserAccount extends User {
    private final String linkedCustomerId;  // (24) links to customer

    public UserAccount(String username, String password, String linkedCustomerId) {
        super(username, password, UserRole.CUSTOMER, true);
        this.linkedCustomerId = linkedCustomerId;  // (24) e.g., "C001"
    }
}
```
**What:** UserAccount has a linkedCustomerId field that references a Customer
**Type:** Dependency (weak reference via ID, not direct object reference)

---

### **🔗 Connection #25: Access Control Check**

```java
// In AuthenticationManager.java
public boolean canAccessAccount(Account account) {
    if (currentUser instanceof Admin) {
        return true;  // Admin can access all accounts
    } else if (currentUser instanceof UserAccount) {
        UserAccount ua = (UserAccount) currentUser;
        String linkedCustId = ua.getLinkedCustomerId();  // (24) get linked ID
        String ownerCustId = account.getOwner().getCustomerId();  // (25) get account owner
        return linkedCustId.equals(ownerCustId);  // (25) check if they match
    }
    return false;
}
```
**What:** AuthenticationManager enforces access control - UserAccount can only access accounts they own
**Type:** Business logic (access control rule)

---

### **🔗 Connection #26: AuthenticationManager → AuditLog**

Already explained in #9

---

## 🎯 Summary: How Everything Connects

### **Top-Down Flow:**
```
Main (1)──> BankingSystem
              │
              ├─(2)◆─> CustomerManager (10)──> Customer (13)──1:*──> Account
              │                                     ↕                   │
              │                              (15) 1:1              (17)1:*
              │                                     ↕                   │
              ├─(3)◆─> AccountManager (11)──> CustomerProfile    Transaction
              │                                                        │
              │                                                   (18) uses
              │                                                        ↓
              ├─(4)◆─> TransactionProc (12)──> Transaction ──> TransactionType
              │                  │                                  (enum)
              │                  │
              │                  └─(19) operates on (polymorphic)
              │                          │
              │                          ▼
              │                    Account (abstract)
              │                          │
              │                     (14) │ IS-A
              │                  ┌───────┴────────┐
              │                  │                │
              │                  ▼                ▼
              │            SavingsAccount  CheckingAccount
              │
              └─(5)◆─> AuthManager (21)──> User (abstract)
                           │                    │
                           │               (23) │ IS-A
                           │              ┌─────┴──────┐
                           │              │            │
                           │              ▼            ▼
                           │          Admin      UserAccount
                           │                          │
                           │                     (24) │ linked to
                           │                          ▼
                           │                      Customer
                           │
                           └─(9)─1:*──> AuditLog
```

Every number in the diagram corresponds to a connection explained above!

---

## 🔍 How to Use This

1. **Find a numbered connection** in the diagram (e.g., "(13)")
2. **Look up the connection** in the reference guide above
3. **See the Java code** showing exactly how it's implemented
4. **Understand the relationship type** (composition, association, inheritance, etc.)

Now you can see **exactly** which part connects to which part and **how**!
