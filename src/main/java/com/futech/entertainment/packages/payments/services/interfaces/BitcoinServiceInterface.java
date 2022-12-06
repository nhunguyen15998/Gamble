package com.futech.entertainment.packages.payments.services.interfaces;

import java.math.BigDecimal;

import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Transaction;

public interface BitcoinServiceInterface {
    public String getAddress(String transactionCode);
    public Transaction getTransaction(String trid);
    public BigDecimal checkBalance();
    public String sendBitcoin(String address, BigDecimal amount, String comment);
}
