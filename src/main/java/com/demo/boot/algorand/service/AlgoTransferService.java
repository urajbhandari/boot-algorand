package com.demo.boot.algorand.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.NodeStatusResponse;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.algorand.algosdk.v2.client.model.PostTransactionsResponse;
import com.demo.boot.algorand.model.AppRuntimeException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
@Log4j2
public class AlgoTransferService {

    private AlgodClient client;

    public AlgoTransferService(AlgodClient client) {
        this.client = client;
    }

    public PendingTransactionResponse transferAlgo(Account fromAccount, Address toAddress, long amountMilliAlgos, String note) throws Exception {
        Transaction txn = createTransaction(fromAccount, toAddress, amountMilliAlgos, note);

        SignedTransaction signedTxn = signTransaction(fromAccount, txn);

        String id = submitTransactionToNetwork(signedTxn);

        PendingTransactionResponse pTrx = waitForTxnConfirmation(id, 4);
        log.info("Transaction " + id + " confirmed in round " + pTrx.confirmedRound);

        return pTrx;
    }

    private Transaction createTransaction(Account fromAccount, Address toAddress, long amountMilliAlgos, String note) throws Exception {
        return Transaction.PaymentTransactionBuilder()
                .sender(fromAccount.getAddress())
                .noteUTF8(note)
                .amount(amountMilliAlgos)
                .receiver(toAddress)
                .lookupParams(client)
                .build();
    }

    private SignedTransaction signTransaction(Account fromAccount, Transaction txn) throws NoSuchAlgorithmException {
        SignedTransaction signedTxn = fromAccount.signTransaction(txn);
        log.info("Signed transaction with txid: " + signedTxn.transactionID);
        return signedTxn;
    }

    private String submitTransactionToNetwork(SignedTransaction signedTxn) throws Exception {
        byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTxn);
        Response<PostTransactionsResponse> resp = client.RawTransaction()
                .rawtxn(encodedTxBytes)
                .execute();
        if (!resp.isSuccessful()) {
            throw new AppRuntimeException(resp.message());
        }
        return resp.body().txId;
    }

    /**
     * utility function to wait on a transaction to be confirmed
     * the timeoutRounds parameter indicates how many rounds do you wish to check pending transactions for
     */
    public PendingTransactionResponse waitForTxnConfirmation(String txID, Integer timeoutRounds)
            throws Exception {

        validateParams(txID, timeoutRounds);

        Long startRound = getStartRound();

        Long currentRound = startRound;
        while (currentRound < (startRound + timeoutRounds)) {
            PendingTransactionResponse pendingInfo = checkPendingTransaction(txID);
            if (pendingInfo != null) {
                return pendingInfo;
            }

            waitForBlock(currentRound);

            currentRound++;
        }

        throw new AppRuntimeException("Transaction not confirmed after " + timeoutRounds + " rounds!");
    }

    private void validateParams(String txID, Integer timeout) {
        if (client == null || txID == null || timeout < 0) {
            throw new IllegalArgumentException("Bad arguments for waitForConfirmation.");
        }
    }

    private Long getStartRound() throws Exception {
        Response<NodeStatusResponse> resp = client.GetStatus().execute();
        if (!resp.isSuccessful()) {
            throw new AppRuntimeException(resp.message());
        }
        NodeStatusResponse nodeStatusResponse = resp.body();
        return nodeStatusResponse.lastRound + 1;
    }

    private PendingTransactionResponse checkPendingTransaction(String txID) throws Exception {
        // Check the pending transactions
        Response<PendingTransactionResponse> resp2 = client.PendingTransactionInformation(txID).execute();
        if (resp2.isSuccessful()) {
            PendingTransactionResponse pendingInfo = resp2.body();
            if (pendingInfo != null) {
                if (pendingInfo.confirmedRound != null && pendingInfo.confirmedRound > 0) {
                    // Got the completed Transaction
                    return pendingInfo;
                }
                if (pendingInfo.poolError != null && pendingInfo.poolError.length() > 0) {
                    // If there was a pool error, then the transaction has been rejected!
                    throw new AppRuntimeException("The transaction has been rejected with a pool error: " + pendingInfo.poolError);
                }
            }
        }
        return null;
    }

    private void waitForBlock(Long currentRound) throws Exception {
        Response<NodeStatusResponse> resp3 = client.WaitForBlock(currentRound).execute();
        if (!resp3.isSuccessful()) {
            throw new AppRuntimeException(resp3.message());
        }
    }

}
