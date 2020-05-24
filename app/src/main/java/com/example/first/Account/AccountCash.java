package com.example.first.Account;

import com.example.first.Account.Repositories.CompositeRepo;
import com.example.first.Account.Repositories.RepoDB;

public class AccountCash {
    private static AccountCash accountCash;

    private CompositeRepo compositeRepo;

    public RepoDB getRepo() {
        return compositeRepo;
    }

    public static AccountCash getInstance() {
        if (accountCash == null) {
            accountCash = new AccountCash();
        }

        return accountCash;
    }

    private AccountCash() {
        compositeRepo = new CompositeRepo();
    }
}
