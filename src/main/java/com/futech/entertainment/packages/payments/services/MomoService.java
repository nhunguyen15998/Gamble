package com.futech.entertainment.packages.payments.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.payments.services.interfaces.MomoServiceInterface;
import com.futech.entertainment.packages.payments.utils.PaymentHelpers;
import com.futech.entertainment.packages.settings.services.interfaces.ConfigServiceInterface;
import com.futech.entertainment.packages.wallets.models.Transaction;
import com.futech.entertainment.packages.wallets.models.UserWallet;
import com.futech.entertainment.packages.wallets.services.interfaces.TransactionServiceInterface;
import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;
import com.google.gson.JsonObject;

@Service
public class MomoService implements MomoServiceInterface {
    @Autowired
    private TransactionServiceInterface transactionServiceInterface;
    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;
    @Autowired
    private ConfigServiceInterface configServiceInterface;
    
    public Map<String, Object> doPost(HttpServletRequest req) throws ServletException, IOException {
        Map<String, Object> job = new HashMap<String, Object>();
        try {
            Map<String, Object> momo_Params = new HashMap<String, Object>();
            var endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
            var partnerCode = "MOMOBKUN20180529"; //run
            var accessKey = "klm05TvNBzhg7h7j";
            var secretKey = "at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa";
            // var accessKey = "XIcSwMOSvHnLmvvS"; //test
            // var partnerCode = "MOMOBHOA20220826";
            // var secretKey = "oxBkfH70OmZpTxCsVOe9FZhxWqrX3qwd";

            // var accessKey = "EjFgVmHQymz9g6Mv"; //real
            // var partnerCode = "MOMOCIST20211013";
            // var secretKey = "pB9obDs5kHLdKSB4gRYFuL5j6y9CYvgR";

            var amount = String.valueOf(Math.round(Double.parseDouble(req.getAttribute("amount").toString())));
            var orderId = req.getAttribute("transactionCode").toString();
            var orderInfo = "MomoTest";
            var requestType = "captureWallet";
            var requestId = Helpers.randomStringWithLength(10);
            var extraData = "";
            var redirectUrl = "http://localhost:9090/proceed/transaction";
            var ipnUrl = "http://localhost:9090/momoCallback/"+orderId;

            var lang = "vi";

            var rawHash = String.format("accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s", 
                                                accessKey, amount, extraData, ipnUrl, orderId, orderInfo, partnerCode, redirectUrl, requestId, requestType);
            var signature = PaymentHelpers.hmacSHA256(secretKey, rawHash);

            momo_Params.put("accessKey", accessKey);
            momo_Params.put("amount", amount);
            momo_Params.put("extraData", extraData);
            momo_Params.put("ipnUrl", ipnUrl);
            momo_Params.put("lang", lang);
            momo_Params.put("orderId", orderId);
            momo_Params.put("orderInfo", orderInfo);
            momo_Params.put("partnerCode", partnerCode);
            momo_Params.put("redirectUrl", redirectUrl);
            momo_Params.put("requestId", requestId);
            momo_Params.put("requestType", requestType);
            momo_Params.put("signature", signature);
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .headers("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(momo_Params.toString()))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);

            var rp = PaymentHelpers.jsonStringToJsonObject(response.body());
            var payurl = rp.get("payUrl");
            job.put("code", 200);
            job.put("message", "success");
            job.put("signature", signature);
            job.put("payUrl", payurl);
        } catch (Exception e) {
            job.put("code", 500);
            job.put("message", e.getMessage());
            job.put("data", "");
        }
        return job;
    }

    public JsonObject doReturn(HttpServletRequest req) throws ServletException, IOException {
        JsonObject job = new JsonObject();
        try {
            var accessKey = "klm05TvNBzhg7h7j";
            var secretKey = "at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa";

            var amount = req.getParameter("amount");
            var extraData = req.getParameter("extraData");
            var message = req.getParameter("message");
            var orderId = req.getParameter("orderId");
            var orderInfo = req.getParameter("orderInfo");
            var orderType = req.getParameter("orderType");
            var partnerCode = req.getParameter("partnerCode");
            var payType = req.getParameter("payType");
            var requestId = req.getParameter("requestId");
            var responseTime = req.getParameter("responseTime");
            var resultCode = req.getParameter("resultCode");
            var transId = req.getParameter("transId");
            var signature = req.getParameter("signature");

            //Checksum 
            var rawHash = String.format("accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s", 
                                                accessKey, amount, extraData, message, orderId, orderInfo, orderType, partnerCode, payType, requestId, responseTime, resultCode, transId);
            
            var partnerSignature = PaymentHelpers.hmacSHA256(secretKey, rawHash);
            if(signature.equals(partnerSignature)){
                //check if transaction exists
                List<DataMapper> transactionConditions = new ArrayList<DataMapper>();
                transactionConditions.add(DataMapper.getInstance("", "code", "=", orderId, ""));
                transactionConditions.add(DataMapper.getInstance("and", "status", "=", String.valueOf(PaymentHelpers.PENDING), ""));
                var transaction = this.transactionServiceInterface.getFirstBy(null, transactionConditions, null);
                if(transaction == null){
                    job.addProperty("code", 400);
                    job.addProperty("msg", "Invalid transaction");
                    return job;
                }
                //compare transaction amounts
                var transactionAmount = Double.parseDouble(transaction.get("amount").toString());
                if(transactionAmount != Double.parseDouble(amount)){
                    job.addProperty("code", 400);
                    job.addProperty("msg", "Invalid amount");
                    return job;
                }
                //check result code returned by momo
                if(resultCode.equals("0")){
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
        } catch (Exception e) {
            job.addProperty("code", 500);
            job.addProperty("msg", e.getMessage());
        }
        return job;
    }
}
