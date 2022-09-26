package com.futech.entertainment.packages.settings.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.settings.models.Config;
import com.futech.entertainment.packages.settings.services.interfaces.ConfigServiceInterface;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class ConfigService extends BaseService<Config> implements ConfigServiceInterface{
    
    public JsonElement getConfigStringElement(Integer type, String name, String configStringElement){
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "type", "=", String.valueOf(type), ""));
            conditions.add(DataMapper.getInstance("and", "name", "=", name, ""));
            var config = this.getFirstBy(null, conditions, null);
            JsonObject cf = JsonParser.parseString(config.get("config_string").toString()).getAsJsonObject();
            return cf.get(configStringElement);
        } catch (Exception e) {
            e.printStackTrace();;
            return null;
        }
    }

    public Map<String, Object> getConfig(Integer type, String name){
        try {
            List<DataMapper> conditions = new ArrayList<DataMapper>();
            conditions.add(DataMapper.getInstance("", "type", "=", String.valueOf(type), ""));
            conditions.add(DataMapper.getInstance("and", "name", "=", name, ""));
            var config = this.getFirstBy(null, conditions, null);
            System.out.println(config);
            return config;
        } catch (Exception e) {
            e.printStackTrace();;
            return null;
        }
    }

}
