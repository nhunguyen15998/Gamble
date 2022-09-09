package com.futech.entertainment.packages.users.models;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Qualifier;

import com.futech.entertainment.packages.core.models.BaseModel;

@Qualifier("UserProfile")
public class UserProfile extends BaseModel {
    
    public UserProfile() {}

    private String table = "user_profiles";

    private String[] columns = { "id", "user_id", "first_name", "last_name", "birth", "thumbnail", "gender" };

    public String getTable() {
        return this.table;
    }

    public String[] getColumns() {
        return this.columns;
    }

    private Integer id;
    private Integer user_id;
    private String first_name;
    private String last_name;
    private String thumbnail;
    private LocalDate birth;
    private int gender;

    public Integer getId() {
        return this.id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return user_id;
    }
    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }

    public String getFirstName() {
        return first_name;
    }
    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }
    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public LocalDate getBirth() {
        return birth;
    }
    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    public int getGender() {
        return gender;
    }
    public void setGender(int gender) {
        this.gender = gender;
    }
    
}
