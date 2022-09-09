package com.futech.entertainment.packages.wallets.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.payments.utils.PaymentHelpers;
import com.futech.entertainment.packages.wallets.modelMappers.TransactionMapper;
import com.futech.entertainment.packages.wallets.models.Transaction;
import com.futech.entertainment.packages.wallets.services.interfaces.TransactionServiceInterface;

@Service
public class TransactionService extends BaseService<Transaction> implements TransactionServiceInterface {

    public Map<String, Object> getTransaction(String sender, String code){
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "code", "=", code, ""));
            conditions.add(DataMapper.getInstance("", "sender", "=", sender, ""));
            var transaction = this.getFirstBy(null, conditions, null);
            return transaction;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, Object>> getTransaction(String userId){
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "sender", "=", userId, ""));
            var transactions = this.getAll(null, conditions, null, null, null, null);
            return transactions;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Transaction createTransaction(TransactionMapper transactionMapper){
        try {
            Transaction transaction = new Transaction();
            transaction.setCode(Helpers.randomStringWithLength(15));
            transaction.setAmount(transactionMapper.getAmount());
            transaction.setType(transactionMapper.getType());
            transaction.setMethod(transactionMapper.getMethod());
            transaction.setNote(transactionMapper.getNote() == null ? null : transactionMapper.getNote().trim());
            transaction.setSender(transactionMapper.getSender());
            transaction.setReceiver(transactionMapper.getReceiver() == null ? null : transactionMapper.getReceiver());
            transaction.setTitle(transactionMapper.getTitle() == null ? null : transactionMapper.getTitle());
            transaction.setcreated_at(LocalDateTime.now());
            transaction.setStatus(PaymentHelpers.PENDING);
            var createdTransaction = this.create(transaction);
            return createdTransaction;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Transaction createTransaction(Transaction transaction){
        try {
            var createdTransaction = this.create(transaction);
            return createdTransaction;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
