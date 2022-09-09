package com.futech.entertainment.packages.wallets.controllers.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.futech.entertainment.packages.payments.services.interfaces.BitcoinServiceInterface;
import com.futech.entertainment.packages.payments.utils.PaymentHelpers;
import com.futech.entertainment.packages.wallets.services.interfaces.PaymentProcessServiceInterface;
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
        return "wallets/my_wallets";
    }

    //bitcoin
    @GetMapping("user/my-wallet/deposit/bitcoin/{transactionCode}/{bcaddress}")
    public String getDepositBitcoin(Model model, @PathVariable String transactionCode, @PathVariable String bcaddress, HttpSession session, RedirectAttributes redirAttrs){
        if(session.getAttribute("phone") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Sign in first");
            return "redirect:/user/sign-in";
        }
        model.addAttribute("bcaddress", bcaddress);
        model.addAttribute("transactionCode", transactionCode);
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




}
