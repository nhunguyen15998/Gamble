package com.futech.entertainment.packages.wallets.repositories;

import org.springframework.stereotype.Repository;

import com.futech.entertainment.packages.core.repositories.BaseRepository;
import com.futech.entertainment.packages.wallets.models.Transaction;
import com.futech.entertainment.packages.wallets.repositories.interfaces.TransactionRepositoryInterface;

@Repository
public class TransactionRepository extends BaseRepository<Transaction> implements TransactionRepositoryInterface {
    public TransactionRepository(){
        super();
        this.setModel(new Transaction());
    }
}
