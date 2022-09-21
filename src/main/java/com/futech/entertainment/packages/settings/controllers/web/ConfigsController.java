package com.futech.entertainment.packages.settings.controllers.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futech.entertainment.packages.settings.services.interfaces.ConfigServiceInterface;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Controller
public class ConfigsController {
    
    @Autowired
    private ConfigServiceInterface configServiceInterface;

    @PostMapping("/configs/update")
    public ResponseEntity<String> saveConfigs(@RequestParam String configs,  
                                                HttpSession session, HttpServletRequest req){
        //Map<String, Object> obj = new HashMap<String, Object>();
        try {
            JsonObject jo = JsonParser.parseString(configs).getAsJsonObject();
            System.out.println(jo.get("change_password"));
            System.out.println(jo.get("withdraw_password"));
            //var savedConfigs = this.configServiceInterface.
            
            return new ResponseEntity<String>(configs, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            // Map<String, Object> err = new HashMap<String,Object>();
            // err.put("message", e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    } 

}
