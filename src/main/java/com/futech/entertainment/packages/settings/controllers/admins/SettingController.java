package com.futech.entertainment.packages.settings.controllers.admins;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.futech.entertainment.packages.settings.models.Config;
import com.futech.entertainment.packages.settings.services.interfaces.ConfigServiceInterface;
import com.futech.entertainment.packages.settings.utils.ConfigHelpers;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Controller
public class SettingController {

    @Autowired
    private ConfigServiceInterface configServiceInterface;

    @GetMapping("/admin/settings/all")
    public String viewSettings(Model model, HttpSession session){
        if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Integer.parseInt(session.getAttribute("is_admin").toString())==0)) return "redirect:/user/sign-in";
        session.setAttribute("title", "All | Settings");
        var configs = this.configServiceInterface.getConfigList();
        var count = configs.size();
        System.out.println(configs);
        model.addAttribute("settings", configs);
        model.addAttribute("settingQty", count);
        return "settings/settings";
    } 

    @PostMapping("/admin/settings/update")
    public ResponseEntity<Map<String, Object>> saveSettings(@RequestParam String settings){
        Map<String, Object> obj = new HashMap<String, Object>();
        try {
            JsonElement json = JsonParser.parseString(settings);
            var vnd = json.getAsJsonObject().get("vnd").getAsString();
            var btc = json.getAsJsonObject().get("btc").getAsString();
            if(!vnd.trim().matches("^[+]?[0-9]*([.][0-9]*)?$") || vnd.equals("") || 
               !btc.trim().matches("^[+]?[0-9]*([.][0-9]*)?$") || btc.equals("")){
                obj.put("code", 400);
                obj.put("message", "Invalid amount format");
                return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
            }

            var vndId = json.getAsJsonObject().get("vndID").getAsString();
            var btcId = json.getAsJsonObject().get("btcID").getAsString();
            var vndStr = ConfigHelpers.createCurrencyConfigString("USD", vnd);
            var btcStr = ConfigHelpers.createCurrencyConfigString("USD", btc);
           
            Config vndConfig = new Config();
            vndConfig.setId(Integer.parseInt(vndId));
            vndConfig.setconfig_string(vndStr);
            var savedVNDConfigs = this.configServiceInterface.updateConfig(vndConfig);

            Config btcConfig = new Config();
            btcConfig.setId(Integer.parseInt(btcId));
            btcConfig.setconfig_string(btcStr);
            var savedBTCConfigs = this.configServiceInterface.updateConfig(btcConfig);

            if(savedVNDConfigs && savedBTCConfigs){
                obj.put("code", 200);
                obj.put("message", "Successfully updated settings");
                return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
            }
            obj.put("code", 400);
            obj.put("message", "Failed to update settings");
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
        } catch (Exception e) {
            obj.put("code", 500);
            obj.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    } 
    
}
