package com.futech.entertainment.packages.wallets.services.interfaces;

import java.util.Map;

import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.wallets.models.UserWallet;
import com.google.gson.JsonObject;

public interface UserWalletServiceInterface extends BaseServiceInterface<UserWallet> {
    public Double getUserBalance(String userId);
    public Map<String, Object> checkUserBalance(String userId, String betAmount);
    public Map<String, Object> getWalletByUser(String userId);
    public JsonObject updateUserWalletAmountWithBitcoin(JsonObject json);
}
