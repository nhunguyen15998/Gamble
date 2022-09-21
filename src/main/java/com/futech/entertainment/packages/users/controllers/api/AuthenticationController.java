package com.futech.entertainment.packages.users.controllers.api;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futech.entertainment.packages.users.modelMappers.SignInMapper;
import com.futech.entertainment.packages.users.modelMappers.SignUpMapper;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
@RequestMapping(path="/api") 
public class AuthenticationController {
    
    @Autowired
    private UserServiceInterface userServiceInterface;

    @PostMapping("/user/register")
	public ResponseEntity<String> register(@Valid @RequestBody SignUpMapper signUpMapper) {
        Gson gson = new Gson();
        JsonObject job = new JsonObject();
        try {
            var created = this.userServiceInterface.createUserSignUp(signUpMapper);
            if(created){
                job.addProperty("code", 200);
                job.addProperty("msg", "user created");
                return ResponseEntity.ok(gson.toJson(job));
            }
            job.addProperty("code", 400);
            job.addProperty("msg", "cannot create user");
        } catch (Exception e) {
            e.printStackTrace();
            job.addProperty("code", 500);
            job.addProperty("msg", e.getMessage());
        }
        return ResponseEntity.ok(gson.toJson(job));
    }

    @PostMapping("/user/authenticate")
	public ResponseEntity<Map<String, Object>> authentication(@Valid @RequestBody SignInMapper signInMapper) {
        Map<String, Object> results = new HashMap<String, Object>();
        try {
            var phone = signInMapper.getPhone();
            String[] selects = {"users.id as user_id, users.*, "+
            "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
            "user_profiles.birth, user_profiles.gender"};
            var user = this.userServiceInterface.getUserByPhone(selects, phone);
            results = this.userServiceInterface.authenticate(user);
            return new ResponseEntity<Map<String, Object>>(results, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            results.put("code", 500);
            results.put("msg", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(results, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
