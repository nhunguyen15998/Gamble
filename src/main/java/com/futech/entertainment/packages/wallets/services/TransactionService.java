package com.futech.entertainment.packages.wallets.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.payments.services.interfaces.BitcoinServiceInterface;
import com.futech.entertainment.packages.payments.utils.PaymentHelpers;
import com.futech.entertainment.packages.wallets.modelMappers.TransactionMapper;
import com.futech.entertainment.packages.wallets.models.Transaction;
import com.futech.entertainment.packages.wallets.services.interfaces.TransactionServiceInterface;

@Service
public class TransactionService extends BaseService<Transaction> implements TransactionServiceInterface {

    @Autowired
    private BitcoinServiceInterface bitcoinServiceInterface;

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
        } catch (Exception e){
            return false;
        }
    }
    public Map<String, Object> updateBitcoinStatus(int id, int method, String bcaddress, String amount, int status){
        Map<String, Object> result =  new HashMap<String, Object>();
        try {
            Transaction trans = new Transaction();
            trans.setId(id);
            if(status == 0 || status == 1){
                trans.setStatus(status);
            }
            if(method == 2){
                //withdraw BTC: send BTC
                //check balance
                var balance = Double.parseDouble(this.bitcoinServiceInterface.checkBalance().toString());
                //balance < amount
                if(balance < Double.parseDouble(amount)){
                    result.put("code", 400);
                    result.put("message", "Your bitcoin wallet does not have enough coin for this transaction. Please deposit first!");
                    return result;
                }
                //send
                var bigDecimalAmount = BigDecimal.valueOf(Double.parseDouble(amount));
                this.bitcoinServiceInterface.sendBitcoin(bcaddress, bigDecimalAmount, "sent from Gamble");
            } 
            this.update(trans);
            result.put("code", 200);
            result.put("message", "This transaction is completed and money is delivered to user's wallet");
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
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
