package com.futech.entertainment.packages.baucua.controllers.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.futech.entertainment.packages.baucua.modelMappers.BauCuaHistory;
import com.futech.entertainment.packages.baucua.modelMappers.BauCuaUserHistory;
import com.futech.entertainment.packages.baucua.services.interfaces.BauCuaHistoryServiceInterface;
import com.futech.entertainment.packages.baucua.services.interfaces.BauCuaUserHistoryServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.games.services.GameHistoryUserService;
import com.futech.entertainment.packages.wallets.models.UserWallet;
import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;
import com.futech.entertainment.packages.wheels.services.interfaces.WheelServiceInterface;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Controller
public class BauCuaController {

    @Autowired
    private BauCuaHistoryServiceInterface historyServiceInterface;
    @Autowired
    private BauCuaUserHistoryServiceInterface userHistoryServiceInterface;
    @Autowired
    private WheelServiceInterface wheelServiceInterface;
    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;
    @Autowired GameHistoryUserService gameHistoryUserService;
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
    @PostMapping("/saveResults")
    public @ResponseBody Boolean SaveResults( Double totalPointBet,Double totalPoint, Double received, String bet, String result, HttpSession session){
      try {
        var bHistory = historyServiceInterface.createBauCuaHistory(new BauCuaHistory(2, "bet: "+bet+", result: "+result, 1, result, null, LocalDateTime.now()));                            // 2 drawn=== 1 win === 0 lose
        var createUserHistory = userHistoryServiceInterface.createBauCuaHistory(new BauCuaUserHistory(bHistory.getId(), Integer.parseInt(session.getAttribute("user_id").toString()), totalPointBet, received, received-totalPointBet==0?2:(received-totalPointBet<0?0:1)));
       List<DataMapper> cond = new ArrayList<DataMapper>();
       cond.add(DataMapper.getInstance("", "h.created_at", ">=", "2022-11-22", ""));
        gameHistoryUserService.getGameHistoryByEachUser(cond);
        if(bHistory!=null && createUserHistory ){
            var old = userWalletServiceInterface.getWalletByUser(session.getAttribute("user_id").toString());
            UserWallet u = new UserWallet();
            u.setId(Integer.parseInt(old.get("id").toString()));
            u.setpre_amount(Double.parseDouble(old.get("cur_amount").toString()));
            u.setcur_amount(totalPoint);
            userWalletServiceInterface.update(u);
        }
        System.out.println("\nbet:"+totalPointBet+" ====== received:"+received+"====== total:"+totalPoint+"\nbetObject:"+bet+"===== result:"+result);
        return true;
      } catch (Exception e) {
        // TODO: handle exception
        return false;
      }  
    }

    @GetMapping("/getBalance")
    public @ResponseBody Double getNumberArrays(HttpSession session){
        try {
            var userId = session.getAttribute("user_id").toString();
            var balance = this.userWalletServiceInterface.getUserBalance(userId);
            return balance;
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<String, Object>();
            err.put("message", e.getMessage());
            return null;
        }
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
