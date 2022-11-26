package com.futech.entertainment.packages.games.services.interfaces;

import java.util.List;
import java.util.Map;

import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.games.models.GameHistoryUser;

public interface GameHistoryUserServiceInterface extends BaseServiceInterface<GameHistoryUser>{
    public List<Map<String, Object>> getGameHistoryByEachUser( List<DataMapper> conditions);
    public  List<Map<Object,Object>> getStatistical(String from, String to, String gameid, Boolean total);
    public  List<Map<String,Object>> getUserStatistical(String from, String to, String gameid,int sort,List<DataMapper> cond, String [] limit);

}
