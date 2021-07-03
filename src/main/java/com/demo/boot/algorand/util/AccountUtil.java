package com.demo.boot.algorand.util;

import com.algorand.algosdk.account.Account;

import java.security.NoSuchAlgorithmException;

public class AccountUtil {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println("app:");
        System.out.println("  accounts:");

        Account alice = new Account();
        System.out.println("    alice: ");
        System.out.println("      address: " + alice.getAddress());
        System.out.println("      mnemonic: " + alice.toMnemonic());

        Account bob = new Account();
        System.out.println("    bob: ");
        System.out.println("      address: " + bob.getAddress());
        System.out.println("      mnemonic: " + bob.toMnemonic());
    }

}
