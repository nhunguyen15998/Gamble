package com.futech.entertainment.packages.settings.repositories;

import org.springframework.stereotype.Repository;

import com.futech.entertainment.packages.core.repositories.BaseRepository;
import com.futech.entertainment.packages.settings.models.UserConfig;
import com.futech.entertainment.packages.settings.repositories.interfaces.UserConfigRepositoryInterface;

@Repository
public class UserConfigRepository extends BaseRepository<UserConfig> implements UserConfigRepositoryInterface{
    public UserConfigRepository(){
        super();
        this.setModel(new UserConfig());
    }
}
