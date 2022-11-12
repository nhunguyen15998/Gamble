package com.futech.entertainment.packages.settings.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ConfigHelpers {
    public static final int TYPE_CURRENCIES = 0; 

    public static final int TYPE_USER_GENERAL_SETTINGS = 1;
    public static final String USER_DEFAULT_GENERAL_CONFIGS = 
        "{\"change_password\":0,\"withdraw_password\":0,\"transfer_password\":0,\"setting_password\":0}";
        //insert into user_configs(user_id, type, config_string) values(4, 1, '{"change_password":0,"withdraw_password":0,"transfer_password":0}');
    public static final int TYPE_ADMIN_SETTINGS = 2;   

    public static final int IS_OFF = 0;   
    public static final int IS_ON = 1;   

    public static Integer getSettingValueByKey(String userConfigString, String key){
        JsonObject configStr = JsonParser.parseString(userConfigString).getAsJsonObject();
        return Integer.parseInt(configStr.get(key).toString());
    }

}
