package com.futech.entertainment.packages.wallets.services;

import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.wallets.models.TransactionWithdraw;
import com.futech.entertainment.packages.wallets.repositories.TransactionWithdrawRepository;
import com.futech.entertainment.packages.wallets.services.interfaces.TransactionWithdrawServiceInterface;

@Service
public class TransactionWithdrawService extends TransactionWithdrawRepository implements TransactionWithdrawServiceInterface {
    
    public TransactionWithdraw createTransactionWithdraw(TransactionWithdraw transactionWithdraw){
        try {
            return this.create(transactionWithdraw);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
