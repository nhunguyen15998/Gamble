package com.futech.entertainment.packages.games.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.games.models.GameHistory;
import com.futech.entertainment.packages.games.services.interfaces.GameHistoryServiceInterface;

@Service
public class GameHistoryService extends BaseService<GameHistory> implements GameHistoryServiceInterface{
    
    public GameHistory createGameWheelHistory(String results){
        try {
            GameHistory gameHistory = new GameHistory();
            gameHistory.setGame_id(1);
            gameHistory.setCreated_at(LocalDateTime.now());
            gameHistory.setResults(results);
            gameHistory.setUser_qty(1);
            return this.create(gameHistory);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, Object>> getUserGameHistoryByUser(String[] selects, List<DataMapper> conditions, String orderBy, String[] limit){
        try {
            List<JoinCondition> joins = new ArrayList<JoinCondition>();
            joins.add(JoinCondition.getInstance("join", "games", DataMapper.getInstance("", "games.id", "=", "game_histories.game_id", "")));
            joins.add(JoinCondition.getInstance("join", "game_history_users", DataMapper.getInstance("", "game_histories.id", "=", "game_history_users.game_history_id", "")));
            return this.getAll(selects, conditions, joins, null, orderBy, limit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
