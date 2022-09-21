package com.futech.entertainment.packages.users.repositories;

import org.springframework.stereotype.Repository;

import com.futech.entertainment.packages.core.repositories.BaseRepository;
import com.futech.entertainment.packages.users.models.UserToken;
import com.futech.entertainment.packages.users.repositories.interfaces.UserTokenRepositoryInterface;

@Repository
public class UserTokenRepository extends BaseRepository<UserToken> implements UserTokenRepositoryInterface {
    
    public UserTokenRepository(){
        super();
        this.setModel(new UserToken());
    }

}
