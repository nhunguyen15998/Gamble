package com.futech.entertainment.packages.games.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.games.models.GameHistoryUser;
import com.futech.entertainment.packages.games.services.interfaces.GameHistoryUserServiceInterface;

@Service
public class GameHistoryUserService extends BaseService<GameHistoryUser> implements GameHistoryUserServiceInterface{

}
