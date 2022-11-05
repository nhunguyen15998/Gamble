package com.futech.entertainment.packages.wallets.services.interfaces;

import java.util.List;
import java.util.Map;

import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.wallets.modelMappers.TransactionMapper;
import com.futech.entertainment.packages.wallets.models.Transaction;

public interface TransactionServiceInterface extends BaseServiceInterface<Transaction> {
    public Map<String, Object> getTransaction(String sender, String code);
    public List<Map<String, Object>> getTransaction(String userId);
    public Transaction createTransaction(TransactionMapper transactionMapper);
    public Transaction createTransaction(Transaction transactionMapper);
    public Map<String, Object> getTransactionByCode(String code);
}
