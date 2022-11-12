package com.futech.entertainment.packages.baucua.services;
import org.springframework.stereotype.Service;
import com.futech.entertainment.packages.baucua.modelMappers.BauCuaUserHistory;
import com.futech.entertainment.packages.baucua.services.interfaces.BauCuaUserHistoryServiceInterface;
import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.games.models.GameHistoryUser;
@Service
public class BauCuaUserHistoryService extends BaseService<GameHistoryUser> implements BauCuaUserHistoryServiceInterface{

    public boolean createBauCuaHistory(BauCuaUserHistory bHistory){
        try {
            GameHistoryUser newGameUserHistory = new GameHistoryUser();
            newGameUserHistory.setGame_history_id(bHistory.getGame_history_id());
            newGameUserHistory.setReceived_amount(bHistory.getReceived_amount());
            newGameUserHistory.setStatus(bHistory.getStatus());
            newGameUserHistory.setBet_amount(bHistory.getBet_amount());
            newGameUserHistory.setUser_id(bHistory.getUser_id());
            this.create(newGameUserHistory);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
