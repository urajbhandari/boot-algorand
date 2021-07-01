package com.demo.boot.algorand.service;

import com.algorand.algosdk.account.Account;
import com.demo.boot.algorand.model.AlgoRuntimeException;
import com.demo.boot.algorand.model.AppAccount;
import com.demo.boot.algorand.model.AppRuntimeException;
import com.demo.boot.algorand.config.AppProperties;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.Map;

@Service
public class AccountFactory {

    private AppProperties appProperties;

    public AccountFactory(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    Account getAccountByAlias(String alias) {
        Map<String, AppAccount> accounts = appProperties.getAccounts();
        AppAccount appAccount = accounts.get(alias);
        if (appAccount == null) {
            throw new AppRuntimeException("No account could be found for alias " + alias);
        }
        try {
            return new Account(appAccount.getPassphrase());
        } catch (GeneralSecurityException e) {
            throw new AlgoRuntimeException("Error creating account from passphrase", e);
        }
    }

}
