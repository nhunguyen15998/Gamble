package com.futech.entertainment.packages.wallets.controllers.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.payments.services.interfaces.BitcoinServiceInterface;
import com.futech.entertainment.packages.payments.utils.PaymentHelpers;
import com.futech.entertainment.packages.settings.services.interfaces.ConfigServiceInterface;
import com.futech.entertainment.packages.wallets.modelMappers.DepositMapper;
import com.futech.entertainment.packages.wallets.modelMappers.TransferMapper;
import com.futech.entertainment.packages.wallets.modelMappers.WithdrawBankMapper;
import com.futech.entertainment.packages.wallets.modelMappers.WithdrawBitcoinMapper;
import com.futech.entertainment.packages.wallets.services.interfaces.PaymentProcessServiceInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Controller
public class PaymentProcessController {
    @Autowired
    private PaymentProcessServiceInterface paymentProcessServiceInterface;
    @Autowired
    private ConfigServiceInterface configServiceInterface;
    @Autowired
    private BitcoinServiceInterface bitcoinServiceInterface;

    //deposit
    @PostMapping("/depositProcess")
    public ResponseEntity<Map<String, Object>> depositProcess(@Valid @RequestBody DepositMapper depositMapper,  
                                                HttpSession session, HttpServletRequest req){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            var sender = Integer.parseInt(session.getAttribute("user_id").toString());
            session.setAttribute("depositMethod", depositMapper.getMethod());
            depositMapper.setSender(sender);
            depositMapper.setType(0);
            obj = this.paymentProcessServiceInterface.depositProcess(depositMapper, req);
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String,Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
    } 


    @PostMapping("/momoCallback/{transactionCode}")
    public ResponseEntity<String> getMoMoCallback(){
        Gson gson = new Gson();
        try {
            var a = "abc";
            System.out.println(a);
            return ResponseEntity.ok(gson.toJson("obj"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(gson.toJson(e.getMessage()));
        }
    }

    //get exchange rate
    @GetMapping("/getExRate")
    public ResponseEntity<String> getExchangeRate(@RequestParam Map<String, Object> name){
        Gson gson = new Gson();
        try {
            var exchangeRate = this.configServiceInterface.getConfigStringElement(0, name.get("name").toString(), "rate").getAsString();
            JsonObject obj = new JsonObject();
            obj.addProperty("exchangeRate", exchangeRate);
            return ResponseEntity.ok(gson.toJson(obj));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(gson.toJson(e.getMessage()));
        }
    } 
    
    //bitcoin
    @PostMapping("/regenerateBitcoin")
    public ResponseEntity<String> regenerateBitcoin(@RequestBody Map<String, Object> transactionCode){
        Gson gson = new Gson();
        try {
            var obj = this.paymentProcessServiceInterface.regenerateBitcoin(transactionCode.get("transactionCode").toString());
            return ResponseEntity.ok(gson.toJson(obj));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(gson.toJson(e.getMessage()));
        }
    }   

    //withdraw
    @PostMapping("/withdrawBankProccess")
    public ResponseEntity<Map<String, Object>> withdrawBankProccess(@Valid @RequestBody WithdrawBankMapper withdrawBankMapper,  
                                                        HttpSession session) {
        Map<String, Object> item = new HashMap<String,Object>();
        try {
            var sender = Integer.parseInt(session.getAttribute("user_id").toString());
            withdrawBankMapper.setSender(sender);
            withdrawBankMapper.setType(PaymentHelpers.WITHDRAW);
            withdrawBankMapper.setMethod(PaymentHelpers.BANK);
            item = this.paymentProcessServiceInterface.withdrawBankProccess(withdrawBankMapper);
            return new ResponseEntity<Map<String, Object>>(item, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String,Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/generateBCAddress")
    public ResponseEntity<String> generateBCAddress(){
        Gson gson = new Gson();
        try {
            var transactionCode = Helpers.randomStringWithLength(15);
            var address = "abc1345";//this.bitcoinServiceInterface.getAddress(transactionCode);
            JsonObject obj = new JsonObject();
            obj.addProperty("bcaddress", address);
            obj.addProperty("transactionCode", transactionCode);
            return ResponseEntity.ok(gson.toJson(obj));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(gson.toJson(e.getMessage()));
        }
    }

    @PostMapping("/withdrawBitcoinProccess")
    public ResponseEntity<Map<String, Object>> withdrawBitcoinProccess(@Valid @RequestBody WithdrawBitcoinMapper withdrawBitcoinMapper,  
                                                    HttpSession session) {
        Map<String, Object> item = new HashMap<String,Object>();
        try {
            var sender = Integer.parseInt(session.getAttribute("user_id").toString());
            withdrawBitcoinMapper.setSender(sender);
            withdrawBitcoinMapper.setType(PaymentHelpers.WITHDRAW);
            withdrawBitcoinMapper.setMethod(PaymentHelpers.BITCOIN);
            item = this.paymentProcessServiceInterface.withdrawBitcoinProccess(withdrawBitcoinMapper);
            return new ResponseEntity<Map<String, Object>>(item, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String,Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
    }

    //transfer
    @PostMapping("/transferProccess")
    public ResponseEntity<Map<String, Object>> transferProccess(@Valid @RequestBody TransferMapper transferMapper,  
                                                    HttpSession session) {
        Map<String, Object> item = new HashMap<String,Object>();
        try {
            var sender = Integer.parseInt(session.getAttribute("user_id").toString());
            transferMapper.setSender(sender);
            transferMapper.setType(PaymentHelpers.TRANSFER);
            var phone = session.getAttribute("phone").toString();
            item = this.paymentProcessServiceInterface.transferProccess(transferMapper, phone);
            return new ResponseEntity<Map<String, Object>>(item, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String,Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
    }

}
