package com.demo.boot.algorand.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.v2.client.algod.AccountInformation;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.demo.boot.algorand.model.AlgoRuntimeException;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AlgodClient client;

    public AccountService(AlgodClient client) {
        this.client = client;
    }

    public Long getAccountBalance(Account account) {
        AccountInformation accountInformation = client.AccountInformation(account.getAddress());
        String[] txHeaders = new String[]{};
        String[] txValues = new String[]{};
        Response<com.algorand.algosdk.v2.client.model.Account> resp = null;
        try {
            resp = accountInformation
                    .execute(txHeaders, txValues);
        } catch (Exception e) {
            throw new AlgoRuntimeException("Error while getting account information for account " + account.getAddress(), e);
        }
        if (!resp.isSuccessful()) {
            throw new AlgoRuntimeException(resp.message());
        }
        com.algorand.algosdk.v2.client.model.Account accountInfo = resp.body();
        return accountInfo.amount;
    }

}
