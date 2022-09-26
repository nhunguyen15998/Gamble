package com.futech.entertainment.packages.settings.services.interfaces;

import java.util.Map;

import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.settings.models.UserConfig;

public interface UserConfigServiceInterface extends BaseServiceInterface<UserConfig>{
    public Map<String, Object> getClientConfigs(String userId);
    public UserConfig createClientConfigs(UserConfig userConfig);
    public boolean updateClientConfigs(int userConfigId, String configs);
}
