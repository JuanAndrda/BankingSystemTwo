# Exception Handling Notes

## getMessage() Method

### What is it?
- `getMessage()` is a **built-in Java method** from the `Throwable` class
- You did NOT write it - it's part of Java itself
- All exceptions inherit this method

### How does it work?

#### Step 1: Create exception with a message
```java
throw new IllegalArgumentException("Amount must be positive");
//                                  ↑
//                    This message gets STORED inside the exception object
```

#### Step 2: Exception object saves the message
```java
class IllegalArgumentException {
    private String message;  // Stored here!

    public IllegalArgumentException(String message) {
        this.message = message;  // Saves YOUR message
    }

    public String getMessage() {
        return this.message;  // Returns the saved message
    }
}
```

#### Step 3: Retrieve the message in catch block
```java
catch (IllegalArgumentException e) {
    System.out.println("✗ Error processing transfer: " + e.getMessage());
    //                                                      ↑
    //                                    Returns "Amount must be positive"
}
```

### Example from your code:
```java
// In Transaction constructor
if (amount <= 0) {
    throw new IllegalArgumentException(ValidationPatterns.AMOUNT_POSITIVE_ERROR);
    // Stores "Amount must be positive"
}

// In TransactionProcessor
catch (IllegalArgumentException e) {
    System.out.println("✗ Error processing withdrawal: " + e.getMessage());
    // Prints: "✗ Error processing withdrawal: Amount must be positive"
}
```

### Message examples from ValidationPatterns:
- `"Amount must be positive"`
- `"Invalid account number format: ACC99"`
- `"Transaction ID cannot be empty"`
- `"Transaction type cannot be null"`

---

## When to Use throw vs if-else

### Use `throw new IllegalArgumentException` when:

#### 1. In Constructors (MOST IMPORTANT!)
**Why:** You can't return `null` from a constructor - object must be created properly or not at all.

```java
public Account(String accountNo, Customer owner) {
    if (accountNo == null || !accountNo.matches(PATTERN)) {
        throw new IllegalArgumentException("Invalid account number");
        // Constructor STOPS, object is NOT created
    }
    this.accountNo = accountNo;
}
```

**Bad example (if-else in constructor):**
```java
public Account(String accountNo, Customer owner) {
    if (accountNo == null) {
        System.out.println("Error!");
        // Constructor continues anyway!
        // Object gets created with null accountNo = BAD!
    }
    this.accountNo = accountNo;  // Sets null!
}
```

#### 2. In Setters (Validation)
**Why:** Invalid data should NEVER be saved to the object.

```java
public void setFromAccountNo(String accountNo) {
    if (!accountNo.matches(ACCOUNT_NO_PATTERN)) {
        throw new IllegalArgumentException("Invalid account format");
        // Method STOPS, field is NOT changed
    }
    this.fromAccountNo = accountNo;
}
```

#### 3. When Caller Should Handle the Error
**Why:** Let the calling code decide how to handle the problem.

```java
// In Transaction class
public Transaction(String txId, TransactionType type, double amount) {
    if (amount <= 0) {
        throw new IllegalArgumentException("Amount must be positive");
        // Let the CALLER handle this!
    }
}

// In TransactionProcessor (the caller)
try {
    Transaction tx = new Transaction("TX001", DEPOSIT, -50);
} catch (IllegalArgumentException e) {
    System.out.println("✗ Error: " + e.getMessage());
    // NOW we handle it with user-friendly message
}
```

---

### Use normal if-else when:

#### 1. User Input Validation (Before creating objects)
```java
public void handleDeposit() {
    Double amount = validator.getValidatedAmount("Amount");
    if (amount == null) {
        return;  // User cancelled, just exit
    }

    if (amount <= 0) {
        System.out.println("✗ Amount must be positive");
        return;  // Exit gracefully
    }

    deposit(accountNo, amount);
}
```

#### 2. Business Logic Checks
```java
public boolean withdraw(double amount) {
    if (balance < amount) {
        System.out.println("✗ Insufficient funds");
        return false;  // Not an error, just a business rule
    }
    balance -= amount;
    return true;
}
```

---

## Quick Reference Table

| Situation | Use | Reason |
|-----------|-----|--------|
| Constructor validation | `throw` | Can't return null, must prevent object creation |
| Setter validation | `throw` | Must prevent invalid data from being saved |
| Invalid data that breaks object | `throw` | Object would be in invalid state |
| User input validation | `if-else` | User-friendly, recoverable |
| Business logic (insufficient funds) | `if-else` | Not an error, just a rule |
| Optional/recoverable errors | `if-else` | Can handle gracefully |

---

## Does throw Stop Execution?

### YES! throw immediately stops execution

```java
public Account(String accountNo, Customer owner) {
    if (accountNo == null) {
        throw new IllegalArgumentException("Account number cannot be null");
        // STOPS HERE! Nothing below runs!
    }
    this.accountNo = accountNo;  // This line NEVER executes if accountNo is null
    this.owner = owner;          // This line NEVER executes either
}
```

**What happens:**
1. Code checks condition
2. If true, throws exception and **STOPS immediately**
3. Jumps to nearest `catch` block
4. Everything after `throw` is **skipped**

---

## Does throw Stop a Loop?

### YES! Exception exits the current iteration

```java
public void processMultipleDeposits() {
    for (int i = 0; i < 5; i++) {
        try {
            System.out.println("Processing deposit " + i);

            if (amounts[i] < 0) {
                throw new IllegalArgumentException("Negative amount!");
                // STOPS HERE for this iteration
            }

            System.out.println("Deposit " + i + " completed");  // Skipped if exception

        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error on deposit " + i + ": " + e.getMessage());
            // Loop CONTINUES to next iteration
        }
    }
}
```

**If amounts = [100, 200, -50, 300, 400]:**
```
Processing deposit 0
Deposit 0 completed
Processing deposit 1
Deposit 1 completed
Processing deposit 2
✗ Error on deposit 2: Negative amount!
Processing deposit 3
Deposit 3 completed
Processing deposit 4
Deposit 4 completed
```

---

## Execution Flow Example

```java
try {
    System.out.println("Step 1");          // Runs
    System.out.println("Step 2");          // Runs
    throw new IllegalArgumentException("Error!");  // STOPS HERE
    System.out.println("Step 3");          // NEVER RUNS
    System.out.println("Step 4");          // NEVER RUNS
} catch (IllegalArgumentException e) {
    System.out.println("Caught: " + e.getMessage());  // Runs this
}
System.out.println("Step 5");              // Continues here
```

**Output:**
```
Step 1
Step 2
Caught: Error!
Step 5
```

---

## Your Code's Validation Layers

### Layer 1 (if-else): User Input Validation
```java
// In InputValidator
if (amount <= 0) {
    System.out.println("✗ Amount must be positive");
    return null;  // User-friendly, recoverable
}
```

### Layer 2 (throw): Object Validation
```java
// In Transaction constructor
if (amount <= 0) {
    throw new IllegalArgumentException("Amount must be positive");
    // Prevents creating broken objects
}
```

**This is defensive programming** - validate early with if-else, but protect objects with exceptions!

---

## ValidationPatterns Class - Centralized Error Messages

### Why Centralize Error Messages?

Instead of writing error messages throughout the code, they're all defined in one place: `ValidationPatterns.java`

**Benefits:**
- ✅ Consistency - same error message everywhere
- ✅ Easy to update - change in one place, applies everywhere
- ✅ No typos - message is defined once
- ✅ Maintainability - easy to find all error messages

### The ValidationPatterns Class

**Location:** `src/com/banking/utilities/ValidationPatterns.java`

```java
public class ValidationPatterns {
    // ========== PATTERN CONSTANTS ==========
    public static final String ACCOUNT_NO_PATTERN = "^ACC\\d{3}$";
    public static final String CUSTOMER_ID_PATTERN = "^C\\d{3}$";
    public static final String PROFILE_ID_PATTERN = "^P\\d{3}$";

    // ========== ERROR MESSAGE CONSTANTS ==========
    // Account validation errors
    public static final String ACCOUNT_NO_ERROR =
        "Account number must follow format ACC### (e.g., ACC001)";
    public static final String ACCOUNT_NO_EMPTY_ERROR =
        "Account number cannot be empty";

    // Amount validation errors
    public static final String AMOUNT_POSITIVE_ERROR =
        "Amount must be positive";
    public static final String AMOUNT_ZERO_ERROR =
        "Amount cannot be zero";

    // Customer validation errors
    public static final String CUSTOMER_ID_ERROR =
        "Customer ID must follow format C### (e.g., C001)";
    public static final String CUSTOMER_NAME_EMPTY_ERROR =
        "Customer name cannot be empty";

    // Transaction validation errors
    public static final String TX_ID_EMPTY_ERROR =
        "Transaction ID cannot be empty";
    public static final String TX_TYPE_NULL_ERROR =
        "Transaction type cannot be null";

    // ========== VALIDATION METHODS ==========
    public static boolean matchesPattern(String value, String pattern) {
        return value != null && value.matches(pattern);
    }
}
```

### How It's Used Throughout the Code

#### Example 1: In Model Constructors
```java
// In Account.java
public void setAccountNo(String accountNo) {
    if (!ValidationPatterns.matchesPattern(accountNo,
            ValidationPatterns.ACCOUNT_NO_PATTERN)) {
        throw new IllegalArgumentException(ValidationPatterns.ACCOUNT_NO_ERROR);
        //                                  ↑
        //                    Message comes from ValidationPatterns!
    }
    this.accountNo = accountNo;
}
```

#### Example 2: In Transaction Constructor
```java
// In Transaction.java
public Transaction(String txId, TransactionType type, double amount) {
    if (txId == null || txId.trim().isEmpty()) {
        throw new IllegalArgumentException(ValidationPatterns.TX_ID_EMPTY_ERROR);
    }

    if (type == null) {
        throw new IllegalArgumentException(ValidationPatterns.TX_TYPE_NULL_ERROR);
    }

    if (amount <= 0) {
        throw new IllegalArgumentException(ValidationPatterns.AMOUNT_POSITIVE_ERROR);
    }

    // ... set fields
}
```

#### Example 3: In InputValidator
```java
// In InputValidator.java
public String getValidatedAccountNumber(String prompt, LinkedList<Account> accountList) {
    String accountNo = scanner.nextLine().trim();

    if (accountNo.isEmpty()) {
        System.out.println(ValidationPatterns.ACCOUNT_NO_EMPTY_ERROR);
        return null;
    }

    if (!ValidationPatterns.matchesPattern(accountNo,
            ValidationPatterns.ACCOUNT_NO_PATTERN)) {
        System.out.println(ValidationPatterns.ACCOUNT_NO_ERROR);
        return null;
    }

    return accountNo;
}
```

### Benefits in Action

**Scenario:** Boss wants to change error message format

**Without ValidationPatterns (BAD):**
```
Need to find and update in 20+ files:
- Account.java (2 places)
- Customer.java (3 places)
- Transaction.java (1 place)
- InputValidator.java (5 places)
- ... etc
```

**With ValidationPatterns (GOOD):**
```java
// Change in ONE place:
public static final String AMOUNT_POSITIVE_ERROR =
    "Error: The amount must be greater than zero";  // Updated!

// Automatically updates everywhere! 🎉
```

---

## Handler Method Try-Catch Pattern

### Complete User-Facing Flow with Exception Handling

Handler methods wrap core operations in try-catch blocks to provide user-friendly error messages and handle exceptions gracefully.

### Pattern Structure

```java
public void handleOperation() {
    // Step 1: Get validated input
    String input = validator.getValidatedInput(...);
    if (input == null) return;  // User cancelled or validation failed

    // Step 2: Wrap operation in try-catch
    try {
        // Call core method (might throw exception)
        coreOperation(input);

        // Log success
        bankingSystem.logAction("OPERATION", "Success message");

    } catch (IllegalArgumentException e) {
        // Handle exception with user-friendly message
        System.out.println("✗ Error: " + e.getMessage());
    }
}
```

### Example 1: handleDeposit() in TransactionProcessor

```java
public void handleDeposit() {
    System.out.println("\n--- DEPOSIT ---");

    // Validate account number
    String accountNo = this.validator.getValidatedAccountNumber(
            "Enter account number: ", this.accountList);
    if (accountNo == null) return;

    // Check access control
    if (!this.bankingSystem.canAccessAccount(accountNo)) {
        System.out.println("✗ Access denied.");
        return;
    }

    // Validate amount
    double amount = this.validator.getValidatedPositiveAmount(
            "Enter amount to deposit: $");
    if (amount == -1) return;

    // ========== TRY-CATCH WRAPPER ==========
    try {
        // Call core deposit method (creates Transaction, validates amount)
        if (this.deposit(accountNo, amount)) {
            this.bankingSystem.logAction("DEPOSIT",
                String.format("Deposited $%.2f to %s", amount, accountNo));
        }
    } catch (IllegalArgumentException e) {
        // Catch exceptions from Transaction constructor or Account methods
        System.out.println("✗ Error processing deposit: " + e.getMessage());
        // User sees: "✗ Error processing deposit: Amount must be positive"
    }
}
```

### Example 2: handleCreateCustomer() in CustomerManager

```java
public void handleCreateCustomer() {
    System.out.println("\n--- CREATE CUSTOMER ---");

    // Get customer name
    String name = validator.getValidatedNonEmptyString("Enter customer name: ");
    if (name == null) return;

    // ========== TRY-CATCH WRAPPER ==========
    try {
        // Generate customer ID
        String customerId = generateNextCustomerId();

        // Create customer (constructor validates name)
        Customer customer = new Customer(customerId, name);
        customers.add(customer);

        // Log action
        bankingSystem.logAction("CREATE_CUSTOMER",
            "Created customer " + customerId + " - " + name);

        System.out.println("✓ Customer created successfully!");

    } catch (IllegalArgumentException e) {
        // Catches validation errors from Customer constructor
        System.out.println("✗ Error creating customer: " + e.getMessage());
        // User sees: "✗ Error creating customer: Customer name cannot be empty"
    }
}
```

### Example 3: handleTransfer() - Multiple Validations

```java
public void handleTransfer() {
    System.out.println("\n--- TRANSFER ---");

    // Validate FROM account
    String fromAccountNo = validator.getValidatedAccountNumber(
            "From account: ", accountList);
    if (fromAccountNo == null) return;

    // Check access to FROM account
    if (!bankingSystem.canAccessAccount(fromAccountNo)) {
        System.out.println("✗ Access denied to source account");
        return;
    }

    // Validate TO account
    String toAccountNo = validator.getValidatedAccountNumber(
            "To account: ", accountList);
    if (toAccountNo == null) return;

    // Validate amount
    double amount = validator.getValidatedPositiveAmount(
            "Enter amount: $");
    if (amount == -1) return;

    // ========== TRY-CATCH WRAPPER ==========
    try {
        if (this.transfer(fromAccountNo, toAccountNo, amount)) {
            bankingSystem.logAction("TRANSFER",
                String.format("Transferred $%.2f from %s to %s",
                    amount, fromAccountNo, toAccountNo));
        }
    } catch (IllegalArgumentException e) {
        System.out.println("✗ Error processing transfer: " + e.getMessage());
    }
}
```

### Why This Pattern?

**Without try-catch wrapper:**
```java
public void handleDeposit() {
    String accountNo = validator.getValidatedAccountNumber(...);
    double amount = validator.getValidatedPositiveAmount(...);

    this.deposit(accountNo, amount);  // Exception crashes program! 💥
}
```

**With try-catch wrapper:**
```java
public void handleDeposit() {
    String accountNo = validator.getValidatedAccountNumber(...);
    double amount = validator.getValidatedPositiveAmount(...);

    try {
        this.deposit(accountNo, amount);  // Exception caught gracefully ✅
    } catch (IllegalArgumentException e) {
        System.out.println("✗ Error: " + e.getMessage());  // User-friendly!
    }
}
```

**Benefits:**
- ✅ Program doesn't crash on invalid input
- ✅ User sees helpful error messages
- ✅ Operations can be retried
- ✅ Audit logging still happens for valid operations
- ✅ Consistent error handling across all operations

---

## Key Takeaways

1. ✅ `getMessage()` is **built-in Java**, you provide the **message text**
2. ✅ Use `throw` in **constructors** and **setters** to prevent invalid objects
3. ✅ Use `if-else` for **user input** and **business logic**
4. ✅ `throw` **stops execution immediately** - like a super-powered `return`
5. ✅ `throw` **exits current iteration** in loops, but catch can let loop continue
6. ✅ Your code uses **both** - if-else for input, throw for object protection
