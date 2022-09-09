package com.futech.entertainment.packages.games.models;

import org.springframework.beans.factory.annotation.Qualifier;

import com.futech.entertainment.packages.core.models.BaseModel;

@Qualifier("GameHistoryUser")
public class GameHistoryUser extends BaseModel{

    public GameHistoryUser() {}

    private String table = "game_history_users";

    private String[] columns = { "id", "game_history_id", "user_id", "bet_amount", "received_amount", "status" };

    public String getTable() {
        return this.table;
    }

    public String[] getColumns() {
        return this.columns;
    }

    private Integer id;
    private Integer game_history_id;
    private Integer user_id;
    private Double bet_amount;
    private Double received_amount;

    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGame_history_id() {
        return game_history_id;
    }
    public void setGame_history_id(Integer game_history_id) {
        this.game_history_id = game_history_id;
    }

    public Integer getUser_id() {
        return user_id;
    }
    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Double getBet_amount() {
        return bet_amount;
    }
    public void setBet_amount(Double bet_amount) {
        this.bet_amount = bet_amount;
    }
    
    public Double getReceived_amount() {
        return received_amount;
    }
    public void setReceived_amount(Double received_amount) {
        this.received_amount = received_amount;
    }

}
