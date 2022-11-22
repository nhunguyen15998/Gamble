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
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.payments.services.interfaces.VNPayServiceInterface;
import com.futech.entertainment.packages.payments.utils.PaymentHelpers;
import com.futech.entertainment.packages.settings.services.interfaces.ConfigServiceInterface;
import com.futech.entertainment.packages.settings.services.interfaces.UserConfigServiceInterface;
import com.futech.entertainment.packages.settings.utils.ConfigHelpers;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.futech.entertainment.packages.wallets.modelMappers.DepositMapper;
import com.futech.entertainment.packages.wallets.modelMappers.TransferMapper;
import com.futech.entertainment.packages.wallets.modelMappers.WithdrawBankMapper;
import com.futech.entertainment.packages.wallets.modelMappers.WithdrawBitcoinMapper;
import com.futech.entertainment.packages.wallets.services.interfaces.PaymentProcessServiceInterface;
import com.futech.entertainment.packages.wallets.services.interfaces.TransactionServiceInterface;
import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
    @Autowired
    private UserConfigServiceInterface userConfigServiceInterface;
    @Autowired
    private ConfigServiceInterface configServiceInterface;

    //get exchange rate
    @GetMapping("/getExRateAndBalance")
    public ResponseEntity<String> getExchangeRate(
            @Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers,
            @RequestParam Map<String, Object> name
    ){
        Gson gson = new Gson();
        try {
            var verifiedToken = headers.get("auth").toString();
            var balance = this.userServiceInterface.getUserByToken(new String[]{"user_wallets.cur_amount"}, verifiedToken).get("cur_amount").toString();
            var exchangeRate = this.configServiceInterface.getConfigStringElement(0, name.get("name").toString(), "rate").getAsString();
            JsonObject obj = new JsonObject();
            obj.addProperty("balance", Double.parseDouble(balance));
            obj.addProperty("exchangeRate", Double.parseDouble(exchangeRate));
            return ResponseEntity.ok(gson.toJson(obj));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(gson.toJson(e.getMessage()));
        }
    } 

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
            req.setAttribute("vnp_ReturnUrl", PaymentHelpers.vnp_Returnurl_Android);
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
            // http://localhost:9090/api/proceed/transaction?vnp_Amount=2000000&vnp_BankCode=NCB&vnp_BankTranNo=VNP13874194&vnp_CardType=ATM&vnp_OrderInfo=TransactionTest&vnp_PayDate=20221109122943&vnp_ResponseCode=00&vnp_TmnCode=P5FNXKXA&vnp_TransactionNo=13874194&vnp_TransactionStatus=00&vnp_TxnRef=8b0e89ccb531411&vnp_SecureHash=d6b3799f50ac54d937d03ef7f12a38f13e89a93ab576debbf191f719b9f32a8bfacb1d51610a86f45027aad6e7ffc8f3f44edd910faf0335d2a339897f5d113e
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
    @PostMapping("/withdrawBankProccess")
    public ResponseEntity<Map<String, Object>> withdrawBankProccess(
            @Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers,
            @Valid @RequestBody WithdrawBankMapper withdrawBankMapper) {
        Map<String, Object> item = new HashMap<String,Object>();
        try {
            var verifiedToken = headers.get("auth").toString();
            var currentUserId = this.userServiceInterface.getUserByToken(new String[]{"users.id as user_id"}, verifiedToken).get("user_id").toString();
            //check setting
            var userConfigString = userConfigServiceInterface.getClientConfigs(currentUserId).get("config_string").toString();
            var withdrawPassword = ConfigHelpers.getSettingValueByKey(userConfigString, "withdraw_password");

            var sender = Integer.parseInt(currentUserId);
            withdrawBankMapper.setSender(sender);
            withdrawBankMapper.setType(PaymentHelpers.WITHDRAW);
            withdrawBankMapper.setMethod(PaymentHelpers.BANK);
            item = this.paymentProcessServiceInterface.withdrawBankProccess(withdrawBankMapper, withdrawPassword);
            return new ResponseEntity<Map<String, Object>>(item, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String,Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
    }

    // @PostMapping("/withdrawBankProccessWithPassword")
    // public ResponseEntity<Map<String, Object>> withdrawBankProccessWithPassword(@RequestParam String data, HttpSession session) {
    //     Map<String, Object> item = new HashMap<String,Object>();
    //     try {
    //         JsonObject jo = JsonParser.parseString(data).getAsJsonObject();
    //         var password = jo.get("require_password").getAsString();
    //         var withdraw = jo.get("withdrawBank").getAsJsonObject();

    //         //verify pass
    //         var phone = session.getAttribute("phone").toString();
    //         item = this.userServiceInterface.verifyPassword(password, phone);
    //         if(Integer.parseInt(item.get("code").toString()) != 200){
    //             return new ResponseEntity<Map<String, Object>>(item, HttpStatus.OK);
    //         }
    //         item.clear();

    //         var sender = Integer.parseInt(session.getAttribute("user_id").toString());
    //         WithdrawBankMapper withdrawBankMapper = new WithdrawBankMapper();
    //         withdrawBankMapper.setAccount_name(withdraw.get("account_name").getAsString());
    //         withdrawBankMapper.setAccount_number(withdraw.get("account_number").getAsString());
    //         withdrawBankMapper.setBank(withdraw.get("bank").getAsString());
    //         withdrawBankMapper.setBank_amount(withdraw.get("bank_amount").getAsString());
    //         withdrawBankMapper.setNotes(withdraw.get("notes").getAsString());
    //         withdrawBankMapper.setSender(sender);
    //         withdrawBankMapper.setType(PaymentHelpers.WITHDRAW);
    //         withdrawBankMapper.setMethod(PaymentHelpers.BANK);
    //         item = this.paymentProcessServiceInterface.withdrawBankProccess(withdrawBankMapper, ConfigHelpers.IS_OFF);
    //         return new ResponseEntity<Map<String, Object>>(item, HttpStatus.OK);
    //     } catch (Exception e) {
    //         Map<String, Object> err = new HashMap<String,Object>();
    //         err.put("message", e.getMessage());
    //         return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
    //     }
    // }

    @GetMapping("/generateBCAddress")
    public ResponseEntity<Map<String, String>> generateBCAddress(
                @Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers){
        Map<String, String> item = new HashMap<String,String>();
        try {
            var transactionCode = Helpers.randomStringWithLength(15);
            var address = "abc1345";//this.bitcoinServiceInterface.getAddress(transactionCode);
            item.put("bcaddress", address);
            item.put("transactionCode", transactionCode);
            return new ResponseEntity<Map<String, String>>(item, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Map<String, String>>(item, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/withdrawBitcoinProccess")
    public ResponseEntity<Map<String, Object>> withdrawBitcoinProccess(
                @Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers,
                @Valid @RequestBody WithdrawBitcoinMapper withdrawBitcoinMapper) {
        Map<String, Object> item = new HashMap<String,Object>();
        try {
            var verifiedToken = headers.get("auth").toString();
            var currentUserId = this.userServiceInterface.getUserByToken(new String[]{"users.id as user_id"}, verifiedToken).get("user_id").toString();
            //check setting
            var userConfigString = userConfigServiceInterface.getClientConfigs(currentUserId).get("config_string").toString();
            var withdrawPassword = ConfigHelpers.getSettingValueByKey(userConfigString, "withdraw_password");

            var sender = Integer.parseInt(currentUserId);
            withdrawBitcoinMapper.setSender(sender);
            withdrawBitcoinMapper.setType(PaymentHelpers.WITHDRAW);
            withdrawBitcoinMapper.setMethod(PaymentHelpers.BITCOIN);
            item = this.paymentProcessServiceInterface.withdrawBitcoinProccess(withdrawBitcoinMapper, withdrawPassword);
            return new ResponseEntity<Map<String, Object>>(item, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String,Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
    }
    
    //transfer
    @PostMapping("/transferProccess")
    public ResponseEntity<Map<String, Object>> transferProccess(
                @Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers,
                @Valid @RequestBody TransferMapper transferMapper) {
        Map<String, Object> item = new HashMap<String,Object>();
        try {
            var verifiedToken = headers.get("auth").toString();
            var currentUser = this.userServiceInterface.getUserByToken(new String[]{"users.id as user_id, users.phone"}, verifiedToken);
            var currentUserId = currentUser.get("user_id").toString();
            var currentUserPhone = currentUser.get("phone").toString();
            //check setting
            var userConfigString = userConfigServiceInterface.getClientConfigs(currentUserId).get("config_string").toString();
            var transferPassword = ConfigHelpers.getSettingValueByKey(userConfigString, "transfer_password");

            var sender = Integer.parseInt(currentUserId);
            transferMapper.setSender(sender);
            transferMapper.setType(PaymentHelpers.TRANSFER);
            item = this.paymentProcessServiceInterface.transferProccess(transferMapper, currentUserPhone, transferPassword);
            return new ResponseEntity<Map<String, Object>>(item, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String,Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
    }


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
