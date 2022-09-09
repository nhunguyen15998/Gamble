package com.futech.entertainment.packages.games.repositories;

import org.springframework.stereotype.Repository;

import com.futech.entertainment.packages.core.repositories.BaseRepository;
import com.futech.entertainment.packages.games.models.GameHistory;
import com.futech.entertainment.packages.games.repositories.interfaces.GameHistoryRepositoryInterface;

@Repository
public class GameHistoryRepository extends BaseRepository<GameHistory> implements GameHistoryRepositoryInterface{
    
    public GameHistoryRepository(){
        super();
        this.setModel(new GameHistory());
    }
}
