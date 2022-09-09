package com.futech.entertainment.packages.wallets.controllers.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;

@RestController
@RequestMapping(path = "/api")
public class WalletsController {
    
    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;

    @GetMapping("/user/wallet/check-balance")
    public ResponseEntity<Map<String, Object>> checkUserBalance(@RequestParam String bet, HttpSession session){
        try {
            var userId = session.getAttribute("user_id").toString();
            Map<String, Object> obj = this.userWalletServiceInterface.checkUserBalance(userId, bet);
            return new ResponseEntity<Map<String, Object>>(obj, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String,Object>();
            err.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(err, HttpStatus.BAD_REQUEST);
        }
    }

}
