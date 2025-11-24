package com.banking.utilities;

import com.banking.models.*;
import java.util.LinkedList;

// Utility class for account operations
public class AccountUtils {

    // Find account by account number
    public static Account findAccount(LinkedList<Account> accountList, String accountNo) {
        for (Account acc : accountList) {
            if (acc.getAccountNo().equals(accountNo)) {
                return acc;
            }
        }
        return null;
    }
}
