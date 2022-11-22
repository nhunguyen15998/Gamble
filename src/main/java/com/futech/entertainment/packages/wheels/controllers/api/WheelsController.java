package com.futech.entertainment.packages.wheels.controllers.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.futech.entertainment.packages.games.services.interfaces.GameServiceInterface;
import com.futech.entertainment.packages.games.utils.GameHelpers;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;
import com.futech.entertainment.packages.wheels.services.interfaces.WheelServiceInterface;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Validated
@RestController
@RequestMapping(path = "/api")
public class WheelsController {

    @Autowired
    private GameServiceInterface gameServiceInterface;
    @Autowired
    private WheelServiceInterface wheelServiceInterface;
    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;
    @Autowired
    private UserServiceInterface userServiceInterface;
    
    @GetMapping("/getSliceArrays")
    public ResponseEntity<Map<String, Object>> getNumberArrays(
        @Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers
    ){
        try {
            var verifiedToken = headers.get("auth").toString();
            var currentUserId = this.userServiceInterface.getUserByToken(new String[]{"users.id as user_id"}, verifiedToken).get("user_id").toString();
            Map<String, Object> nums = new HashMap<String, Object>();
            var wheel = this.gameServiceInterface.getGameByCode(GameHelpers.WHEEL_CODE);
            nums = this.wheelServiceInterface.getGameWheelConfigs(wheel);
            var histories = this.wheelServiceInterface.getUserGameWheelHistory(currentUserId);
            nums.put("histories", histories);
            var balance = this.userWalletServiceInterface.getUserBalance(currentUserId);
            nums.put("balance", balance);
            return new ResponseEntity<Map<String, Object>>(nums, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String, Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/wheels/result")
    public ResponseEntity<Map<String, Object>> returnWheelResults(
            @Authentication(message = "Unauthenticated") @RequestHeader Map<String, String> headers,
            @RequestBody String bet
    ) {
        Map<String, Object> nums = new HashMap<String,Object>();
        try {
            var verifiedToken = headers.get("auth").toString();
            var currentUserId = this.userServiceInterface.getUserByToken(new String[]{"users.id as user_id"}, verifiedToken).get("user_id").toString();
            JsonObject jo = JsonParser.parseString(bet).getAsJsonObject();
            var betAmount = jo.get("betAmount").getAsString();
            var isPartialBet = jo.get("isPartialBet").getAsBoolean();
            
            nums = this.wheelServiceInterface.createGameWheelUserHistory(betAmount, Integer.parseInt(currentUserId), isPartialBet);
            if(Integer.parseInt(nums.get("code").toString()) == 200){
                var histories = this.wheelServiceInterface.getUserGameWheelHistory(currentUserId);
                nums.put("histories", histories);
            }
            return new ResponseEntity<Map<String, Object>>(nums, HttpStatus.OK);
        } catch (Exception e) {
            nums.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(nums, HttpStatus.BAD_REQUEST);
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
