package com.futech.entertainment.packages.baucua.services.interfaces;

import java.util.List;
import java.util.Map;

import com.futech.entertainment.packages.baucua.modelMappers.BauCuaHistory;
import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.games.models.GameHistory;

public interface BauCuaHistoryServiceInterface extends BaseServiceInterface<GameHistory>{
    public GameHistory createBauCuaHistory(BauCuaHistory bHistory);
}
