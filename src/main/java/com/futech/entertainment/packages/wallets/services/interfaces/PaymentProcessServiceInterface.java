package com.futech.entertainment.packages.wallets.services.interfaces;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.futech.entertainment.packages.wallets.modelMappers.DepositMapper;
import com.futech.entertainment.packages.wallets.modelMappers.TransferMapper;
import com.futech.entertainment.packages.wallets.modelMappers.WithdrawBankMapper;
import com.futech.entertainment.packages.wallets.modelMappers.WithdrawBitcoinMapper;
import com.google.gson.JsonObject;

public interface PaymentProcessServiceInterface {
    public Map<String, Object> depositProcess(DepositMapper depositMapper, HttpServletRequest req);
    public JsonObject returnDepositResult(HttpServletRequest req, Integer depositMethod);
    public JsonObject regenerateBitcoin(String transactionCode);

    public Map<String, Object> withdrawBankProccess(WithdrawBankMapper withdrawBankMapper);
    public Map<String, Object> withdrawBitcoinProccess(WithdrawBitcoinMapper withdrawBitcoinMapper);
    public Map<String, Object> transferProccess(TransferMapper transferMapper, String phone);
}
