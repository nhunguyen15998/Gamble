package com.futech.entertainment.packages.wheels.controllers.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.games.services.interfaces.GameHistoryServiceInterface;
import com.futech.entertainment.packages.games.services.interfaces.GameServiceInterface;
import com.futech.entertainment.packages.games.utils.GameHelpers;
import com.futech.entertainment.packages.wheels.services.interfaces.WheelServiceInterface;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private GameHistoryServiceInterface gameHistoryServiceInterface;
    @Autowired
    private WheelServiceInterface wheelServiceInterface;

    @GetMapping("/getSliceArrays")
    public ResponseEntity<Map<String, Object>> getNumberArrays(){
        try {
            Map<String, Object> nums = new HashMap<String, Object>();
            var wheel = this.gameServiceInterface.getGameByCode(GameHelpers.WHEEL_CODE);
            nums = this.wheelServiceInterface.getGameWheelConfigs(wheel);
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
                String[] selects = {"games.code, games.id,"+ 
                                "game_histories.id, game_histories.game_id, game_histories.results, game_histories.created_at,"+
                                "game_history_users.bet_amount, game_history_users.game_history_id, game_history_users.received_amount,"+
                                "game_history_users.id, game_history_users.status, game_history_users.user_id"};
                String orderBy = "created_at desc";
                String[] limit = {"3"};
                List<DataMapper> conditions = new ArrayList<DataMapper>();
                conditions.add(DataMapper.getInstance("", "games.code", "=", GameHelpers.WHEEL_CODE, ""));
                conditions.add(DataMapper.getInstance("and", "game_history_users.user_id", "=", userId, ""));
                conditions.add(DataMapper.getInstance("and", "game_histories.created_at", ">=", LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toString(), ""));
                conditions.add(DataMapper.getInstance("and", "game_histories.created_at", "<=", LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toString(), ""));
                var histories = this.gameHistoryServiceInterface.getUserGameHistoryByUser(selects, conditions, orderBy, limit);
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
