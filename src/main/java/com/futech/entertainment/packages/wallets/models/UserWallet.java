package com.futech.entertainment.packages.wallets.models;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;

import com.futech.entertainment.packages.core.models.BaseModel;

@Qualifier("UserWallet")
public class UserWallet extends BaseModel {
    
    public UserWallet() {}
    
    private String table = "user_wallets";

    private String[] columns = { "id", "user_id", "pre_amount", "cur_amount", "status", "created_at" };

    public String getTable() {
        return this.table;
    }

    public String[] getColumns() {
        return this.columns;
    }

    private Integer id;
    private Integer user_id;
    private Double pre_amount;
    private Double cur_amount;
    private LocalDateTime created_at;
    private Integer status;

    public Integer getId() {
        return this.id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getuser_id() {
        return user_id;
    }
    public void setuser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Double getpre_amount() {
        return pre_amount;
    }
    public void setpre_amount(Double pre_amount) {
        this.pre_amount = pre_amount;
    }

    public Double getcur_amount() {
        return cur_amount;
    }
    public void setcur_amount(Double cur_amount) {
        this.cur_amount = cur_amount;
    }

    public LocalDateTime getcreated_at() {
        return created_at;
    }
    public void setcreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }

}

