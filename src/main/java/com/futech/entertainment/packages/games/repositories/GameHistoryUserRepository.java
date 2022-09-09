package com.futech.entertainment.packages.games.repositories;

import org.springframework.stereotype.Repository;

import com.futech.entertainment.packages.core.repositories.BaseRepository;
import com.futech.entertainment.packages.games.models.GameHistoryUser;
import com.futech.entertainment.packages.games.repositories.interfaces.GameHistoryUserRepositoryInterface;

@Repository
public class GameHistoryUserRepository extends BaseRepository<GameHistoryUser> implements GameHistoryUserRepositoryInterface{
    
    public GameHistoryUserRepository(){
        super();
        this.setModel(new GameHistoryUser());
    }
}
