package com.futech.entertainment.packages.users.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.futech.entertainment.packages.core.models.EmailDetails;
import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.services.interfaces.EmailServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.settings.models.UserConfig;
import com.futech.entertainment.packages.settings.services.interfaces.UserConfigServiceInterface;
import com.futech.entertainment.packages.settings.utils.ConfigHelpers;
import com.futech.entertainment.packages.users.modelMappers.ChangePasswordMapper;
import com.futech.entertainment.packages.users.modelMappers.SignUpMapper;
import com.futech.entertainment.packages.users.modelMappers.UserMapper;
import com.futech.entertainment.packages.users.modelMappers.UserProfileMapper;
import com.futech.entertainment.packages.users.modelMappers.UserUpdateMapper;
import com.futech.entertainment.packages.users.models.User;
import com.futech.entertainment.packages.users.models.UserProfile;
import com.futech.entertainment.packages.users.services.interfaces.UserProfileServiceInterface;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.futech.entertainment.packages.users.services.interfaces.UserTokenServiceInterface;
import com.futech.entertainment.packages.wallets.models.UserWallet;
import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;

@Service
public class UserService extends BaseService<User> implements UserServiceInterface{

    @Autowired 
    private EmailServiceInterface emailServiceInterface;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserProfileServiceInterface userProfileServiceInterface;
    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;
    @Autowired
    private UserTokenServiceInterface userTokenServiceInterface;
    @Autowired
    private UserConfigServiceInterface userConfigServiceInterface;

    private final String SECRET_KEY = "UPXO0fc4DuyN2CgfR0aKbgeIT0Ntpf9sjglIc2aO";

    public boolean createUserSignUp(SignUpMapper signUpMapper){
        try {
            String activateCode = Helpers.randomStringWithLength(10);
            User user = this.createUser(signUpMapper, activateCode);
            if(user != null){
                UserProfile userProfile = this.userProfileServiceInterface.createUserProfile(user, signUpMapper);
                if(userProfile != null){
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
                    var userId = Integer.parseInt(user.get("id").toString());

                    User updatedUser = new User();
                    updatedUser.setId(userId);
                    updatedUser.setStatus(Helpers.ACTIVATED);
                    updatedUser.setactivate_code("");
                    this.update(updatedUser);

                    //create user wallet
                    UserWallet userWallet = new UserWallet();
                    userWallet.setuser_id(userId);
                    userWallet.setcur_amount(0.0);
                    userWallet.setpre_amount(0.0);
                    userWallet.setcreated_at(LocalDateTime.now());
                    userWallet.setStatus(0);
                    this.userWalletServiceInterface.create(userWallet);

                    //create user default configs
                    UserConfig userConfig = new UserConfig();
                    userConfig.setuser_id(userId);
                    userConfig.setconfig_string(ConfigHelpers.USER_DEFAULT_GENERAL_CONFIGS);
                    userConfig.setType(ConfigHelpers.TYPE_USER_GENERAL_SETTINGS);
                    this.userConfigServiceInterface.createClientConfigs(userConfig);

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

    public Map<String, Object> getUserById(String[] selects, int id){
        try {           
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "users.id", "=", String.valueOf(id), ""));
            List<JoinCondition> joins = new ArrayList<JoinCondition>();
            joins.add(JoinCondition.getInstance("join", "user_profiles", 
                        DataMapper.getInstance("", "users.id", "=", "user_profiles.user_id", "")));
               return this.getFirstBy(selects, conditions, joins);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
            joins.add(JoinCondition.getInstance("join", "user_configs", 
                        DataMapper.getInstance("", "users.id", "=", "user_configs.user_id", "")));
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

    //crud
    public User createUser(SignUpMapper signUpMapper, String activateCode){
        try {
            User newUser = new User();
            newUser.setPhone(signUpMapper.getPhone());
            newUser.setEmail(signUpMapper.getEmail());
            newUser.setplain_password(signUpMapper.getplain_password());
            newUser.sethash_password(passwordEncoder.encode(signUpMapper.getplain_password()));
            newUser.setcreated_at(LocalDateTime.now());
            newUser.setStatus(Helpers.DEACTIVATED);
            newUser.setactivate_code(activateCode);
            newUser.setIs_admin(0);
            User user = this.create(newUser);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean createUser(UserMapper userMapper){
        try {
            User newUser = new User();
            newUser.setPhone(userMapper.getPhone());
            newUser.setEmail(userMapper.getEmail());
            newUser.setplain_password(userMapper.getPlain_password());
            newUser.sethash_password(passwordEncoder.encode(userMapper.getPlain_password()));
            newUser.setcreated_at(LocalDateTime.now());
            newUser.setStatus(Helpers.ACTIVATED);
            newUser.setactivate_code(null);
            newUser.setIs_admin(1);
            User user = this.create(newUser);
            UserConfig config = new UserConfig();
            config.setType(1);
            config.setuser_id(user.getId());
            config.setconfig_string("{'withdraw_password':0,'transfer_password':0,'setting_password':0}");
            this.userConfigServiceInterface.create(config);
            UserProfile userProfile = new UserProfile();
            userProfile.setUserId(user.getId());
            userProfile.setFirstName(userMapper.getFirst_name());
            userProfile.setLastName(userMapper.getLast_name());
            userProfile.setThumbnail(userMapper.getThumbnail());
            userProfile.setBirth(LocalDate.parse(userMapper.getBirth()));
            userProfile.setGender(userMapper.getGender());
            this.userProfileServiceInterface.create(userProfile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

    public boolean updateUserUserProfile(UserProfileMapper userProfileMapper){
        try {
            User user = new User();
            user.setId(userProfileMapper.getuser_id());
            user.setEmail(userProfileMapper.getEmail());
            var updatedUser = this.update(user);

            UserProfile userProfile = new UserProfile();
            userProfile.setId(userProfileMapper.getId());
            userProfile.setUserId(userProfileMapper.getuser_id());
            userProfile.setFirstName(userProfileMapper.getfirst_name());
            userProfile.setLastName(userProfileMapper.getlast_name());
            userProfile.setBirth(LocalDate.parse(userProfileMapper.getBirth()));
            userProfile.setGender(userProfileMapper.getGender());
            var updatedUserProfile = this.userProfileServiceInterface.updateUserProfile(userProfile);

            if(updatedUser && updatedUserProfile){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateUserUserProfile(UserUpdateMapper userMapper){
        try {
            User user = new User();
            user.setId(userMapper.getUser_id());
            if(userMapper.getEmail()!=null) user.setEmail(userMapper.getEmail());
            if(userMapper.getStatus()!=null) user.setStatus(userMapper.getStatus());
            if(userMapper.getNew_password()!=null)  {user.setplain_password(userMapper.getNew_password()); user.sethash_password(passwordEncoder.encode(userMapper.getNew_password()));}
            var updatedUser = this.update(user);
            
            var updatedUserProfile = true;

            if(userMapper.getNew_password()==null){
            UserProfile userProfile = new UserProfile();
            userProfile.setId(userMapper.getUser_profile_id());
            userProfile.setUserId(userMapper.getUser_id());
            userProfile.setFirstName(userMapper.getFirst_name());
            userProfile.setLastName(userMapper.getLast_name());
            userProfile.setBirth(LocalDate.parse(userMapper.getBirth()));
            userProfile.setThumbnail(userMapper.getThumbnail());
            userProfile.setGender(userMapper.getGender());
            updatedUserProfile = this.userProfileServiceInterface.updateUserProfile(userProfile);

            }
            
            if(updatedUser && updatedUserProfile){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    //end crud
    public boolean checkPassword(UserUpdateMapper userMapper){
        String[] selects = {"users.hash_password"};
        var user = this.getUserById(selects, userMapper.getUser_id());
        if(user!=null ){
            if(!passwordEncoder.matches(userMapper.getCurrent_password(), user.get("hash_password").toString()))
            return false;
        }
        return true;
        
    }

    //change password
    public Map<String, Object> changePassword(ChangePasswordMapper changePasswordMapper){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            var userId = changePasswordMapper.getUser_id();
            var currentPassword = changePasswordMapper.getOld_password().trim();
            var newPassword = changePasswordMapper.getNew_password().trim();
            var newConfirmationPassword = changePasswordMapper.getConfirm_new_password().trim();

            //check if old password match
            String[] selects = {"users.hash_password"};
            var user = this.getUserById(selects, userId);
            if(user == null){
                obj.put("code", 404);
                obj.put("message", "Invalid user");
                return obj;
            }
            var checkPass = this.verifyPassword(currentPassword, user.get("hash_password").toString());
            if(Integer.parseInt(checkPass.get("code").toString()) != 200){
                return checkPass;
            }

            //check if confirm pass match
            if(!newPassword.equals(newConfirmationPassword)){
                obj.put("code", 400);
                obj.put("message", "Passwords do not match");
                return obj;
            }

            //update user
            User updateUser = new User();
            updateUser.setId(changePasswordMapper.getUser_id());
            updateUser.setplain_password(changePasswordMapper.getNew_password());
            updateUser.sethash_password(passwordEncoder.encode(changePasswordMapper.getNew_password()));
            var updatedUser = this.update(updateUser);
            if(updatedUser){
                obj.put("code", 200);
                obj.put("message", "OK");
            } else {
                obj.put("code", 400);
                obj.put("message", "Failed");
            }
            return obj;
        } catch (Exception e) {
            obj.put("code", 500);
            obj.put("message", e.getMessage());
            return obj;
        }
    }

    //verify password
    public Map<String, Object> verifyPassword(String password, String hashPassword){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            System.out.println(password);
            if(password.isBlank()){
                obj.put("code", 400);
                obj.put("message", "Password is required");
                return obj;
            }
            if(!passwordEncoder.matches(password, hashPassword)){
                obj.put("code", 400);
                obj.put("message", "Invalid current password");
                return obj;
            }
            obj.put("code", 200);
            obj.put("message", "OK");
        } catch (Exception e) {
            obj.put("code", 500);
            obj.put("message", e.getMessage());
        }
        return obj;
    }

    //authentication with token
    public Map<String, Object> authenticate(Map<String, Object> user){
        Map<String, Object> results = new HashMap<String, Object>();
        try {
            if(user == null){
                results.put("code", 400);
                results.put("message", "Invalid user");
                return results;
            }
            //create token
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, 30);
            Date expireDate = calendar.getTime();
            
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            String token = JWT.create()
                              .withIssuer("auth0")
                              .withClaim("phone", user.get("phone").toString())
                              .withExpiresAt(expireDate)
                              .sign(algorithm);
            //save token
            this.userTokenServiceInterface.createToken(Integer.parseInt(user.get("id").toString()), token);
            
            results.put("code", 200);
            results.put("user", user);
            results.put("token", token);//"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwaG9uZSI6IjA5ODc2NTQzMjEiLCJpc3MiOiJhdXRoMCIsImV4cCI6MTY3NTMzMTIyNX0.UrlEIIK4uu2nme5qqhiNdTkWz7EAzArXXU7ETodklSQ");
            results.put("message", "user authenticated");
        } catch (Exception e) {
            results.put("code", 500);
            results.put("message", e.getMessage());
        }
        return results;
    }

    public String verifyToken(Map<String, String> headers){
        try {
            var token = headers.get("auth");
            if(token == null || token.trim() == ""){
                return null;
            }
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY); 
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build(); 
            DecodedJWT jwt = verifier.verify(token);
            System.out.println("jwt: "+jwt);
            return token;
        } catch (JWTVerificationException e){
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> getUserByToken(String[] selects, String token){
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "user_tokens.token", "=", token, ""));
            conditions.add(DataMapper.getInstance("and", "users.status", "=", String.valueOf(Helpers.ACTIVATED), ""));
            List<JoinCondition> joins = new ArrayList<JoinCondition>();
            joins.add(JoinCondition.getInstance("join", "user_tokens", 
                        DataMapper.getInstance("", "users.id", "=", "user_tokens.user_id", "")));
            joins.add(JoinCondition.getInstance("join", "user_profiles", 
                        DataMapper.getInstance("", "users.id", "=", "user_profiles.user_id", "")));
            joins.add(JoinCondition.getInstance("join", "user_wallets", 
                        DataMapper.getInstance("", "users.id", "=", "user_wallets.user_id", "")));
            return this.getFirstBy(selects, conditions, joins);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sessionClear(HttpSession session){
        try {
            session.removeAttribute("firstName");
            session.removeAttribute("lastName");
            session.removeAttribute("thumbnail");
            session.removeAttribute("phone");
            session.removeAttribute("user_id");
            session.removeAttribute("user_profile_id");
            session.removeAttribute("is_admin");
            session.removeAttribute("user_config_id");
            session.removeAttribute("user_config_string");
            session.removeAttribute("createdAt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}