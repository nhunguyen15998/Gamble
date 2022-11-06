package com.futech.entertainment.packages.settings.controllers.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.futech.entertainment.packages.settings.services.interfaces.UserConfigServiceInterface;
import com.futech.entertainment.packages.settings.utils.ConfigHelpers;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Controller
public class ConfigsController {
    
    @Autowired
    private UserServiceInterface userServiceInterface;
    @Autowired
    private UserConfigServiceInterface userConfigServiceInterface;

    @GetMapping("/user/configs")
    public ResponseEntity<Map<String, Object>> getConfigs(HttpSession session){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            var userId = session.getAttribute("user_id").toString();
            var userConfigs = this.userConfigServiceInterface.getClientConfigs(userId);
            if(userConfigs != null){
                var config = userConfigs.get("config_string").toString();
                System.out.println(config);
                obj.put("code", 200);
                obj.put("configs", config);
                return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
            }
            obj.put("code", 404);
            obj.put("message", "No configs found");
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            obj.put("code", 500);
            obj.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/user/configs/update")
    public ResponseEntity<Map<String, Object>> saveConfigs(@RequestParam String configs, HttpSession session){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            var userConfigId = Integer.parseInt(session.getAttribute("user_config_id").toString());
            var userConfigString = session.getAttribute("user_config_string").toString();

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
                session.setAttribute("user_config_string", configs);
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
    public ResponseEntity<Map<String, Object>> saveConfigsWithPassword(@RequestParam String data, HttpSession session){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            JsonObject jo = JsonParser.parseString(data).getAsJsonObject();
            var password = jo.get("require_password").getAsString();
            var configs = jo.get("configs").toString();
            var userConfigId = Integer.parseInt(session.getAttribute("user_config_id").toString());

            //verify pass
            var phone = session.getAttribute("phone").toString();
            String[] selects = {"users.*, "+
            "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
            "user_profiles.birth, user_profiles.gender"};
            var user = this.userServiceInterface.getUserByPhone(selects, phone);
            if(user == null){
                obj.put("code", 404);
                obj.put("message", "Not found");
                return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
            }
            obj = this.userServiceInterface.verifyPassword(password, user.get("hash_password").toString());
            if(Integer.parseInt(obj.get("code").toString()) != 200){
                return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
            }
            obj.clear();

            var savedConfigs = this.userConfigServiceInterface.updateClientConfigs(userConfigId, configs);
            if(savedConfigs){
                obj.put("code", 200);
                obj.put("message", "Successfully updated configs");
                session.setAttribute("user_config_string", configs);
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

}
