package com.futech.entertainment.packages.payments.services.interfaces;

import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Transaction;

public interface BitcoinServiceInterface {
    public String getAddress(String transactionCode);
    public Transaction getTransaction(String trid);
}
