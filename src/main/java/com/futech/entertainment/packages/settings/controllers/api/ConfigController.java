package com.futech.entertainment.packages.settings.controllers.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futech.entertainment.packages.core.middlewares.auth.interfaces.Authentication;
import com.futech.entertainment.packages.settings.services.interfaces.UserConfigServiceInterface;
import com.futech.entertainment.packages.settings.utils.ConfigHelpers;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Validated
@RestController
@RequestMapping(path = "/api")
public class ConfigController {
    
    @Autowired
    private UserServiceInterface userServiceInterface;
    @Autowired
    private UserConfigServiceInterface userConfigServiceInterface;

    @GetMapping("/user/configs")
    public ResponseEntity<Map<String, Object>> getConfigs(
            @Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers
    ){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            var verifiedToken = headers.get("auth").toString();
            var currentUserId = this.userServiceInterface.getUserByToken(new String[]{"users.id as user_id"}, verifiedToken).get("user_id").toString();
            var userConfigs = this.userConfigServiceInterface.getClientConfigs(currentUserId);
            if(userConfigs != null){
                var config = userConfigs.get("config_string").toString();
                System.out.println(config);
                obj.put("code", 200);
                obj.put("configs", config);
                return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
            }
            obj.put("code", 404);
            obj.put("message", "No configs found");
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("code", 500);
            obj.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/user/configs/update")
    public ResponseEntity<Map<String, Object>> saveConfigs(
            @Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers,
            @RequestBody String configs
    ){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            var verifiedToken = headers.get("auth").toString();
            var currentUserId = this.userServiceInterface.getUserByToken(new String[]{"users.id as user_id"}, verifiedToken).get("user_id").toString();
            var userConfigs = this.userConfigServiceInterface.getClientConfigs(currentUserId);
            var userConfigString = userConfigs.get("config_string").toString();
            var userConfigId = Integer.parseInt(userConfigs.get("id").toString());
            var settingPassword = ConfigHelpers.getSettingValueByKey(userConfigString, "setting_password");
            if(settingPassword == ConfigHelpers.IS_ON){
                obj.put("code", 406);
                obj.put("message", "Please fill in your password first");
                return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
            }
            var savedConfigs = this.userConfigServiceInterface.updateClientConfigs(userConfigId, configs);
            if(savedConfigs){
                obj.put("code", 200);
                obj.put("message", "Successfully updated configs");
                return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
            }
            obj.put("code", 304);
            obj.put("message", "Fail to update configs");
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("code", 500);
            obj.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    } 

    @PostMapping("/user/configs/updateWithPassword")
    public ResponseEntity<Map<String, Object>> saveConfigsWithPassword(
            @Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers,
            @RequestBody String data
    ){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            JsonObject jo = JsonParser.parseString(data).getAsJsonObject();
            var password = jo.get("require_password").getAsString();
            jo.remove("require_password");

            //verify pass
            var verifiedToken = headers.get("auth").toString();
            var currentUser = this.userServiceInterface.getUserByToken(new String[]{"users.id as user_id, users.hash_password"}, verifiedToken);
            var currentUserId = currentUser.get("user_id").toString();
            var hashPassword = currentUser.get("hash_password").toString();
            obj = this.userServiceInterface.verifyPassword(password, hashPassword);

            if(Integer.parseInt(obj.get("code").toString()) != 200){
                return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
            }
            obj.clear();

            var userConfigs = this.userConfigServiceInterface.getClientConfigs(currentUserId);
            var userConfigId = Integer.parseInt(userConfigs.get("id").toString());

            var savedConfigs = this.userConfigServiceInterface.updateClientConfigs(userConfigId, jo.toString());
            if(savedConfigs){
                obj.put("code", 200);
                obj.put("message", "Successfully updated configs");
                return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
            }
            obj.put("code", 304);
            obj.put("message", "Fail to update configs");
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("code", 500);
            obj.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
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
