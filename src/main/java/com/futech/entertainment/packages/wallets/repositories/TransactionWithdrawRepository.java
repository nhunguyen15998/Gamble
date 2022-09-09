package com.futech.entertainment.packages.wallets.repositories;

import org.springframework.stereotype.Repository;

import com.futech.entertainment.packages.core.repositories.BaseRepository;
import com.futech.entertainment.packages.wallets.models.TransactionWithdraw;
import com.futech.entertainment.packages.wallets.repositories.interfaces.TransactionWithdrawRepositoryInterface;

@Repository
public class TransactionWithdrawRepository extends BaseRepository<TransactionWithdraw> implements TransactionWithdrawRepositoryInterface{
    public TransactionWithdrawRepository() {
        super();
        this.setModel(new TransactionWithdraw());
    }
}
