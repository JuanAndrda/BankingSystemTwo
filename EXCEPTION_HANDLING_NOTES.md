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

## Key Takeaways

1. ✅ `getMessage()` is **built-in Java**, you provide the **message text**
2. ✅ Use `throw` in **constructors** and **setters** to prevent invalid objects
3. ✅ Use `if-else` for **user input** and **business logic**
4. ✅ `throw` **stops execution immediately** - like a super-powered `return`
5. ✅ `throw` **exits current iteration** in loops, but catch can let loop continue
6. ✅ Your code uses **both** - if-else for input, throw for object protection
