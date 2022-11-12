package com.futech.entertainment.packages.baucua.services.interfaces;

import java.util.List;
import java.util.Map;

import com.futech.entertainment.packages.baucua.modelMappers.BauCuaHistory;
import com.futech.entertainment.packages.baucua.modelMappers.BauCuaUserHistory;
import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.games.models.GameHistoryUser;

public interface BauCuaUserHistoryServiceInterface extends BaseServiceInterface<GameHistoryUser>{
    public boolean createBauCuaHistory(BauCuaUserHistory bHistory);
}
