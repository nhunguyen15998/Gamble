package com.futech.entertainment.packages.wallets.controllers.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.futech.entertainment.packages.wallets.modelMappers.TransactionMapper;
import com.futech.entertainment.packages.wallets.services.interfaces.TransactionServiceInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Controller
public class TransactionController {
    private String title = "Transaction title";
    @Autowired
    private TransactionServiceInterface transactionServiceInterface;

    @PostMapping("/createTransaction")
    public ResponseEntity<Map<String, Object>> createTransaction(@Valid @RequestBody TransactionMapper transactionMapper) {
        try {
            transactionMapper.setTitle(title);
            var createdTransaction = this.transactionServiceInterface.createTransaction(transactionMapper);
            Map<String, Object> transaction = new HashMap<String,Object>();
            if(createdTransaction != null){
                transaction.put("transaction", createdTransaction);
                return new ResponseEntity<Map<String, Object>>(transaction, HttpStatus.CREATED);
            }
            return new ResponseEntity<Map<String, Object>>(transaction, HttpStatus.NOT_MODIFIED);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String,Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getTransaction/{code}")
    public ResponseEntity<String> getTransactionByCode(@PathVariable String code, HttpSession session){
        Gson gson = new Gson();
        JsonObject obj = new JsonObject();
        try {
            var sender = session.getAttribute("user_id").toString();
            var transaction = this.transactionServiceInterface.getTransaction(sender, code);
            if(transaction != null){
                obj.addProperty("status", Integer.parseInt(transaction.get("status").toString()));
                obj.addProperty("code", 200);
            } else {
                obj.addProperty("code", 500);
                obj.addProperty("message", "Invalid transaction");
            }
        } catch (Exception e) {
            obj.addProperty("code", 500);
        }
        return ResponseEntity.ok(gson.toJson(obj));
    } 

    @GetMapping("/users/transactions")
    public ResponseEntity<List<Map<String, Object>>> getUserTransactions(HttpSession session) {
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        try {
            var userId = session.getAttribute("user_id").toString();
            list = this.transactionServiceInterface.getTransaction(userId);
            return new ResponseEntity<List<Map<String, Object>>>(list, HttpStatus.OK);
        } catch (Exception e) {
            List<Map<String, Object>> errs = new ArrayList<Map<String,Object>>();
            Map<String, Object> err = new HashMap<String, Object>();
            err.put("message", e.getMessage());
            errs.add(1, err);
            return new ResponseEntity<List<Map<String, Object>>>(errs, HttpStatus.BAD_REQUEST);
        }
    }
    
}
