package com.futech.entertainment.packages.games.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.games.models.Game;
import com.futech.entertainment.packages.games.services.interfaces.GameServiceInterface;

@Service
public class GameService extends BaseService<Game> implements GameServiceInterface {

    @Override
    public Game createGame(Game game) {
        try {
            return this.create(game);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, Object> getGameByCode(String code) {
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "code", "=", code, ""));
            return this.getFirstBy(null, conditions, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
