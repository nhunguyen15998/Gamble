package com.futech.entertainment.packages.wheels.services.interfaces;

import java.util.List;
import java.util.Map;

import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.games.models.Game;

public interface WheelServiceInterface extends BaseServiceInterface<Game>{
    public Map<String, Object> getGameWheelConfigs(Map<String, Object> wheel);
    public Map<String, Object> createGameWheelUserHistory(String betAmount, int userId);
    public List<Map<String, Object>> getUserGameWheelHistory(String userId);
    public void playBackgroundMusic(String sound);
}
