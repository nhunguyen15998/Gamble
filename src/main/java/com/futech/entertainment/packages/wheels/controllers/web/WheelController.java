package com.futech.entertainment.packages.wheels.controllers.web;

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
public class WheelController {

    @Autowired
    private GameServiceInterface gameServiceInterface;
    @Autowired
    private WheelServiceInterface wheelServiceInterface;
    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;
    
    @GetMapping("/games/wheels")
    public String wheelIndex(Model model, HttpSession session){
        var userId = session.getAttribute("user_id");
        if(userId == null){
            return "redirect:/";
        }
        return "wheels/index";
    }

    @GetMapping("/getSliceArrays")
    public ResponseEntity<Map<String, Object>> getNumberArrays(HttpSession session){
        try {
            var userId = session.getAttribute("user_id").toString();
            Map<String, Object> nums = new HashMap<String, Object>();
            var wheel = this.gameServiceInterface.getGameByCode(GameHelpers.WHEEL_CODE);
            nums = this.wheelServiceInterface.getGameWheelConfigs(wheel);
            var histories = this.wheelServiceInterface.getUserGameWheelHistory(userId);
            nums.put("histories", histories);
            var balance = this.userWalletServiceInterface.getUserBalance(userId.toString());
            nums.put("balance", balance);
            //this.wheelServiceInterface.playBackgroundMusic("story-theme.wav");
            return new ResponseEntity<Map<String, Object>>(nums, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String, Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/wheels/result")
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
