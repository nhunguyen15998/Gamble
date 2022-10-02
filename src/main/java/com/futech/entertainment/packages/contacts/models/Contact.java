package com.futech.entertainment.packages.contacts.models;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;

import com.futech.entertainment.packages.core.models.BaseModel;

@Qualifier("Contact")
public class Contact extends BaseModel {
    public Contact() {}

    private String table = "contacts";

    private String[] columns = { "id", "name", "email", "message", "reply", "created_at", "status" };

    public String getTable() {
        return this.table;
    }

    public String[] getColumns() {
        return this.columns;
    }

    private Integer id;
    private String name;
    private String email;
    private String message;
    private String reply;
    private LocalDateTime created_at;
    private Integer status;  

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getReply() {
        return reply;
    }
    public void setReply(String reply) {
        this.reply = reply;
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
    
}

