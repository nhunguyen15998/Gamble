package com.futech.entertainment.packages.users.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.users.models.UserToken;
import com.futech.entertainment.packages.users.services.interfaces.UserTokenServiceInterface;

@Service
public class UserTokenService extends BaseService<UserToken> implements UserTokenServiceInterface {
    
    //crud
    public UserToken createToken(int userId, String token){
        try {
            UserToken userToken = new UserToken();
            userToken.setUser_id(userId);
            userToken.setToken(token);
            userToken.setCreated_at(LocalDateTime.now());
            return this.create(userToken);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //end crud
}
