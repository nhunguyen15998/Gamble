package com.futech.entertainment.packages.baucua.services;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.baucua.modelMappers.BauCuaHistory;
import com.futech.entertainment.packages.baucua.services.interfaces.BauCuaHistoryServiceInterface;
import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.games.models.GameHistory;
@Service
public class BauCuaHistoryService extends BaseService<GameHistory> implements BauCuaHistoryServiceInterface{

    public GameHistory createBauCuaHistory(BauCuaHistory bHistory){
        try {
            GameHistory newGameHistory = new GameHistory();
            newGameHistory.setGame_id(2);
            newGameHistory.setCreated_at(LocalDateTime.now());
            newGameHistory.setResults(bHistory.getResults());
            newGameHistory.setUser_qty(1);
            this.create(newGameHistory);
            return newGameHistory;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
