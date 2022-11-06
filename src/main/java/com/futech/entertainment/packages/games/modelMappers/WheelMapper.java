package com.futech.entertainment.packages.games.modelMappers;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

public class WheelMapper {
    
    public Integer id;
    @NotBlank(message = "Code is required.")
    public String code;
    @NotBlank(message = "Name is required.")
    public String name;
    public LocalDateTime created_at;
    public Integer status;
    public String configs;

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
