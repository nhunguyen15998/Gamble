package com.futech.entertainment.packages.games.services.interfaces;

import java.util.List;
import java.util.Map;

import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.games.models.GameHistory;

public interface GameHistoryServiceInterface extends BaseServiceInterface<GameHistory>{
    public GameHistory createGameWheelHistory(String results);
    public List<Map<String, Object>> getUserGameHistoryByUser(String[] selects, List<DataMapper> conditions, String orderBy, String[] limit);
}
