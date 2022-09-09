package com.futech.entertainment.packages.users.modelMappers;

import javax.validation.constraints.NotBlank;

import com.futech.entertainment.packages.core.validations.interfaces.Email;

public class UserProfileMapper {
    private Integer id;
    private Integer user_id;
    
    @NotBlank(message = "First name is required")
    private String first_name;

    @NotBlank(message = "Last name is required")
    private String last_name;

    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String birth;
    private Integer gender;
    private String thumbnail;

    private Integer status;

    public UserProfileMapper() {}

    public Integer getId() {
        return id;
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

    public String getfirst_name() {
        return first_name;
    }
    public void setfirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getlast_name() {
        return last_name;
    }
    public void setlast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirth() {
        return birth;
    }
    public void setBirth(String birth) {
        this.birth = birth;
    }

    public Integer getGender() {
        return gender;
    }
    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
}
