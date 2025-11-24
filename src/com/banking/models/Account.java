package com.banking.models;

import java.util.LinkedList;
import com.banking.utilities.ValidationPatterns;

// Abstract base class - demonstrates ABSTRACTION and INHERITANCE
public abstract class Account {
    private String accountNo;
    private double balance;
    private Customer owner;
    private LinkedList<Transaction> transactionHistory;

    public Account(String accountNo, Customer owner) {
        this.setAccountNo(accountNo);
        this.setOwner(owner);
        this.balance = 0.0;
        this.transactionHistory = new LinkedList<>();
    }

    public void deposit(double amount) {
        if (this.validateAmount(amount)) {
            this.balance += amount;
            System.out.println("✓ Deposited $" + amount + " to " + this.accountNo);
        }
    }

    // Abstract method - subclasses implement specific withdrawal logic
    public abstract boolean withdraw(double amount);

    public String getDetails() {
        String ownerName = (this.owner != null) ? this.owner.getName() : "NO OWNER";
        return String.format("%s | Owner: %s | Balance: $%.2f",
                this.getAccountNo(), ownerName, this.getBalance());
    }

    protected boolean validateAmount(double amount) {
        if (amount <= 0) {
            System.out.println("✗ Invalid amount. Must be positive.");
            return false;
        }
        return true;
    }

    public void addTransaction(Transaction t) {
        this.transactionHistory.add(t);
    }

    // Getters
    public String getAccountNo() { return this.accountNo; }
    public double getBalance() { return this.balance; }
    public Customer getOwner() { return this.owner; }
    public LinkedList<Transaction> getTransactionHistory() { return this.transactionHistory; }

    // Setters with validation
    public void setAccountNo(String accountNo) {
        if (!ValidationPatterns.matchesPattern(accountNo, ValidationPatterns.ACCOUNT_NO_PATTERN)) {
            throw new IllegalArgumentException(ValidationPatterns.ACCOUNT_NO_ERROR);
        }
        this.accountNo = accountNo;
    }

    public void setOwner(Customer owner) {
        if (owner == null) {
            throw new IllegalArgumentException(ValidationPatterns.ACCOUNT_OWNER_NULL_ERROR);
        }
        this.owner = owner;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }
}
