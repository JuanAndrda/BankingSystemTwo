package com.banking.models;

import java.util.LinkedList;
import java.util.Iterator;
import com.banking.utilities.ValidationPatterns;

// Customer with accounts and profile - demonstrates 1-to-Many and 1-to-1 relationships
public class Customer {
    private String customerId;
    private String name;
    private LinkedList<Account> accounts;
    private CustomerProfile profile;

    public Customer(String customerId, String name) {
        this.setCustomerId(customerId);
        this.setName(name);
        this.accounts = new LinkedList<>();
    }

    public void addAccount(Account a) {
        if (a != null) {
            for (Account existing : this.accounts) {
                if (existing.getAccountNo().equals(a.getAccountNo())) {
                    System.out.println("✗ Account " + a.getAccountNo() + " already added to customer " + this.name);
                    return;
                }
            }
            this.accounts.add(a);
            System.out.println("✓ Account " + a.getAccountNo() + " added to customer " + this.name);
        }
    }

    public boolean removeAccount(String accountNo) {
        Iterator<Account> iterator = this.accounts.iterator();
        while (iterator.hasNext()) {
            Account acc = iterator.next();
            if (acc.getAccountNo().equals(accountNo)) {
                iterator.remove();
                System.out.println("✓ Account " + accountNo + " removed from customer " + this.name);
                return true;
            }
        }
        System.out.println("✗ Account " + accountNo + " not found");
        return false;
    }

    // Getters
    public LinkedList<Account> getAccounts() { return this.accounts; }
    public String getCustomerId() { return this.customerId; }
    public String getName() { return this.name; }
    public CustomerProfile getProfile() { return this.profile; }

    // Setters with validation
    public void setCustomerId(String customerId) {
        if (!ValidationPatterns.matchesPattern(customerId, ValidationPatterns.CUSTOMER_ID_PATTERN)) {
            throw new IllegalArgumentException(ValidationPatterns.CUSTOMER_ID_ERROR);
        }
        this.customerId = customerId;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.CUSTOMER_NAME_EMPTY_ERROR);
        }
        this.name = name.trim();
    }

    public void setProfile(CustomerProfile profile) {
        this.profile = profile;
        if (profile != null) {
            profile.setCustomer(this);
        }
    }

    @Override
    public String toString() {
        return String.format("Customer[ID=%s, Name=%s, Accounts=%d]",
                this.getCustomerId(), this.getName(), this.getAccounts().size());
    }
}
