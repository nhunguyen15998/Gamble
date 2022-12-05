package com.futech.entertainment.packages.wallets.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futech.entertainment.packages.payments.services.interfaces.BitcoinServiceInterface;
import com.futech.entertainment.packages.payments.services.interfaces.MomoServiceInterface;
import com.futech.entertainment.packages.payments.services.interfaces.VNPayServiceInterface;
import com.futech.entertainment.packages.payments.utils.PaymentHelpers;
import com.futech.entertainment.packages.settings.services.interfaces.ConfigServiceInterface;
import com.futech.entertainment.packages.settings.utils.ConfigHelpers;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.wallets.modelMappers.DepositMapper;
import com.futech.entertainment.packages.wallets.modelMappers.TransferMapper;
import com.futech.entertainment.packages.wallets.modelMappers.WithdrawBankMapper;
import com.futech.entertainment.packages.wallets.modelMappers.WithdrawBitcoinMapper;
import com.futech.entertainment.packages.wallets.models.Transaction;
import com.futech.entertainment.packages.wallets.models.TransactionWithdraw;
import com.futech.entertainment.packages.wallets.models.UserWallet;
import com.futech.entertainment.packages.wallets.services.interfaces.PaymentProcessServiceInterface;
import com.futech.entertainment.packages.wallets.services.interfaces.TransactionServiceInterface;
import com.futech.entertainment.packages.wallets.services.interfaces.TransactionWithdrawServiceInterface;
import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;
import com.google.gson.JsonObject;

@Service
public class PaymentProcessService implements PaymentProcessServiceInterface {
    
    @Autowired
    private TransactionServiceInterface transactionServiceInterface;
    @Autowired
    private VNPayServiceInterface vnpayServiceInterface;
    @Autowired
    private MomoServiceInterface momoServiceInterface;
    @Autowired
    private ConfigServiceInterface configServiceInterface;
    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;
    @Autowired
    private TransactionWithdrawServiceInterface transactionWithdrawServiceInterface;
    @Autowired
    private UserServiceInterface userServiceInterface;
    @Autowired
    private BitcoinServiceInterface bitcoinServiceInterface;

    @Transactional
    public Map<String, Object> depositProcess(DepositMapper depositMapper, HttpServletRequest req){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            var method = depositMapper.getMethod();
            var deposit = depositMapper.getAmount();
            Transaction transaction = new Transaction();
            var transactionCode = Helpers.randomStringWithLength(15);
            transaction.setCode(transactionCode);
            transaction.setType(depositMapper.getType());//deposit
            transaction.setMethod(method);
            transaction.setNote(depositMapper.getNote() == null ? null : depositMapper.getNote());
            transaction.setSender(depositMapper.getSender());
            transaction.setTitle(depositMapper.getTitle() == null ? null : depositMapper.getTitle());
            transaction.setcreated_at(LocalDateTime.now());
            transaction.setStatus(PaymentHelpers.PENDING);
            //type - vnpay, momo, bitcoin, paypal
            if(deposit != null && method != null){
                if(deposit.toString().trim().matches("^[+]?[0-9]*([.][0-9]*)?$")){
                    var dp = Double.parseDouble(deposit);
                    var vndExchangeRate = Double.parseDouble(this.configServiceInterface.getConfigStringElement(0, "VND", "rate").getAsString());
                    var fromCurrency = this.configServiceInterface.getConfig(0, "VND").get("name").toString();
                    switch(method){
                        case PaymentHelpers.VNPAY:
                            if(dp < 10000 || dp > 10000000){
                                obj.put("code", 400);
                                obj.put("amount", "Invalid amount");
                                return obj;
                            }
                            transaction.setexchange_rate(vndExchangeRate);
                            transaction.setAmount(Double.parseDouble(deposit));
                            transaction.setfrom_currency(fromCurrency);
                            var VNPayTransaction = this.transactionServiceInterface.createTransaction(transaction);
                            if(VNPayTransaction != null){
                                req.setAttribute("bankcode", "VNBANK");
                                req.setAttribute("transactionCode", VNPayTransaction.getCode());
                                req.setAttribute("amount", VNPayTransaction.getAmount());
                                obj = this.vnpayServiceInterface.doPost(req);
                            }
                            break;
                        case PaymentHelpers.MOMO:
                            if(dp < 1000 || dp > 50000000){
                                obj.put("code", 400);
                                obj.put("amount", "Invalid amount");
                                return obj;
                            }
                            transaction.setexchange_rate(vndExchangeRate);
                            transaction.setAmount(Double.parseDouble(deposit));
                            transaction.setfrom_currency(fromCurrency);
                            var MomoTransaction = this.transactionServiceInterface.createTransaction(transaction);
                            if(MomoTransaction != null){
                                req.setAttribute("transactionCode", transactionCode);
                                req.setAttribute("amount", Double.parseDouble(deposit));
                                obj = this.momoServiceInterface.doPost(req);
                                obj.put("transactionCode", transactionCode);
                            }
                            break;
                        case PaymentHelpers.BITCOIN:
                            if(dp < 0 || dp > 1){
                                obj.put("code", 400);
                                obj.put("amount", "Invalid amount");
                                return obj;
                            }
                            var bitcoinExchangeRate = Double.parseDouble(this.configServiceInterface.getConfigStringElement(0, "BITCOIN", "rate").getAsString());
                            transaction.setexchange_rate(bitcoinExchangeRate);
                            transaction.setfrom_currency("BITCOIN");
                            transaction.setAmount(Double.parseDouble(deposit));
                            var bcaddress = this.bitcoinServiceInterface.getAddress(transactionCode); //"abc123";//
                            transaction.setBcaddress(bcaddress);
                            var bitcoinTransaction = this.transactionServiceInterface.createTransaction(transaction);
                            obj.put("code", 200);
                            obj.put("transactionCode", bitcoinTransaction.getCode());
                            obj.put("bcaddress", bcaddress);
                            obj.put("data", "http://localhost:9090/user/my-wallet/deposit/bitcoin");
                            break;
                        case PaymentHelpers.PAYPAL:
                            break;
                    }
                } else {
                    obj.put("code", 400);
                    obj.put("amount", "Invalid format");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", 500);
            obj.put("msg", e.getMessage());
        }
        return obj;
    }

    @Transactional
    public JsonObject returnDepositResult(HttpServletRequest req, Integer depositMethod){
        JsonObject obj = new JsonObject();
        try {
            switch(depositMethod){
                case PaymentHelpers.VNPAY:
                    obj = vnpayServiceInterface.doReturn(req);
                    break;
                case PaymentHelpers.MOMO:
                    obj = momoServiceInterface.doReturn(req);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            obj.addProperty("msg", e.getMessage());
        }
        return obj;
    }

    @Transactional
    public JsonObject regenerateBitcoin(String transactionCode){
        JsonObject obj = new JsonObject();
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "code", "=", transactionCode, ""));
            var transaction = this.transactionServiceInterface.getFirstBy(null, conditions, null);
            if(transaction != null){
                var bcaddress = this.bitcoinServiceInterface.getAddress(transactionCode); //"abc123";//
                Transaction trans = new Transaction();
                trans.setId(Integer.parseInt(transaction.get("id").toString()));
                trans.setBcaddress(bcaddress);
                this.transactionServiceInterface.update(trans);
                obj.addProperty("transactionCode", transactionCode);
                obj.addProperty("bcaddress", bcaddress);
                obj.addProperty("data", "http://localhost:9090/user/my-wallet/deposit/bitcoin");
            } else {
                obj.addProperty("msg", "Invalid transaction");
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.addProperty("msg", e.getMessage());
        }
        return obj;
    }

    //withdraw
    @Transactional
    public Map<String, Object> withdrawBankProccess(WithdrawBankMapper withdrawBankMapper, int withdrawPassword){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            var method = withdrawBankMapper.getMethod();
            var user = withdrawBankMapper.getSender();
            if(!withdrawBankMapper.getBank_amount().trim().matches("^[+]?[0-9]*([.][0-9]*)?$")){
                obj.put("code", 400);
                obj.put("amount", "Invalid amount format");
                return obj;
            }
            var amount = Double.parseDouble(withdrawBankMapper.getBank_amount());
            if(amount <= 0){
                obj.put("code", 400);
                obj.put("amount", "Invalid amount");  
                return obj;
            }
            //check if amount <= userwallet
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "user_id", "=", String.valueOf(user), ""));
            var userWallet = this.userWalletServiceInterface.getFirstBy(null, conditions, null);
            var userAmount = Double.parseDouble(userWallet.get("cur_amount").toString());
            
            //create transaction
            Transaction transaction = new Transaction();
            var transactionCode = Helpers.randomStringWithLength(15);
            transaction.setCode(transactionCode);
            transaction.setType(withdrawBankMapper.getType());
            transaction.setMethod(method);
            transaction.setNote(withdrawBankMapper.getNotes() == null ? null : withdrawBankMapper.getNotes());
            transaction.setSender(withdrawBankMapper.getSender());
            transaction.setcreated_at(LocalDateTime.now());
            transaction.setStatus(PaymentHelpers.PENDING);

            //update user wallet
            var wallet = new UserWallet();
            wallet.setId(Integer.parseInt(userWallet.get("id").toString()));
            wallet.setpre_amount(userAmount);

            var exchangeRate = Double.parseDouble(this.configServiceInterface.getConfigStringElement(0, "VND", "rate").getAsString());
            var fromCurrency = this.configServiceInterface.getConfig(0, "VND").get("name").toString();
            var withdrawAmount = amount*exchangeRate;
            if(withdrawAmount > userAmount){
                obj.put("code", 400);
                obj.put("amount", "Amount exceeds available amount");
                return obj;
            }
            if(withdrawPassword == ConfigHelpers.IS_ON){
                obj.put("code", 406);
                obj.put("message", "Please fill in your password first");
                return obj;
            }
            transaction.setexchange_rate(exchangeRate);
            transaction.setAmount(amount);//*exchangeRate);
            transaction.setfrom_currency(fromCurrency);
            transaction.setreceived_amount(amount*exchangeRate);
            var trans = this.transactionServiceInterface.createTransaction(transaction);

            //create transaction withdraw
            if(trans != null){
                TransactionWithdraw transactionWithdraw = new TransactionWithdraw();
                transactionWithdraw.setAccount_name(withdrawBankMapper.getAccount_name());
                transactionWithdraw.setAccount_number(withdrawBankMapper.getAccount_number());
                transactionWithdraw.setBank(withdrawBankMapper.getBank());
                transactionWithdraw.setNotes(withdrawBankMapper.getNotes() != "" ? withdrawBankMapper.getNotes() : null);
                transactionWithdraw.setTransaction_id(trans.getId());
                transactionWithdraw.setAmount(amount);
                this.transactionWithdrawServiceInterface.create(transactionWithdraw);
            }

            var currentAmount = userAmount - withdrawAmount;
            wallet.setcur_amount(currentAmount);
            var updated = this.userWalletServiceInterface.update(wallet);
            if(updated){
                obj.put("code", 200);
                obj.put("wallet", currentAmount);
                obj.put("message", "Withdraw successfully! We 'll transfer to you in 24 hours");
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("message", e.getMessage());
        }
        return obj;
    }

    @Transactional
    public Map<String, Object> withdrawBitcoinProccess(WithdrawBitcoinMapper withdrawBitcoinMapper, int withdrawPassword){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            var method = withdrawBitcoinMapper.getMethod();
            var user = withdrawBitcoinMapper.getSender();
            if(!withdrawBitcoinMapper.getBitcoin_amount().trim().matches("^[+]?[0-9]*([.][0-9]*)?$")){
                obj.put("code", 400);
                obj.put("amount", "Invalid amount format");
                return obj;
            }
            var amount = Double.parseDouble(withdrawBitcoinMapper.getBitcoin_amount());
            if(amount < 0.0000546){
                obj.put("code", 400);
                obj.put("amount", "Invalid amount, withdraw amount must be at least 0.0000546 BTC"); 
                return obj; 
            }
            //check if amount <= userwallet
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "user_id", "=", String.valueOf(user), ""));
            var userWallet = this.userWalletServiceInterface.getFirstBy(null, conditions, null);
            var userAmount = Double.parseDouble(userWallet.get("cur_amount").toString());

            //create transaction
            Transaction transaction = new Transaction();
            transaction.setCode(withdrawBitcoinMapper.getTransaction_code());
            transaction.setType(withdrawBitcoinMapper.getType());
            transaction.setMethod(method);
            transaction.setSender(withdrawBitcoinMapper.getSender());
            transaction.setcreated_at(LocalDateTime.now());
            transaction.setStatus(PaymentHelpers.PENDING);
            transaction.setBcaddress(withdrawBitcoinMapper.getBcaddress());

            //update user wallet
            var wallet = new UserWallet();
            wallet.setId(Integer.parseInt(userWallet.get("id").toString()));
            wallet.setpre_amount(userAmount);

            var exchangeRate = Double.parseDouble(this.configServiceInterface.getConfigStringElement(0, "BITCOIN", "rate").getAsString());
            var fromCurrency = this.configServiceInterface.getConfig(0, "BITCOIN").get("name").toString();
            var withdrawAmount = amount*exchangeRate;
            if(withdrawAmount > userAmount){
                obj.put("code", 400);
                obj.put("amount", "Amount exceeds available amount");
                return obj;
            }
            if(withdrawPassword == ConfigHelpers.IS_ON){
                obj.put("code", 406);
                obj.put("message", "Please fill in your password first");
                return obj;
            }
            transaction.setexchange_rate(exchangeRate);
            //no need to exchange to USD
            transaction.setAmount(amount);//*exchangeRate);
            transaction.setfrom_currency(fromCurrency);
            transaction.setreceived_amount(amount*exchangeRate);
            this.transactionServiceInterface.createTransaction(transaction);

            var currentAmount = userAmount - withdrawAmount;
            wallet.setcur_amount(currentAmount);
            var updated = this.userWalletServiceInterface.update(wallet);
            if(updated){
                obj.put("code", 200);
                obj.put("wallet", currentAmount);
                obj.put("message", "Withdraw successfully! We 'll transfer to you in 24 hours");
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("message", e.getMessage());
        }
        return obj;
    }

    @Transactional
    public Map<String, Object> transferProccess(TransferMapper transferMapper, String phone, int withdrawPassword){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            var user = transferMapper.getSender();
            var receiverPhone = transferMapper.getPhone();
            if(receiverPhone.equals(phone)){
                obj.put("code", 400);
                obj.put("phone", "Invalid phone number");
                return obj;
            }
            //get receiver
            List<DataMapper> recConditions = new ArrayList<DataMapper>();
            recConditions.add(DataMapper.getInstance("", "phone", "=", receiverPhone, ""));
            var receiver = this.userServiceInterface.getFirstBy(null, recConditions, null);
            if(receiver == null){
                obj.put("code", 400);
                obj.put("phone", "Invalid phone number");
                return obj;
            }
            var receiverId = Integer.parseInt(receiver.get("id").toString());

            if(!transferMapper.getAmount().trim().matches("^[+]?[0-9]*([.][0-9]*)?$")){
                obj.put("code", 400);
                obj.put("amount", "Invalid amount format");
                return obj;
            }
            var amount = Double.parseDouble(transferMapper.getAmount());
            if(amount <= 0){
                obj.put("code", 400);
                obj.put("amount", "Invalid amount");  
                return obj;
            }
            //check if amount <= userwallet
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "user_id", "=", String.valueOf(user), ""));
            var userWallet = this.userWalletServiceInterface.getFirstBy(null, conditions, null);
            var userAmount = Double.parseDouble(userWallet.get("cur_amount").toString());

            if(amount > userAmount){
                obj.put("code", 400);
                obj.put("amount", "Amount exceeds available amount");
                return obj;
            }
            if(withdrawPassword == ConfigHelpers.IS_ON){
                obj.put("code", 406);
                obj.put("message", "Please fill in your password first");
                return obj;
            }

            //create transaction
            var transactionCode = Helpers.randomStringWithLength(15);
            Transaction transaction = new Transaction();
            transaction.setCode(transactionCode);
            transaction.setType(transferMapper.getType());
            transaction.setMethod(0);
            transaction.setSender(transferMapper.getSender());
            transaction.setcreated_at(LocalDateTime.now());
            transaction.setStatus(PaymentHelpers.SUCCESS);
            transaction.setNote(transferMapper.getNotes().trim() != "" ? transferMapper.getNotes() : null);
            transaction.setReceiver(receiverId);

            //update sender wallet
            var wallet = new UserWallet();
            wallet.setId(Integer.parseInt(userWallet.get("id").toString()));
            wallet.setpre_amount(userAmount);

            transaction.setAmount(amount);
            transaction.setreceived_amount(amount);
            this.transactionServiceInterface.createTransaction(transaction);

            var currentAmount = userAmount - amount;
            wallet.setcur_amount(currentAmount);
            var updated = this.userWalletServiceInterface.update(wallet);

            //update receiver wallet
            List<DataMapper> receiverConditions = new ArrayList<DataMapper>();
            receiverConditions.add(DataMapper.getInstance("", "user_id", "=", String.valueOf(receiverId), ""));
            var receiverWallet = this.userWalletServiceInterface.getFirstBy(null, receiverConditions, null);
            var recCurrentAmount = Double.parseDouble(receiverWallet.get("cur_amount").toString());
            var recWallet = new UserWallet();
            recWallet.setId(Integer.parseInt(receiverWallet.get("id").toString()));
            recWallet.setpre_amount(recCurrentAmount);
            var currentReceiverAmount = recCurrentAmount + amount;
            recWallet.setcur_amount(currentReceiverAmount);
            var receiverWalletUpdated = this.userWalletServiceInterface.update(recWallet);

            if(updated && receiverWalletUpdated){
                obj.put("code", 200);
                obj.put("wallet", currentAmount);
                obj.put("message", "You've successfully transferred your money to ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("message", e.getMessage());
        }
        return obj;
    }
}
