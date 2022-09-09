package com.futech.entertainment.packages.wallets.repositories.interfaces;

import org.springframework.stereotype.Component;

import com.futech.entertainment.packages.core.repositories.interfaces.BaseRepositoryInterface;
import com.futech.entertainment.packages.wallets.models.TransactionWithdraw;

@Component
public interface TransactionWithdrawRepositoryInterface extends BaseRepositoryInterface<TransactionWithdraw> {
    
}
