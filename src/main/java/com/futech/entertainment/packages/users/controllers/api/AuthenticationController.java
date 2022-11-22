package com.futech.entertainment.packages.users.controllers.api;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futech.entertainment.packages.users.modelMappers.SignInMapper;
import com.futech.entertainment.packages.users.modelMappers.SignUpMapper;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Validated
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
            var password = signUpMapper.getplain_password();
            var confirmedPassword = signUpMapper.getconfirm_password();
            if(!password.equals(confirmedPassword)){
                job.addProperty("code", 400);
                job.addProperty("message", "Password do not match");
                return ResponseEntity.ok(gson.toJson(job));
            }
            var created = this.userServiceInterface.createUserSignUp(signUpMapper);
            if(created){
                job.addProperty("code", 200);
                job.addProperty("message", "user created");
                return ResponseEntity.ok(gson.toJson(job));
            }
            job.addProperty("code", 400);
            job.addProperty("message", "cannot create user");
        } catch (Exception e) {
            e.printStackTrace();
            job.addProperty("code", 500);
            job.addProperty("message", e.getMessage());
        }
        return ResponseEntity.ok(gson.toJson(job));
    }

    @PostMapping("/user/authenticate")
	public ResponseEntity<Map<String, Object>> authentication(@Valid @RequestBody SignInMapper signInMapper, BindingResult bindingResult) {
        Map<String, Object> results = new HashMap<String, Object>();
        try {
            if(bindingResult.hasErrors()){
                var errors = bindingResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
                results.put("code", 400);
                results.put("message", errors.get(0));
                return new ResponseEntity<Map<String, Object>>(results, HttpStatus.OK);
            }
            var phone = signInMapper.getPhone();
            var password = signInMapper.getplain_password();
            String[] selects = {"users.*, "+
            "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
            "user_profiles.birth, user_profiles.gender"};
            var user = this.userServiceInterface.getUserByPhone(selects, phone);
            var hashPassword = user.get("hash_password").toString();
            results = this.userServiceInterface.verifyPassword(password, hashPassword);
            var code = Integer.parseInt(results.get("code").toString());
            if(code == 200){
                results = this.userServiceInterface.authenticate(user);
            }
            return new ResponseEntity<Map<String, Object>>(results, HttpStatus.OK);
        } catch (Exception e) {
            results.put("code", 500);
            results.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(results, HttpStatus.INTERNAL_SERVER_ERROR);
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
