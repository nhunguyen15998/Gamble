package com.futech.entertainment.packages.baucua.services;

import java.io.File;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.baucua.services.interfaces.BauCuaServiceInterface;
import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.games.modelMappers.WheelMapper;
import com.futech.entertainment.packages.games.models.Game;
import com.futech.entertainment.packages.games.models.GameHistoryUser;
import com.futech.entertainment.packages.games.services.interfaces.GameHistoryServiceInterface;
import com.futech.entertainment.packages.games.services.interfaces.GameHistoryUserServiceInterface;
import com.futech.entertainment.packages.games.services.interfaces.GameServiceInterface;
import com.futech.entertainment.packages.games.utils.GameHelpers;
import com.futech.entertainment.packages.wallets.models.UserWallet;
import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;
import com.futech.entertainment.packages.wheels.services.interfaces.WheelServiceInterface;
import com.futech.entertainment.packages.wheels.utils.WheelHelpers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class BauCuaService extends BaseService<Game> implements BauCuaServiceInterface{
    
}
