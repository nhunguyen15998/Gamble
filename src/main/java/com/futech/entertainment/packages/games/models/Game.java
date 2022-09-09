package com.futech.entertainment.packages.games.models;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;

import com.futech.entertainment.packages.core.models.BaseModel;

@Qualifier("Game")
public class Game extends BaseModel {
    public Game() {}

    private String table = "games";

    private String[] columns = { "id", "code", "name", "created_at", "status", "configs" };

    public String getTable() {
        return this.table;
    }

    public String[] getColumns() {
        return this.columns;
    }

    private Integer id;
    private String code;
    private String name;
    private LocalDateTime created_at;
    private Integer status;
    private String configs;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }
    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getConfigs() {
        return configs;
    }
    public void setConfigs(String configs) {
        this.configs = configs;
    }
}
