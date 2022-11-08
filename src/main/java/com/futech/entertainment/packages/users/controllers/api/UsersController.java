package com.futech.entertainment.packages.users.controllers.api;
 
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.futech.entertainment.packages.core.middlewares.auth.interfaces.Authentication;
import com.futech.entertainment.packages.users.modelMappers.UserProfileMapper;
import com.futech.entertainment.packages.users.services.interfaces.UserProfileServiceInterface;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Validated
@RestController
@RequestMapping(path="/api") 
public class UsersController {

    @Autowired
    private UserServiceInterface userServiceInterface;
    @Autowired
    private UserProfileServiceInterface userProfileService;
    
    @GetMapping("/get-user")
	public ResponseEntity<Map<String, Object>> getUser(@Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers) {
        Map<String, Object> user = new HashMap<String,Object>();
        try {
            var verifiedToken = headers.get("auth").toString();
            String[] selects = {"users.id, users.phone, users.email, users.status, users.created_at, "+ "user_wallets.cur_amount," +
            "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
            "user_profiles.birth, user_profiles.gender, user_profiles.wallpaper"};
            user = this.userServiceInterface.getUserByToken(selects, verifiedToken);
            if(user != null){
                return new ResponseEntity<Map<String, Object>>(user, HttpStatus.OK);
            }
            return new ResponseEntity<Map<String, Object>>(user, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            user.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(user, HttpStatus.BAD_REQUEST);
        }
	}

    @GetMapping("/get-user-by-phone")
	public ResponseEntity<Map<String, Object>> getUserByPhone(@Authentication @RequestHeader Map<String, String> headers, @RequestParam String phone) {
        Map<String, Object> user = new HashMap<String, Object>();
        try {
            var verifiedToken = headers.get("auth").toString();
            String[] selects = {"users.id as user_id, users.phone, users.email, users.status, "+
                                    "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
                                    "user_profiles.birth, user_profiles.gender"};
            user = this.userServiceInterface.getUserByToken(selects, verifiedToken);
            if(user == null || !user.get("phone").toString().equals(phone)){
                return new ResponseEntity<Map<String, Object>>(user, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<Map<String, Object>>(user, HttpStatus.OK);
        } catch (Exception e) {
            user.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(user, HttpStatus.BAD_REQUEST);
        }
	}

    @PostMapping("/update-user")
    public ResponseEntity<Map<String, Object>> updateUser(@Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers, @Valid @RequestBody UserProfileMapper userProfileMapper) {
        Map<String, Object> items = new HashMap<String,Object>();
        try {
            var verifiedToken = headers.get("auth").toString();
            String[] selects = {"users.id as user_id, users.phone, users.email, users.status, "+
                                    "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
                                    "user_profiles.birth, user_profiles.gender"};
            var dbUser = this.userServiceInterface.getUserByToken(selects, verifiedToken);
            var dbUserId = Integer.parseInt(dbUser.get("user_id").toString());
            var dbUserProfileId = Integer.parseInt(dbUser.get("user_profile_id").toString());

            userProfileMapper.setId(dbUserProfileId);
            userProfileMapper.setuser_id(dbUserId);
            var updated = this.userServiceInterface.updateUserUserProfile(userProfileMapper);

            if(updated){
                items.put("code", 200);
                items.put("message", "successful");
                return new ResponseEntity<Map<String, Object>>(items, HttpStatus.OK);
            }
            items.put("code", 400);
            items.put("message", "failed");
            return new ResponseEntity<Map<String, Object>>(items, HttpStatus.OK);
        } catch (Exception e) {
            items.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(items, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/change-profile-photo")
    public ResponseEntity<Map<String, Object>> changeProfilePhoto(@Authentication @RequestHeader Map<String, String> headers, MultipartFile photo) throws IOException {
        try {
            var verifiedToken = headers.get("auth").toString();
            String[] selects = {"users.id as user_id, users.phone, users.email, users.status, "+
                                    "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
                                    "user_profiles.birth, user_profiles.gender"};
            var dbUser = this.userServiceInterface.getUserByToken(selects, verifiedToken);

            var userProfileId = Integer.parseInt(dbUser.get("user_profile_id").toString());
            var userId = dbUser.get("user_id").toString();
            var user = this.userProfileService.updateProfilePhoto(photo, userProfileId, userId);
            var code = Integer.parseInt(user.get("code").toString());
            var photoName = user.get("data").toString();
            if(code == 200){
                user.put("thumbnail",  "/images/users/user"+userId+"/"+photoName);
                return new ResponseEntity<Map<String, Object>>(user, HttpStatus.OK);
            }
            return new ResponseEntity<Map<String, Object>>(user, HttpStatus.NOT_MODIFIED);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String,Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> onValidationError(ConstraintViolationException e) {
        Map<String, Object> errors = new HashMap<String,Object>();
        errors.put("code", 400);
        errors.put("message", e.getMessage().split(": ")[1]);
        return new ResponseEntity<Map<String, Object>>(errors, HttpStatus.BAD_REQUEST);
    }
    
}
