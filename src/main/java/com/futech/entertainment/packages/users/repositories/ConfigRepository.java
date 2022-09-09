package com.futech.entertainment.packages.users.repositories;

import org.springframework.stereotype.Repository;

import com.futech.entertainment.packages.core.repositories.BaseRepository;
import com.futech.entertainment.packages.users.models.Config;
import com.futech.entertainment.packages.users.repositories.interfaces.ConfigRepositoryInterface;

@Repository
public class ConfigRepository extends BaseRepository<Config> implements ConfigRepositoryInterface{
    public ConfigRepository(){
        super();
        this.setModel(new Config());
    }
}
