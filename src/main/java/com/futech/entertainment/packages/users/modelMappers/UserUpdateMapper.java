package com.futech.entertainment.packages.users.modelMappers;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.futech.entertainment.packages.core.middlewares.validations.interfaces.Email;
import com.futech.entertainment.packages.core.middlewares.validations.interfaces.IsExisted;
import com.futech.entertainment.packages.core.middlewares.validations.interfaces.Phone;

public class UserUpdateMapper {
    private Integer user_id;
    private Integer user_profile_id;


    @NotBlank(message = "First name is required")
    private String first_name;

    @NotBlank(message = "Last name is required")
    private String last_name;

    private String phone;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Birth is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;

    private Integer gender;
    private String thumbnail;

    private boolean is_admin;
 
    public UserUpdateMapper() {
    }

    public UserUpdateMapper(Integer user_id, Integer user_profile_id, String first_name,
            String last_name, String phone,String email, LocalDate birth, Integer gender, String thumbnail,
            boolean is_admin) {
        this.user_id = user_id;
        this.user_profile_id = user_profile_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
        this.email = email;
        this.birth = birth;
        this.gender = gender;
        this.thumbnail = thumbnail;
        this.is_admin = is_admin;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getUser_profile_id() {
        return user_profile_id;
    }

    public void setUser_profile_id(Integer user_profile_id) {
        this.user_profile_id = user_profile_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
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

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
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

    public boolean isIs_admin() {
        return is_admin;
    }

    public void setIs_admin(boolean is_admin) {
        this.is_admin = is_admin;
    }
    
    
}
