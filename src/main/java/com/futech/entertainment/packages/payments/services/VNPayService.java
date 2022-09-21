package com.futech.entertainment.packages.payments.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.payments.services.interfaces.VNPayServiceInterface;
import com.futech.entertainment.packages.payments.utils.PaymentHelpers;
import com.futech.entertainment.packages.settings.services.interfaces.ConfigServiceInterface;
import com.futech.entertainment.packages.wallets.models.Transaction;
import com.futech.entertainment.packages.wallets.models.UserWallet;
import com.futech.entertainment.packages.wallets.services.interfaces.TransactionServiceInterface;
import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Service
public class VNPayService implements VNPayServiceInterface{
    @Autowired
    private TransactionServiceInterface transactionServiceInterface;
    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;
    @Autowired
    private ConfigServiceInterface configServiceInterface;

    public Map<String, Object> doPost(HttpServletRequest req) throws ServletException, IOException {
        Map<String, Object> job = new HashMap<String, Object>();
        try {
            Map<String, String> vnp_Params = new HashMap<String, String>();
            //REQUIRED - amount, code, language, bankcode
            String vnp_Version = "2.1.0"; 
            String vnp_Command = "pay";
            String vnp_TmnCode = PaymentHelpers.vnp_TmnCode;
            String vnp_Amount = String.valueOf(Math.round(Double.parseDouble(req.getAttribute("amount").toString())) * 100);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Date dt = new Date();
            String vnp_CreateDate = formatter.format(dt);
            String vnp_CurrCode = "VND";
            //String vnp_IpAddr = Helpers.getIpAddress(req);
            String vnp_Locale = req.getAttribute("language") != null ? req.getAttribute("language").toString() : null;
            String vnp_OrderInfo = "TransactionTest";//req.getParameter("vnp_OrderInfo");
            String vnp_ReturnUrl = PaymentHelpers.vnp_Returnurl;
            String vnp_TxnRef = req.getAttribute("transactionCode").toString();
            //-vnp_SecureHash
            
            //OPTIONAL
            String vnp_BankCode = req.getAttribute("bankcode") != null ? req.getAttribute("bankcode").toString() : null;
            //String orderType = "billing";//req.getParameter("ordertype");

            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", vnp_Amount);
            vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
            //vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            if (vnp_Locale != null && !vnp_Locale.isEmpty()) {
                vnp_Params.put("vnp_Locale", vnp_Locale);
            } else {
                vnp_Params.put("vnp_Locale", "vn");
            }
            if (vnp_BankCode != null && !vnp_BankCode.isEmpty()) {
                vnp_Params.put("vnp_BankCode", vnp_BankCode);
            }
            //vnp_Params.put("vnp_OrderType", orderType);
            
            ///////hash
            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString())); 
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String vnp_SecureHash = PaymentHelpers.hmacSHA512(PaymentHelpers.vnp_HashSecret, hashData.toString());
            String queryUrl = query.toString();
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = PaymentHelpers.vnp_PayUrl + "?" + queryUrl;
            job.put("code", "200");
            job.put("message", "success");
            job.put("data", paymentUrl);
        } catch (Exception e) {
            job.put("code", "500");
            job.put("message", e.getMessage());
            job.put("data", "");
        }
        return job;
    }
    
    public JsonObject doReturn(HttpServletRequest req) throws ServletException, IOException{
        JsonObject job = new JsonObject();
        try {
            Map fields = new HashMap<String, String>();
            for(Enumeration params = req.getParameterNames(); params.hasMoreElements();) {
                String fieldName = (String) params.nextElement();
                String fieldValue = req.getParameter(fieldName);
                
                if ((fieldValue != null) && (fieldValue.length() > 0)){
                    fields.put(fieldName, fieldValue);
                }
            }
    
            String vnp_SecureHash = req.getParameter("vnp_SecureHash");
            if(fields.containsKey("vnp_SecureHashType")){
                fields.remove("vnp_SecureHashType");
            }
            if(fields.containsKey("vnp_SecureHash")){
                fields.remove("vnp_SecureHash");
            }
            
            // Check checksum
            String signValue = PaymentHelpers.hashAllFields(fields);
            System.out.println(signValue);
            System.out.println(vnp_SecureHash);
            if(signValue.equals(vnp_SecureHash)){
                //check if transaction exists
                String code = fields.get("vnp_TxnRef") != null ? fields.get("vnp_TxnRef").toString() : null;
                List<DataMapper> transactionConditions = new ArrayList<DataMapper>();
                transactionConditions.add(DataMapper.getInstance("", "code", "=", code, ""));
                transactionConditions.add(DataMapper.getInstance("and", "status", "=", String.valueOf(PaymentHelpers.PENDING), ""));
                var transaction = this.transactionServiceInterface.getFirstBy(null, transactionConditions, null);
                if(transaction == null){
                    job.addProperty("code", 400);
                    job.addProperty("msg", "Invalid transaction");
                    return job;
                }

                //compare transaction amounts
                var transactionAmount = Double.parseDouble(transaction.get("amount").toString());
                var returnedAmount = Double.parseDouble(fields.get("vnp_Amount").toString())/100;
                if(transactionAmount != returnedAmount){
                    job.addProperty("code", 400);
                    job.addProperty("msg", "Invalid amount");
                    return job;
                }

                //check result code returned by vnpay
                if(req.getParameter("vnp_ResponseCode").equals("00")){
                    //update transaction stt
                    var exchangeRate = Double.parseDouble(this.configServiceInterface.getConfigStringElement(0, "VND", "rate").getAsString());
                    Transaction trans = new Transaction();
                    trans.setId(Integer.parseInt(transaction.get("id").toString()));
                    trans.setAmount(transactionAmount*exchangeRate);
                    trans.setreceived_amount(transactionAmount*exchangeRate);
                    trans.setStatus(PaymentHelpers.SUCCESS);
                    this.transactionServiceInterface.update(trans);
                    //update wallet
                    List<DataMapper> walletConditions = new ArrayList<DataMapper>();
                    walletConditions.add(DataMapper.getInstance("", "user_id", "=", transaction.get("sender").toString(), ""));
                    var wallet = this.userWalletServiceInterface.getFirstBy(null, walletConditions, null);
                    UserWallet userWallet = new UserWallet();
                    userWallet.setId(Integer.parseInt(wallet.get("id").toString()));
                    userWallet.setpre_amount(Double.parseDouble(wallet.get("cur_amount").toString()));
                    var currentAmount = Double.parseDouble(wallet.get("cur_amount").toString()) + (transactionAmount*exchangeRate);
                    userWallet.setcur_amount(currentAmount);
                    this.userWalletServiceInterface.update(userWallet);
                    job.addProperty("code", 200);
                    job.addProperty("msg", "Transaction successful");
                }
            } else {
                job.addProperty("code", 400);
                job.addProperty("msg", "Invalid Checksum");
            }
        } catch(Exception e) {
            job.addProperty("code", 500);
            job.addProperty("msg", e.getMessage());
        }
        return job;
    }
}
