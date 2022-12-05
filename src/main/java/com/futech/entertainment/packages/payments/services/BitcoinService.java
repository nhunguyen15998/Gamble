package com.futech.entertainment.packages.payments.services;

import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Transaction;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.payments.services.interfaces.BitcoinServiceInterface;

@Service
public class BitcoinService implements BitcoinServiceInterface{
    private BitcoindRpcClient client = new BitcoinJSONRPCClient();

    public String getAddress(String transactionCode){
        return client.getNewAddress(transactionCode);
    }

    public Transaction getTransaction(String trid){
        return client.getTransaction(trid);
    }

    //check balance
    public BigDecimal checkBalance(){
        return client.getBalance();
    }

    //send bitcoin
    public String sendBitcoin(String address, BigDecimal amount, String comment){
        client.walletPassPhrase("0987654321", 5);
        //txid
        return client.sendToAddress(address, amount, comment);
    }
}
