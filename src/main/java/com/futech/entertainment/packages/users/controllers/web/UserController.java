package com.futech.entertainment.packages.users.controllers.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.futech.entertainment.packages.users.modelMappers.UserProfileMapper;
import com.futech.entertainment.packages.users.services.interfaces.UserProfileServiceInterface;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class UserController {

    @Autowired
    private UserServiceInterface userServiceInterface;
    @Autowired
    private UserProfileServiceInterface userProfileService;
    
    @GetMapping("/getUser")
	public ResponseEntity<Map<String, Object>> getUser(HttpSession session) {
        Map<String, Object> user = new HashMap<String,Object>();
        try {
            if(session.getAttribute("phone") == null){
                user.put("message", "Login first");
                return new ResponseEntity<Map<String, Object>>(user, HttpStatus.BAD_REQUEST);
            }
            String phone = session.getAttribute("phone").toString();
            String[] selects = {"users.id as user_id, users.phone, users.email, users.status, "+
            "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
            "user_profiles.birth, user_profiles.gender"};
            user = this.userServiceInterface.getUserByPhone(selects, phone);
            if(user != null){
                return new ResponseEntity<Map<String, Object>>(user, HttpStatus.OK);
            }
            return new ResponseEntity<Map<String, Object>>(user, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            user.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(user, HttpStatus.BAD_REQUEST);
        }
	}

    @GetMapping("/getUserByPhone")
	public ResponseEntity<Map<String, Object>> getUserByPhone(@RequestParam String phone, HttpSession session) {
        try {
            var currentPhone = session.getAttribute("phone").toString();
            Map<String, Object> user = new HashMap<String, Object>();
            if(!currentPhone.equals(phone)){
                String[] selects = {"users.id as user_id, users.phone, users.email, users.status, "+
                                    "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
                                    "user_profiles.birth, user_profiles.gender"};
                user = this.userServiceInterface.getUserByPhone(selects, phone);
                if(user != null){
                    return new ResponseEntity<Map<String, Object>>(user, HttpStatus.OK);
                }   
            }
            return new ResponseEntity<Map<String, Object>>(user, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String,Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
	}

    @PostMapping("/updateUser")
    public ResponseEntity<Map<String, Object>> updateUser(@Valid @RequestBody UserProfileMapper userProfileMapper, HttpSession session) {
        Map<String, Object> items = new HashMap<String,Object>();
        try {
            var dbUserId = Integer.parseInt(session.getAttribute("user_id").toString());
            var dbUserProfileId = Integer.parseInt(session.getAttribute("user_profile_id").toString());
            userProfileMapper.setId(dbUserProfileId);
            userProfileMapper.setuser_id(dbUserId);
            var updated = this.userServiceInterface.updateUserUserProfile(userProfileMapper);

            if(updated){
                //reset session
                session.setAttribute("firstName", userProfileMapper.getfirst_name());
                session.setAttribute("lastName", userProfileMapper.getlast_name());
                items.put("name", userProfileMapper.getfirst_name()+" "+userProfileMapper.getlast_name());
                return new ResponseEntity<Map<String, Object>>(items, HttpStatus.OK);
            }
            return new ResponseEntity<Map<String, Object>>(items, HttpStatus.NOT_MODIFIED);
        } catch (Exception e) {
            items.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(items, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/changeProfilePhoto")
    public ResponseEntity<Map<String, Object>> changeProfilePhoto(MultipartFile photo, HttpSession session) throws IOException {
        try {
            var userProfileId = Integer.parseInt(session.getAttribute("user_profile_id").toString());
            var userId = session.getAttribute("user_id").toString();
            var user = this.userProfileService.updateProfilePhoto(photo, userProfileId, userId);
            var code = Integer.parseInt(user.get("code").toString());
            var photoName = user.get("data").toString();
            if(code == 200){
                user.put("thumbnail",  "/images/users/user"+userId+"/"+photoName);
                session.setAttribute("thumbnail", "/images/users/user"+userId+"/"+photoName);
                return new ResponseEntity<Map<String, Object>>(user, HttpStatus.OK);
            }
            return new ResponseEntity<Map<String, Object>>(user, HttpStatus.NOT_MODIFIED);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String,Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
    }

    
}
