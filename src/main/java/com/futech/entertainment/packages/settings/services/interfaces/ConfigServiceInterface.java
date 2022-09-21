package com.futech.entertainment.packages.settings.services.interfaces;

import java.util.Map;

import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.settings.models.Config;
import com.google.gson.JsonElement;

public interface ConfigServiceInterface extends BaseServiceInterface<Config>{
    public JsonElement getConfigStringElement(Integer type, String name, String configStringElement);
    public Map<String, Object> getConfig(Integer type, String name);
}
