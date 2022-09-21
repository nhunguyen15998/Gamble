package com.futech.entertainment.packages.users.repositories.interfaces;

import org.springframework.stereotype.Component;

import com.futech.entertainment.packages.core.repositories.interfaces.BaseRepositoryInterface;
import com.futech.entertainment.packages.users.models.UserToken;

@Component
public interface UserTokenRepositoryInterface extends BaseRepositoryInterface<UserToken> {

}
