package com.futech.entertainment.packages.wheels.controllers.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futech.entertainment.packages.games.services.interfaces.GameServiceInterface;
import com.futech.entertainment.packages.games.utils.GameHelpers;
import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;
import com.futech.entertainment.packages.wheels.services.interfaces.WheelServiceInterface;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping(path = "/api")
public class WheelsController {

    @Autowired
    private GameServiceInterface gameServiceInterface;
    @Autowired
    private WheelServiceInterface wheelServiceInterface;
    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;

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
    public ResponseEntity<Map<String, Object>> returnWheelResults(@RequestParam String betAmount, HttpSession session) {
        try {
            Map<String, Object> nums = new HashMap<String,Object>();
            var userId = session.getAttribute("user_id").toString();
            nums = this.wheelServiceInterface.createGameWheelUserHistory(betAmount, Integer.parseInt(userId));
            if(Integer.parseInt(nums.get("code").toString()) == 200){
                var histories = this.wheelServiceInterface.getUserGameWheelHistory(userId);
                nums.put("histories", histories);
            }
            return new ResponseEntity<Map<String, Object>>(nums, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String,Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
    }
    
}
