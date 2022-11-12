package com.futech.entertainment.packages.wallets.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.payments.utils.PaymentHelpers;
import com.futech.entertainment.packages.wallets.modelMappers.TransactionMapper;
import com.futech.entertainment.packages.wallets.models.Transaction;
import com.futech.entertainment.packages.wallets.services.interfaces.TransactionServiceInterface;

@Service
public class TransactionService extends BaseService<Transaction> implements TransactionServiceInterface {

    public Map<String, Object> getTransaction(String sender, String code){
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "code", "=", code, "and"));
            conditions.add(DataMapper.getInstance("", "sender", "=", sender, ""));
            var transaction = this.getFirstBy(null, conditions, null);
            return transaction;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> getTransactionByCode(String code){
        try {
            String[] selects = {"transactions.*, p.first_name, p.last_name, u.is_admin, w.account_name, w.account_number, w.bank, w.amount as 'wAmount', w.notes as 'wNotes'"};
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "code", "=", code, ""));

            List<JoinCondition> lsJoin = new ArrayList<JoinCondition>();
            lsJoin.add(JoinCondition.getInstance("left join", "users u ", DataMapper.getInstance("", "u.id", "=", "transactions.sender", "")));
            lsJoin.add(JoinCondition.getInstance("left join", "user_profiles p ", DataMapper.getInstance("", "u.id", "=", "p.user_id", "")));
            lsJoin.add(JoinCondition.getInstance("left join", "transaction_withdraws w ", DataMapper.getInstance("", "w.transaction_id", "=", "transactions.id", "")));
           
            var transaction = this.getFirstBy(selects, conditions, lsJoin);
            return transaction;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean updateStatus(int id, int status){
        try {
            Transaction trans = new Transaction();
            trans.setId(id);
            trans.setStatus(status);
            this.update(trans);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }

    public List<Map<String, Object>> getTransactions(String userId){
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

    //api
    public Map<String, Object> getTransactionById(String[] select, List<DataMapper> conditions){
        try {
            List<JoinCondition> joins = new ArrayList<JoinCondition>();
            joins.add(JoinCondition.getInstance("left join", "transaction_withdraws", DataMapper.getInstance("", "transaction_withdraws.transaction_id", "=", "transactions.id", "")));
            joins.add(JoinCondition.getInstance("left join", "user_profiles", DataMapper.getInstance("", "user_profiles.user_id", "=", "transactions.sender", "")));
            joins.add(JoinCondition.getInstance("left join", "user_profiles up", DataMapper.getInstance("", "up.user_id", "=", "transactions.receiver", "")));
            var transactions = this.getFirstBy(select, conditions, joins);
            return transactions;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, Object>> getTransactionsByUserId(String[] select, String currentUserId, String[] limit){
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "transactions.receiver", "=", currentUserId, ""));
            conditions.add(DataMapper.getInstance("or", "transactions.sender", "=", currentUserId, ""));
            List<JoinCondition> joins = new ArrayList<JoinCondition>();
            joins.add(JoinCondition.getInstance("join", "users", DataMapper.getInstance("", "users.id", "=", currentUserId, "")));
            joins.add(JoinCondition.getInstance("join", "user_profiles", DataMapper.getInstance("", "user_profiles.user_id", "=", "transactions.sender", "")));
            joins.add(JoinCondition.getInstance("left join", "user_profiles up", DataMapper.getInstance("", "up.user_id", "=", "transactions.receiver", "")));
            String orderBy = "transactions.created_at desc";
            var transactions = this.getAll(select, conditions, joins, null, orderBy, limit);
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
