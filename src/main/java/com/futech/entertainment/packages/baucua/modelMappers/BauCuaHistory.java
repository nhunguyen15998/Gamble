package com.futech.entertainment.packages.baucua.modelMappers;

import java.time.LocalDateTime;

public class BauCuaHistory {
    
    public BauCuaHistory(Integer game_id, String results, Integer user_qty, String room, Integer room_host,
            LocalDateTime created_at) {
        this.game_id = game_id;
        this.results = results;
        this.user_qty = user_qty;
        this.room = room;
        this.room_host = room_host;
        this.created_at = created_at;
    }
    private Integer id;
    private Integer game_id;
    private String results;
    private Integer user_qty;
    private String room;
    private Integer room_host;
    private LocalDateTime created_at;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGame_id() {
        return game_id;
    }
    public void setGame_id(Integer game_id) {
        this.game_id = game_id;
    }

    public String getResults() {
        return results;
    }
    public void setResults(String results) {
        this.results = results;
    }

    public Integer getUser_qty() {
        return user_qty;
    }
    public void setUser_qty(Integer user_qty) {
        this.user_qty = user_qty;
    }

    public String getRoom() {
        return room;
    }
    public void setRoom(String room) {
        this.room = room;
    }

    public Integer getRoom_host() {
        return room_host;
    }
    public void setRoom_host(Integer room_host) {
        this.room_host = room_host;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }
    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}
