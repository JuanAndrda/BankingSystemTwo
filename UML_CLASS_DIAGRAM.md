# Banking System - UML Class Diagram

## Table of Contents
1. [Package Overview](#package-overview)
2. [Models Package](#models-package)
3. [Auth Package](#auth-package)
4. [Managers Package](#managers-package)
5. [Main System Package](#main-system-package)
6. [Relationships Summary](#relationships-summary)

---

## Package Overview

```
com.banking.models          - Domain models (Account, Customer, Transaction)
com.banking.auth            - Authentication & authorization (User, Admin, UserAccount)
com.banking.managers        - Business logic managers
com.banking                 - Main system (BankingSystem, Main)
com.banking.utilities       - Validation utilities
```

---

## Models Package

### 📦 `com.banking.models`

---

### Account (Abstract Class)

```
┌─────────────────────────────────────────┐
│          <<abstract>>                   │
│             Account                     │
├─────────────────────────────────────────┤
│ - accountNo: String                     │
│ - balance: double                       │
│ - owner: Customer                       │
│ - transactionHistory: LinkedList<Tx>    │
├─────────────────────────────────────────┤
│ + Account(accountNo, owner)             │
│ + deposit(amount: double): void         │
│ + withdraw(amount: double): boolean     │  ⚠️ ABSTRACT
│ + getDetails(): String                  │
│ # validateAmount(amount): boolean       │
│ + addTransaction(t: Transaction): void  │
│ + getAccountNo(): String                │
│ + getBalance(): double                  │
│ + getOwner(): Customer                  │
└─────────────────────────────────────────┘
              ▲
              │
      ┌───────┴────────┐
      │                │
```

| Attribute | Type | Visibility | Description |
|-----------|------|------------|-------------|
| accountNo | String | private | Account number (format: ACC###) |
| balance | double | private | Current account balance |
| owner | Customer | private | Reference to account owner |
| transactionHistory | LinkedList\<Transaction\> | private | List of all transactions |

| Method | Return Type | Visibility | Description |
|--------|-------------|------------|-------------|
| deposit(amount) | void | public | Deposits money into account |
| withdraw(amount) | boolean | public | **Abstract** - Subclasses implement withdrawal logic |
| getDetails() | String | public | Returns formatted account details |
| validateAmount(amount) | boolean | protected | Validates amount is positive |

---

### SavingsAccount (Concrete Class)

```
┌─────────────────────────────────────────┐
│         SavingsAccount                  │
│         extends Account                 │
├─────────────────────────────────────────┤
│ - interestRate: double                  │
├─────────────────────────────────────────┤
│ + SavingsAccount(accountNo, owner, rate)│
│ + withdraw(amount: double): boolean     │  ✅ Implemented (no overdraft)
│ + applyInterest(): void                 │
│ + getDetails(): String                  │
│ + getInterestRate(): double             │
│ + setInterestRate(rate: double): void   │
└─────────────────────────────────────────┘
```

**Polymorphic Behavior:**
- ❌ **No overdraft allowed** - withdrawal fails if amount > balance
- ✅ **Interest application** - applies interest based on interestRate

| Attribute | Type | Description |
|-----------|------|-------------|
| interestRate | double | Interest rate (0.0 to 1.0) |

---

### CheckingAccount (Concrete Class)

```
┌─────────────────────────────────────────┐
│        CheckingAccount                  │
│         extends Account                 │
├─────────────────────────────────────────┤
│ - overdraftLimit: double                │
├─────────────────────────────────────────┤
│ + CheckingAccount(accountNo, owner, od) │
│ + withdraw(amount: double): boolean     │  ✅ Implemented (with overdraft)
│ + getDetails(): String                  │
│ + getOverdraftLimit(): double           │
│ + setOverdraftLimit(limit: double): void│
└─────────────────────────────────────────┘
```

**Polymorphic Behavior:**
- ✅ **Overdraft allowed** - can withdraw up to (balance + overdraftLimit)

| Attribute | Type | Description |
|-----------|------|-------------|
| overdraftLimit | double | Maximum overdraft amount allowed |

---

### Customer

```
┌─────────────────────────────────────────┐
│            Customer                     │
├─────────────────────────────────────────┤
│ - customerId: String                    │
│ - name: String                          │
│ - accounts: LinkedList<Account>         │
│ - profile: CustomerProfile              │
├─────────────────────────────────────────┤
│ + Customer(customerId, name)            │
│ + addAccount(a: Account): void          │
│ + removeAccount(accountNo): boolean     │
│ + getCustomerId(): String               │
│ + getName(): String                     │
│ + getAccounts(): LinkedList<Account>    │
│ + getProfile(): CustomerProfile         │
│ + setProfile(profile): void             │
└─────────────────────────────────────────┘
```

| Attribute | Type | Multiplicity | Description |
|-----------|------|--------------|-------------|
| customerId | String | 1 | Unique ID (format: C###) |
| name | String | 1 | Customer full name |
| accounts | LinkedList\<Account\> | 0..* | List of customer's accounts |
| profile | CustomerProfile | 0..1 | Customer profile (1-to-1) |

**Relationships:**
- **1 → *** with Account (one customer owns many accounts)
- **1 ↔ 1** with CustomerProfile (bidirectional one-to-one)

---

### CustomerProfile

```
┌─────────────────────────────────────────┐
│        CustomerProfile                  │
├─────────────────────────────────────────┤
│ - profileId: String                     │
│ - address: String                       │
│ - phone: String                         │
│ - email: String                         │
│ - customer: Customer                    │
├─────────────────────────────────────────┤
│ + CustomerProfile(id, addr, phone, mail)│
│ + getProfileId(): String                │
│ + getAddress(): String                  │
│ + getPhone(): String                    │
│ + getEmail(): String                    │
│ + getCustomer(): Customer               │
│ + setCustomer(customer): void           │
└─────────────────────────────────────────┘
```

| Attribute | Type | Description |
|-----------|------|-------------|
| profileId | String | Profile ID (format: P###) |
| address | String | Mailing address |
| phone | String | Phone number (min 10 digits) |
| email | String | Email address (validated) |
| customer | Customer | Back-reference to Customer |

**Relationship:**
- **1 ↔ 1** with Customer (bidirectional one-to-one)

---

### Transaction

```
┌─────────────────────────────────────────┐
│          Transaction                    │
├─────────────────────────────────────────┤
│ - txId: String                          │
│ - fromAccountNo: String                 │
│ - toAccountNo: String                   │
│ - type: TransactionType                 │
│ - amount: double                        │
│ - timestamp: LocalDateTime              │
│ - status: String                        │
├─────────────────────────────────────────┤
│ + Transaction(txId, type, amount)       │
│ + getTxId(): String                     │
│ + getFromAccountNo(): String            │
│ + getToAccountNo(): String              │
│ + getType(): TransactionType            │
│ + getAmount(): double                   │
│ + getTimestamp(): LocalDateTime         │
│ + getStatus(): String                   │
│ + setStatus(status: String): void       │
└─────────────────────────────────────────┘
```

| Attribute | Type | Description |
|-----------|------|-------------|
| txId | String | Unique transaction ID (TX###) |
| fromAccountNo | String | Source account (for transfers) |
| toAccountNo | String | Destination account (for transfers) |
| type | TransactionType | DEPOSIT, WITHDRAW, or TRANSFER |
| amount | double | Transaction amount |
| timestamp | LocalDateTime | When transaction occurred |
| status | String | PENDING, COMPLETED, FAILED |

**Relationships:**
- **Many → 1** with Account (many transactions belong to one account)
- **→** TransactionType (uses enum)

---

### TransactionType (Enum)

```
┌─────────────────────────────────────────┐
│      <<enumeration>>                    │
│       TransactionType                   │
├─────────────────────────────────────────┤
│ DEPOSIT                                 │
│ WITHDRAW                                │
│ TRANSFER                                │
└─────────────────────────────────────────┘
```

| Value | Description |
|-------|-------------|
| DEPOSIT | Money deposited into account |
| WITHDRAW | Money withdrawn from account |
| TRANSFER | Money transferred between accounts |

---

## Auth Package

### 📦 `com.banking.auth`

---

### User (Abstract Class)

```
┌─────────────────────────────────────────┐
│          <<abstract>>                   │
│              User                       │
├─────────────────────────────────────────┤
│ - username: String                      │
│ - password: String                      │
│ - userRole: UserRole                    │
│ - passwordChangeRequired: boolean       │
├─────────────────────────────────────────┤
│ + User(username, pwd, role, pwdReq)     │
│ + getPermissions(): LinkedList<String>  │  ⚠️ ABSTRACT
│ + authenticate(pwd: String): boolean    │
│ + hasPermission(permission): boolean    │
│ + getUsername(): String                 │
│ + getUserRole(): UserRole               │
│ + isPasswordChangeRequired(): boolean   │
└─────────────────────────────────────────┘
              ▲
              │
      ┌───────┴────────┐
      │                │
```

| Attribute | Type | Description |
|-----------|------|-------------|
| username | String | User login name (immutable) |
| password | String | Encrypted password (immutable) |
| userRole | UserRole | ADMIN or CUSTOMER |
| passwordChangeRequired | boolean | Force password change on next login |

**Key Methods:**
- `authenticate(providedPassword)` - Verifies password matches
- `hasPermission(permission)` - Checks if user has specific permission
- `getPermissions()` - **Abstract** - Subclasses define their permissions

---

### Admin (Concrete Class)

```
┌─────────────────────────────────────────┐
│             Admin                       │
│         extends User                    │
├─────────────────────────────────────────┤
│ (no additional attributes)              │
├─────────────────────────────────────────┤
│ + Admin(username, password)             │
│ + getPermissions(): LinkedList<String>  │  ✅ Implemented (full access)
└─────────────────────────────────────────┘
```

**Permissions (Full System Access):**
- ✅ Session: LOGOUT, EXIT_APP
- ✅ Customers: CREATE_CUSTOMER, VIEW_CUSTOMER, VIEW_ALL_CUSTOMERS, DELETE_CUSTOMER
- ✅ Profiles: CREATE_PROFILE, UPDATE_PROFILE
- ✅ Accounts: CREATE_ACCOUNT, VIEW_ACCOUNT_DETAILS, VIEW_ALL_ACCOUNTS, DELETE_ACCOUNT, UPDATE_OVERDRAFT
- ✅ Transactions: VIEW_TRANSACTION_HISTORY
- ✅ Operations: APPLY_INTEREST, SORT_BY_NAME, SORT_BY_BALANCE
- ✅ Audit: VIEW_AUDIT_TRAIL

---

### UserAccount (Concrete Class)

```
┌─────────────────────────────────────────┐
│          UserAccount                    │
│         extends User                    │
├─────────────────────────────────────────┤
│ - linkedCustomerId: String              │
├─────────────────────────────────────────┤
│ + UserAccount(username, pwd, custId)    │
│ + getPermissions(): LinkedList<String>  │  ✅ Implemented (limited access)
│ + getLinkedCustomerId(): String         │
└─────────────────────────────────────────┘
```

| Attribute | Type | Description |
|-----------|------|-------------|
| linkedCustomerId | String | Links to Customer entity (enforces access control) |

**Permissions (Limited Access - Own Accounts Only):**
- ✅ Session: LOGOUT, EXIT_APP
- ✅ Accounts: VIEW_ACCOUNT_DETAILS (own accounts only)
- ✅ Transactions: DEPOSIT, WITHDRAW, TRANSFER, VIEW_TRANSACTION_HISTORY
- ✅ Security: CHANGE_PASSWORD

**Access Control:**
- Can only access accounts where `account.owner.customerId == this.linkedCustomerId`

**Relationship:**
- **→** Customer (via linkedCustomerId field)

---

### UserRole (Enum)

```
┌─────────────────────────────────────────┐
│      <<enumeration>>                    │
│          UserRole                       │
├─────────────────────────────────────────┤
│ ADMIN                                   │
│ CUSTOMER                                │
├─────────────────────────────────────────┤
│ + getDisplayName(): String              │
└─────────────────────────────────────────┘
```

| Value | Description |
|-------|-------------|
| ADMIN | Administrator with full system access |
| CUSTOMER | Customer user with limited access |

---

### AuditLog

```
┌─────────────────────────────────────────┐
│           AuditLog                      │
├─────────────────────────────────────────┤
│ - logId: String                         │
│ - username: String                      │
│ - action: String                        │
│ - timestamp: LocalDateTime              │
├─────────────────────────────────────────┤
│ + AuditLog(logId, username, action)     │
│ + getLogId(): String                    │
│ + getUsername(): String                 │
│ + getAction(): String                   │
│ + getTimestamp(): LocalDateTime         │
│ + toString(): String                    │
└─────────────────────────────────────────┘
```

| Attribute | Type | Description |
|-----------|------|-------------|
| logId | String | Unique log entry ID |
| username | String | User who performed action |
| action | String | Description of action performed |
| timestamp | LocalDateTime | When action occurred |

**Relationship:**
- **Many → 1** with AuthenticationManager

---

## Managers Package

### 📦 `com.banking.managers`

---

### CustomerManager

```
┌─────────────────────────────────────────┐
│        CustomerManager                  │
├─────────────────────────────────────────┤
│ - allCustomers: LinkedList<Customer>    │
│ - allAccounts: LinkedList<Account>      │
│ - bankingSystem: BankingSystem          │
│ - scanner: Scanner                      │
├─────────────────────────────────────────┤
│ + CustomerManager(scanner: Scanner)     │
│ + createCustomer(): Customer            │
│ + viewCustomer(): void                  │
│ + viewAllCustomers(): void              │
│ + deleteCustomer(): void                │
│ + generateCustomerId(): String          │
│ + findCustomerById(id: String): Customer│
│ + setBankingSystem(bs: BankingSystem)   │
└─────────────────────────────────────────┘
```

**Responsibilities:**
- ✅ Customer CRUD operations
- ✅ Auto-generate Customer IDs (C001, C002, ...)
- ✅ Customer search and validation
- ✅ Orchestrate customer onboarding workflow

**Key Operations:**
- `createCustomer()` - Interactive workflow: create customer → profile → account
- `findCustomerById(id)` - Search for customer by ID
- `generateCustomerId()` - Auto-increment ID generation

---

### AccountManager

```
┌─────────────────────────────────────────┐
│         AccountManager                  │
├─────────────────────────────────────────┤
│ - allAccounts: LinkedList<Account>      │
│ - allCustomers: LinkedList<Customer>    │
│ - bankingSystem: BankingSystem          │
│ - scanner: Scanner                      │
├─────────────────────────────────────────┤
│ + AccountManager(scanner: Scanner)      │
│ + createAccount(): void                 │
│ + viewAccountDetails(): void            │
│ + viewAllAccounts(): void               │
│ + deleteAccount(): void                 │
│ + applyInterest(): void                 │
│ + updateOverdraft(): void               │
│ + sortAccountsByCustomerName(): void    │
│ + sortAccountsByBalance(): void         │
│ + findAccountByNo(no: String): Account  │
│ + setBankingSystem(bs: BankingSystem)   │
└─────────────────────────────────────────┘
```

**Responsibilities:**
- ✅ Account CRUD operations
- ✅ Polymorphic account creation (Savings vs Checking)
- ✅ Apply interest to savings accounts
- ✅ Update overdraft limits for checking accounts
- ✅ Sorting algorithms (Insertion Sort)

**Key Operations:**
- `createAccount()` - Create Savings or Checking account for customer
- `sortAccountsByCustomerName()` - Sort using Insertion Sort (ascending)
- `sortAccountsByBalance()` - Sort using Insertion Sort (descending)
- `applyInterest()` - Apply interest to all savings accounts

---

### TransactionProcessor

```
┌─────────────────────────────────────────┐
│      TransactionProcessor               │
├─────────────────────────────────────────┤
│ - allAccounts: LinkedList<Account>      │
│ - bankingSystem: BankingSystem          │
│ - scanner: Scanner                      │
├─────────────────────────────────────────┤
│ + TransactionProcessor(scanner: Scanner)│
│ + deposit(): void                       │
│ + withdraw(): void                      │
│ + transfer(): void                      │
│ + viewTransactionHistory(): void        │
│ + generateTransactionId(): String       │
│ + setBankingSystem(bs: BankingSystem)   │
└─────────────────────────────────────────┘
```

**Responsibilities:**
- ✅ Process deposits (add funds to account)
- ✅ Process withdrawals (polymorphic - different for Savings vs Checking)
- ✅ Process transfers (two-phase commit for atomicity)
- ✅ Display transaction history (LIFO using Stack)

**Key Operations:**
- `transfer()` - **Two-phase commit**: withdraw from source → deposit to destination → create Transaction records
- `viewTransactionHistory()` - Display transactions in LIFO order (most recent first) using Stack
- `generateTransactionId()` - Auto-generate TX IDs (TX001, TX002, ...)

**Data Structures Used:**
- **LinkedList** - Store transaction history in Account
- **Stack** - Display transactions in LIFO order (newest first)

---

### AuthenticationManager

```
┌─────────────────────────────────────────┐
│     AuthenticationManager               │
├─────────────────────────────────────────┤
│ - users: LinkedList<User>               │
│ - auditTrail: LinkedList<AuditLog>      │
│ - currentUser: User                     │
│ - scanner: Scanner                      │
├─────────────────────────────────────────┤
│ + AuthenticationManager(scanner)        │
│ + login(): User                         │
│ + logout(): void                        │
│ + changePassword(): void                │
│ + viewAuditTrail(): void                │
│ + logAction(action: String): void       │
│ + canAccessAccount(acc: Account): bool  │
│ + getCurrentUser(): User                │
│ + initializeUsers(): void               │
└─────────────────────────────────────────┘
```

**Responsibilities:**
- ✅ User authentication (login/logout)
- ✅ Password management
- ✅ Audit logging (all system actions)
- ✅ Role-based access control (RBAC)
- ✅ Account access validation

**Key Operations:**
- `login()` - Authenticate user, return User object (Admin or UserAccount)
- `canAccessAccount(account)` - Verify user has permission to access account
- `logAction(action)` - Record action in audit trail with timestamp
- `changePassword()` - Create new User object with new password (immutability)

**Access Control Logic:**
```
if (currentUser is Admin):
    → Allow access to ALL accounts
else if (currentUser is UserAccount):
    → Allow access ONLY if account.owner.customerId == userAccount.linkedCustomerId
else:
    → Deny access
```

---

## Main System Package

### 📦 `com.banking`

---

### BankingSystem

```
┌─────────────────────────────────────────┐
│         BankingSystem                   │
├─────────────────────────────────────────┤
│ - customers: LinkedList<Customer>       │
│ - accounts: LinkedList<Account>         │
│ - customerManager: CustomerManager      │
│ - accountManager: AccountManager        │
│ - transactionProcessor: TransactionProc │
│ - authManager: AuthenticationManager    │
│ - scanner: Scanner                      │
├─────────────────────────────────────────┤
│ + BankingSystem(scanner: Scanner)       │
│ + start(): void                         │
│ + displayMenu(): void                   │
│ - initializeTestData(): void            │
│ + getCustomers(): LinkedList<Customer>  │
│ + getAccounts(): LinkedList<Account>    │
└─────────────────────────────────────────┘
```

**Responsibilities:**
- ✅ Main system orchestrator
- ✅ Coordinates all managers
- ✅ Displays menu and routes user actions
- ✅ Initializes demo/test data
- ✅ Manages application lifecycle

**Composition Relationships:**
- **◆→** CustomerManager (BankingSystem owns/contains)
- **◆→** AccountManager
- **◆→** TransactionProcessor
- **◆→** AuthenticationManager

**Design Patterns:**
- **Dependency Injection** - Scanner injected into constructor
- **Two-Phase Initialization** - Resolves circular dependency between BankingSystem and managers
- **Composition over Inheritance** - HAS managers, not IS-A manager

---

### Main

```
┌─────────────────────────────────────────┐
│             Main                        │
├─────────────────────────────────────────┤
│ (no attributes)                         │
├─────────────────────────────────────────┤
│ + static main(args: String[]): void     │
└─────────────────────────────────────────┘
```

**Responsibilities:**
- ✅ Application entry point
- ✅ Creates Scanner
- ✅ Creates BankingSystem
- ✅ Starts the application

---

### MenuAction (Enum)

```
┌─────────────────────────────────────────┐
│      <<enumeration>>                    │
│         MenuAction                      │
├─────────────────────────────────────────┤
│ LOGIN                                   │
│ LOGOUT                                  │
│ CREATE_CUSTOMER                         │
│ VIEW_CUSTOMER                           │
│ VIEW_ALL_CUSTOMERS                      │
│ DELETE_CUSTOMER                         │
│ CREATE_ACCOUNT                          │
│ VIEW_ACCOUNT_DETAILS                    │
│ ... (and more)                          │
├─────────────────────────────────────────┤
│ + getLabel(): String                    │
└─────────────────────────────────────────┘
```

**Purpose:** Type-safe enumeration of all menu options

---

## Relationships Summary

### 🔗 Inheritance Relationships (IS-A)

```
Account (abstract)
    ├── SavingsAccount
    └── CheckingAccount

User (abstract)
    ├── Admin
    └── UserAccount
```

---

### 🔗 Association Relationships

| From | Relationship | To | Multiplicity | Description |
|------|-------------|-----|--------------|-------------|
| Customer | owns → | Account | 1 → * | One customer owns many accounts |
| Customer | has ↔ | CustomerProfile | 1 ↔ 1 | Bidirectional one-to-one |
| Account | contains → | Transaction | 1 → * | One account has many transactions |
| Transaction | uses → | TransactionType | * → 1 | Transaction uses enum type |
| User | has → | UserRole | * → 1 | User has a role (ADMIN/CUSTOMER) |
| UserAccount | linked to → | Customer | 1 → 1 | UserAccount linked to one Customer |
| AuthManager | maintains → | AuditLog | 1 → * | AuthManager maintains audit logs |

---

### 🔗 Composition Relationships (Strong Ownership - ◆)

```
BankingSystem
    ◆──→ CustomerManager
    ◆──→ AccountManager
    ◆──→ TransactionProcessor
    ◆──→ AuthenticationManager
```

**Meaning:** BankingSystem **owns** these managers. If BankingSystem is destroyed, managers are destroyed too.

---

### 🔗 Dependency Relationships (Uses - - - →)

| From | Depends On | How |
|------|-----------|-----|
| CustomerManager | Customer | Creates and manages Customer objects |
| CustomerManager | Account | Accesses customer accounts |
| AccountManager | Account | Creates and manages Account objects |
| AccountManager | Customer | Links accounts to customers |
| TransactionProcessor | Account | Processes transactions on accounts |
| TransactionProcessor | Transaction | Creates Transaction records |
| AuthenticationManager | User | Manages user authentication |
| Main | BankingSystem | Creates and starts BankingSystem |

---

## Visual Relationship Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                                                                  │
│  Main ────creates───→ BankingSystem                            │
│                            │                                     │
│                            │ (Composition - owns)                │
│                            ◆                                     │
│           ┌────────────────┼────────────────┐                   │
│           │                │                │                   │
│           ▼                ▼                ▼                   │
│    CustomerManager  AccountManager  TransactionProcessor        │
│           │                │                │                   │
│           │ manages        │ manages        │ processes         │
│           ▼                ▼                ▼                   │
│       Customer ──1:*──→ Account ──1:*──→ Transaction           │
│           │                │                │                   │
│           │ 1:1            │                │                   │
│           ↕                │                │                   │
│    CustomerProfile         │                │                   │
│                            │                │                   │
│                            │                ▼                   │
│                            │         TransactionType            │
│                            │           (enum)                   │
│                            │                                     │
│                            ▼                                     │
│              ┌─────────Account (abstract)                       │
│              │                                                   │
│         ┌────┴────┐                                             │
│         │         │                                             │
│         ▼         ▼                                             │
│   SavingsAccount  CheckingAccount                              │
│                                                                  │
│                                                                  │
│   AuthenticationManager                                         │
│         │        │                                              │
│         │        │ maintains                                    │
│         │        └────1:*───→ AuditLog                         │
│         │                                                        │
│         │ manages                                               │
│         ▼                                                        │
│   User (abstract) ───→ UserRole (enum)                         │
│         │                                                        │
│    ┌────┴────┐                                                  │
│    │         │                                                  │
│    ▼         ▼                                                  │
│  Admin   UserAccount ───linked to───→ Customer                 │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## Key Design Patterns

### 1. **Abstraction & Polymorphism**
- `Account` is abstract with `withdraw()` method
- `SavingsAccount` and `CheckingAccount` implement different withdrawal logic
- `User` is abstract with `getPermissions()` method
- `Admin` and `UserAccount` return different permission sets

### 2. **Composition over Inheritance**
- `BankingSystem` **HAS** managers (composition)
- Not: `BankingSystem extends Manager` (inheritance)

### 3. **Dependency Injection**
- `Scanner` is injected into `BankingSystem`
- Shared collections passed to managers
- Reduces tight coupling

### 4. **Two-Phase Initialization**
- BankingSystem creates managers first
- Then calls `setBankingSystem()` on each manager
- Resolves circular dependency

### 5. **Strategy Pattern**
- Different `withdraw()` implementations per account type
- Called polymorphically through base class reference

### 6. **Role-Based Access Control (RBAC)**
- `User.hasPermission()` checks permissions
- `AuthenticationManager.canAccessAccount()` enforces access rules

---

## Data Structures Used

| Data Structure | Usage | Location |
|----------------|-------|----------|
| **LinkedList\<Customer\>** | Store all customers | BankingSystem, CustomerManager |
| **LinkedList\<Account\>** | Store all accounts | BankingSystem, AccountManager, Customer |
| **LinkedList\<Transaction\>** | Store transaction history | Account |
| **LinkedList\<User\>** | Store all users | AuthenticationManager |
| **LinkedList\<AuditLog\>** | Store audit trail | AuthenticationManager |
| **Stack\<Transaction\>** | Display transactions LIFO (newest first) | TransactionProcessor |
| **Insertion Sort** | Sort accounts by name/balance | AccountManager |

---

## Multiplicity Guide

| Notation | Meaning |
|----------|---------|
| 1 | Exactly one |
| 0..1 | Zero or one (optional) |
| * | Zero or many |
| 1..* | One or many (at least one) |
| n | Exactly n |

---

## Method Visibility Guide

| Symbol | Visibility | Description |
|--------|-----------|-------------|
| + | public | Accessible from anywhere |
| - | private | Accessible only within class |
| # | protected | Accessible to class and subclasses |
| ~ | package | Accessible within package |

---

## End of UML Class Diagram

**Generated for:** Banking System Project
**Package:** com.banking
**Language:** Java 11+
**Total Classes:** 22
**Total Lines:** ~4,058
