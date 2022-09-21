package com.futech.entertainment.packages.settings.models;

import org.springframework.beans.factory.annotation.Qualifier;

import com.futech.entertainment.packages.core.models.BaseModel;

@Qualifier("Config")
public class Config extends BaseModel {

    public Config() {}

    private String table = "configs";

    private String[] columns = { "id", "type", "config_string", "name" };

    public String getTable() {
        return this.table;
    }

    public String[] getColumns() {
        return this.columns;
    }

    private Integer id;
    private Integer type;
    private String config_string;
    private String name;

    public Integer getId() {
        return this.id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public Integer getType() {
        return this.type;
    }

    public String getconfig_string() {
        return config_string;
    }
    public void setconfig_string(String config_string) {
        this.config_string = config_string;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
