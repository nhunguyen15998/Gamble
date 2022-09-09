package com.futech.entertainment.packages.users.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.models.EmailDetails;
import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.services.interfaces.EmailServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.users.modelMappers.SignUpMapper;
import com.futech.entertainment.packages.users.models.User;
import com.futech.entertainment.packages.users.models.UserProfile;
import com.futech.entertainment.packages.users.services.interfaces.UserProfileServiceInterface;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.futech.entertainment.packages.wallets.models.UserWallet;
import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;

@Service
public class UserService extends BaseService<User> implements UserServiceInterface{

    @Autowired 
    private EmailServiceInterface emailServiceInterface;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserProfileServiceInterface userProfileService;
    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;
    
    public boolean createUserSignUp(SignUpMapper signUpMapper){
        try {
            String activateCode = Helpers.randomStringWithLength(10);
            User newUser = new User();
            newUser.setPhone(signUpMapper.getPhone());
            newUser.setEmail(signUpMapper.getEmail());
            newUser.setplain_password(signUpMapper.getplain_password());
            newUser.sethash_password(passwordEncoder.encode(signUpMapper.getplain_password()));
            newUser.setcreated_at(LocalDateTime.now());
            newUser.setStatus(Helpers.DEACTIVATED);
            newUser.setactivate_code(activateCode);
            User user = this.create(newUser);
            if(user != null){
                UserProfile userProfile = new UserProfile();
                userProfile.setUserId(user.getId());
                userProfile.setFirstName(signUpMapper.getfirst_name());
                userProfile.setLastName(signUpMapper.getlast_name());
                userProfile.setThumbnail("/images/defaults/no-user.jpeg");
                userProfile.setBirth(LocalDate.parse("1998-02-10"));
                userProfile.setGender(Helpers.MALE);
                UserProfile newUserProfile = this.userProfileService.create(userProfile);
                if(newUserProfile != null){
                    boolean sent = this.emailServiceInterface.sendMailWithAttachment(new EmailDetails(
                        signUpMapper.getEmail(),
                        "<p>Dear our beloved user,</p>"+
                        "<br/>"+
                        "<p>Thank you for your registration. Please click the link below to verify your account in order to experience our services.</p>"+
                        "<br/>"+
                        "http://localhost:9090/user/activate-account/"+activateCode,
                        "Account Verification"
                    ), true);
                    return sent;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Object> activateAccount(String activateCode){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "activate_code", "=", activateCode, ""));
            var user = this.getFirstBy(null, conditions, null);
            if(user != null){
                if(Integer.parseInt(user.get("status").toString()) == Helpers.DEACTIVATED){
                    
                    User updatedUser = new User();
                    updatedUser.setId(Integer.parseInt(user.get("id").toString()));
                    updatedUser.setStatus(Helpers.ACTIVATED);
                    updatedUser.setactivate_code("");
                    this.update(updatedUser);

                    //create user wallet
                    UserWallet userWallet = new UserWallet();
                    userWallet.setuser_id(Integer.parseInt(user.get("id").toString()));
                    userWallet.setcur_amount(0.0);
                    userWallet.setpre_amount(0.0);
                    userWallet.setcreated_at(LocalDateTime.now());
                    userWallet.setStatus(0);
                    this.userWalletServiceInterface.create(userWallet);
                    obj.put("code", 200);
                } else {
                    obj.put("code", 400);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", 500);
        }
        return obj;
    }

    //for sign in
    public Map<String, Object> getUserByPhone(String[] selects, String phone){
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "users.phone", "=", phone, ""));
            conditions.add(DataMapper.getInstance("and", "users.status", "=", String.valueOf(Helpers.ACTIVATED), ""));
            List<JoinCondition> joins = new ArrayList<JoinCondition>();
            joins.add(JoinCondition.getInstance("join", "user_profiles", 
                        DataMapper.getInstance("", "users.id", "=", "user_profiles.user_id", "")));
            return this.getFirstBy(selects, conditions, joins);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> getUserByForgotPasswordCode(String code){
        try {
            String[] selects = {"users.id as user_id, users.*, "+
            "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
            "user_profiles.birth, user_profiles.gender"};
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "users.forgot_password_code", "=", code, ""));
            conditions.add(DataMapper.getInstance("and", "users.status", "=", String.valueOf(Helpers.ACTIVATED), ""));
            List<JoinCondition> joins = new ArrayList<JoinCondition>();
            joins.add(JoinCondition.getInstance("join", "user_profiles", 
                        DataMapper.getInstance("", "users.id", "=", "user_profiles.user_id", "")));
            return this.getFirstBy(selects, conditions, joins);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateUser(User user){
        try {
            return this.update(user);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
