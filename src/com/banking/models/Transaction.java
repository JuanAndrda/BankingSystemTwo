package com.banking.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.banking.utilities.ValidationPatterns;

// Transaction record - stores transaction details
public class Transaction {
    private String txId;
    private String fromAccountNo;
    private String toAccountNo;
    private TransactionType type;
    private double amount;
    private LocalDateTime timestamp;
    private String status;

    public Transaction(String txId, TransactionType type, double amount) {
        this.setTxId(txId);
        this.setType(type);
        this.setAmount(amount);
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
    }

    // Getters
    public String getTxId() { return this.txId; }
    public String getFromAccountNo() { return this.fromAccountNo; }
    public String getToAccountNo() { return this.toAccountNo; }
    public TransactionType getType() { return this.type; }
    public double getAmount() { return this.amount; }
    public LocalDateTime getTimestamp() { return this.timestamp; }
    public String getStatus() { return this.status; }

    // Setters with validation
    public void setTxId(String txId) {
        if (txId == null || txId.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.TRANSACTION_ID_EMPTY_ERROR);
        }
        this.txId = txId;
    }

    public void setFromAccountNo(String accountNo) {
        if (accountNo != null && !accountNo.matches(ValidationPatterns.ACCOUNT_NO_PATTERN)) {
            throw new IllegalArgumentException(ValidationPatterns.ACCOUNT_NO_FORMAT_ERROR + ": " + accountNo);
        }
        this.fromAccountNo = accountNo;
    }

    public void setToAccountNo(String accountNo) {
        if (accountNo != null && !accountNo.matches(ValidationPatterns.ACCOUNT_NO_PATTERN)) {
            throw new IllegalArgumentException(ValidationPatterns.ACCOUNT_NO_FORMAT_ERROR + ": " + accountNo);
        }
        this.toAccountNo = accountNo;
    }

    public void setType(TransactionType type) {
        if (type == null) {
            throw new IllegalArgumentException(ValidationPatterns.TRANSACTION_TYPE_NULL_ERROR);
        }
        this.type = type;
    }

    public void setAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(ValidationPatterns.AMOUNT_POSITIVE_ERROR);
        }
        this.amount = amount;
    }

    public void setStatus(String status) {
        if (status == null || !status.matches(ValidationPatterns.TRANSACTION_STATUS_PATTERN)) {
            throw new IllegalArgumentException(ValidationPatterns.TRANSACTION_STATUS_ERROR);
        }
        this.status = status;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("TX[%s] %s $%.2f [%s] %s",
                this.getTxId(), this.getType(), this.getAmount(), this.getStatus(), this.getTimestamp().format(formatter));
    }
}
