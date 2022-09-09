package com.futech.entertainment.packages.games.repositories;

import org.springframework.stereotype.Repository;

import com.futech.entertainment.packages.core.repositories.BaseRepository;
import com.futech.entertainment.packages.games.models.Game;
import com.futech.entertainment.packages.games.repositories.interfaces.GameRepositoryInterface;

@Repository
public class GameRepository extends BaseRepository<Game> implements GameRepositoryInterface {
 
    public GameRepository(){
        super();
        this.setModel(new Game());
    }

}
