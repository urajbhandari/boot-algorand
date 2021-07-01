package com.demo.boot.algorand.service;

import com.algorand.algosdk.account.Account;

import java.security.NoSuchAlgorithmException;

public class AccountUtil {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Account account1 = new Account();
        System.out.println("app.accounts.one.address=" + account1.getAddress());
        System.out.println("app.accounts.one.passphrase=" + account1.toMnemonic());

        Account account2 = new Account();
        System.out.println("app.accounts.two.address=" + account2.getAddress());
        System.out.println("app.accounts.two.passphrase=" + account2.toMnemonic());
    }

}
