package com.futech.entertainment.packages.settings.models;

import org.springframework.beans.factory.annotation.Qualifier;

import com.futech.entertainment.packages.core.models.BaseModel;

@Qualifier("UserConfig")
public class UserConfig extends BaseModel {

    public UserConfig() {}

    private String table = "user_configs";

    private String[] columns = { "id", "type", "config_string", "user_id" };

    public String getTable() {
        return this.table;
    }

    public String[] getColumns() {
        return this.columns;
    }

    private Integer id;
    private Integer type;
    private String config_string;
    private Integer user_id;

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

    public Integer getuser_id() {
        return user_id;
    }
    public void setuser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
