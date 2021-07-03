package com.demo.boot.algorand.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Log4j2
class AlgoTransferServiceTest {

    @Autowired
    AlgoTransferService transferService;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountFactory accountFactory;

    @Test
    void testTransferAlgoFromAliceToBob() throws Exception {
        Account aliceAccount = accountFactory.getAccountByAlias("alice");
        Long aliceAccountInitialBalance = accountService.getAccountBalance(aliceAccount);
        log.info("Alic account initial balance: " + aliceAccountInitialBalance);
        //if account balance is less than 0.001 (which is equivalent to 1000microAlgo) Algo, fail the test
        if (aliceAccountInitialBalance < 1000) {
            Assertions.fail("Alice account does not have enough balance: " + aliceAccountInitialBalance);
        }
        Account bobAccount = accountFactory.getAccountByAlias("bob");
        Long bobAccountInitialBalance = accountService.getAccountBalance(bobAccount);
        log.info("Bob account initial balance: " + bobAccountInitialBalance);

        //transfer the amount
        int transferAmountMicroAlgos = 1000000;
        String note = "Alice to Bob micro algo transfer: " + transferAmountMicroAlgos;
        PendingTransactionResponse transactionResponse = transferService.transferAlgo(aliceAccount, bobAccount.getAddress(),
                transferAmountMicroAlgos, note);

        //verify the account balances are correct after the transfer
        Long aliceAccountBalanceAfterTransfer = accountService.getAccountBalance(aliceAccount);
        Long bobAccountBalanceAfterTransfer = accountService.getAccountBalance(bobAccount);
        assertThat(aliceAccountBalanceAfterTransfer).isEqualTo(aliceAccountInitialBalance - transferAmountMicroAlgos - 1000);
        assertThat(bobAccountBalanceAfterTransfer).isEqualTo(bobAccountInitialBalance + transferAmountMicroAlgos);
        assertThat(getNote(transactionResponse)).isEqualTo(note);
    }

    private String getNote(PendingTransactionResponse transactionResponse) throws JSONException {
        JSONObject jsonObj = new JSONObject(transactionResponse.toString());
        log.info("Transaction information (with notes): " + jsonObj.toString(2));
        return new String(transactionResponse.txn.tx.note);
    }

}