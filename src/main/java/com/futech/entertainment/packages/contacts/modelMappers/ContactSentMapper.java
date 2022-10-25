package com.futech.entertainment.packages.contacts.modelMappers;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

public class ContactSentMapper {
    
    
    
    public ContactSentMapper(Integer id, String name, String email, String case_number, String message, String reply,
            LocalDateTime created_at, Integer status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.case_number = case_number;
        this.message = message;
        this.reply = reply;
        this.created_at = created_at;
        this.status = status;
    }
    public ContactSentMapper(){};
    private Integer id;
    private String name;
    private String email;
    private String case_number;
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
    
    public String getCase_number() {
        return case_number;
    }

    public void setCase_number(String case_number) {
        this.case_number = case_number;
    }

}
