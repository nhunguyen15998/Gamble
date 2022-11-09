package com.futech.entertainment.packages.wallets.controllers.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futech.entertainment.packages.core.middlewares.auth.interfaces.Authentication;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.payments.services.interfaces.VNPayServiceInterface;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.futech.entertainment.packages.wallets.modelMappers.DepositMapper;
import com.futech.entertainment.packages.wallets.services.interfaces.PaymentProcessServiceInterface;
import com.futech.entertainment.packages.wallets.services.interfaces.TransactionServiceInterface;
import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;

@Validated
@RestController
@RequestMapping(path = "/api")
public class WalletsController {
    
    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;
    @Autowired
    private TransactionServiceInterface transactionServiceInterface;
    @Autowired
    private UserServiceInterface userServiceInterface;
    @Autowired
    private PaymentProcessServiceInterface paymentProcessServiceInterface;
    @Autowired
    private VNPayServiceInterface vnpayServiceInterface;

    //deposit
    @PostMapping("/depositProcess")
    public ResponseEntity<Map<String, Object>> depositProcess(
            @Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers,
            @Valid @RequestBody DepositMapper depositMapper, HttpServletRequest req){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            var verifiedToken = headers.get("auth").toString();
            var currentUserId = this.userServiceInterface.getUserByToken(new String[]{"users.id as user_id"}, verifiedToken).get("user_id").toString();
            depositMapper.setSender(Integer.parseInt(currentUserId));
            depositMapper.setType(0);
            obj = this.paymentProcessServiceInterface.depositProcess(depositMapper, req);
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.BAD_REQUEST);
        }
    } 

    @GetMapping("/proceed/transaction")
    public ResponseEntity<Map<String, Object>> returnVnpayDepositResult(HttpServletRequest req){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            var jo = this.vnpayServiceInterface.doReturn(req);
            var code = Integer.parseInt(jo.get("code").toString());
            var msg = jo.get("msg").getAsString();
            obj.put("code", code);
            obj.put("message", msg);
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.BAD_REQUEST);
        }
    }

    //withdraw

    //transfer


    //transaction
    @GetMapping("/user/transactions")
	public ResponseEntity<List<Map<String, Object>>> getUserTransactions(@Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers, @RequestParam String page) {
        List<Map<String, Object>> transactions = new ArrayList<Map<String,Object>>();
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            var verifiedToken = headers.get("auth").toString();
            var currentUserId = this.userServiceInterface.getUserByToken(new String[]{"users.id as user_id"}, verifiedToken).get("user_id").toString();
            String[] selects = {"transactions.id as transaction_id, transactions.amount, transactions.code, transactions.method, transactions.type, transactions.note,"+
                "transactions.sender, transactions.receiver, transactions.created_at as transaction_created_at, transactions.status as transaction_status,"+
                "users.id as user_id, user_profiles.first_name, user_profiles.last_name, up.first_name as receiver_first_name, up.last_name as receiver_last_name"};
            int itemPerPage = 6;
            String[] limit = {String.valueOf(Integer.parseInt(page)*itemPerPage-itemPerPage), String.valueOf(itemPerPage)};
            transactions = this.transactionServiceInterface.getTransactionsByUserId(selects, currentUserId, limit);
            return new ResponseEntity<List<Map<String, Object>>>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("code", 500);
            obj.put("message", e.getMessage());
            transactions.add(obj);
            return new ResponseEntity<List<Map<String, Object>>>(transactions, HttpStatus.BAD_REQUEST);
        }
	}

    @GetMapping("/user/transaction")
	public ResponseEntity<Map<String, Object>> getUserTransaction(@Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers, @RequestParam String transactionId) {
        Map<String, Object> transactions = new HashMap<String,Object>();
        try {
            var verifiedToken = headers.get("auth").toString();
            var currentUser = this.userServiceInterface.getUserByToken(new String[]{"users.id as user_id"}, verifiedToken);
            if(currentUser == null){
                transactions.put("code", 404);
                transactions.put("message", "Not found");
                return new ResponseEntity<Map<String, Object>>(transactions, HttpStatus.OK);
            }
            String[] selects = {"transactions.id as transaction_id, transactions.amount, transactions.code, transactions.method, transactions.type, transactions.note, transactions.bcaddress,"+
                "transactions.sender, transactions.receiver, transactions.created_at as transaction_created_at, transactions.status as transaction_status,"+
                "user_profiles.first_name as sender_first_name, user_profiles.last_name as sender_last_name,"+
                "up.first_name as receiver_first_name, up.last_name as receiver_last_name,"+
                "transaction_withdraws.transaction_id, transaction_withdraws.account_name, transaction_withdraws.account_number, transaction_withdraws.bank"
            };
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "transactions.id", "=", transactionId, ""));
            transactions = this.transactionServiceInterface.getTransactionById(selects, conditions);
            return new ResponseEntity<Map<String, Object>>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            transactions.put("code", 500);
            transactions.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(transactions, HttpStatus.BAD_REQUEST);
        }
	}

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> onValidationError(ConstraintViolationException e) {
        Map<String, Object> errors = new HashMap<String,Object>();
        errors.put("code", 400);
        errors.put("message", e.getMessage().split(": ")[1]);
        return new ResponseEntity<Map<String, Object>>(errors, HttpStatus.BAD_REQUEST);
    }

}
