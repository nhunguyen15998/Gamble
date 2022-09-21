package com.futech.entertainment.packages.users.services.interfaces;

import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.users.models.UserToken;

public interface UserTokenServiceInterface extends BaseServiceInterface<UserToken> {
    public UserToken createToken(int userId, String token);
}
