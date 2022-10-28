package com.futech.entertainment.packages.users.services.interfaces;

import java.util.Map;

import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.users.modelMappers.SignUpMapper;
import com.futech.entertainment.packages.users.modelMappers.UserMapper;
import com.futech.entertainment.packages.users.modelMappers.UserProfileMapper;
import com.futech.entertainment.packages.users.modelMappers.UserUpdateMapper;
import com.futech.entertainment.packages.users.models.User;
import com.futech.entertainment.packages.users.models.UserProfile;

public interface UserServiceInterface extends BaseServiceInterface<User> {
   public boolean createUserSignUp(SignUpMapper signUpMapper);
   public Map<String, Object> activateAccount(String activateCode);
   public Map<String, Object> getUserByPhone(String[] selects, String phone);
   public Map<String, Object> getUserByForgotPasswordCode(String code);
   public Map<String, Object> getUserById(String[] selects,int id);
   
   public User createUser(SignUpMapper signUpMapper, String activateCode);
   public boolean createUser(UserMapper usermapper);
   public boolean updateUser(User user);
   public boolean updateUserUserProfile(UserProfileMapper userProfileMapper);
   public boolean updateUserUserProfile(UserUpdateMapper userMapper);
   public boolean checkPassword(UserUpdateMapper userMapper);

   public Map<String, Object> verifyPassword(String password, String phone);

   public Map<String, Object> authenticate(Map<String, Object> user);
   public String verifyToken(Map<String, String> headers);
   public Map<String, Object> getUserByToken(String[] selects, String token);
}