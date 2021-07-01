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
    void testTransferAlgoFromOneAccountToTwoAccount() throws Exception {
        Account accountOne = accountFactory.getAccountByAlias("one");
        Long accountOneBalance = accountService.getAccountBalance(accountOne);
        log.info("Account one initial balance: " + accountOneBalance);
        //if account balance is less than 0.001 (which is equivalent to 1000microAlgo) Algo, fail the test
        if (accountOneBalance < 1000) {
            Assertions.fail("Account one does not have enough balance: " + accountOneBalance);
        }
        Account accountTwo = accountFactory.getAccountByAlias("two");
        Long accountTwoBalance = accountService.getAccountBalance(accountTwo);
        log.info("Account two initial balance: " + accountTwoBalance);

        //transfer the amount
        int transferAmountMilliAlgos = 1000000;
        PendingTransactionResponse transactionResponse = transferService.transferAlgo(accountOne, accountTwo.getAddress(),
                transferAmountMilliAlgos, "Test note from Junit");

        //verify the account balances are correct after the transfer
        Long accountOneBalanceAfterTransfer = accountService.getAccountBalance(accountOne);
        Long accountTwoBalanceAfterTransfer = accountService.getAccountBalance(accountTwo);
        assertThat(accountOneBalanceAfterTransfer).isEqualTo(accountOneBalance - transferAmountMilliAlgos - 1000);
        assertThat(accountTwoBalanceAfterTransfer).isEqualTo(accountTwoBalance + transferAmountMilliAlgos);
        assertThat(getNote(transactionResponse)).isEqualTo("Test note from Junit");
    }

    private String getNote(PendingTransactionResponse transactionResponse) throws JSONException {
        JSONObject jsonObj = new JSONObject(transactionResponse.toString());
        log.info("Transaction information (with notes): " + jsonObj.toString(2));
        return new String(transactionResponse.txn.tx.note);
    }

}