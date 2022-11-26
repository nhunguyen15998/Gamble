package com.futech.entertainment.packages.games.services.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.games.models.Game;

public interface GameServiceInterface extends BaseServiceInterface<Game>{
    public Game createGame(Game game);
    public Map<String, Object> getGameByCode(String code);
}
