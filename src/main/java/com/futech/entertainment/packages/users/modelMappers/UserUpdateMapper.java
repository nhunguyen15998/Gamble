package com.futech.entertainment.packages.users.modelMappers;

import javax.validation.constraints.NotBlank;

import com.futech.entertainment.packages.core.middlewares.validations.interfaces.Email;
import com.futech.entertainment.packages.core.middlewares.validations.interfaces.Size;

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

    @NotBlank(message = "Birth is required")
    private String birth;

    private Integer gender;
    private String thumbnail;
    private Integer status;
    
    private String current_password;
    private String new_password;
    
    private String confirm_password;
    

    // private boolean is_admin;
    

    public UserUpdateMapper() {
    }
    

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
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

    public void setStatus(int status) {
        this.status = status;
    }
    // public boolean isIs_admin() {
    //     return is_admin;
    // }

    // public void setIs_admin(Boolean is_admin) {
    //     this.is_admin = is_admin;
    // }
   
    public String getConfirm_password() {
        return confirm_password;
    }

    public void setConfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
    }
    public Integer getUser_profile_id() {
        return user_profile_id;
    }


    public void setUser_profile_id(Integer user_profile_id) {
        this.user_profile_id = user_profile_id;
    }


    public void setStatus(Integer status) {
        this.status = status;
    }


    public String getCurrent_password() {
        return current_password;
    }


    public void setCurrent_password(String current_password) {
        this.current_password = current_password;
    }


    public String getNew_password() {
        return new_password;
    }


    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
