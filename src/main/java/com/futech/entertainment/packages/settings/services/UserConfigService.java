package com.futech.entertainment.packages.settings.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.settings.models.UserConfig;
import com.futech.entertainment.packages.settings.services.interfaces.UserConfigServiceInterface;

@Service
public class UserConfigService extends BaseService<UserConfig> implements UserConfigServiceInterface{

    //crud
    public Map<String, Object> getClientConfigs(String userId){
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "user_id", "=", userId, ""));
            return this.getFirstBy(null, conditions, null); 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UserConfig createClientConfigs(UserConfig userConfig){
        try {
            return this.create(userConfig);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //save client configs
    public boolean updateClientConfigs(int userConfigId, String configs){
        try {
            UserConfig userConfig = new UserConfig();
            userConfig.setId(userConfigId);
            userConfig.setconfig_string(configs);
            return this.update(userConfig);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //end crud

}
