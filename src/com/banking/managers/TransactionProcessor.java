package com.banking.managers;

import com.banking.models.*;
import com.banking.utilities.*;
import com.banking.BankingSystem;
import java.util.*;

// Transaction processor - demonstrates COMPOSITION (HAS-A relationship)
public class TransactionProcessor {
    private LinkedList<Account> accountList;
    private int txCounter;
    private InputValidator validator;
    private BankingSystem bankingSystem;

    public TransactionProcessor(LinkedList<Account> accountList, InputValidator validator) {
        this.accountList = accountList;
        this.txCounter = 1;
        this.validator = validator;
        this.bankingSystem = null;
    }

    public boolean deposit(String accountNo, double amount) {
        Account account = AccountUtils.findAccount(this.accountList, accountNo);
        if (account == null) {
            System.out.println("✗ Account not found");
            return false;
        }

        try {
            Transaction tx = new Transaction("TX" + String.format("%03d", this.txCounter++),
                    TransactionType.DEPOSIT, amount);
            tx.setToAccountNo(accountNo);

            account.deposit(amount);
            tx.setStatus("COMPLETED");
            account.addTransaction(tx);
            System.out.println("✓ Deposit processed: " + tx.getTxId());
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error processing deposit: " + e.getMessage());
            return false;
        }
    }

    public boolean withdraw(String accountNo, double amount) {
        Account account = AccountUtils.findAccount(this.accountList, accountNo);
        if (account == null) {
            System.out.println("✗ Account not found");
            return false;
        }

        try {
            Transaction tx = new Transaction("TX" + String.format("%03d", this.txCounter++),
                    TransactionType.WITHDRAW, amount);
            tx.setFromAccountNo(accountNo);

            if (account.withdraw(amount)) {
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

    public boolean transfer(String fromAccountNo, String toAccountNo, double amount) {
        Account fromAccount = AccountUtils.findAccount(this.accountList, fromAccountNo);
        Account toAccount = AccountUtils.findAccount(this.accountList, toAccountNo);

        if (fromAccount == null || toAccount == null) {
            System.out.println("✗ One or both accounts not found");
            return false;
        }

        try {
            Transaction tx = new Transaction("TX" + String.format("%03d", this.txCounter++),
                    TransactionType.TRANSFER, amount);
            tx.setFromAccountNo(fromAccountNo);
            tx.setToAccountNo(toAccountNo);

            if (fromAccount.withdraw(amount)) {
                toAccount.deposit(amount);
                tx.setStatus("COMPLETED");
                fromAccount.addTransaction(tx);
                toAccount.addTransaction(tx);
                System.out.println("✓ Transfer processed: " + tx.getTxId());
                return true;
            } else {
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

    // Uses Stack to show most recent transactions first (LIFO)
    public Stack<Transaction> getAccountTransactionsAsStack(String accountNo) {
        Account acc = AccountUtils.findAccount(this.accountList, accountNo);
        if (acc == null) return new Stack<>();

        Stack<Transaction> txStack = new Stack<>();
        LinkedList<Transaction> history = acc.getTransactionHistory();

        for (Transaction tx : history) {
            txStack.push(tx);
        }

        return txStack;
    }

    public void handleDeposit() {
        System.out.println("\n--- DEPOSIT MONEY ---");

        Account account = this.validator.getValidatedAccount(
                "✗ Account not found. Cannot deposit to non-existent account.");
        if (account == null) return;

        if (!this.bankingSystem.canAccessAccount(account.getAccountNo())) {
            System.out.println("✗ Access denied. You can only perform transactions on your own accounts.");
            return;
        }

        Double depAmt = this.validator.getValidatedAmount("Amount");
        if (depAmt == null) return;

        boolean success = this.deposit(account.getAccountNo(), depAmt);
        if (success) {
            System.out.println("✓ Deposit successful!");
            InputValidator.safeLogAction(bankingSystem, "DEPOSIT", "Amount: $" + depAmt + " to account: " + account.getAccountNo());
        } else {
            System.out.println("✗ Deposit failed. Please try again.");
        }
    }

    public void handleWithdraw() {
        System.out.println("\n--- WITHDRAW MONEY ---");

        Account account = this.validator.getValidatedAccount(
                "✗ Account not found. Cannot withdraw from non-existent account.");
        if (account == null) return;

        if (!this.bankingSystem.canAccessAccount(account.getAccountNo())) {
            System.out.println("✗ Access denied. You can only perform transactions on your own accounts.");
            return;
        }

        Double witAmt = this.validator.getValidatedAmount("Amount");
        if (witAmt == null) return;

        boolean success = this.withdraw(account.getAccountNo(), witAmt);
        if (success) {
            System.out.println("✓ Withdrawal successful!");
            InputValidator.safeLogAction(bankingSystem, "WITHDRAW", "Amount: $" + witAmt + " from account: " + account.getAccountNo());
        } else {
            System.out.println("✗ Withdrawal failed. Please try again.");
        }
    }

    public void handleTransfer() {
        System.out.println("\n--- TRANSFER MONEY ---");

        Account fromAccount = this.validator.getValidatedAccount(
                "✗ Source account not found. Cannot transfer from non-existent account.");
        if (fromAccount == null) return;

        if (!this.bankingSystem.canAccessAccount(fromAccount.getAccountNo())) {
            System.out.println("✗ Access denied. You can only transfer FROM your own accounts.");
            return;
        }

        Account toAccount = this.validator.getValidatedAccount(
                "✗ Destination account not found. Cannot transfer to non-existent account.");
        if (toAccount == null) return;

        if (fromAccount.getAccountNo().equals(toAccount.getAccountNo())) {
            System.out.println("✗ Cannot transfer to the same account");
            return;
        }

        Double amt = this.validator.getValidatedAmount("Amount");
        if (amt == null) return;

        boolean success = this.transfer(fromAccount.getAccountNo(), toAccount.getAccountNo(), amt);
        if (success) {
            System.out.println("✓ Transfer successful!");
            InputValidator.safeLogAction(bankingSystem, "TRANSFER", "Amount: $" + amt + " from " + fromAccount.getAccountNo() + " to " + toAccount.getAccountNo());
        } else {
            System.out.println("✗ Transfer failed. Please try again.");
        }
    }

    public void handleViewTransactionHistory() {
        System.out.println("\n--- VIEW TRANSACTION HISTORY ---");

        Account account = this.validator.getValidatedAccount();
        if (account == null) return;

        if (!this.bankingSystem.canAccessAccount(account.getAccountNo())) {
            System.out.println("✗ Access denied. You can only view transaction history for your own accounts.");
            return;
        }

        Stack<Transaction> txStack = this.getAccountTransactionsAsStack(account.getAccountNo());
        System.out.println("\n=== TRANSACTION HISTORY (LIFO - Most Recent First) ===");
        if (txStack.isEmpty()) {
            System.out.println("No transactions found for this account.");
        } else {
            System.out.println("Displaying " + txStack.size() + " transaction(s) using Stack (LIFO):\n");
            while (!txStack.isEmpty()) {
                System.out.println(txStack.pop());
            }
        }
    }

    public void setBankingSystem(BankingSystem bankingSystem) {
        this.bankingSystem = bankingSystem;
    }

}
