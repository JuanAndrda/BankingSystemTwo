package com.banking.models;

import com.banking.utilities.ValidationPatterns;

// Checking account with overdraft - demonstrates POLYMORPHISM
public class CheckingAccount extends Account {
    private double overdraftLimit;

    public CheckingAccount(String accountNo, Customer owner, double overdraftLimit) {
        super(accountNo, owner);
        this.setOverdraftLimit(overdraftLimit);
    }

    // Override - allows overdraft
    @Override
    public boolean withdraw(double amount) {
        if (!this.validateAmount(amount)) return false;

        if (amount > this.getBalance() + this.overdraftLimit) {
            System.out.println("✗ Exceeds overdraft limit. Available: $" +
                    (this.getBalance() + this.overdraftLimit));
            return false;
        }

        this.setBalance(this.getBalance() - amount);
        System.out.println("✓ Withdrew $" + amount + " from " + this.getAccountNo());
        return true;
    }

    // Override - adds overdraft limit info
    @Override
    public String getDetails() {
        return "[CHECKING] " + super.getDetails() + " | Overdraft: $" + this.getOverdraftLimit();
    }

    public double getOverdraftLimit() { return this.overdraftLimit; }

    public void setOverdraftLimit(double limit) {
        if (limit < 0) {
            throw new IllegalArgumentException(ValidationPatterns.OVERDRAFT_NEGATIVE_ERROR);
        }
        this.overdraftLimit = limit;
    }
}
