package com.futech.entertainment.packages.wallets.services.interfaces;

import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.wallets.models.TransactionWithdraw;

public interface TransactionWithdrawServiceInterface extends BaseServiceInterface<TransactionWithdraw>{
    public TransactionWithdraw createTransactionWithdraw(TransactionWithdraw transactionWithdraw);
}
