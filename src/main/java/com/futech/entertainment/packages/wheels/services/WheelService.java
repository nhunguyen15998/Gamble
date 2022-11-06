package com.futech.entertainment.packages.wheels.services;

import java.io.File;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.games.modelMappers.WheelMapper;
import com.futech.entertainment.packages.games.models.Game;
import com.futech.entertainment.packages.games.models.GameHistoryUser;
import com.futech.entertainment.packages.games.services.interfaces.GameHistoryServiceInterface;
import com.futech.entertainment.packages.games.services.interfaces.GameHistoryUserServiceInterface;
import com.futech.entertainment.packages.games.services.interfaces.GameServiceInterface;
import com.futech.entertainment.packages.games.utils.GameHelpers;
import com.futech.entertainment.packages.wallets.models.UserWallet;
import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;
import com.futech.entertainment.packages.wheels.services.interfaces.WheelServiceInterface;
import com.futech.entertainment.packages.wheels.utils.WheelHelpers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class WheelService extends BaseService<Game> implements WheelServiceInterface{
    
    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;
    @Autowired
    private GameHistoryServiceInterface gameHistoryServiceInterface;
    @Autowired
    private GameHistoryUserServiceInterface gameHistoryUserServiceInterface;
    @Autowired
    private GameServiceInterface gameServiceInterface;

    public Map<String, Object> getGameWheelConfigs(Map<String, Object> wheel){
        Map<String, Object> obj = new HashMap<String,Object>();
        try {
            var configs = wheel.get("configs").toString().split("\\\\");
            for (int i = 0; i < configs.length; i++) {
                JsonObject jo = JsonParser.parseString(configs[i]).getAsJsonObject();
                var labels = jo.get("labels").getAsJsonArray();
                var backgroundColors = jo.get("backgroundColors").getAsJsonArray();
                var textColors = jo.get("textColors").getAsJsonArray();
                WheelHelpers.toStringArray(labels);
                String[] labelArray = WheelHelpers.toStringArray(labels);
                String[] backgroundColorArray = WheelHelpers.toStringArray(backgroundColors);
                String[] textColorArray = WheelHelpers.toStringArray(textColors);
                obj.put("slices"+(i+1), labelArray);
                obj.put("colors"+(i+1), backgroundColorArray);
                obj.put("textColors"+(i+1), textColorArray);
            }
            obj.put("code", 200);
        } catch (Exception e) {
            obj.put("code", 500);
            obj.put("message", e.getMessage());
        }
        return obj;
    }

    public Map<String, Object> createGameWheelUserHistory(String betAmount, int userId, boolean isPartialBet){
        Map<String, Object> obj = new HashMap<String,Object>();
        try {
            if(betAmount == null || betAmount.toString().trim() == ""){
                obj.put("code", 400);
                obj.put("message", "You must bet first!");  
            }
            if(!betAmount.trim().matches("^[+]?[0-9]*([.][0-9]*)?$")){
                obj.put("code", 400);
                obj.put("message", "Invalid format");
                return obj;
            }

            var bet = Double.parseDouble(betAmount);
            if(bet <= 0){
                obj.put("code", 400);
                obj.put("message", "Invalid amount");  
                return obj;
            }

            //check if amount <= userwallet
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "user_id", "=", String.valueOf(userId), ""));
            var userWallet = this.userWalletServiceInterface.getFirstBy(null, conditions, null);
            var userAmount = Double.parseDouble(userWallet.get("cur_amount").toString());
            if(bet > userAmount){
                obj.put("code", 400);
                obj.put("message", "Bet amount exceeds available amount");
                return obj;
            }

            var betamount = Double.parseDouble(betAmount);
            if(!isPartialBet){
                bet = userAmount;
                betamount = userAmount;
            }

            //get configs
            var wheel = this.gameServiceInterface.getGameByCode(GameHelpers.WHEEL_CODE);
            var configs = this.getGameWheelConfigs(wheel);

            var smSize = Array.getLength(configs.get("slices1"));
            var sm = (int)Math.floor(Math.random() * smSize);
            var small = Array.get(configs.get("slices1"), sm).toString();
            bet = WheelHelpers.countBettingResult(small, bet);
            String results = String.format("%s", small);
            obj.put("sm", sm);
            
            if(small.charAt(0) == 'N'){
                var mdSize = Array.getLength(configs.get("slices2"));
                var md = (int)Math.floor(Math.random() * mdSize);
                var medium = Array.get(configs.get("slices2"), md).toString();
                bet = WheelHelpers.countBettingResult(medium, bet);
                results += String.format(",%s", medium);
                obj.put("md", md);
                obj.put("small", small);
                if(medium.charAt(0) == 'N'){
                    var lgSize = Array.getLength(configs.get("slices3"));
                    var lg = (int)Math.floor(Math.random() * lgSize);
                    var large = Array.get(configs.get("slices3"), lg).toString();
                    bet = WheelHelpers.countBettingResult(large, bet);
                    results += String.format(",%s", large);
                    obj.put("lg", lg);
                    obj.put("medium", medium);
                }
            }

            //create game history
            var gameHistory = this.gameHistoryServiceInterface.createGameWheelHistory(results);
            if(gameHistory != null){
                var gameHistoryId = gameHistory.getId();
                var status = betamount > bet ? 0 : (betamount == bet ? 2 : 1);
                GameHistoryUser gameHistoryUser = new GameHistoryUser();
                gameHistoryUser.setBet_amount(betamount);
                gameHistoryUser.setGame_history_id(gameHistoryId);
                gameHistoryUser.setUser_id(userId);
                gameHistoryUser.setStatus(status);
                gameHistoryUser.setReceived_amount(bet);
                this.gameHistoryUserServiceInterface.create(gameHistoryUser);
                switch (status) {
                    case 0:
                        obj.put("status", 0);
                        obj.put("message", "Lose");
                        break;
                    case 1:
                        obj.put("status", 1);
                        obj.put("message", "Win");
                        break;
                    case 2:
                        obj.put("status", 2);
                        obj.put("message", "Drawn");
                        break;
                }
            }

            //update user wallet
            var curAmount = Double.parseDouble(userWallet.get("cur_amount").toString());
            var updatedAmount = curAmount - betamount + bet;
            var updateWallet = new UserWallet();
            updateWallet.setId(Integer.parseInt(userWallet.get("id").toString()));
            updateWallet.setpre_amount(curAmount);
            updateWallet.setcur_amount(updatedAmount);
            this.userWalletServiceInterface.update(updateWallet);

            obj.put("code", 200);
            obj.put("balance", updatedAmount);
        } catch (Exception e) { 
            obj.put("code", 500);
            obj.put("message", e.getMessage());
        }
        return obj;
    }

    public List<Map<String, Object>> getUserGameWheelHistory(String userId){
        try {
            String[] selects = {"games.code, games.id,"+ 
                                "game_histories.id, game_histories.game_id, game_histories.results, game_histories.created_at,"+
                                "game_history_users.bet_amount, game_history_users.game_history_id, game_history_users.received_amount,"+
                                "game_history_users.id, game_history_users.status, game_history_users.user_id"};
            String orderBy = "created_at desc";
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "games.code", "=", GameHelpers.WHEEL_CODE, ""));
            conditions.add(DataMapper.getInstance("and", "game_history_users.user_id", "=", userId, ""));
            conditions.add(DataMapper.getInstance("and", "game_histories.created_at", ">=", LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toString(), ""));
            conditions.add(DataMapper.getInstance("and", "game_histories.created_at", "<=", LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toString(), ""));
            return this.gameHistoryServiceInterface.getUserGameHistoryByUser(selects, conditions, orderBy, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void playBackgroundMusic(String sound){
        try {
            var path = new File(System.getProperty("user.dir") + "/src/main/resources/static/sounds/"+sound);
            if(path.exists()){
                AudioInputStream audio = AudioSystem.getAudioInputStream(path);
                Clip clip = AudioSystem.getClip();
                clip.open(audio);
                clip.start(); 
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean updateWheel(WheelMapper wheelMapper){
        try {
            Game upGame = new Game();
            upGame.setId(wheelMapper.getId());
            upGame.setConfigs(wheelMapper.getConfigs());
            upGame.setStatus(wheelMapper.getStatus());
            var updated = this.update(upGame);

            if(updated){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
