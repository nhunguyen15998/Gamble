package com.futech.entertainment.packages.wallets.repositories;

import org.springframework.stereotype.Repository;

import com.futech.entertainment.packages.core.repositories.BaseRepository;
import com.futech.entertainment.packages.wallets.models.UserWallet;
import com.futech.entertainment.packages.wallets.repositories.interfaces.UserWalletRepositoryInterface;

@Repository
public class UserWalletRepository extends BaseRepository<UserWallet> implements UserWalletRepositoryInterface {
    public UserWalletRepository(){
        super();
        this.setModel(new UserWallet());
    }
}
