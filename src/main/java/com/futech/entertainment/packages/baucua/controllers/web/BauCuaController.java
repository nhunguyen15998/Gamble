package com.futech.entertainment.packages.baucua.controllers.web;

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

import com.futech.entertainment.packages.games.services.interfaces.GameServiceInterface;
import com.futech.entertainment.packages.games.utils.GameHelpers;
import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;
import com.futech.entertainment.packages.wheels.services.interfaces.WheelServiceInterface;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Controller
public class BauCuaController {

    @Autowired
    private GameServiceInterface gameServiceInterface;
    @Autowired
    private WheelServiceInterface wheelServiceInterface;
    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;
    
    @PostMapping("/games/baucua/authentication")
    public ResponseEntity<Map<String, Object>> wheelGame(Model model, HttpSession session){
        Map<String, Object> obj = new HashMap<String,Object>();
        var userId = session.getAttribute("user_id");
        if(userId == null){
            obj.put("code", 401);
            obj.put("message", "Please sign in to play game");
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
        }
        obj.put("code", 200);
        obj.put("message", "/games/baucua");
        return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
    }

    @GetMapping("/games/baucua")
    public String wheel(){
        return "games/baucua/index";
    }


    @PostMapping("/baucua/result")
    public ResponseEntity<Map<String, Object>> returnWheelResults(@RequestParam String bet, HttpSession session) {
        Map<String, Object> nums = new HashMap<String,Object>();
        try {
            var userId = session.getAttribute("user_id").toString();
            JsonObject jo = JsonParser.parseString(bet).getAsJsonObject();
            var betAmount = jo.get("betAmount").getAsString();
            var isPartialBet = jo.get("isPartialBet").getAsBoolean();
            
            nums = this.wheelServiceInterface.createGameWheelUserHistory(betAmount, Integer.parseInt(userId), isPartialBet);
            if(Integer.parseInt(nums.get("code").toString()) == 200){
                var histories = this.wheelServiceInterface.getUserGameWheelHistory(userId);
                nums.put("histories", histories);
            }
            return new ResponseEntity<Map<String, Object>>(nums, HttpStatus.OK);
        } catch (Exception e) {
            nums.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(nums, HttpStatus.BAD_REQUEST);
        }
    }
}
