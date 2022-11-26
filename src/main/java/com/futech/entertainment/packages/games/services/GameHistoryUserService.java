package com.futech.entertainment.packages.games.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.games.models.GameHistoryUser;
import com.futech.entertainment.packages.games.services.interfaces.GameHistoryUserServiceInterface;

@Service
public class GameHistoryUserService extends BaseService<GameHistoryUser> implements GameHistoryUserServiceInterface{
    public Map<Object,Object> map = null;
    public List<Map<Object,Object>> dataPoints1 = new ArrayList<Map<Object,Object>>();
   
    @Override
    public List<Map<String, Object>> getGameHistoryByEachUser(List<DataMapper> conditions) {
        List<JoinCondition> join = new ArrayList<JoinCondition>();
        join.add(JoinCondition.getInstance("join", "game_histories h", DataMapper.getInstance("", "game_history_id", "=", "h.id", "")));
        join.add(JoinCondition.getInstance("join", "users u", DataMapper.getInstance("", "user_id", "=", "u.id", "")));
        var ls = this.getAll(new String[]{"game_history_users.*"}, conditions, join, null, null, null);
       
        return null;
    }
    
    @Override
    public List<Map<Object, Object>> getStatistical(String from, String to, String gameid, Boolean total) {
        try {
            
            dataPoints1.clear();
            String[] selects = new String[]{"ROUND(sum(bet_amount)-sum(received_amount), 2) as 'res',CAST(h.created_at AS DATE) as dateCreated"};

            
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "CAST(h.created_at as DATE) ", "between", from, " and '"+to+"'"));
            if(Integer.parseInt(gameid)!=-1)conditions.add(DataMapper.getInstance(" and", "h.game_id", "=", gameid, ""));
            List<JoinCondition> join = new ArrayList<JoinCondition>();
            join.add(JoinCondition.getInstance("join", "game_histories h", DataMapper.getInstance("", "game_history_id", "=", "h.id", "")));
            join.add(JoinCondition.getInstance("join", "users u", DataMapper.getInstance("", "user_id", "=", "u.id", "")));
            join.add(JoinCondition.getInstance("join", "user_profiles p", DataMapper.getInstance("", "p.user_id", "=", "u.id", "")));
            var ls = this.getAll(selects, conditions, join, !total?new String[]{"CAST(h.created_at AS DATE)"}:null, "dateCreated asc", null);
            
            for (Map<String,Object> item : ls) {
		    map = new HashMap<Object,Object>(); map.put("x", item.get("dateCreated")); map.put("y", item.get("res"));dataPoints1.add(map);
                
            }
            return dataPoints1;
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }

    @Override
    public List<Map<String, Object>> getUserStatistical(String from, String to, String gameid,int sort,List<DataMapper> cond, String [] limit) {
       try {
        String[] selects = new String[]{"concat(p.first_name,' ',p.last_name) as 'userName',game_history_users.user_id as 'userID',u.is_admin as userType,ROUND(sum(received_amount)-sum(bet_amount), 2) as 'res',ROUND(sum(received_amount), 2), ROUND(sum(bet_amount), 2) ,CAST(h.created_at AS DATE)"};
        String[] groupBy = new String[]{"game_history_users.user_id"};
        
        List<JoinCondition> join = new ArrayList<JoinCondition>();
        join.add(JoinCondition.getInstance("join", "game_histories h", DataMapper.getInstance("", "game_history_id", "=", "h.id", "")));
        join.add(JoinCondition.getInstance("join", "users u", DataMapper.getInstance("", "user_id", "=", "u.id", "")));
        join.add(JoinCondition.getInstance("join", "user_profiles p", DataMapper.getInstance("", "p.user_id", "=", "u.id", "")));
        var ls = this.getAll(selects, cond, join, groupBy, "ROUND(sum(received_amount)-sum(bet_amount), 2) "+(sort==1?"desc":"asc"), limit);
        
        return ls;
       } catch (Exception e) {
        // TODO: handle exception
        return null;
       }
    }
}
