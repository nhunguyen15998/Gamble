package com.futech.entertainment.packages.users.services.interfaces;

import java.util.Map;

import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.users.modelMappers.SignUpMapper;
import com.futech.entertainment.packages.users.models.User;

public interface UserServiceInterface extends BaseServiceInterface<User> {
   public boolean createUserSignUp(SignUpMapper signUpMapper);
   public Map<String, Object> activateAccount(String activateCode);
   public Map<String, Object> getUserByPhone(String[] selects, String phone);
   public Map<String, Object> getUserByForgotPasswordCode(String code);
   public boolean updateUser(User user);
}
