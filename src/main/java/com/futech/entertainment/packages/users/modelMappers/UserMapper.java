package com.futech.entertainment.packages.users.modelMappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.apache.tomcat.jni.Local;
import org.springframework.format.annotation.DateTimeFormat;

import com.futech.entertainment.packages.core.middlewares.validations.interfaces.Email;
import com.futech.entertainment.packages.core.middlewares.validations.interfaces.IsExisted;
import com.futech.entertainment.packages.core.middlewares.validations.interfaces.Phone;
import com.futech.entertainment.packages.core.middlewares.validations.interfaces.Size;

public class UserMapper {
    private Integer user_id;
    private Integer user_profile_id;


    @NotBlank(message = "First name is required")
    private String first_name;

    @NotBlank(message = "Last name is required")
    private String last_name;

    @IsExisted(message = "User is already existed")
    @NotBlank(message = "Phone is required")
    @Phone(message = "Invalid phone format")
    private String phone;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    private String birth;

    private Integer gender;
    private String thumbnail;
    private Integer status;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 15, message = "Password must range from 6 to 15")
    private String plain_password;

    

    @NotBlank(message = "Password confirmation is required")
    private String confirm_password;
    

    // private boolean is_admin;
    

    public UserMapper() {
    }
    

    // public UserMapper(Integer user_id,Integer user_pf_id, String first_name,
    //        String last_name, String phone,String email,
    //        LocalDate birth, Integer gender, String thumbnail, Integer status, Boolean is_admin) {
    //     this.user_id = user_id;
    //     this.first_name = first_name;
    //     this.last_name = last_name;
    //     this.phone = phone;
    //     this.email = email;
    //     this.birth = birth;
    //     this.gender = gender;
    //     this.thumbnail = thumbnail;
    //     this.status = status;
    //     this.is_admin = is_admin;
    //     this.user_profile_id=user_pf_id;
    // }



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
    public String getPlain_password() {
        return plain_password;
    }

    public void setPlain_password(String plain_password) {
        this.plain_password = plain_password;
    }
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


    public String getBirth() {
        return birth;
    }


    public void setBirth(String birth) {
        this.birth = birth;
    }


    public void setStatus(Integer status) {
        this.status = status;
    }
}
