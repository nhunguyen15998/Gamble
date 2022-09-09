package com.futech.entertainment.packages.games.repositories.interfaces;

import org.springframework.stereotype.Component;

import com.futech.entertainment.packages.core.repositories.interfaces.BaseRepositoryInterface;
import com.futech.entertainment.packages.games.models.GameHistory;

@Component
public interface GameHistoryRepositoryInterface extends BaseRepositoryInterface<GameHistory>{
    
}
