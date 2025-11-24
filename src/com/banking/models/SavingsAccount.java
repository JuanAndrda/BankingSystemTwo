package com.banking.models;

import com.banking.utilities.ValidationPatterns;

// Savings account with interest - demonstrates POLYMORPHISM
public class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(String accountNo, Customer owner, double interestRate) {
        super(accountNo, owner);
        this.setInterestRate(interestRate);
    }

    public void applyInterest() {
        double interest = this.getBalance() * this.interestRate;
        this.setBalance(this.getBalance() + interest);
        System.out.println("✓ Interest applied: $" + String.format("%.2f", interest));
    }

    // Override - no overdraft allowed
    @Override
    public boolean withdraw(double amount) {
        if (!this.validateAmount(amount)) return false;

        if (amount > this.getBalance()) {
            System.out.println("✗ Insufficient funds. Available: $" + this.getBalance());
            return false;
        }

        this.setBalance(this.getBalance() - amount);
        System.out.println("✓ Withdrew $" + amount + " from " + this.getAccountNo());
        return true;
    }

    // Override - adds interest rate info
    @Override
    public String getDetails() {
        return "[SAVINGS] " + super.getDetails() + " | Interest: " + (this.getInterestRate() * 100) + "%";
    }

    public double getInterestRate() { return this.interestRate; }

    public void setInterestRate(double rate) {
        if (rate < 0 || rate > 1) {
            throw new IllegalArgumentException(ValidationPatterns.INTEREST_RATE_RANGE_ERROR);
        }
        this.interestRate = rate;
    }
}
