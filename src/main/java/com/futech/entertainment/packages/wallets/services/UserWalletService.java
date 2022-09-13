package com.futech.entertainment.packages.wallets.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.payments.utils.PaymentHelpers;
import com.futech.entertainment.packages.users.services.interfaces.ConfigServiceInterface;
import com.futech.entertainment.packages.wallets.models.Transaction;
import com.futech.entertainment.packages.wallets.models.UserWallet;
import com.futech.entertainment.packages.wallets.services.interfaces.TransactionServiceInterface;
import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;
import com.google.gson.JsonObject;

@Service
public class UserWalletService extends BaseService<UserWallet> implements UserWalletServiceInterface {
    
    @Autowired
    private TransactionServiceInterface transactionServiceInterface;
    @Autowired
    private ConfigServiceInterface configServiceInterface;
    
    public Double getUserBalance(String userId){
        try {
            var wallet = this.getWalletByUser(userId);
            var balance = PaymentHelpers.formatMoney(Double.parseDouble(wallet.get("cur_amount").toString()), "0.00000");
            return balance;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public Map<String, Object> checkUserBalance(String userId, String betAmount){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            if(betAmount == null || betAmount.trim() == ""){
                obj.put("code", 400);
                obj.put("amount", "Required");
                return obj;
            }
            if(!betAmount.trim().matches("^[+]?[0-9]*([.][0-9]*)?$")){
                obj.put("code", 400);
                obj.put("amount", "Invalid format");
                return obj;
            }
            var bet = Double.parseDouble(betAmount);
            if(bet <= 0){
                obj.put("code", 400);
                obj.put("amount", "Invalid amount");  
                return obj;
            }
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "user_id", "=", userId, ""));
            var userWallet = this.getFirstBy(null, conditions, null);
            var curAmount = Double.parseDouble(userWallet.get("cur_amount").toString());
            if(bet > curAmount){
                obj.put("code", 400);
                obj.put("amount", "Invalid amount");
                return obj;
            }
            obj.put("code", 200);
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("msg", e.getMessage());
        }
        return obj;
    }

    public Map<String, Object> getWalletByUser(String userId){
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "user_id", "=", userId, ""));
            return this.getFirstBy(null, conditions, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public JsonObject updateUserWalletAmountWithBitcoin(JsonObject json){
        JsonObject obj = new JsonObject();
        try {
            //get transaction by code, status == 0 -> get stt, amount, sender
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "code", "=", json.get("transactionCode").getAsString(), ""));
            conditions.add(DataMapper.getInstance("and", "status", "=", String.valueOf(PaymentHelpers.PENDING), ""));
            //compare address
            conditions.add(DataMapper.getInstance("and", "bcaddress", "=", json.get("bcaddress").getAsString(), ""));
            var transaction = this.transactionServiceInterface.getFirstBy(null, conditions, null);
            //transaction != null -> transaction amount match
            if(transaction == null){
                obj.addProperty("code", 400);
                obj.addProperty("msg", "Invalid transaction");
                return obj;
            }
            var transactionAmount = Double.parseDouble(transaction.get("amount").toString());
            var bitcoinExchangeRate = Double.parseDouble(this.configServiceInterface.getConfigStringElement(0, "BITCOIN", "rate").getAsString());
            var receivedAmount = Double.parseDouble(json.get("amount").getAsString())*bitcoinExchangeRate;
            //req: 10000 > send: 2000
            if(transactionAmount > receivedAmount){
                obj.addProperty("code", 400);
                obj.addProperty("msg", "Received amount is smaller than requested one, which is " + transactionAmount + " BTC");
                return obj;
            }
            //req: 10000 < send: 12000 -> update transaction status = 1, amount received, txid
            if(transactionAmount <= receivedAmount){
                var trans = new Transaction();
                trans.setId(Integer.parseInt(transaction.get("id").toString()));
                trans.setreceived_amount(receivedAmount);
                trans.setTxid(json.get("txid").getAsString());
                trans.setStatus(PaymentHelpers.SUCCESS);
                this.transactionServiceInterface.update(trans);
                //get wallet by sender -> update wallet amount 
                var userWallet = this.getWalletByUser(transaction.get("sender").toString());
                if(userWallet != null){
                    var wallet = new UserWallet();
                    wallet.setId(Integer.parseInt(userWallet.get("id").toString()));
                    var currentAmount = Double.parseDouble(userWallet.get("cur_amount").toString());
                    wallet.setpre_amount(currentAmount);
                    wallet.setcur_amount(currentAmount+transactionAmount);
                    this.update(wallet);
                    obj.addProperty("code", 200);
                    obj.addProperty("msg", "Wallet updated");
                } else {
                    obj.addProperty("code", 400);
                    obj.addProperty("msg", "Invalid wallet");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.addProperty("msg", e.getMessage());
        }
        return obj;
    }
}
