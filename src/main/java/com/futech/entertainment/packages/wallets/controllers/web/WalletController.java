package com.futech.entertainment.packages.wallets.controllers.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.futech.entertainment.packages.payments.services.interfaces.BitcoinServiceInterface;
import com.futech.entertainment.packages.wallets.services.interfaces.PaymentProcessServiceInterface;
import com.futech.entertainment.packages.wallets.services.interfaces.TransactionServiceInterface;
import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Controller
public class WalletController {

    @Autowired
    private BitcoinServiceInterface bitcoinServiceInterface;
    @Autowired
    private PaymentProcessServiceInterface paymentProcessServiceInterface;
    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;
    @Autowired
    private TransactionServiceInterface transactionServiceInterface;

    public WalletController() {}

    @GetMapping("user/my-wallet")
    public String getWallet(Model model, HttpSession session, HttpServletRequest req, RedirectAttributes redirAttrs){
        if(session.getAttribute("phone") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Sign in first");
            return "redirect:/user/sign-in";
        }
        String userId = session.getAttribute("user_id").toString();
        var balance = this.userWalletServiceInterface.getUserBalance(userId);
        model.addAttribute("balance", balance);
        model.addAttribute("profileURL", req.getRequestURL());
        model.addAttribute("walletSection", req.getRequestURI());
        return "wallets/my_wallets";
    }

    //bitcoin
    @GetMapping("user/my-wallet/deposit/bitcoin/{transactionCode}/{bcaddress}")
    public String getDepositBitcoin(Model model, @PathVariable String transactionCode, @PathVariable String bcaddress, HttpSession session, RedirectAttributes redirAttrs){
        if(session.getAttribute("phone") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Sign in first");
            return "redirect:/user/sign-in";
        }
        //get amount
        var transaction = this.transactionServiceInterface.getTransactionByCode(transactionCode);
        if(Integer.parseInt(transaction.get("status").toString()) != 0){
            return "redirect:/results/error";
        }
        var transactionAmount = Double.parseDouble(transaction.get("amount").toString());
        model.addAttribute("bcaddress", bcaddress);
        model.addAttribute("transactionCode", transactionCode);
        model.addAttribute("transactionAmount", String.format("%,.6f", transactionAmount));
        return "wallets/bitcoin";
    }
    @GetMapping("user/my-wallet/deposit/bitcoin/callback/{trid}")
    public void getCallbackBitcoin(@PathVariable String trid){
        try {
            var transaction = this.bitcoinServiceInterface.getTransaction(trid);
            System.out.println(transaction);
            var detail = transaction.mapStr("details").replace("[", "").replace("]", "");
            JsonElement json = JsonParser.parseString(detail);
            var transactionCode = json.getAsJsonObject().get("label").getAsString();
            var amount = transaction.mapStr("amount");
            var txid = transaction.mapStr("txid");
            var address = json.getAsJsonObject().get("address").getAsString();
            JsonObject js = new JsonObject();
            js.addProperty("transactionCode", transactionCode);
            js.addProperty("amount", amount);
            js.addProperty("txid", txid);
            js.addProperty("bcaddress", address);
            this.userWalletServiceInterface.updateUserWalletAmountWithBitcoin(js);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/proceed/transaction")
    public String returnDepositResult(HttpServletRequest req, Model model, HttpSession session, RedirectAttributes redirAttrs){
        if(session.getAttribute("phone") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Sign in first");
            return "redirect:/user/sign-in";
        }
        try {
            var depositMethod = Integer.parseInt(session.getAttribute("depositMethod").toString());
            var obj = this.paymentProcessServiceInterface.returnDepositResult(req, depositMethod);
            var code = Integer.parseInt(obj.get("code").toString());
            var msg = obj.get("msg").getAsString();
            model.addAttribute("msg", msg);
            session.removeAttribute("depositMethod");
            if(code == 200){
                return "redirect:/results/success";
            }
            return "redirect:/results/error";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", e.getMessage());
            return "redirect:/results/error";
        }
    }

    @GetMapping("/user/wallet/check-balance")
    public ResponseEntity<Map<String, Object>> checkUserBalance(@RequestParam String bet, HttpSession session){
        try {
            var userId = session.getAttribute("user_id").toString();
            Map<String, Object> obj = this.userWalletServiceInterface.checkUserBalance(userId, bet);
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String,Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
    }


}
