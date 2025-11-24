package com.banking.models;

/**
 * Enumeration of all possible banking transaction types.
 * Demonstrates type-safe classification of transactions.
 *
 * - DEPOSIT: Add funds to an account
 * - WITHDRAW: Remove funds from an account
 * - TRANSFER: Move funds between two accounts
 */
public enum TransactionType {
    /** Money deposited into an account */
    DEPOSIT,

    /** Money withdrawn from an account */
    WITHDRAW,

    /** Money transferred between accounts */
    TRANSFER
}
